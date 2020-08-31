package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {

  @NotNull(message = "latitude cannot be null")
  public double latitude;

  @NotNull(message = "longitude cannot be null")
  public double longitude;

}
