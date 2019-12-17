package com.enterprise.luisferreira.webservices;

import com.enterprise.luisferreira.services.CallsService;
import com.enterprise.luisferreira.utils.CallList;
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
    public String deleteCall(@QueryParam("limit") int limit,
                             @QueryParam("offset") int offset,
                             @QueryParam("callType") final CallType callType) {
        LOG.info("Retrieving calls for the type={}", callType.getType());
        callsService.retrieveCalls(limit, offset, callType);
        return MessageType.SUCCESS.getMessage();
    }
}
