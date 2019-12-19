package com.enterprise.luisferreira.services;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.dto.CallList;
import com.enterprise.luisferreira.dto.CallStatistics;
import com.enterprise.luisferreira.exceptions.CommonException;
import com.enterprise.luisferreira.utils.CallType;

import java.util.Date;
import java.util.List;

public interface CallsService {

    void processCalls(CallList calls);

    void deleteCall(Long callId);

    List<Call> retrieveCalls(int limit, int offset, CallType callType) throws CommonException;

    CallStatistics getStatistics(String startDate, String endDate);
}
