package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.VisibilityType;
import javax.validation.constraints.NotNull;

public class EditActivityVisibilityRequest {

  @NotNull
  @JsonProperty("visibility")
  public VisibilityType visibilityType;

  public VisibilityType getVisibilityType() {
    return visibilityType;
  }
}
