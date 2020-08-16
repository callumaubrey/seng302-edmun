package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.responses.SearchActivityResponse;
import com.springvuegradle.team6.responses.SearchProfileResponse;
import com.springvuegradle.team6.security.UserSecurityService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
@RequestMapping("/activities")
public class SearchActivityController {

  private final ActivityRepository activityRepository;

  SearchActivityController(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  /**
   * This endpoint is used for searching for an activity. Anyone that is logged in can use this
   * endpoint.
   *
   * @param activityName name to search for
   * @param activityTypes activity types to search for
   * @param activityTypesMethod the method of searching activity types (OR or AND)
   * @param hashtags hashtags to search for
   * @param hashtagsMethod the method of searching hashtags (OR or AND)
   * @param time whether searching for duration or continuous activities, if not provided search for
   *     all activities
   * @param startDate if duration is selected, the startDate specifies the earliest starting date
   *     any activity can have in the results
   * @param endDate if duration is selected, the endDate specified the latest ending date any
   *     activity can have in the results
   * @param offset how many activities to skip in the results
   * @param limit how many activities to return in the results
   * @param session the logged in session
   * @return list of activities based on the search parameters
   */
  @GetMapping()
  public ResponseEntity getActivities(
      @RequestParam(name = "name", required = false) String activityName,
      @RequestParam(name = "types", required = false) String activityTypes,
      @RequestParam(name = "types-method", required = false) String activityTypesMethod,
      @RequestParam(name = "hashtags", required = false) String hashtags,
      @RequestParam(name = "hashtags-method", required = false) String hashtagsMethod,
      @RequestParam(name = "time", required = false) String time,
      @RequestParam(name = "start-date", required = false) String startDate,
      @RequestParam(name = "end-date", required = false) String endDate,
      @RequestParam(name = "offset", required = false) Integer offset,
      @RequestParam(name = "limit", required = false) Integer limit,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Integer profileId = (Integer) id;

    boolean isAdmin = UserSecurityService.checkIsAdmin();

    String[] activityTypesArray = null;
    if (activityTypes != null) {
      activityTypesArray = activityTypes.replaceAll("%20", " ").split(" ");
    }

    String[] hashtagsArray = null;
    if (hashtags != null) {
      hashtagsArray = hashtags.replaceAll("%20", " ").split(" ");
    }

    if (activityName != null) {
      activityName = activityName.replaceAll("%20", " ");
    }

    if (activityTypesMethod != null) {
      activityTypesMethod = activityTypesMethod.toLowerCase();
    }

    if (hashtagsMethod != null) {
      hashtagsMethod = hashtagsMethod.toLowerCase();
    }

    LocalDateTime startDateLDT = null;
    LocalDateTime endDateLDT = null;

    if (time != null && time.toLowerCase().equals("duration")) {
      if (startDate != null) {
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(4, 6);
        String startDay = startDate.substring(6, 8);
        startDateLDT =
            LocalDateTime.of(
                Integer.parseInt(startYear),
                Integer.parseInt(startMonth),
                Integer.parseInt(startDay),
                0,
                0);
      }
      if (endDate != null) {
        String endYear = endDate.substring(0, 4);
        String endMonth = endDate.substring(4, 6);
        String endDay = endDate.substring(6, 8);
        endDateLDT =
            LocalDateTime.of(
                Integer.parseInt(endYear),
                Integer.parseInt(endMonth),
                Integer.parseInt(endDay),
                23,
                59,
                59,
                999);
      }
    }

    if (offset == null) {
      offset = -1;
    }

    if (limit == null) {
      limit = -1;
    }

    List<Activity> activityList =
        activityRepository.searchActivity(
            activityName,
            activityTypesArray,
            hashtagsArray,
            activityTypesMethod,
            hashtagsMethod,
            time,
            startDateLDT,
            endDateLDT,
            limit,
            offset,
            profileId,
            isAdmin);
    List<SearchActivityResponse> results = new ArrayList<>();
    for (Activity activity : activityList) {
      SearchActivityResponse result =
          new SearchActivityResponse(
              activity.getActivityName(),
              activity.getId(),
              activity.getProfile().getId(),
              activity.getDescription(),
              activity.getActivityTypes(),
              activity.getTags(),
              activity.isContinuous(),
              activity.getStartTime(),
              activity.getEndTime(),
              activity.getLocation(),
              activity.getVisibilityType());
      results.add(result);
    }
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * This endpoint is used for finding how many activities will return from the specifed parameters.
   * Anyone that is logged in can use this endpoint.
   *
   * @param activityName name to search for
   * @param activityTypes activity types to search for
   * @param activityTypesMethod the method of searching activity types (OR or AND)
   * @param hashtags hashtags to search for
   * @param hashtagsMethod the method of searching hashtags (OR or AND)
   * @param time whether searching for duration or continuous activities, if not provided search for
   *     all activities
   * @param startDate if duration is selected, the startDate specifies the earliest starting date
   *     any activity can have in the results
   * @param endDate if duration is selected, the endDate specified the latest ending date any
   *     activity can have in the results
   * @param session the logged in session
   * @return number of activities that will return based on the search parameters.
   */
  @GetMapping()
  @RequestMapping(value = "/count")
  public ResponseEntity getActivitiesCount(
      @RequestParam(name = "name", required = false) String activityName,
      @RequestParam(name = "types", required = false) String activityTypes,
      @RequestParam(name = "types-method", required = false) String activityTypesMethod,
      @RequestParam(name = "hashtags", required = false) String hashtags,
      @RequestParam(name = "hashtags-method", required = false) String hashtagsMethod,
      @RequestParam(name = "time", required = false) String time,
      @RequestParam(name = "start-date", required = false) String startDate,
      @RequestParam(name = "end-date", required = false) String endDate,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Integer profileId = (Integer) id;

    boolean isAdmin = UserSecurityService.checkIsAdmin();

    String[] activityTypesArray = null;
    if (activityTypes != null) {
      activityTypesArray = activityTypes.replaceAll("%20", " ").split(" ");
    }

    String[] hashtagsArray = null;
    if (hashtags != null) {
      hashtagsArray = hashtags.replaceAll("%20", " ").split(" ");
    }

    if (activityName != null) {
      activityName = activityName.replaceAll("%20", " ");
    }

    if (activityTypesMethod != null) {
      activityTypesMethod = activityTypesMethod.toLowerCase();
    }

    if (hashtagsMethod != null) {
      hashtagsMethod = hashtagsMethod.toLowerCase();
    }

    LocalDateTime startDateLDT = null;
    LocalDateTime endDateLDT = null;

    if (time != null && time.toLowerCase().equals("duration")) {
      if (startDate != null) {
        // YYYY-MM-DD
        // 0123456789
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(5, 7);
        String startDay = startDate.substring(8, 9);
        startDateLDT =
            LocalDateTime.of(
                Integer.parseInt(startYear),
                Integer.parseInt(startMonth),
                Integer.parseInt(startDay),
                0,
                0);
      }
      if (endDate != null) {
        String startYear = startDate.substring(0, 4);
        String startMonth = startDate.substring(5, 7);
        String startDay = startDate.substring(8, 9);
        endDateLDT =
            LocalDateTime.of(
                Integer.parseInt(endYear),
                Integer.parseInt(endMonth),
                Integer.parseInt(endDay),
                23,
                59,
                59,
                999);
      }
    }

    Integer count =
        activityRepository.searchActivityCount(
            activityName,
            activityTypesArray,
            hashtagsArray,
            activityTypesMethod,
            hashtagsMethod,
            time,
            startDateLDT,
            endDateLDT,
            profileId,
            isAdmin);
    return new ResponseEntity(count, HttpStatus.OK);
  }
}
