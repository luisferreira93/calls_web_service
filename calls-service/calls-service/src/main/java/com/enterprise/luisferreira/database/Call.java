package com.enterprise.luisferreira.database;

import com.enterprise.luisferreira.utils.CallType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Call extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "call_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "caller_number")
    private String callerNumber;

    @Column(name = "callee_number")
    private String calleeNumber;

    @Column(name = "start_timestamp")
    private Date startTimestamp;

    @Column(name = "end_timestamp")
    private Date endTimestamp;

    @Column(name = "call_type")
    private CallType callType;

    public Call(String callerNumber, String calleeNumber, Date startTimestamp, Date endTimestamp, CallType callType) {
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.callType = callType;
    }

    public Call() {
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getCalleeNumber() {
        return calleeNumber;
    }

    public void setCalleeNumber(String calleeNumber) {
        this.calleeNumber = calleeNumber;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return Objects.equals(callerNumber, call.callerNumber) &&
                Objects.equals(calleeNumber, call.calleeNumber) &&
                Objects.equals(startTimestamp, call.startTimestamp) &&
                Objects.equals(endTimestamp, call.endTimestamp) &&
                callType == call.callType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(callerNumber, calleeNumber, startTimestamp, endTimestamp, callType);
    }

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", callerNumber='" + callerNumber + '\'' +
                ", calleeNumber='" + calleeNumber + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", callType=" + callType +
                '}';
    }
}
