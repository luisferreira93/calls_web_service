package com.enterprise.luisferreira.services;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.dto.CallStatistics;
import com.enterprise.luisferreira.dto.DayStatistics;
import com.enterprise.luisferreira.exceptions.CommonException;
import com.enterprise.luisferreira.exceptions.CommonExceptionConstants;
import com.enterprise.luisferreira.utils.CallType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.enterprise.luisferreira.utils.Constants.DEFAULT_PAGINATION_VALUE;


@ApplicationScoped
public class CallsServiceImpl implements CallsService {

    private static final Logger LOG = LoggerFactory.getLogger(CallsServiceImpl.class);

    /**
     * This method inserts calls in the database.
     *
     * @param calls {@link CallList} with a list of Call to be inserted.
     */
    @Transactional
    @Override
    public void createCalls(CallList calls) {
        for (Call call : calls.getCalls()) {
            LOG.info("Creating call. callType={}, callerNumber={}, calleeNumber={}",
                    call.getCallType(), call.getCallerNumber(), call.getCalleeNumber());
            createCall(call);
        }
    }

    /**
     * This method delete calls in the database.
     *
     * @param callId {@link Long} the identifier of the call to be deleted.
     */
    @Transactional
    @Override
    public void deleteCall(Long callId) throws CommonException {
        final Call call = Call.findById(callId);

        if (call == null) {
            throw new CommonException(CommonExceptionConstants.CALL_NOT_FOUND,
                    "The call you are trying to delete does not exist in the system.");
        }

        call.delete();
    }

