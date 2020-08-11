package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.SpecialMetric;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

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

    @JsonProperty("special_metric")
    private SpecialMetric specialMetric;

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

    public SpecialMetric getSpecialMetric() {
        return specialMetric;
    }
}
