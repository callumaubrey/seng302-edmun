package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PathCoordinateRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("latitude")
    private Double latitiude;

    @NotNull
    @NotEmpty
    @JsonProperty("longitude")
    private Double longitude;

    public Double getLongitude() {
        return longitude;
    }
    public Double getLatitude() {
        return latitiude;
    }
}