    /**
     * Method to retrieve a certain amount of calls with pagination.
     *
     * @param limit    {@link Integer} defines the maximum results to return.
     * @param offset   {@link Integer} an int to define where the list starts.
     * @param callType {@link CallType} the callType to filter.
     * @return returns a Response {@link Response} wuith the information.
     */
    @Override
    public Response retrieveCalls(int limit, int offset, CallType callType) {
        try {
            CallList callList = new CallList();
            callList.setCalls(processList(retrieveCallList(callType), limit, offset));
            return Response.ok()
                    .header("content-type", MediaType.APPLICATION_JSON)
                    .entity(callList)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (CommonException e) {
            return Response.status(e.getReason().getStatusCode()).entity(e.getMessage()).build();
        }
    }

    /**
     * This method returns the statistics in a certain interval of dates.
     *
     * @param startDate {@link Date} the beginning of the interval.
     * @param endDate   {@link Date} the end of the interval.
     * @return a Response {@link Response} wuith the information.
     */
    @Override
    public Response getStatistics(String startDate, String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = dateFormat.parse(startDate);
            Date finalDate = dateFormat.parse(endDate);
            List<Call> calls = new ArrayList<>();
            for (Call call : retrieveCallList(null).list()) {
                if (checkDate(call, beginDate, finalDate)) {
                    calls.add(call);
                }
            }
            return Response.ok()
                    .entity(collectStats(calls).getStatistics())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (ParseException | CommonException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The date inserted is in the wrong format. It must be yyyy-MM-dd")
                    .build();
        }
    }

    /**
     * Method to parse the statistics.
     *
     * @param calls {@link List} a list with the calls to process.
     * @return a CallStatistics object {@link CallStatistics} with the information.
     */
    private CallStatistics collectStats(List<Call> calls) {
        List<DayStatistics> dayStats = new ArrayList<>();
        for (Call call : calls) {
            if (dayStats.stream().noneMatch(day -> call.getStartTimestamp()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .equals(day.getDay()))) {
                DayStatistics dayStatistics = new DayStatistics();
                dayStatistics.setDay(call.getStartTimestamp().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
                processDurationType(dayStatistics.getTypeDurations(), call);
                processCallerNumber(dayStatistics.getCallerNumberCalls(), call);
                processCalleeNumber(dayStatistics.getCalleeNumberCalls(), call);
                dayStatistics.incrementCall();
                dayStatistics.setTotalCost(dayStatistics.getTotalCost() + processCost(call));
                dayStats.add(dayStatistics);
            } else {
                DayStatistics day = dayStats.stream()
                        .filter(x -> call.getStartTimestamp().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate().equals(x.getDay()))
                        .findAny()
                        .orElse(null);
                if (day != null) {
                    processDurationType(day.getTypeDurations(), call);
                    processCallerNumber(day.getCallerNumberCalls(), call);
                    processCalleeNumber(day.getCalleeNumberCalls(), call);
                    day.incrementCall();
                    day.setTotalCost(day.getTotalCost() + processCost(call));
                }
            }
        }
        CallStatistics callStatistics = new CallStatistics();
        callStatistics.setStatistics(dayStats);
        return callStatistics;
    }

    /**
     * Method to process the cost of a call.
     *
     * @param call {@link Call} the call to calculate the costs.
     * @return a value {@link Double} with the costs.
     */
    private Double processCost(Call call) {
        int callType = call.getCallType().getType();
        Double totalCost = 0.0;
        if (callType == 1) {
            Long minutesDouble = convertMillisecondsToMinutes(calculateDuration(call));
            double doubleMinutes = minutesDouble.doubleValue();
            if (doubleMinutes <= 5) {
                totalCost = +(doubleMinutes * 0.10);
            } else {
                totalCost = +(5.0 * 0.10) + ((doubleMinutes - 5.0) * 0.05);
            }
        }
        return totalCost;
    }

    /**
     * Inserts the caller number in a map and counts the calls.
     *
     * @param callerNumberCalls a map {@link Map} with the already existing information to update.
     * @param call              the call {@link Call} to update the Map.
     */
    private void processCallerNumber(Map<String, Long> callerNumberCalls, Call call) {
        callerNumberCalls.putIfAbsent(call.getCallerNumber(), (long) 0);
        callerNumberCalls.put(call.getCallerNumber(),
                callerNumberCalls.get(call.getCallerNumber()) + 1);
    }

    /**
     * Inserts the callee number in a map and counts the calls.
     *
     * @param calleeNumberCalls a map {@link Map} with the already existing information to update.
     * @param call              the call {@link Call} to update the Map.
     */
    private void processCalleeNumber(Map<String, Long> calleeNumberCalls, Call call) {
        calleeNumberCalls.putIfAbsent(call.getCalleeNumber(), (long) 0);
        calleeNumberCalls.put(call.getCalleeNumber(),
                calleeNumberCalls.get(call.getCalleeNumber()) + 1);
    }

    /**
     * Method to calculate the duration of a call based on the type.
     *
     * @param typeDurations a map {@link Map} to update the information regarding CallType
     *                      {@link CallType}.
     * @param call          the call {@link Call} to process.
     */
    private void processDurationType(Map<String, Long> typeDurations, Call call) {
        if (call.getCallType().getType() == 0) {
            typeDurations.putIfAbsent("INBOUND", (long) 0);
            typeDurations.put("INBOUND",
                    typeDurations.get("INBOUND") + convertMillisecondsToMinutes(calculateDuration(call)));
        } else {
            typeDurations.putIfAbsent("OUTBOUND", (long) 0);
            typeDurations.put("OUTBOUND",
                    typeDurations.get("OUTBOUND") + convertMillisecondsToMinutes(calculateDuration(call)));
        }
    }

    /**
     * Calculates the duration of a call.
     *
     * @param call the call {@link Call} to calculate the duration.
     * @return a value {@link Long} with the duration.
     */
    private Long calculateDuration(Call call) {
        return Math.abs(call.getEndTimestamp().getTime() - call.getStartTimestamp().getTime());
    }

    /**
     * Auxiliary method to convert milliseconds to minutes.
     *
     * @param time a variable {@link Long} with the time to convert.
     * @return a variable {@link Long} with the time in minutes.
     */
    private long convertMillisecondsToMinutes(long time) {
        return time / 60000;
    }

    /**
     * This method checks if a certain date ios between an interval of dates.
     *
     * @param call      the call {@link Call} with the date to check.
     * @param startDate {@link Date} the beginning of the interval.
     * @param endDate   {@link Date} the end of the interval.
     * @return a boolean {@link Boolean} with the response.
     */
    private boolean checkDate(Call call, Date startDate, Date endDate) throws CommonException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date callDate = dateFormat.parse(call.getStartTimestamp().toString());
            return callDate.before(endDate) && callDate.after(startDate);
        } catch (ParseException e) {
            throw new CommonException(CommonExceptionConstants.WRONG_FORMAT,
                    "The date inserted is in the wrong format. It must be yyyy-MM-dd");
        }
    }

