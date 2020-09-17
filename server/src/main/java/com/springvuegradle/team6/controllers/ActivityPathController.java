package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.EditPathRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;


/**
 * This controller contains end points related to getting, creating, editing and deleting the
 * activity.
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
    private final PathRepository pathRepository;
    private final LocationRepository locationRepository;
    private final ActivityRoleRepository roleRepository;


    ActivityPathController(
            ProfileRepository profileRepository,
            ActivityRepository activityRepository,
            PathRepository pathRepository,
            LocationRepository locationRepository,
            ActivityRoleRepository roleRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.pathRepository = pathRepository;
        this.locationRepository = locationRepository;
        this.roleRepository = roleRepository;
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
        if (!optionalActivity.isPresent()) {
            return new ResponseEntity<>(
                    "Activity doesnt exist",
                    HttpStatus.BAD_REQUEST);
        }
        Activity activity = optionalActivity.get();

        if (!UserSecurityService.checkIsAdminOrCreatorOrOrganiser((Integer) id, activity.getProfile().getId(), activityId, roleRepository)) {
            return new ResponseEntity<>(
                    "You are not authorized to edit the path of this activity",
                    HttpStatus.UNAUTHORIZED);
        }
        Path oldPath = pathRepository.findByActivity_Id(activityId);

        Path newPath = request.generatePath(activity,locationRepository);
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

        // Check Path exists
        Optional<Path> optionalPath = Optional.ofNullable(pathRepository.findByActivity_Id(activityId));
        if (optionalPath.isEmpty()) {
            return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
        }
        Path path = optionalPath.get();

        return ResponseEntity.ok(path);
    }
}
