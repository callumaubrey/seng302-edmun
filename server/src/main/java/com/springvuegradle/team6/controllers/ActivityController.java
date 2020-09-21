package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;
import com.springvuegradle.team6.requests.CreateActivityRequest;
import com.springvuegradle.team6.requests.EditActivityHashtagRequest;
import com.springvuegradle.team6.requests.EditActivityRequest;
import com.springvuegradle.team6.requests.EditActivityTypeRequest;
import com.springvuegradle.team6.requests.EditActivityVisibilityRequest;
import com.springvuegradle.team6.requests.objects.EmailRolePair;
import com.springvuegradle.team6.security.UserSecurityService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
public class ActivityController {
  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;
  private final ActivityRoleRepository activityRoleRepository;
  private final LocationRepository locationRepository;
  private final TagRepository tagRepository;
  private final SubscriptionHistoryRepository subscriptionHistoryRepository;
  private final ActivityHistoryRepository activityHistoryRepository;
  private final ActivityQualificationMetricRepository activityQualificationMetricRepository;
  private final ActivityResultRepository activityResultRepository;

  ActivityController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityRoleRepository activityRoleRepository,
      LocationRepository locationRepository,
      TagRepository tagRepository,
      SubscriptionHistoryRepository subscriptionHistoryRepository,
      ActivityHistoryRepository activityHistoryRepository,
      ActivityQualificationMetricRepository activityQualificationMetricRepository,
      ActivityResultRepository activityResultRepository) {
    this.profileRepository = profileRepository;
    this.activityRepository = activityRepository;
    this.activityRoleRepository = activityRoleRepository;
    this.locationRepository = locationRepository;
    this.tagRepository = tagRepository;
    this.subscriptionHistoryRepository = subscriptionHistoryRepository;
    this.activityHistoryRepository = activityHistoryRepository;
    this.activityQualificationMetricRepository = activityQualificationMetricRepository;
    this.activityResultRepository = activityResultRepository;
  }

  /**
   * This method is required to enable direct field access for activity controller, due to a weird
   * bug where the fields of LocationUpdateRequest not being able to be accessed even with getters.
   *
   * @param dataBinder
   */
  @InitBinder
  private void activateDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /**
   * Check if user is authorised to edit activity.
   *
   * @param activity activity to be edited
   * @param session http session
   * @return ResponseEntity or null
   */
  private ResponseEntity<String> checkAuthorisedToEditActivity(
      Activity activity, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    ActivityRole role =
        activityRoleRepository.findByProfile_IdAndActivity_Id((Integer) id, activity.getId());

    //Check if user is either admin/creator/organiser
    if (!UserSecurityService.checkIsAdminOrCreator((Integer) id, activity.getProfile().getId())) {
      if (role == null || (role != null
          && role.getActivityRoleType() != ActivityRoleType.Organiser)) {
        return new ResponseEntity<>("You can only edit your own activity", HttpStatus.UNAUTHORIZED);
      }
    }

    if (!activityRepository.existsById(activity.getId())) {
      return new ResponseEntity<>("No such activity", HttpStatus.NOT_FOUND);
    }
    return null;
  }

  /**
   * Update/replace list of user activity types
   *
   * @param profileId user id to be updated
   * @param request EditActivityTypeRequest
   * @param session HTTPSession
   * @return unauthorised response or 201/400 HttpStatus Response
   */
  @PutMapping("/profiles/{profileId}/activity-types")
  public ResponseEntity<String> updateActivityTypes(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditActivityTypeRequest request,
      HttpSession session) {
    Optional<Profile> profile = profileRepository.findById(profileId);
    if (profile.isPresent()) {
      Profile user = profile.get();

      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      user.setActivityTypes(request.getActivities());
      profileRepository.save(user);
    }
    return ResponseEntity.ok("User activity types updated");
  }
  /**
   * Get all activity types
   *
   * @return 200 response with headers
   */
  @GetMapping("/profiles/activity-types")
  public ResponseEntity getActivityTypes() {
    List<String> activityList = new ArrayList();
    for (ActivityType myVar : ActivityType.values()) {
      activityList.add(myVar.toString());
    }
    return ResponseEntity.ok(activityList);
  }

  /**
   * Helper function used by createActivity to check if the date/time provided for start and end
   * date are valid
   *
   * @param activity The activity that the user wants to create or update
   * @return null if there are no errors, otherwise return ResponseEntity with error message and
   *     status code
   */
  public ResponseEntity<String> checkCreateActivityDateTime(Activity activity) {
    if (!activity.isContinuous()) {
      if (activity.getStartTime() == null) {
        return new ResponseEntity<>(
            "Start date/time cannot be empty for a non continuous activity",
            HttpStatus.BAD_REQUEST);
      }
      if (activity.getEndTime() == null) {
        return new ResponseEntity<>(
            "End date/time cannot be empty for a non continuous activity", HttpStatus.BAD_REQUEST);
      }

      // Stand and End date/time validations
      LocalDateTime startDateTime;
      LocalDateTime endDateTime;
      try {
        startDateTime = activity.getStartTime();
        if (startDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        System.out.println(e);
        return new ResponseEntity(
            "Start date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      try {
        endDateTime = activity.getEndTime();
        if (endDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "End date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "End date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      if (startDateTime.isAfter(endDateTime)) {
        return new ResponseEntity(
            "Start date/time cannot be after End date/time", HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }

  /**
   * Helper function used by editActivity to check if the date/time provided for start and end date
   * are valid
   *
   * @param activity The activity that the user wants to create or update
   * @return null if there are no errors, otherwise return ResponseEntity with error message and
   *     status code
   */
  public ResponseEntity<String> checkEditActivityDateTime(
      EditActivityRequest request, Activity activity) {
    if (!request.continuous) {
      if (request.startTime == null) {
        return new ResponseEntity<>(
            "Start date/time cannot be empty for a non continuous activity",
            HttpStatus.BAD_REQUEST);
      }
      if (request.endTime == null) {
        return new ResponseEntity<>(
            "End date/time cannot be empty for a non continuous activity", HttpStatus.BAD_REQUEST);
      }

      // Start and End date/time validations
      LocalDateTime newStartDateTime;
      LocalDateTime oldStartDateTime;
      LocalDateTime endDateTime;

      // Checks if new start time is before now.
      try {
        newStartDateTime =
            LocalDateTime.parse(
                request.startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (activity.getStartTime() != null) {
          oldStartDateTime = activity.getStartTime();
          // If the activity has already begun but the author changes the activity name, then it
          // needs to make sure the to accept the old start time which is before now
          if (newStartDateTime.isBefore(LocalDateTime.now())
              && !newStartDateTime.isEqual(oldStartDateTime)) {
            return new ResponseEntity(
                "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
            // if activity is previously a continuous activity, will not need checks for old start time
          } else {
            if (newStartDateTime.isBefore(LocalDateTime.now())) {
              return new ResponseEntity(
                  "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
            }
          }
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "Start date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      // Checks if end time is before now.
      try {
        endDateTime =
            LocalDateTime.parse(
                request.endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (endDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "End date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "End date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      if (!(newStartDateTime.isEqual(endDateTime)) && newStartDateTime.isAfter(endDateTime)) {
        // Checks if start time is after end time.
        return new ResponseEntity(
            "Start date/time cannot be after End date/time", HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }

  /**
   * Helper function to check if tags associated to the activity is not more that 30 and check if
   * each tag only contains alphanumeric characters
   *
   * @param hashtags set of hashtags associated to the activity
   * @return return ResponseEntity with personalised error message if error is found, otherwise null
   */
  private ResponseEntity<String> checkAllTagsValidity(Set<Tag> hashtags) {
    if (hashtags != null) {
      if (hashtags.size() > 30) {
        return new ResponseEntity<>(
            "More than 30 hashtags per activity is not allowed", HttpStatus.BAD_REQUEST);
      }
      for (Tag tag : hashtags) {
        formatHashtagToStoreInDb(tag);

        // check if tag name only contains alphanumeric characters and underscores
        if (!tag.getName().matches("^[a-zA-Z0-9_]*$")) {
          return new ResponseEntity<>(
              "Tag name "
                  + tag.getName()
                  + " contains characters other than alphanumeric characters and underscores",
              HttpStatus.BAD_REQUEST);
        }
      }
    }

    return null;
  }

  /**
   * Helper function to convert hashtag to lower case and remove '#'
   *
   * @param hashtag
   */
  private void formatHashtagToStoreInDb(Tag hashtag) {
    hashtag.setName(hashtag.getName().toLowerCase().substring(1));
  }

  /**
   * Put request to edit the hashtags associated to the activity specified in the path
   *
   * @param profileId author of the activity
   * @param activityId activity id
   * @param request EditActivityHashtagRequest
   * @param session HttpSession
   * @return The response code and message
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/hashtags")
  public ResponseEntity<String> editActivityHashTag(
      @PathVariable Integer profileId,
      @PathVariable Integer activityId,
      @Valid @RequestBody EditActivityHashtagRequest request,
      HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      ResponseEntity<String> activityAuthorizedResponse =
          this.checkAuthorisedToEditActivity(activity, session);
      if (activityAuthorizedResponse != null) {
        return activityAuthorizedResponse;
      }

      // Check hashtags
      ResponseEntity<String> checkHashtagsValidityResponse =
          checkAllTagsValidity(request.getHashtags());
      if (checkHashtagsValidityResponse != null) {
        return checkHashtagsValidityResponse;
      }

      Set<Tag> hashtags = new HashSet<>();
      if (request.getHashtags() != null) {
        for (Tag tag : request.getHashtags()) {
          Tag dbTag = tagRepository.findByName(tag.getName());
          if (dbTag == null) {
            tag = tagRepository.save(tag);
            hashtags.add(tag);
          } else {
            hashtags.add(dbTag);
          }
        }
        activity.setTags(hashtags);
      }
      activityRepository.save(activity);

      return ResponseEntity.ok(
          "Hashtags of Activity '" + activity.getActivityName() + "' were updated.");
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Post Request to create an activity for the given profile based on the request
   *
   * @param profileId The id of the profile where the activity is created for
   * @param request The request with values to create the activity
   * @param session The session of the currently logged in user
   * @return The response code and message
   */
  @PostMapping("/profiles/{profileId}/activities")
  public ResponseEntity<String> createActivity(
      @PathVariable Integer profileId,
      @Valid @RequestBody CreateActivityRequest request,
      HttpSession session) {
    ResponseEntity<String> checkAuthorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (checkAuthorisedResponse != null) {
      return checkAuthorisedResponse;
    }
    Optional<Profile> optionalProfile = profileRepository.findById(profileId);
    if (optionalProfile.isEmpty()) {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.BAD_REQUEST);
    }

    Profile profile = optionalProfile.get();

    ResponseEntity<String> checkHashtagsValidityResponse = checkAllTagsValidity(request.hashTags);
    if (checkHashtagsValidityResponse != null) {
      return checkHashtagsValidityResponse;
    }

    Activity activity = new Activity(request, profile);
    if (activity.getLocation() != null) {
      Optional<Location> optionalLocation =
          locationRepository.findByLatitudeAndLongitude(
              activity.getLocation().getLatitude(),
              activity.getLocation().getLongitude());
      if (optionalLocation.isPresent()) {
        activity.setLocation(optionalLocation.get());
      } else {
        locationRepository.save(activity.getLocation());
      }
    }
    ResponseEntity<String> checkActivityDateTimeResponse = checkCreateActivityDateTime(activity);
    if (checkActivityDateTimeResponse != null) {
      return checkActivityDateTimeResponse;
    }

    Set<Tag> hashtags = new HashSet<>();
    if (activity.getTags() != null) {
      for (Tag tag : activity.getTags()) {
        Tag dbTag = tagRepository.findByName(tag.getName());
        if (dbTag == null) {
          tag = tagRepository.save(tag);
          hashtags.add(tag);
        } else {
          hashtags.add(dbTag);
        }
      }
    }
    activity.setTags(hashtags);

    // Set creation date to now
    activity.setCreationDate(LocalDateTime.now());
    activity.setFileName(request.photoFileName);

    activityRepository.save(activity);

    addMetricsToActivity(activity, request.metrics);

    SubscriptionHistory subscriptionHistory =
        new SubscriptionHistory(profile, activity, SubscribeMethod.SELF);

    subscriptionHistoryRepository.save(subscriptionHistory);

    // Set creator of activity in ActivityRoleRepository
    ActivityRole creator = new ActivityRole();
    creator.setActivity(activity);
    creator.setProfile(profile);
    creator.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(creator);

    return new ResponseEntity(activity.getId(), HttpStatus.CREATED);
  }

  /**
   * Given a HTTP request, change the values of activity to those that are in request
   *
   * @param request The request containing changes to activity
   * @param activity The activity that is currently present in the database
   */
  public void editActivityFromRequest(EditActivityRequest request, Activity activity) {
    activity.setActivityName(request.activityName);
    activity.setDescription(request.description);
    activity.setActivityTypes(request.activityTypes);
    activity.setContinuous(request.continuous);
    if (request.visibility != null) {
      activity.setVisibilityTypeByString(request.visibility);
    }
    if (activity.isContinuous()) {
      activity.setStartTime(null);
      activity.setEndTime(null);
    } else {
      activity.setStartTime(
          LocalDateTime.parse(
              request.startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
      activity.setEndTime(
          LocalDateTime.parse(
              request.endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    }
    if (request.visibility != null) {
      activity.setVisibilityTypeByString(request.visibility);
    }

    if (request.location != null) {
      Location location =
          new Location(
              request.location.latitude, request.location.longitude);
      activity.setLocation(location);

      if (activity.getLocation() != null) {
        Optional<Location> optionalLocation =
            locationRepository.findByLatitudeAndLongitude(
                activity.getLocation().getLatitude(),
                activity.getLocation().getLongitude());
        if (optionalLocation.isPresent()) {
          activity.setLocation(optionalLocation.get());
        } else {
          locationRepository.save(location);
          activity.setLocation(location);
        }
      }
    } else {
      activity.setLocation(null);
    }

    Set<Tag> hashtags = new HashSet<>();
    if (request.hashTags != null) {
      for (Tag tag : request.hashTags) {
        Tag dbTag = tagRepository.findByName(tag.getName());
        if (dbTag == null) {
          tag = tagRepository.save(tag);
          hashtags.add(tag);
        } else {
          hashtags.add(dbTag);
        }
      }
      activity.setTags(hashtags);
    }

    editMetricsFromRequest(activity, request.metrics);
  }

  /**
   * Method to add metrics to activity. Nothing will be done if no metric is passed into request
   *
   * @param activity activity in the request
   * @param requestMetrics metric list that was passed into request
   */
  private void addMetricsToActivity(
      Activity activity, List<ActivityQualificationMetric> requestMetrics) {
    List<ActivityQualificationMetric> metrics = new ArrayList<>();
    if (requestMetrics != null) {
      for (ActivityQualificationMetric metric : requestMetrics) {
        metric.setActivity(activity);
        activityQualificationMetricRepository.save(metric);
        metrics.add(metric);
      }
      activity.setMetrics(metrics);
    }
  }

  /**
   * Edits a metric if metric ID matches one already on that activity
   * Adds a metric if metric ID = 0 or if there are none in the DB
   * Set the activities metrics to the newly edited/added list of metrics
   *
   * @param activity Activity object
   * @param requestMetrics list of ActivityQualificationMetric objects
   */
  private void editMetricsFromRequest(
      Activity activity, List<ActivityQualificationMetric> requestMetrics) {
    List<ActivityQualificationMetric> newMetrics = new ArrayList<>();
    List<ActivityQualificationMetric> dbMetrics = activityQualificationMetricRepository.findByActivity_Id(activity.getId());
    if (requestMetrics != null) {
      if (dbMetrics.size() == 0) {
        for (ActivityQualificationMetric requestMetric : requestMetrics) {
          // Adding
          requestMetric.setActivity(activity);
          activityQualificationMetricRepository.save(requestMetric);
          newMetrics.add(requestMetric);
        }
      } else {
        for (ActivityQualificationMetric metric : dbMetrics) {
          for (ActivityQualificationMetric requestMetric : requestMetrics) {
            if (requestMetric.getId() == 0) {
              // Adding
              requestMetric.setActivity(activity);
              activityQualificationMetricRepository.save(requestMetric);
              newMetrics.add(requestMetric);
            } else {
              if (metric.getId() == requestMetric.getId() && metric.getEditable() == true) {
                // Editing
                requestMetric.setActivity(activity);
                activityQualificationMetricRepository.save(requestMetric);
                newMetrics.add(requestMetric);
              }
            }
          }
        }
      }
      activity.setMetrics(newMetrics);
    }
  }

  /**
   * This method will deal with the different cases of visibility and which roles to delete and
   * which users to unsubscribe
   *
   * @param request the request with the new visibility
   * @param activity the activity with the original visibility
   */
  void editActivityVisibilityHandling(EditActivityRequest request, Activity activity) {

    if (activity.getVisibilityType() == VisibilityType.Restricted
        && request.visibility.equals(
        "public")) { // access roles dont need to exist if going from restricted to public;
      activityRoleRepository.deleteAllAccessRolesOfActivity(activity.getId());
    } else if (!(activity.getVisibilityType() == VisibilityType.Public
        && request.visibility.equals("public"))
        && !(activity.getVisibilityType() == VisibilityType.Restricted
        && request.visibility.equals(
        "restricted"))) { // This checks if there was no change in visibility.
      activityRoleRepository.deleteAllActivityRolesExceptOwner(
          activity.getId(), activity.getProfile().getId());
      subscriptionHistoryRepository.unSubscribeAllButCreator(
          activity.getId(), activity.getProfile().getId(), LocalDateTime.now());
    }
  }

  /**
   * Deletes an activity metric if editable and if logged
   * in user is the activity owner
   *
   * @param profileId profileId of activity owner
   * @param activityId activity ID
   * @param metricId metricId
   * @param session HttpSession
   * @return ResponseEntity 200 if OK else 4xx client error
   */
  @DeleteMapping("/profiles/{profileId}/activities/{activityId}/{metricId}")
  public ResponseEntity deleteActivityMetric(
      @PathVariable Integer profileId,
      @PathVariable Integer activityId,
      @PathVariable Integer metricId,
      HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (!optionalActivity.isPresent()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }

    Activity activity = optionalActivity.get();

    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    ResponseEntity<String> activityAuthorizedResponse =
        this.checkAuthorisedToEditActivity(activity, session);
    if (activityAuthorizedResponse != null) {
      return activityAuthorizedResponse;
    }

    ActivityQualificationMetric metric = activityQualificationMetricRepository.getOne(metricId);
    if (metric == null) {
      return new ResponseEntity("No such metric", HttpStatus.BAD_REQUEST);
    }

    boolean matchesActivity = false;
    for (ActivityQualificationMetric metricActivity : activity.getMetrics()) {
      if (metricActivity.getId() == metric.getId()) {
        matchesActivity = true;
      }
    }

    if (!matchesActivity) {
      return new ResponseEntity("Metric does not belong to that activity", HttpStatus.BAD_REQUEST);
    }

    if (metric.getEditable() == false) {
      return new ResponseEntity("This metric cannot be deleted", HttpStatus.BAD_REQUEST);
    }

    activityQualificationMetricRepository.delete(metric);

    return new ResponseEntity("Metric deleted", HttpStatus.OK);
  }

  /**
   * Put Request to update an activity for the given profile based on the request
   *
   * @param profileId The id of the author of the activity
   * @param request The request with values to update the activity
   * @param session The session of the currently logged in user
   * @return The response code and message
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}")
  public ResponseEntity<String> editActivity(
      @PathVariable Integer profileId,
      @PathVariable Integer activityId,
      @Valid @RequestBody EditActivityRequest request,
      HttpSession session)
      throws JsonProcessingException {

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      ResponseEntity<String> activityAuthorizedResponse =
          this.checkAuthorisedToEditActivity(activity, session);
      if (activityAuthorizedResponse != null) {
        return activityAuthorizedResponse;
      }

      // Check hashtags
      ResponseEntity<String> checkHashtagsValidityResponse = checkAllTagsValidity(request.hashTags);
      if (checkHashtagsValidityResponse != null) {
        return checkHashtagsValidityResponse;
      }

      ResponseEntity<String> checkActivityDateTimeResponse =
          checkEditActivityDateTime(request, activity);
      if (checkActivityDateTimeResponse != null) {
        return checkActivityDateTimeResponse;
      }

      ObjectMapper mapper = new ObjectMapper();
      String preJson = mapper.writeValueAsString(activity);

      if (request.visibility != null) {
        editActivityVisibilityHandling(request, activity); // this handles the visibility logic
      }

      if (request.metrics != null) {
        for (ActivityQualificationMetric metric : request.metrics) {
          if (metric.getId() != 0) {
            if (metric.getId() != 0 && activityQualificationMetricRepository.findById(metric.getId()).isEmpty()) {
              return new ResponseEntity<>("Metric does not exist to be edited", HttpStatus.BAD_REQUEST);
            }
            ActivityQualificationMetric savedMetric =
                activityQualificationMetricRepository.findById(metric.getId()).get();
            if (!checkTwoMetricsAreTheSame(savedMetric, metric) && !savedMetric.getEditable()) {
              return new ResponseEntity<>("Can not edit this activity", HttpStatus.BAD_REQUEST);
            }
            }
        }
        activity.setMetrics(request.metrics);
      }
      editActivityFromRequest(request, activity);

      String postJson = mapper.writeValueAsString(activity);
      if (!preJson.equals(postJson)) {
        String editorName =
            profileRepository
                .findById(Integer.parseInt(session.getAttribute("id").toString()))
                .getFullname();
        ActivityHistory activityHistory =
            new ActivityHistory(
                activity,
                "Activity '" + activity.getActivityName() + "' was updated by " + editorName);
        activityHistoryRepository.save(activityHistory);
      }

      return ResponseEntity.ok("Activity: " + activity.getActivityName() + " was updated.");
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }
  public boolean checkTwoMetricsAreTheSame(ActivityQualificationMetric metric1, ActivityQualificationMetric metric2){
    return metric1.getRankByAsc() == metric2
        .getRankByAsc() && metric1.getTitle().equals(metric2.getTitle()) && metric1.getDescription()
        .equals(metric2.getDescription()) && metric1.getUnit().name()
        .equals(metric2.getUnit().name());
  }

  /**
   * Delete Request to delete an activity from a given profile id and activity id
   *
   * @param profileId The id of the profile where the activity is deleted from
   * @param activityId The id of the activity
   * @param session The session of the currently logged in user
   * @return The response code and message
   */
  @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
  public ResponseEntity<String> deleteActivity(
      @PathVariable Integer profileId, @PathVariable Integer activityId, HttpSession session) {
    ResponseEntity<String> checkAuthorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (checkAuthorisedResponse != null) {
      return checkAuthorisedResponse;
    }
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();
      if (!activity.getProfile().getId().equals(profileId)) {
        return new ResponseEntity<>(
            "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
      }
      activity.setArchived(true);
      activityRepository.save(activity);

      String editorName =
          profileRepository
              .findById(Integer.parseInt(session.getAttribute("id").toString()))
              .getFullname();
      String activityArchivedMessage =
          "Activity '" + activity.getActivityName() + "' was archived by " + editorName;
      ActivityHistory activityHistory = new ActivityHistory(activity, activityArchivedMessage);
      activityHistoryRepository.save(activityHistory);

      return new ResponseEntity<>("Activity is now archived", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get activity data by ID
   *
   * @param activityId The id of the requested activity
   * @return 200 response with headers
   */
  @GetMapping("/activities/{activityId}")
  public ResponseEntity<String> getActivity(@PathVariable int activityId, HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();
    if (activity.isArchived()) {
      return new ResponseEntity<>("Activity is archived", HttpStatus.OK);
    }
    // This activityAuthorizedResponse checks if user is owner or admin. It is null if it is either
    // of these two, is a response if unauthorized.
    ResponseEntity<String> activityAuthorizedResponse =
        this.checkAuthorisedToEditActivity(activity, session);

    Object profileId = session.getAttribute("id");
    if (!profileId.toString().equals(activity.getProfile().getId().toString())) {
      if (activity.getVisibilityType() == VisibilityType.Private) {
        if (activityAuthorizedResponse != null) {
          return new ResponseEntity<>("Activity is private", HttpStatus.UNAUTHORIZED);
        }
      }

      if (activity.getVisibilityType() == VisibilityType.Restricted) {
        ActivityRole activityRoles =
            activityRoleRepository.findByProfile_IdAndActivity_Id(
                Integer.parseInt(profileId.toString()), activityId);
        if (activityRoles == null) {
          if (activityAuthorizedResponse != null) {
            return new ResponseEntity<>("Activity is restricted", HttpStatus.UNAUTHORIZED);
          }
        }
      }
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      String postJson = mapper.writeValueAsString(activity);
      return ResponseEntity.ok(postJson);
    } catch (Exception e) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Given the activity id, find the creator of the activity and return the id of the creator
   *
   * @param activityId id of the activity
   * @return the id of the creator of the activity
   */
  @GetMapping("/activities/{activityId}/creatorId")
  public ResponseEntity<String> getActivityCreator(@PathVariable int activityId) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();
    if (activity.isArchived()) {
      return new ResponseEntity<>("Activity is archived", HttpStatus.OK);
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      String postJson = mapper.writeValueAsString(activity.getProfile().getId());
      return ResponseEntity.ok(postJson);
    } catch (Exception e) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get all activity that a user has created by their userID that is not archived. No activities
   * that are private should be returned unless the user is either an admin or the creator of the
   * activity. It should also return all restricted activities that the requesting user has access
   * to.
   *
   * @param profileId The id of the user profile
   * @param session The session of the current user logged in
   * @return 200 response with headers
   */
  @GetMapping("/profiles/{profileId}/activities")
  public ResponseEntity getUserActivities(@PathVariable int profileId, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile profile = profileRepository.findById(profileId);
    if (profile == null) {
      return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);
    }

    List<Activity> response;
    if (UserSecurityService.checkIsAdminOrCreator((int) id, profileId)) {
      response = activityRepository.findByProfile_IdAndArchivedFalse(profileId);
    } else {
      response = activityRepository.getPublicAndRestrictedActivities(profileId, (int) id);
    }

    return ResponseEntity.ok(response);
  }

  /**
   * This takes care of all subscription adn activity role logic when changing visibility of an
   * activity using visibility endpoint.
   *
   * @param request the request with the new visibility and accessors
   * @param activity the activity to be edited
   * @param activityId the activity id to be edited
   * @return a NOT FOUND response entity if an accessor is not found, otherwise return null
   */
  private ResponseEntity<String> editActivityRoles(
      EditActivityVisibilityRequest request, Activity activity, int activityId) {
    if (request.getAccessors() == null) {
      return null;
    }

    if (request.getVisibility().equals("public") || request.getVisibility().equals("private")) {
      if (request.getAccessors().size() > 0) {
        return new ResponseEntity(
            "Can't have accessors when changing to private or public.", HttpStatus.BAD_REQUEST);
      }
    }

    for (EmailRolePair emailRolePair : request.getAccessors()) {
      Profile profile = profileRepository.findByEmails_address(emailRolePair.getEmail());
      if (profile == null) {
        return new ResponseEntity(
            "User with email " + emailRolePair.getEmail() + " does not exist",
            HttpStatus.NOT_FOUND);
      }
    }

    if (request.getVisibility().equals("public")) {
      List<ActivityRole> activityRoles =
          activityRoleRepository.findByActivityRoleTypeAndActivity_Id(
              ActivityRoleType.Access, activityId);
      activityRoleRepository.deleteInBatch(activityRoles);
    }

    // Changing activity to private removes all users apart from the creator
    if (request.getVisibility().equals("private")) {
      List<ActivityRole> activityRoles = activityRoleRepository.findByActivity_Id(activityId);
      if (activityRoles.size() > 0) {
        for (ActivityRole activityRole : activityRoles) {
          if (!activityRole.getActivityRoleType().equals(ActivityRoleType.Creator)) {
            activityRoleRepository.delete(activityRole);
            List<SubscriptionHistory> subHistory =
                subscriptionHistoryRepository.findActive(
                    activityId, activityRole.getProfile().getId());
            if (subHistory.size() > 0) {
              subHistory.get(0).setEndDateTime(LocalDateTime.now());
              subHistory.get(0).setUnsubscribeMethod(UnsubscribeMethod.REMOVED);
              subscriptionHistoryRepository.save(subHistory.get(0));
            }
          }
        }
      }
    }

    if (request.getVisibility().equals("restricted")) {
      List<ActivityRole> activityRoles = activityRoleRepository.findByActivity_Id(activityId);
      List<Integer> currentProfileIds = new ArrayList<>();
      for (ActivityRole activityRole : activityRoles) {
        currentProfileIds.add(activityRole.getProfile().getId());
      }
      List<Integer> requestProfileIds = new ArrayList<>();

      for (EmailRolePair emailRolePair : request.getAccessors()) {
        String email = emailRolePair.getEmail();
        Profile profile = profileRepository.findByEmails_address(email);
        requestProfileIds.add(profile.getId());
        ActivityRole role =
            activityRoleRepository.findByProfile_IdAndActivity_Id(profile.getId(), activityId);
        ActivityRoleType requestActivityRoleType =
            ActivityRoleType.valueOf(
                emailRolePair.getRole().substring(0, 1).toUpperCase()
                    + emailRolePair.getRole().substring(1));

        if (role == null) {
          ActivityRole activityRole = new ActivityRole();
          activityRole.setActivity(activity);
          activityRole.setProfile(profile);

          activityRole.setActivityRoleType(requestActivityRoleType);
          activityRoleRepository.save(activityRole);
          switch (requestActivityRoleType) {
            case Access:
              break;
            case Follower:
            case Participant:
            case Organiser:
              SubscriptionHistory subscriptionHistory =
                  new SubscriptionHistory(profile, activity, SubscribeMethod.ADDED);
              subscriptionHistoryRepository.save(subscriptionHistory);
          }
        } else {
          ActivityRoleType currentActivityRoleType = role.getActivityRoleType();
          if (requestActivityRoleType != currentActivityRoleType) {
            role.setActivityRoleType(requestActivityRoleType);
            switch (currentActivityRoleType) {
              case Access:
                SubscriptionHistory subscriptionHistory =
                    new SubscriptionHistory(profile, activity, SubscribeMethod.ADDED);
                subscriptionHistoryRepository.save(subscriptionHistory);
                break;
              case Follower:
              case Participant:
              case Organiser:
                if (requestActivityRoleType == ActivityRoleType.Access) {
                  List<SubscriptionHistory> subHistory =
                      subscriptionHistoryRepository.findActive(activityId, profile.getId());
                  if (subHistory.size() > 0) {
                    subHistory.get(0).setEndDateTime(LocalDateTime.now());
                    subHistory.get(0).setUnsubscribeMethod(UnsubscribeMethod.REMOVED);
                    subscriptionHistoryRepository.save(subHistory.get(0));
                  }
                }
                break;
            }
          }
        }
      }

      // Find the difference between the current profile ids and the profile ids in the request
      currentProfileIds.removeAll(requestProfileIds);
      // Remove all activity roles for profile ids that are not in the request for the activity
      // which is not creator
      for (Integer profileId : currentProfileIds) {
        ActivityRole activityRole =
            activityRoleRepository.findByProfile_IdAndActivity_Id(profileId, activityId);
        if (activityRole.getActivityRoleType() != ActivityRoleType.Creator) {
          activityRoleRepository.delete(activityRole);
          List<SubscriptionHistory> subHistory =
              subscriptionHistoryRepository.findActive(
                  activityId, activityRole.getProfile().getId());
          // Remove subscription if they were a follower or above
          if (subHistory.size() > 0) {
            subHistory.get(0).setEndDateTime(LocalDateTime.now());
            subHistory.get(0).setUnsubscribeMethod(UnsubscribeMethod.REMOVED);
            subscriptionHistoryRepository.save(subHistory.get(0));
          }
        }
      }
    }
    return null;
  }

  /**
   * Changes the visibility of a users activity. If the visibility is private, all users apart from
   * the owner are removed from the activity. If the visibility is restricted, users in the request
   * and the owner are the only users able to view the activity. If the visibility is public, all
   * access users are removed, as public activities give access to all
   *
   * @param profileId The id of the user profile
   * @param activityId The id of the activity
   * @param session The current session of the logged in user
   * @return
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/visibility")
  public ResponseEntity changeActivityVisibility(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @RequestBody @Valid EditActivityVisibilityRequest request,
      HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();
      if (!UserSecurityService.checkIsAdminOrCreator((Integer) id, activity.getProfile().getId())) {
        return new ResponseEntity<>(
            "You are not authorized to change visibility for this activity",
            HttpStatus.UNAUTHORIZED);
      }

      ResponseEntity<String> editActivityRolesResponse =
          editActivityRoles(request, activity, activityId);
      if (editActivityRolesResponse != null) {
        return editActivityRolesResponse;
      }

      activity.setVisibilityTypeByString(request.getVisibility());
      activityRepository.save(activity);

      return new ResponseEntity<>("Activity visibility has been saved", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }
}
