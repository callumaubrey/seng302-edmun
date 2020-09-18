package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.PathType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateActivityPathRequest {

    @JsonProperty("type")
    @NotNull(message = "type cannot be null")
    public PathType type;

    @JsonProperty("locations")
    @Size(min = 2)
    @NotNull(message = "locations connot be null")
    public List<LocationUpdateRequest> locations;

    public List<LocationUpdateRequest> getLocation() {
        return locations;
    }

    public void setLocation(List<LocationUpdateRequest> locations) {
        this.locations = locations;
    }
}
