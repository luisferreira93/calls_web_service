package com.enterprise.luisferreira;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.services.CallsServiceImpl;
import com.enterprise.luisferreira.utils.CallType;
import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mock
@ApplicationScoped
public class MockTest  extends CallsServiceImpl {

    @Override
    public Response retrieveCalls(int limit, int offset, CallType callType) {
        if(limit < 0 || offset < 0){
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            return Response.ok()
                    .header("content-type", MediaType.APPLICATION_JSON)
                    .entity(prepareList())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    public CallList prepareList() {
        CallList callList = new CallList();
        Call call = new Call("911111111", "917441984", new Date(), new Date(), CallType.INBOUND);
        Call call2 = new Call("922222222", "919685412", new Date(), new Date(), CallType.INBOUND);
        List<Call> calls = new ArrayList<>();
        calls.add(call);
        calls.add(call2);
        callList.setCalls(calls);
        return callList;
    }
}
