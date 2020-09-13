package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.PathType;
import com.springvuegradle.team6.models.repositories.LocationRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EditPathRequest {

    @Size(min = 2)
    @NotNull
    @JsonProperty("coordinates")
    private List<PathCoordinateRequest> coordinates;

    @JsonProperty("pathType")
    private String pathType;

    public Path generatePath(Activity activity) {
        ArrayList<Location> pathCoordinates = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            Location pathPoint = new Location(coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude());
            pathCoordinates.add(pathPoint);
        }

        Path finalPath;
        if (pathType.equals("straight")) {
            finalPath = new Path(activity, pathCoordinates, PathType.STRAIGHT);
        } else {
            finalPath = new Path(activity, pathCoordinates, PathType.DEFINED);
        }
        return finalPath;
    }
}
