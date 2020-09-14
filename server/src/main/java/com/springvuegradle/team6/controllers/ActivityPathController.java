package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.CreateActivityPathRequest;
import com.springvuegradle.team6.requests.LocationUpdateRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This controller contains end points related to getting, creating, editing and deleting an
 * activity's path.
 */
@CrossOrigin(
        origins = {
                "http://localhost:9000",
                "http://localhost:9500",
                "https://csse-s302g7.canterbury.ac.nz/test",
                "https://csse-s302g7.canterbury.ac.nz/prod"
        },
        allowCredentials = "true",
        allowedHeaders = "://",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.DELETE,
                RequestMethod.PUT,
                RequestMethod.PATCH
        })
@RestController
@RequestMapping("")
public class ActivityPathController {

    private final ProfileRepository profileRepository;
    private final ActivityRepository activityRepository;
    private final LocationRepository locationRepository;
    private final PathRepository pathRepository;
    private final ActivityRoleRepository activityRoleRepository;

    public ActivityPathController(ProfileRepository profileRepository, ActivityRepository activityRepository, LocationRepository locationRepository, PathRepository pathRepository, ActivityRoleRepository activityRoleRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.locationRepository = locationRepository;
        this.pathRepository = pathRepository;
        this.activityRoleRepository = activityRoleRepository;
    }

    @PostMapping("/profiles/{profileId}/activities/{activityId}/path")
    public ResponseEntity<String> createActivityPath(
            @PathVariable Integer profileId,
            @PathVariable Integer activityId,
            @Valid @RequestBody CreateActivityPathRequest request,
            HttpSession session) {

        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            return new ResponseEntity<>("Profile does not exist", HttpStatus.BAD_REQUEST);
        }

        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        Integer loggedInId = (Integer) id;

        Profile profile = optionalProfile.get();

        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            return new ResponseEntity<>("Activity does not exist", HttpStatus.BAD_REQUEST);
        }

        Activity activity = optionalActivity.get();

        List<ActivityRole> roles =
                activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, loggedInId);
        boolean isOrganiser = false;
        for (ActivityRole role : roles) {
            if (role.getRole().equals("organiser")) {
                isOrganiser = true;
            }
        }

        if (!isOrganiser) {
            ResponseEntity<String> checkAuthorisedResponse =
            UserSecurityService.checkAuthorised(profileId, session, profileRepository);
            if (checkAuthorisedResponse != null && checkAuthorisedResponse.getStatusCode() != null) {
                return checkAuthorisedResponse;
            }
        }

        List<Location> locations = new ArrayList<>();

        try {
            for (LocationUpdateRequest locationUpdateRequest : request.locations) {
                Location location = new Location(locationUpdateRequest.latitude, locationUpdateRequest.longitude);
                location = locationRepository.save(location);
                locations.add(location);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Path path = new Path(activity, locations, request.type);

        path = pathRepository.save(path);

        Path path2 = pathRepository.findByActivity_Id(activityId);

        return new ResponseEntity(path2.getLocations().size(), HttpStatus.CREATED);
    }
}
