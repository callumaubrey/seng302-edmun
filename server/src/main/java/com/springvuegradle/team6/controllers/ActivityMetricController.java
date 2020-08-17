package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityHistory;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityResult;
import com.springvuegradle.team6.models.entities.ActivityResultCount;
import com.springvuegradle.team6.models.entities.ActivityResultDistance;
import com.springvuegradle.team6.models.entities.ActivityResultDuration;
import com.springvuegradle.team6.models.entities.ActivityResultStartFinish;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Unit;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityHistoryRepository;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.CreateActivityResultRequest;
import com.springvuegradle.team6.requests.EditActivityResultRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  private final CustomizedActivityResultRepositoryImpl customizedActivityResultRepository;

  ActivityMetricController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityRoleRepository activityRoleRepository,
      ActivityQualificationMetricRepository activityQualificationMetricRepository,
      ActivityResultRepository activityResultRepository,
      ActivityHistoryRepository activityHistoryRepository,
      CustomizedActivityResultRepositoryImpl customizedActivityResultRepository) {
    this.profileRepository = profileRepository;
    this.activityRepository = activityRepository;
    this.activityRoleRepository = activityRoleRepository;
    this.activityQualificationMetricRepository = activityQualificationMetricRepository;
    this.activityResultRepository = activityResultRepository;
    this.activityHistoryRepository = activityHistoryRepository;
    this.customizedActivityResultRepository = customizedActivityResultRepository;
  }

  /**
   * This endpoint creates a new activity result for a Participant of an activity An admin can
   * create a new activity result for an owner and a participant An owner can create a new activity
   * result for a participant
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

    List<ActivityRole> activityRoles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profile.getId());
    if (activityRoles.isEmpty()) {
      return new ResponseEntity("You don't have access", HttpStatus.UNAUTHORIZED);
    } else {
      ActivityRole activityRole = activityRoles.get(0);
      Integer intId = (Integer) id;
      if (intId.equals(profile.getId())) {
        // Doing your own result
        if (!ownerProfile.getId().equals(profile.getId())) {
          // If we are not the owner then we check if participant
          if (!activityRole.getActivityRoleType().equals(ActivityRoleType.Participant) && !activityRole.getActivityRoleType().equals(ActivityRoleType.Organiser)) {
            return new ResponseEntity("You must be a participant", HttpStatus.UNAUTHORIZED);
          }
        }
      } else {
        // Doing it for someone else
        boolean isAdminOrCreator =
            UserSecurityService.checkIsAdminOrCreator((Integer) id, ownerProfile.getId());
        if (isAdminOrCreator) {
          if (!ownerProfile.getId().equals(profile.getId())) {
            // If we are making result for owner as admin
            if (!activityRole.getActivityRoleType().equals(ActivityRoleType.Participant)) {
              return new ResponseEntity("You must be a participant B", HttpStatus.UNAUTHORIZED);
            }
          }
        } else {
          return new ResponseEntity(
              "You can't make a result for this profile", HttpStatus.UNAUTHORIZED);
        }
      }
    }

    Optional<ActivityQualificationMetric> metricOptional =
        activityQualificationMetricRepository.findById(request.getMetricId());
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

    String message =
        profile.getFirstname()
            + " participated in "
            + metric.getTitle()
            + " for "
            + activity.getActivityName()
            + " and recorded ";

    if (metricUnit.equals(Unit.Count)) {
      ActivityResult result =
          new ActivityResultCount(metric, profile, Integer.parseInt(request.getValue()));
      result.overrideResult(request.getSpecialMetric());
      activityResultRepository.save(result);
      message += request.getValue();
    } else if (metricUnit.equals(Unit.Distance)) {
      ActivityResult result =
          new ActivityResultDistance(metric, profile, Float.parseFloat(request.getValue()));
      result.overrideResult(request.getSpecialMetric());
      activityResultRepository.save(result);
      message += "distance: " + request.getValue() + " km";
    } else if (metricUnit.equals(Unit.TimeDuration)) {
      // in the format H:I:S
      String durationString =
          Duration.between(LocalTime.MIN, LocalTime.parse(request.getValue())).toString();
      Duration duration = Duration.parse(durationString);
      ActivityResult result = new ActivityResultDuration(metric, profile, duration);
      result.overrideResult(request.getSpecialMetric());
      activityResultRepository.save(result);
      message += "duration: " + request.getValue();
    } else if (metricUnit.equals(Unit.TimeStartFinish)) {
      ActivityResult result =
          new ActivityResultStartFinish(metric, profile, request.getStart(), request.getEnd());
      result.overrideResult(request.getSpecialMetric());
      activityResultRepository.save(result);
      message +=
          "start date/time: " + request.getStart() + " and end date/time: " + request.getEnd();
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
      @PathVariable int activityId, HttpSession session) {
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

    boolean isAdminOrCreator =
        UserSecurityService.checkIsAdminOrCreator((Integer) id, activity.getProfile().getId());

    if (!isAdminOrCreator) {
      if (activity.getVisibilityType() != VisibilityType.Public) {
        List<ActivityRole> activityRoles =
            activityRoleRepository.findByActivity_IdAndProfile_Id(
                activityId, loggedInProfile.getId());
        if (activityRoles.isEmpty()) {
          return new ResponseEntity("You don't have access", HttpStatus.FORBIDDEN);
        }
      }
    }

    List<ActivityResult> results = activityResultRepository.findAllResultsForAnActivity(activityId);

    return new ResponseEntity(results, HttpStatus.OK);
  }

  /**
   * This endpoint edits an activity result for a Participant of an activity An admin can edit an
   * activity result for an owner and a participant An owner or organiser can edit an activity
   * result for a participant
   *
   * @param profileId the user that is the activity result is for
   * @param activityId activity ID
   * @param request the EditActivityResultRequest class
   * @param session the HttpSession
   * @return
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/result/{resultId}")
  public ResponseEntity editActivityResult(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @PathVariable int resultId,
      @RequestBody @Valid EditActivityResultRequest request,
      HttpSession session) {

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Object id = session.getAttribute("id");
    Profile profile = profileRepository.findById(profileId);
    if (profile == null) {
      return new ResponseEntity("User does not exist OWNER", HttpStatus.NOT_FOUND);
    }

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    int loggedInId = (Integer) id;
    Activity activity = optionalActivity.get();
    Profile ownerProfile = activity.getProfile();
    Profile loggedInProfile = profileRepository.findById(loggedInId);

    boolean isOwnerOrAdmin =
        UserSecurityService.checkIsAdminOrCreator(loggedInId, ownerProfile.getId());

    // Check if user is actually has a participating role
    List<ActivityRole> userRoles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profileId);
    boolean isParticipating = false;
    // User who is being edited or not is organiser or not
    boolean organiser = false;
    for (ActivityRole role : userRoles) {
      if (!role.getRole().equals("follower") && !role.getRole().equals("access")) {
        isParticipating = true;
        if (role.getRole().equals("organiser")) {
          organiser = true;
        }
      }
    }
    if (!isParticipating) {
      return new ResponseEntity("Specified user is not a participant", HttpStatus.BAD_REQUEST);
    }

    // Check right to edit result
    // Check if not admin or owner
    if (!isOwnerOrAdmin) {
      // Check if not organiser
      List<ActivityRole> roles =
          activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, loggedInId);
      boolean isOrganiser = false;
      for (ActivityRole role : roles) {
        if (role.getRole().equals("organiser")) {
          isOrganiser = true;
        }
      }
      if (!isOrganiser) {
        // Check if not logged in user
        if (profile != loggedInProfile) {
          return new ResponseEntity("You can not edit this users results", HttpStatus.FORBIDDEN);
        }
      } else {
        // An organiser should not be able to edit another organisers results
        if (organiser) {
          return new ResponseEntity(
              "You can not edit another organisers results", HttpStatus.FORBIDDEN);
        }
      }
    }

    // Test Request is legit
    Optional<ActivityQualificationMetric> metricOptional =
        activityQualificationMetricRepository.findById(request.getMetricId());
    if (metricOptional.isEmpty()) {
      return new ResponseEntity("No such metric ID", HttpStatus.NOT_FOUND);
    }

    ActivityQualificationMetric metric = metricOptional.get();
    Unit metricUnit = metric.getUnit();

    if (metricUnit.equals(Unit.TimeStartFinish)) {
      if (request.getEnd() == null || request.getStart() == null) {
        return new ResponseEntity("Must provide start AND end times", HttpStatus.BAD_REQUEST);
      }
    } else {
      if (request.getValue().isEmpty() || request.getValue() == null) {
        return new ResponseEntity("Request value is empty", HttpStatus.BAD_REQUEST);
      }
    }

    String message = "";
    if (loggedInProfile == profile) {
      message +=
          profile.getFirstname()
              + " edited their results for "
              + metric.getTitle()
              + " in "
              + activity.getActivityName()
              + " their new results are: ";
    } else {
      message +=
          "An event organiser edited "
              + profile.getFirstname()
              + "'s results for "
              + metric.getTitle()
              + " in "
              + activity.getActivityName()
              + " their new results are: ";
    }
    Integer activityResultId;

    if (metricUnit.equals(Unit.Count)) {
      Optional<ActivityResultCount> optionalResult =
          activityResultRepository.findSpecificCountResult(resultId);
      if (!optionalResult.isPresent()) {
        return new ResponseEntity(
            "No result found for this user, activity and metric", HttpStatus.NOT_FOUND);
      }
      ActivityResultCount oldResult = optionalResult.get();
      Integer oldResultId = oldResult.getId();
      ActivityQualificationMetric activityQualificationMetric =
          activityQualificationMetricRepository.getOne(request.getMetricId());
      ActivityResultCount result =
          new ActivityResultCount(
              activityQualificationMetric, profile, Integer.parseInt(request.getValue()));
      result.overrideResult(request.getSpecialMetric());
      result.setId(oldResultId);
      activityResultRepository.delete(oldResult);
      activityResultId = activityResultRepository.save(result).getId();
      message += "count: " + request.getValue();
    } else if (metricUnit.equals(Unit.Distance)) {
      Optional<ActivityResultDistance> optionalResult;
      optionalResult = activityResultRepository.findSpecificDistanceResult(resultId);
      if (!optionalResult.isPresent()) {
        return new ResponseEntity(
            "No result found for this user, activity and metric", HttpStatus.NOT_FOUND);
      }
      ActivityResultDistance oldResult = optionalResult.get();
      Integer oldResultId = oldResult.getId();
      ActivityQualificationMetric activityQualificationMetric =
          activityQualificationMetricRepository.getOne(request.getMetricId());
      ActivityResultDistance result =
          new ActivityResultDistance(
              activityQualificationMetric, profile, Float.parseFloat(request.getValue()));
      result.overrideResult(request.getSpecialMetric());
      result.setId(oldResultId);
      activityResultRepository.delete(oldResult);
      activityResultId = activityResultRepository.save(result).getId();
      message += "distance: " + request.getValue();
    } else if (metricUnit.equals(Unit.TimeDuration)) {
      // in the format H:I:S
      Optional<ActivityResultDuration> optionalResult =
          activityResultRepository.findSpecificDurationResult(resultId);
      if (!optionalResult.isPresent()) {
        return new ResponseEntity(
            "No result found for this user, activity and metric", HttpStatus.NOT_FOUND);
      }
      ActivityResultDuration oldResult = optionalResult.get();
      Integer oldResultId = oldResult.getId();
      ActivityQualificationMetric activityQualificationMetric =
          activityQualificationMetricRepository.getOne(request.getMetricId());
      String durationString =
          Duration.between(LocalTime.MIN, LocalTime.parse(request.getValue())).toString();
      Duration duration = Duration.parse(durationString);
      ActivityResultDuration result =
          new ActivityResultDuration(activityQualificationMetric, profile, duration);
      result.overrideResult(request.getSpecialMetric());
      result.setId(oldResultId);
      activityResultRepository.delete(oldResult);
      activityResultId = activityResultRepository.save(result).getId();
      message += "duration: " + durationString;
    } else if (metricUnit.equals(Unit.TimeStartFinish)) {
      Optional<ActivityResultStartFinish> optionalResult =
          activityResultRepository.findSpecificStartFinishResult(resultId);
      if (!optionalResult.isPresent()) {
        return new ResponseEntity(
            "No result found for this user, activity and metric", HttpStatus.NOT_FOUND);
      }
      ActivityResultStartFinish oldResult = optionalResult.get();
      Integer oldResultId = oldResult.getId();
      ActivityQualificationMetric activityQualificationMetric =
          activityQualificationMetricRepository.getOne(request.getMetricId());
      ActivityResultStartFinish result =
          new ActivityResultStartFinish(
              activityQualificationMetric, profile, request.getStart(), request.getEnd());
      result.overrideResult(request.getSpecialMetric());
      result.setId(oldResultId);
      activityResultRepository.delete(oldResult);
      activityResultId = activityResultRepository.save(result).getId();
      message +=
          "start date/time: " + request.getStart() + " and end date/time: " + request.getEnd();
    } else {
      activityResultId = null;
    }

    ActivityHistory activityHistory = new ActivityHistory(activity, message);
    activityHistoryRepository.save(activityHistory);

    return new ResponseEntity(activityResultId, HttpStatus.OK);
  }

  /**
   * Gets all the different metrics of an activity only if session has permission
   *
   * @param profileId the owner of the activity
   * @param activityId the activity id
   * @param session the session of the user who has called the endpoint
   * @return 200 Response with metrics in body if successful
   */
  @GetMapping("/profiles/{profileId}/activities/{activityId}/metrics")
  public ResponseEntity getActivityMetrics(
      @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();
    if (activity.isArchived()) {
      return new ResponseEntity<>("Activity is archived", HttpStatus.OK);
    }

    Object sessionId = session.getAttribute("id");

    if (!(activity.getVisibilityType() == VisibilityType.Public)) {
      ActivityRole activityRole =
          activityRoleRepository.findByProfile_IdAndActivity_Id(
              Integer.parseInt(sessionId.toString()), activityId);
      if (activityRole == null) {
        if (!UserSecurityService.checkIsAdminOrCreator(
            Integer.parseInt(sessionId.toString()), profileId)) {
          return new ResponseEntity<>("Activity is restricted", HttpStatus.UNAUTHORIZED);
        }
      }
    }

    List<ActivityQualificationMetric> activityMetrics =
        activityQualificationMetricRepository.findByActivity_Id(activityId);
    try {
      ObjectMapper mapper = new ObjectMapper();
      String postJson = mapper.writeValueAsString(activityMetrics);
      return ResponseEntity.ok(postJson);
    } catch (Exception e) {
      return new ResponseEntity<>("Activity Metrics do not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Gets all an activities results for a particular user
   *
   * @param profileId the id of the user who has the results
   * @param activityId the id of the activity
   * @param session
   * @return activity results, if user has no results return 404
   */
  @GetMapping("/activities/{activityId}/result/{metricId}/{profileId}")
  public ResponseEntity getActivityResultsByMetricAndProfileId(
      @PathVariable int profileId, @PathVariable int activityId, @PathVariable int metricId, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile profile = profileRepository.findById(profileId);
    if (profile == null) {
      return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();

    boolean authorised =
        UserSecurityService.checkIsAdminOrCreator((int) id, activity.getProfile().getId());

    if (activity.getVisibilityType() != VisibilityType.Public && !authorised) {
      List<ActivityRole> activityRoles =
          activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, (int) id);
      if (activityRoles.isEmpty()) {
        return new ResponseEntity("You don't have access", HttpStatus.UNAUTHORIZED);
      }
    }

    List<ActivityResult> results = customizedActivityResultRepository.getSortedResultsByMetricIdAndProfile(metricId, profileId);
    if (results.isEmpty()) {
      return new ResponseEntity("No results for this activity", HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity(results, HttpStatus.OK);
  }

  /**
   * Gets all a single metrics results for a particular activity
   *
   * @param activityId the id of the activity
   * @param session
   * @return activity results, if user has no results return 404
   */
  @GetMapping("/activities/{activityId}/result/{metricId}")
  public ResponseEntity getAllActivityResultsForSingleMetric(
      @PathVariable int activityId, @PathVariable int metricId, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Optional<ActivityQualificationMetric> metric =
        activityQualificationMetricRepository.findById(metricId);
    if (metric.isEmpty()) {
      return new ResponseEntity("Activity metric does not exist", HttpStatus.NOT_FOUND);
    }

    List<ActivityResult> results = customizedActivityResultRepository.getSortedResultsByMetricId(metricId);
    if (results.isEmpty()) {
      return new ResponseEntity("No results for this activity", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity(results, HttpStatus.OK);
  }

  /**
   * Deletes an activity result based on the resultId A owner, participant and admin can delete
   * result
   *
   * @param profileId owner of activity
   * @param activityId activityId
   * @param resultId activity result ID
   * @param session the HttpSession
   * @return
   */
  @DeleteMapping("/profiles/{profileId}/activities/{activityId}/result/{resultId}")
  public ResponseEntity deleteActivityResult(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @PathVariable int resultId,
      HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile loggedInProfile = profileRepository.findById((int) id);

    Profile ownerProfile = profileRepository.findById(profileId);
    if (ownerProfile == null) {
      return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Activity activity = optionalActivity.get();
    if (!activity.getProfile().getId().equals(ownerProfile.getId())) {
      return new ResponseEntity(
          "ProfileID and activity owner ID don't match", HttpStatus.BAD_REQUEST);
    }

    Optional<ActivityResult> activityResultOptional = activityResultRepository.findById(resultId);
    if (activityResultOptional.isEmpty()) {
      return new ResponseEntity("Activity result does not exist", HttpStatus.NOT_FOUND);
    }

    ActivityResult activityResult = activityResultOptional.get();

    boolean isAdminOrCreator =
        UserSecurityService.checkIsAdminOrCreator((Integer) id, ownerProfile.getId());
    if (!isAdminOrCreator) {
      if (!loggedInProfile.getId().equals(activityResult.getUserId())) {
        return new ResponseEntity(
            "You cannot delete another users result", HttpStatus.UNAUTHORIZED);
      }

      // Then participant is removing result
      List<ActivityRole> activityRoles =
          activityRoleRepository.findByActivity_IdAndProfile_Id(
              activityId, loggedInProfile.getId());
      if (activityRoles.isEmpty()) {
        return new ResponseEntity("You don't have access", HttpStatus.UNAUTHORIZED);
      } else {
        if (!activityRoles.get(0).getActivityRoleType().equals(ActivityRoleType.Participant)) {
          return new ResponseEntity(
              "You are not a participant of this activity", HttpStatus.UNAUTHORIZED);
        }
      }
    }

    activityResultRepository.delete(activityResult);

    return new ResponseEntity("Activity result deleted", HttpStatus.OK);
  }
}
