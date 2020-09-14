package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.EditActivityVisibilityRequest;
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


    ActivityPathController(
            ProfileRepository profileRepository,
            ActivityRepository activityRepository,
            PathRepository pathRepository,
            LocationRepository locationRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.pathRepository = pathRepository;
        this.locationRepository = locationRepository;
    }

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

        if (!activity.getProfile().getId().equals((Integer) id)) {
            return new ResponseEntity<>(
                    "You are not authorized to edit the path of this activity",
                    HttpStatus.UNAUTHORIZED);
        }

        Path oldPath = pathRepository.findByActivity_Id(activityId);
        if (oldPath != null) {
            pathRepository.delete(oldPath);
        }

        Path newPath = request.generatePath(activity, locationRepository, pathRepository);
        newPath = pathRepository.save(newPath);

        activity.setPath(newPath);
        activityRepository.save(activity);

        return new ResponseEntity<>("Path updated for activity " + activityId, HttpStatus.OK);
    }
}
