package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.Unit;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CreateActivityResult {

  @NotEmpty
  @NotNull
  @JsonProperty("profile_id")
  private int profileId;

  @NotNull
  @NotEmpty
  @JsonProperty("metric_id")
  private int metricId;

  @NotEmpty
  @JsonProperty("value")
  private String value;

  public int getMetricId() {
    return metricId;
  }
}
