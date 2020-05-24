package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotNull;

public class AdminEditRole {

    @NotNull
    public String role_name;

    public String getRole() {
        return role_name;
    }
}
