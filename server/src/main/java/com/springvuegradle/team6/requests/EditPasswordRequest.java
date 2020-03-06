package com.springvuegradle.team6.requests;

import javax.validation.constraints.*;

public class EditPasswordRequest {
    
    /**
     * User id
     */
    @NotNull
    public Integer id;

    /**
     * Old user password, cannot be null
     */
    @NotNull
    public String oldpassword;

    /**
     * New user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    public String newpassword;

    /**
     * Repeated new user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    public String repeatedpassword;
}