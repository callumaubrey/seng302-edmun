package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {

  /** double value for exact latitude coordinate */
  @NotNull(message = "latitude cannot be null")
  public double latitude;

  /** double value for exact longitude coordinate */
  @NotNull(message = "longitude cannot be null")
  public double longitude;
}
