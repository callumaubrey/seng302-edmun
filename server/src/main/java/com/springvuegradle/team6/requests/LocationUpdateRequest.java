package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.location.NamedLocation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {
    @NotNull @NotEmpty
    public String city;

    public String state;

    @NotNull @NotEmpty
    public String country;

    public NamedLocation getLocation() {
        return new NamedLocation(country, state, city);
    }
}
