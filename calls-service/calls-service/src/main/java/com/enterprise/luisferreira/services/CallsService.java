package com.enterprise.luisferreira.services;

import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.exceptions.CommonException;
import com.enterprise.luisferreira.utils.CallType;
import javax.ws.rs.core.Response;

public interface CallsService {

    void processCalls(CallList calls);

    void deleteCall(Long callId) throws CommonException;

    Response retrieveCalls(int limit, int offset, CallType callType) throws CommonException;

    Response getStatistics(String startDate, String endDate);
}
