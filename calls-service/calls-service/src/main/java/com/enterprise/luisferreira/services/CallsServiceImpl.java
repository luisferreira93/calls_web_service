package com.enterprise.luisferreira.services;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.utils.CallList;
import com.enterprise.luisferreira.utils.CallType;
import com.enterprise.luisferreira.webservices.CallsResource;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CallsServiceImpl implements CallsService {

    private static final Logger LOG = LoggerFactory.getLogger(CallsServiceImpl.class);


    @Transactional
    @Override
    public void processCalls(CallList calls) {
        for(Call call : calls.getCalls()){
            createCall(call);
        }
    }

    @Transactional
    @Override
    public void deleteCall(Long callId){
        final Call call = Call.findById(callId);

        if (call == null){
            //throw exception
        }

        call.delete();
    }

    @Override
    public List<Call> retrieveCalls(int limit, int offset, CallType callType) {
        List<Call> calls = Call.list("call_type", callType.getType());
        LOG.info(calls.toString());
        return null;
    }

    private void createCall(Call call){
        PanacheEntityBase.persist(new Call(call.getCallerNumber(),
                call.getCalleeNumber(),
                call.getStartTimestamp(),
                call.getEndTimestamp(),
                call.getCallType()));
    }
}
