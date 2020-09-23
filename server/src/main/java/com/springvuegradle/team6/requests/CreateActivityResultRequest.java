package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.SpecialMetric;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

public class CreateActivityResultRequest {

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

  public void setMetricId(int metricId) {
    this.metricId = metricId;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public void setEnd(LocalDateTime end) {
    this.end = end;
  }

  public void setSpecialMetric(SpecialMetric specialMetric) {
    this.specialMetric = specialMetric;
  }
}
