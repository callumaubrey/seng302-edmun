package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

public class LocationUpdateRequest {

  @JsonProperty("latitude")
  @Range(min=-90, max=90, message = "latitude must be between -90 and 90")
  public double latitude;

  @JsonProperty("longitude")
  @Range(min=-180, max=180, message = "longitude must be between -180 and 180")
  public double longitude;
}
