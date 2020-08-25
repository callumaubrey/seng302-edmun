package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {

  @NotNull(message = "latitude cannot be null")
  @NotEmpty(message = "latitude cannot be empty")
  public double latitude;

  @NotNull(message = "longitude cannot be null")
  @NotEmpty(message = "longitude cannot be empty")
  public double longitude;

}