    /**
     * Method to paginate a list.
     *
     * @param retrieveCallList a query request result {@link PanacheQuery}.
     * @param limit            {@link Integer} defines the maximum results to return.
     * @param offset           {@link Integer} an int to define where the list starts.
     * @return
     * @throws CommonException
     */
    private List<Call> processList(PanacheQuery<Call> retrieveCallList, int limit, int offset)
            throws CommonException {
        LOG.info("Filtering the list of calls. limit={}, offset={}, callList={}",
                limit, offset, retrieveCallList.list());
        validatePagination(limit, offset, retrieveCallList);
        List<Call> calls = retrieveCallList.list();
        if (limit != DEFAULT_PAGINATION_VALUE && offset == DEFAULT_PAGINATION_VALUE) {
            return calls.subList(0, limit);
        } else if (limit == DEFAULT_PAGINATION_VALUE && offset != DEFAULT_PAGINATION_VALUE) {
            return calls.subList(offset, calls.size());
        } else if (limit != DEFAULT_PAGINATION_VALUE) {
            return calls.subList(offset, validateListSize(calls.size(), limit, offset));
        }
        return calls;
    }

    /**
     * Method to validate if a list is too big for the max results input
     *
     * @param size   the size of the original list
     * @param limit  {@link Integer} defines the maximum results to return.
     * @param offset {@link Integer} an int to define where the list starts.
     * @return the size to use in order to format the list
     */
    private static int validateListSize(int size, int limit, int offset) {
        if (size > limit && size > offset + limit && offset < size) {
            return offset + limit;
        }
        return size;
    }

    /**
     * Method to validate a list pagination.
     *
     * @param limit            {@link Integer} defines the maximum results to return.
     * @param offset           {@link Integer} an int to define where the list starts.
     * @param retrieveCallList a list of calls to paginate.
     * @throws CommonException an exception thrown if any validation fails.
     */
    private void validatePagination(int limit, int offset, PanacheQuery<Call> retrieveCallList)
            throws CommonException {
        if (offset < 0) {
            throw new CommonException(CommonExceptionConstants.PAGINATED_REQUEST_OUT_OF_BOUNDS_INDEX,
                    "'offset' filter cannot be negative.");
        }

        if (offset > retrieveCallList.count()) {
            throw new CommonException(CommonExceptionConstants.PAGINATED_REQUEST_OUT_OF_BOUNDS_INDEX,
                    "'offset' filter cannot be greater than the total of calls that exist in the database.");
        }

        if (limit < 0) {
            throw new CommonException(CommonExceptionConstants.ILLEGAL_ARGUMENT_EXCEPTION,
                    "'limit' filter cannot be smaller than the default value.");
        }

        if (limit > retrieveCallList.count()) {
            throw new CommonException(CommonExceptionConstants.PAGINATED_REQUEST_OUT_OF_BOUNDS_INDEX,
                    "'limit' filter cannot be greater than the total of calls that exist in the database.");
        }
    }

    private PanacheQuery<Call> retrieveCallList(CallType callType) {
        return callType != null ? Call.find("call_type", callType.getType()) : Call.findAll();
    }

    private void createCall(Call call) {
        PanacheEntityBase.persist(
                new Call(call.getCallerNumber(), call.getCalleeNumber(), call.getStartTimestamp(),
                        call.getEndTimestamp(), call.getCallType()));
    }

}
