package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.PathType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class EditPathRequest {

    @Size(min = 2)
    @NotNull
    private List<Location> coordinates;

    private PathType pathType;

    public List<Location> getCoordinates() { return this.coordinates; }

    public PathType getPathType() { return this.pathType; }

}
