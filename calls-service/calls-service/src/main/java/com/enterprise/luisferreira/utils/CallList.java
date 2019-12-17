package com.enterprise.luisferreira.utils;

import com.enterprise.luisferreira.database.Call;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * POJO to retrieve a list of calls.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallList implements Serializable {

    @JsonProperty("calls")
    private List<Call> calls;

    public List<Call> getCalls() {
        return calls;
    }

    public void setCalls(List<Call> calls) {
        this.calls = calls;
    }

    @Override
    public String toString() {
        return "CallList{" +
                "calls=" + calls +
                '}';
    }
}
