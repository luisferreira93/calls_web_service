package com.enterprise.luisferreira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO that represents each day entry of statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DayStatistics {

    @JsonProperty("day")
    private LocalDate day;

    @JsonProperty("typeDurations")
    Map<String, Long> typeDurations;

    @JsonProperty("totalCalls")
    int totalCalls;

    @JsonProperty("callerNumberCalls")
    Map<String, Long> callerNumberCalls;

    @JsonProperty("calleeNumberCalls")
    Map<String, Long> calleeNumberCalls;

    @JsonProperty("totalCost")
    Double totalCost;

    public DayStatistics() {
        this.typeDurations = new HashMap<>();
        this.calleeNumberCalls = new HashMap<>();
        this.callerNumberCalls = new HashMap<>();
        this.totalCost = 0.0;
    }

    /**
     * Gets the value of the day property.
     */
    public LocalDate getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     *
     * @param day allowed object is
     *            {@link Date }
     */
    public void setDay(LocalDate day) {
        this.day = day;
    }

    /**
     * Gets the value of the typeDurations property.
     */
    public Map<String, Long> getTypeDurations() {
        return typeDurations;
    }

    /**
     * Sets the value of the typeDurations property.
     *
     * @param typeDurations allowed object is
     *                      {@link Map }
     */
    public void setTypeDurations(Map<String, Long> typeDurations) {
        this.typeDurations = typeDurations;
    }

    /**
     * Gets the value of the totalCalls property.
     */
    public int getTotalCalls() {
        return totalCalls;
    }

    /**
     * Sets the value of the totalCalls property.
     *
     * @param totalCalls allowed object is
     *                   {@link Integer }
     */
    public void setTotalCalls(int totalCalls) {
        this.totalCalls = totalCalls;
    }

    /**
     * Gets the value of the callerNumberCalls property.
     */
    public Map<String, Long> getCallerNumberCalls() {
        return callerNumberCalls;
    }

    /**
     * Sets the value of the callerNumberCalls property.
     *
     * @param callerNumberCalls allowed object is
     *                          {@link Map }
     */
    public void setCallerNumberCalls(Map<String, Long> callerNumberCalls) {
        this.callerNumberCalls = callerNumberCalls;
    }

    /**
     * Gets the value of the calleeNumberCalls property.
     */
    public Map<String, Long> getCalleeNumberCalls() {
        return calleeNumberCalls;
    }

    /**
     * Sets the value of the calleeNumberCalls property.
     *
     * @param calleeNumberCalls allowed object is
     *                          {@link Map }
     */
    public void setCalleeNumberCalls(Map<String, Long> calleeNumberCalls) {
        this.calleeNumberCalls = calleeNumberCalls;
    }

    /**
     * Gets the value of the totalCost property.
     */
    public Double getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the value of the totalCost property.
     *
     * @param totalCost allowed object is
     *                  {@link Double }
     */
    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Auxiliary method to increment the property totalCalls.
     */
    public void incrementCall() {
        this.totalCalls++;
    }

    @Override
    public String toString() {
        return "DayStatistics{" +
                "day=" + day +
                ", typeDurations=" + typeDurations +
                ", totalCalls=" + totalCalls +
                ", callerNumberCalls=" + callerNumberCalls +
                ", calleeNumberCalls=" + calleeNumberCalls +
                ", totalCost=" + totalCost +
                '}';
    }
}
