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
    private List<PathCoordinateRequest> coordinates;

    @JsonProperty("pathType")
    private String pathType;

    public Path generatePath(Activity activity, LocationRepository locationRepository, PathRepository pathRepository) {
        ArrayList<Location> pathCoordinates = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) {
            Location pathPoint = new Location(coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude());
            pathPoint = locationRepository.save(pathPoint);
            pathCoordinates.add(pathPoint);
        }
        Path finalPath = new Path(activity, pathCoordinates, PathType.STRAIGHT);

        if (pathType.equals("straight")) {
            finalPath.setType(PathType.STRAIGHT);
        } else {
            finalPath.setType(PathType.DEFINED);
        }
        return finalPath;
    }

    public List<PathCoordinateRequest> getCoordinates() { return coordinates; }

    public String getPathType() { return pathType; }
}
