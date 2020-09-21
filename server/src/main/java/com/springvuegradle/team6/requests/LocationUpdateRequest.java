package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public class LocationUpdateRequest {

  @Range(min = -90, max = 90, message = "latitude must be between -90 and 90")
  @NotNull(message = "latitude can not be null")
  public Double latitude;

  @Range(min = -180, max = 180, message = "longitude must be between -180 and 180")
  @NotNull(message = "longitude can not be null")
  public Double longitude;
}
