package com.enterprise.luisferreira.webservices;

import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.exceptions.CommonException;
import com.enterprise.luisferreira.services.CallsService;
import com.enterprise.luisferreira.utils.CallType;
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

@Path("")
public class CallsResource {

    private static final Logger LOG = LoggerFactory.getLogger(CallsResource.class);

    @Inject
    private CallsService callsService;

    @Operation(summary = "Create Call", description = "Use this service to create any number of "
            + "calls you need")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response createCall(
            @RequestBody(name = "calls", required = true) final CallList calls) {
        LOG.info("Create call request received and being processed. calls={}", calls);
        callsService.createCalls(calls);
        LOG.info("The calls were created with success");
        return Response.ok().build();
    }

    @Operation(summary = "Delete Call", description = "Use this service to delete a call you need")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "404", description = "Call was not found"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{callId}")
    public Response deleteCall(
            @PathParam("callId") final Long callId) {
        try {
            LOG.info("Delete call request received and being processed. call_id={}", callId);
            callsService.deleteCall(callId);
            LOG.info("The call with the id {} was deleted with success", callId);
        } catch (CommonException e) {
            return Response.noContent().build();
        }
        return Response.ok().build();
    }

    @Operation(summary = "Get Calls", description = "Use this service to retrieve any call")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "404", description = "No content"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieve")
    public Response getCall(
            @QueryParam("limit")
                    int limit,
            @QueryParam("offset")
                    int offset,
            @QueryParam("callType") final CallType callType) {
        LOG.info("Retrieve request for calls has been received and it is being processed. "
                        + "limit={}, offset={}, CallType={}",
                limit, offset, callType);
        return callsService.retrieveCalls(limit, offset, callType);
    }

    @Operation(summary = "Get Statistics for a certain amount of days", description = "Use this "
            + "service to retrieve statistics of calls")
    @APIResponses(value = {@APIResponse(responseCode = "200", description = "Successful operation"),
            @APIResponse(responseCode = "400", description = "Request parameters not acceptable"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/statistics")
    public Response getStatistics(
            @QueryParam("startDate")
                    String startDate,
            @QueryParam("endDate")
                    String endDate) {
        LOG.info("Statistics request for an interval of days has been received and it is being processed. "
                        + "startDate={}, endDate={}",
                startDate, endDate);
        return callsService.getStatistics(startDate, endDate);
    }

}
