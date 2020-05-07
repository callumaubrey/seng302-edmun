package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.location.NamedLocation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {
    @NotNull(message="city cannot be null") @NotEmpty(message="city cannot be empty")
    public String city;

    public String state;

    @NotNull(message="country cannot be null") @NotEmpty(message="country cannot be empty")
    public String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
