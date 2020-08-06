package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.ActivityHistoryRepository;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.requests.CreateActivityResultRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class ActivityMetricController {

  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;
  private final ActivityRoleRepository activityRoleRepository;
  private final ActivityQualificationMetricRepository activityQualificationMetricRepository;
  private final ActivityResultRepository activityResultRepository;
  private final ActivityHistoryRepository activityHistoryRepository;

  ActivityMetricController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityRoleRepository activityRoleRepository,
      ActivityQualificationMetricRepository activityQualificationMetricRepository,
      ActivityResultRepository activityResultRepository,
      ActivityHistoryRepository activityHistoryRepository) {
    this.profileRepository = profileRepository;
    this.activityRepository = activityRepository;
    this.activityRoleRepository = activityRoleRepository;
    this.activityQualificationMetricRepository = activityQualificationMetricRepository;
    this.activityResultRepository = activityResultRepository;
    this.activityHistoryRepository = activityHistoryRepository;
  }

  /**
   * This endpoint creates a new activity result for a Participant of an activity
   * An admin can create a new activity result for an owner and a participant
   * An owner can create a new activity result for a participant
   *
   * @param profileId the user that is the activity result is for (not the activity owner id)
   * @param activityId activity ID
   * @param request the CreateActivityResultRequest class
   * @param session the HttpSession
   * @return
   */
  @PostMapping("/profiles/{profileId}/activities/{activityId}/result")
  public ResponseEntity createActivityResult(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @RequestBody @Valid CreateActivityResultRequest request,
      HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile profile = profileRepository.findById(profileId);
    if (profile == null) {
      return new ResponseEntity("User does not exist OWNER", HttpStatus.NOT_FOUND);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Activity activity = optionalActivity.get();
    Profile ownerProfile = activity.getProfile();

    List<ActivityRole> activityRoles = activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profile.getId());
    if (activityRoles.isEmpty()) {
      return new ResponseEntity("You don't have access", HttpStatus.UNAUTHORIZED);
    } else {
      ActivityRole activityRole = activityRoles.get(0);
      if (id.equals(profile.getId())) {
        // Doing your own result
        if (!ownerProfile.getId().equals(profile.getId())) {
          // If we are not the owner then we check if participant
          if (!activityRole.getActivityRoleType().equals(ActivityRoleType.Participant)) {
            return new ResponseEntity("You must be a participant" , HttpStatus.UNAUTHORIZED);
          }
        }
      } else {
        // Doing it for someone else
        boolean isAdminOrCreator = UserSecurityService.checkIsAdminOrCreator((Integer) id, ownerProfile.getId());
        if (isAdminOrCreator) {
          if (!ownerProfile.getId().equals(profile.getId())) {
            // If we are making result for owner as admin
            if (!activityRole.getActivityRoleType().equals(ActivityRoleType.Participant)) {
              return new ResponseEntity("You must be a participant B", HttpStatus.UNAUTHORIZED);
            }
          }
        } else {
          return new ResponseEntity("You can't make a result for this profile", HttpStatus.UNAUTHORIZED);
        }
      }
    }

    Optional<ActivityQualificationMetric> metricOptional = activityQualificationMetricRepository.findById(request.getMetricId());
    if (metricOptional.isEmpty()) {
      return new ResponseEntity("No such metric ID", HttpStatus.NOT_FOUND);
    }

    ActivityQualificationMetric metric = metricOptional.get();
    Unit metricUnit = metric.getUnit();

    if (metricUnit.equals(Unit.TimeStartFinish)) {
      if (request.getEnd() == null || request.getStart() == null) {
        return new ResponseEntity("Start/end datetime is empty", HttpStatus.BAD_REQUEST);
      }
    } else {
      if (request.getValue().isEmpty() || request.getValue() == null) {
        return new ResponseEntity("Request value is empty", HttpStatus.BAD_REQUEST);
      }
    }

    String message = profile.getFirstname() + " participated in " + metric.getTitle() + " for "
        + activity.getActivityName() + " and recorded ";

    if (metricUnit.equals(Unit.Count)) {
      ActivityResult result = new ActivityResultCount(metric, profile, Integer.parseInt(request.getValue()));
      activityResultRepository.save(result);
      message += request.getValue();
    } else if (metricUnit.equals(Unit.Distance)) {
      ActivityResult result = new ActivityResultDistance(metric, profile, Float.parseFloat(request.getValue()));
      activityResultRepository.save(result);
      message += "distance: " + request.getValue() + " km";
    } else if (metricUnit.equals(Unit.TimeDuration)) {
      // in the format H:I:S
      String durationString = Duration.between(LocalTime.MIN, LocalTime.parse(request.getValue())).toString();
      Duration duration = Duration.parse(durationString);
      ActivityResult result = new ActivityResultDuration(metric, profile, duration);
      activityResultRepository.save(result);
      message += "duration: " + request.getValue();
    } else if (metricUnit.equals(Unit.TimeStartFinish)) {
      ActivityResult result = new ActivityResultStartFinish(metric, profile, request.getStart(), request.getEnd());
      activityResultRepository.save(result);
      message += "start date/time: " + request.getStart() + " and end date/time: " + request.getEnd();
    }

    ActivityHistory activityHistory = new ActivityHistory(activity, message);
    activityHistoryRepository.save(activityHistory);

    return new ResponseEntity("Activity result saved", HttpStatus.OK);
  }


  /**
   * Gets and returns all the results for all users for a specific activity
   *
   * @param activityId the id of the activity with the desired results
   * @param session current http session
   * @return response entity containing list of all results for activity
   */
  @GetMapping("/profiles/activities/{activityId}/result")
  public ResponseEntity getAllResultsForActivity(
          @PathVariable int activityId,
          HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Optional<Profile> optionalLoggedInProfile = profileRepository.findById((Integer) id);
    if (!optionalLoggedInProfile.isPresent()) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile loggedInProfile = optionalLoggedInProfile.get();
    Activity activity = optionalActivity.get();

    if (activity.getVisibilityType() != VisibilityType.Public) {
      List<ActivityRole> activityRoles =
              activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, loggedInProfile.getId());
      if (activityRoles.isEmpty()) {
        return new ResponseEntity("You don't have access", HttpStatus.FORBIDDEN);
      }
    }

    List<ActivityResult> results = activityResultRepository.findAllResultsForAnActivity(activityId);

    return new ResponseEntity(results, HttpStatus.OK);
  }
}
