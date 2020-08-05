package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.Unit;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CreateActivityResult {

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
