package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {

  @JsonProperty("latitude")
  @Range(min=-90, max=90, message = "latitude must be between -90 and 90")
  @NotNull
  public Double latitude;

  @JsonProperty("longitude")
  @Range(min=-180, max=180, message = "longitude must be between -180 and 180")
  @NotNull
  public Double longitude;
}
