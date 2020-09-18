package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.CreateActivityPathRequest;
import com.springvuegradle.team6.requests.LocationUpdateRequest;
import com.springvuegradle.team6.requests.EditPathRequest;
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

    public ActivityPathController(ProfileRepository profileRepository,
                                  ActivityRepository activityRepository,
                                  LocationRepository locationRepository,
                                  PathRepository pathRepository,
                                  ActivityRoleRepository activityRoleRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.locationRepository = locationRepository;
        this.pathRepository = pathRepository;
        this.activityRoleRepository = activityRoleRepository;
    }

    /**
     * Creates a path for the given activity using the given path type and locations
     * @param profileId the profile id of the activity owner
     * @param activityId the id of the activity receiving the  path
     * @param request request body consisting of activity path type and a list of locations
     * @param session the current session
     * @return http response
     */
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
            return new ResponseEntity<>("Invalid location data", HttpStatus.BAD_REQUEST);
        }

        Path path = new Path(locations, request.type);

        path.setLocations(locations);

        path = pathRepository.save(path);

        return new ResponseEntity(path.getLocations().size(), HttpStatus.CREATED);
    }

    /**
     * This endpoint edits the path. The old path is deleted along with the locations in it.
     * @param profileId profileId of of owner of activity
     * @param activityId id of the activity
     * @param request edit path request including the coordinates and the path type.
     * @param session the session of the user
     * @return OK for a success and 4xx for an error
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}/path")
    public ResponseEntity editPath(@PathVariable int profileId,
                                   @PathVariable int activityId,
                                   @RequestBody @Valid EditPathRequest request,
                                   HttpSession session) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            return new ResponseEntity<>(
                    "Activity doesnt exist",
                    HttpStatus.BAD_REQUEST);
        }
        Activity activity = optionalActivity.get();

        if (!UserSecurityService.checkIsAdminOrCreatorOrOrganiser((Integer) id, activity.getProfile().getId(), activityId, activityRoleRepository)) {
            return new ResponseEntity<>(
                    "You are not authorized to edit the path of this activity",
                    HttpStatus.UNAUTHORIZED);
        }
        Path oldPath = activity.getPath();

        Path newPath = request.generatePath(locationRepository);
        newPath = pathRepository.save(newPath);
        activity.setPath(newPath);
        activityRepository.save(activity);

        if (oldPath != null) {
            pathRepository.delete(oldPath);
        }

        return new ResponseEntity<>("Path updated for activity " + activityId, HttpStatus.OK);
    }

    /**
     * Returns an activities path if it exists
     * 401 UNAUTHORIZED: If session id is empty
     * 404 NOT_FOUND:  If activity does not exist or path does not exist.
     * @param profileId activity owners id
     * @param activityId activity id
     * @param session session data
     * @return Activity path if exists otherwise 4xx error
     */
    @GetMapping("/profiles/{profileId}/activities/{activityId}/path")
    public ResponseEntity getActivityPath(
            @PathVariable int profileId,
            @PathVariable int activityId,
            HttpSession session) {

        // Check user is logged in
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        // Check if activity exists
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
        }
        Activity activity = optionalActivity.get();

        // Check Path exists
        Optional<Path> optionalPath = Optional.ofNullable(activity.getPath());
        if (optionalPath.isEmpty()) {
            return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
        }
        Path path = optionalPath.get();

        return ResponseEntity.ok(path);
    }
}
