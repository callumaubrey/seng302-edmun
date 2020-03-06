package com.springvuegradle.team6.requests;


import javax.validation.constraints.*;


public class LoginRequest {

    @NotNull
    @Email(message = "Email should be valid")
    public String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    public String password;

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
