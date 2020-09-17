package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.PathType;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.PathRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EditPathRequest {

    @Size(min = 2)
    @NotNull
    @JsonProperty("coordinates")
    private List<Location> coordinates;

    @NotNull
    @JsonProperty("pathType")
    private PathType pathType;

    public Path generatePath(LocationRepository locationRepository) {
        ArrayList<Location> pathCoordinates = new ArrayList<>();
        for (Location location : coordinates) {
            location = locationRepository.save(location);
            pathCoordinates.add(location);
        }
        return new Path(pathCoordinates, pathType);
    }

    public List<Location> getCoordinates() { return coordinates; }

    public PathType getPathType() { return pathType; }
}
