package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EditActivityResultRequest {
    @NotNull
    @JsonProperty("metric_id")
    private int metricId;

    @JsonProperty("value")
    private String value;

    @JsonProperty("start")
    private LocalDateTime start;

    @JsonProperty("end")
    private LocalDateTime end;

    public int getMetricId() {
        return metricId;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
