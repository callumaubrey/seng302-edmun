package com.springvuegradle.team6.requests.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EmailRolePair {
    @JsonProperty("email")
    @NotNull(message = "email cannot be null")
    @NotEmpty(message = "email cannot be empty")
    private String email;

    @JsonProperty("role")
    @NotNull(message = "role cannot be null")
    @NotEmpty(message = "role cannot be empty")
    private String role;

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
