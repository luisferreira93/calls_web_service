package com.enterprise.luisferreira.webservices;

import com.enterprise.luisferreira.dto.CallStatistics;
import com.enterprise.luisferreira.exceptions.CommonException;
import com.enterprise.luisferreira.services.CallsService;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.utils.CallType;
import com.enterprise.luisferreira.utils.MessageType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("")
public class CallsResource {

    private static final Logger LOG = LoggerFactory.getLogger(CallsResource.class);

    @Inject
    private CallsService callsService;

    @Operation(summary = "Create Call",
            description = "Use this service to create any number of calls you need")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create")
    public String createCall(@RequestBody(name = "calls", required = true) final CallList calls) {
        LOG.info("Create call request received and being processed. calls={}", calls);
        callsService.processCalls(calls);
        LOG.info("The calls were created with success");
        return MessageType.SUCCESS.getMessage();
    }

    @Operation(summary = "Delete Call",
            description = "Use this service to delete a call you need")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "404", description = "Call was not found"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/delete/{callId}")
    public String deleteCall(@PathParam("callId") final Long callId) {
        LOG.info("Delete call request received and being processed. call_id={}", callId);
        callsService.deleteCall(callId);
        LOG.info("The call with the id {} was deleted with success", callId);
        return MessageType.SUCCESS.getMessage();
    }

    @Operation(summary = "Get Calls",
            description = "Use this service to retrieve any call")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/retrieve")
    public List getCall(@DefaultValue("0") @QueryParam("limit") int limit,
                        @DefaultValue("0") @QueryParam("offset") int offset,
                        @QueryParam("callType") final CallType callType) {
        List calls = new ArrayList();
        try {
            calls = callsService.retrieveCalls(limit, offset, callType);
        } catch (CommonException e) {
            e.printStackTrace();
        }
        return calls;
    }

    @Operation(summary = "Get Statistics for a certain amount of days",
            description = "Use this service to retrieve statistics of calls")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/statistics")
    public Response getStatistics(@QueryParam("startDate") String startDate,
                                        @QueryParam("endDate") String endDate) {
        return Response.status(Response.Status.OK)
                .entity(callsService.getStatistics(startDate, endDate))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
