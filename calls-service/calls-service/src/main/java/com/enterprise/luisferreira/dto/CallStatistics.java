package com.enterprise.luisferreira.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * POJO to represent the object that saves the statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallStatistics {

    @JsonProperty("callStatistics")
    private List<DayStatistics> dayStatistics;

    /**
     * Gets the value of the dayStatistics property.
     */
    public List<DayStatistics> getStatistics() {
        return dayStatistics;
    }

    /**
     * Sets the value of the dayStatistics property.
     *
     * @param dayStatistics allowed object is
     *                      {@link List }
     */
    public void setStatistics(List<DayStatistics> dayStatistics) {
        this.dayStatistics = dayStatistics;
    }


    @Override
    public String toString() {
        return "CallStatistics{" +
                "dayStatistics=" + dayStatistics +
                '}';
    }
}
