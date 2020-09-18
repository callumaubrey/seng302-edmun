package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ChangePasswordWithoutOldPasswordRequest {

    /**
     * New user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @Size(min = 8)
    @JsonProperty("new_password")
    public String newPassword;


    /**
     * Repeated new user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @Size(min = 8)
    @JsonProperty("repeat_password")
    public String repeatPassword;


}
