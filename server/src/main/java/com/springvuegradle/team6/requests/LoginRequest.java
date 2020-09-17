package com.springvuegradle.team6.requests;

import javax.validation.constraints.*;

public class LoginRequest {

  @NotNull
  @Email(message = "Email should be valid")
  @Pattern(
      regexp =
          "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")
  public String email;

  @NotNull public String password;

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }
}
