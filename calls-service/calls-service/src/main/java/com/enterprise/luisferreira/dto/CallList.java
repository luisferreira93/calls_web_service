package com.enterprise.luisferreira.dto;

import com.enterprise.luisferreira.database.Call;
import com.enterprise.luisferreira.utils.CallType;
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

    /**
     * Gets the value of the calls property.
     */
    public List<Call> getCalls() {
        return calls;
    }

    /**
     * Sets the value of the calls property.
     *
     * @param calls allowed object is
     *              {@link List }
     */
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
