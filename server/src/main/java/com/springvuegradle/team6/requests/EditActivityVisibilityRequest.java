package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.VisibilityType;
import javax.validation.constraints.NotNull;

public class EditActivityVisibilityRequest {

  @NotNull
  @JsonProperty("visibility")
  public String visibilityType;

  public VisibilityType generateVisibilityType() {
    String toUpperCase = visibilityType.substring(0, 1).toUpperCase() + visibilityType.substring(1);
    return VisibilityType.valueOf(toUpperCase);
  }
}
