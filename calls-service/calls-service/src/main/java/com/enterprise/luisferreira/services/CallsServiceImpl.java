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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.enterprise.luisferreira.utils.Constants.DEFAULT_PAGINATION_VALUE;


@ApplicationScoped
public class CallsServiceImpl implements CallsService {

  private static final Logger LOG = LoggerFactory.getLogger(CallsServiceImpl.class);

  @Transactional
  @Override
  public void processCalls(CallList calls) {
    for (Call call : calls.getCalls()) {
      createCall(call);
    }
  }

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
      return null;
    }
  }

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
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return Response.noContent().build();
  }

  private CallStatistics collectStats(List<Call> calls) {
    List<DayStatistics> dayStats = new ArrayList<>();
    for (Call call : calls) {
      if (dayStats.stream().noneMatch(day -> call.getStartTimestamp().equals(day.getDay()))) {
        DayStatistics dayStatistics = new DayStatistics();
        dayStatistics.setDay(call.getStartTimestamp());
        processDurationType(dayStatistics.getTypeDurations(), call);
        processCallerNumber(dayStatistics.getCallerNumberCalls(), call);
        processCalleeNumber(dayStatistics.getCalleeNumberCalls(), call);
        dayStatistics.incrementCall();
        dayStatistics.setTotalCost(dayStatistics.getTotalCost() + processCost(call));
        dayStats.add(dayStatistics);
      } else {
        DayStatistics day = dayStats.stream()
            .filter(x -> call.getStartTimestamp().equals(x.getDay()))
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

  private void processCallerNumber(Map<String, Long> callerNumberCalls, Call call) {
    callerNumberCalls.putIfAbsent(call.getCallerNumber(), (long) 0);
    callerNumberCalls.put(call.getCallerNumber(),
        callerNumberCalls.get(call.getCallerNumber()) + 1);
  }

  private void processCalleeNumber(Map<String, Long> calleeNumberCalls, Call call) {
    calleeNumberCalls.putIfAbsent(call.getCalleeNumber(), (long) 0);
    calleeNumberCalls.put(call.getCalleeNumber(),
        calleeNumberCalls.get(call.getCalleeNumber()) + 1);
  }

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

  private Long calculateDuration(Call call) {
    return Math.abs(call.getEndTimestamp().getTime() - call.getStartTimestamp().getTime());
  }

  private long convertMillisecondsToMinutes(long time) {
    return time / 60000;
  }

  private boolean checkDate(Call call, Date startDate, Date endDate) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date callDate = dateFormat.parse(call.getStartTimestamp().toString());
      return callDate.before(endDate) && callDate.after(startDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  private List<Call> processList(PanacheQuery<Call> retrieveCallList, int limit, int offset)
      throws CommonException {
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
   * @param size  the size of the original list
   * @param limit the query input parameter of max results
   *
   * @return the size to use in order to format the list
   */
  private static int validateListSize(int size, int limit, int offset) {
    if (size > limit && size > offset + limit && offset < size) {
      return offset + limit;
    }
    return size;
  }


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
