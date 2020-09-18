package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ResetPasswordRequest {

  @NotNull
  @NotEmpty
  @JsonProperty("email")
  @javax.validation.constraints.Email
  public String email;

  public String getEmail() {
    return email;
  }
}
