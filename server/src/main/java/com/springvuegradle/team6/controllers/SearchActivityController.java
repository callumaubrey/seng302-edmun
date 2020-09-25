package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.SortActivity;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.responses.SearchActivityResponse;
import com.springvuegradle.team6.security.UserSecurityService;
import com.springvuegradle.team6.services.LocationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
@Validated
public class SearchActivityController {

  private final ActivityRepository activityRepository;
  private final LocationService locationService;

  SearchActivityController(ActivityRepository activityRepository, LocationService locationService) {
    this.activityRepository = activityRepository;
    this.locationService = locationService;
  }

  /**
   * This method handles ConstraintViolationExceptions which are thrown when a request parameter is
   * invalid. This method would return a response entity with bad request with the error message for
   * the end points in this class
   *
   * @param e ConstraintViolationException
   * @return ResponseEntity with BAD_REQUEST and error message
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * This endpoint is used for searching for an activity. Anyone that is logged in can use this
   * endpoint. If no request parameters are provided, then it will return all activities by most
   * recently created.
   *
   * <p>API example:
   * /activities?name=biking&hashtags=biking_is_cool%20hotday&hashtags-method=or&types=bike%20run&types-method=and&time=duration&start-date=20200812&end-date=20201029&limit=10&offset=0&lon=10.0234&lat=52.23423&radius=10&sort=closest_location
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
   * @param longitude the longitude coordinate of the location of the activity
   * @param latitude the latitude coordinate of the location of the activity
   * @param radius the radius of the the circle centred at the location coordinate
   * @param sortActivity the sorting method for the activities returned
   * @param session the logged in session
   * @return list of activities based on the search parameters
   */
  @GetMapping()
  public ResponseEntity<Object> getActivities(
      @RequestParam(name = "name", required = false) String activityName,
      @RequestParam(name = "types", required = false) String activityTypes,
      @RequestParam(name = "types-method", required = false)
          @Pattern(
              regexp = "(?i)and|or",
              message = "activity types method can only be 'and' or 'or'")
          String activityTypesMethod,
      @RequestParam(name = "hashtags", required = false) String hashtags,
      @RequestParam(name = "hashtags-method", required = false)
          @Pattern(regexp = "(?i)and|or", message = "hashtags method can only be 'and' or 'or'")
          String hashtagsMethod,
      @RequestParam(name = "time", required = false)
          @Pattern(
              regexp = "(?i)duration|continuous",
              message = "time can only be 'duration' or 'continuous'")
          String time,
      @RequestParam(name = "start-date", required = false)
          @Pattern(
              regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
              message = "start date must be in valid in YYYY-MM-DD format")
          String startDate,
      @RequestParam(name = "end-date", required = false)
          @Pattern(
              regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
              message = "end date must be in valid in YYYY-MM-DD format")
          String endDate,
      @RequestParam(name = "offset", required = false)
          @Min(value = 0, message = "offset cannot be less than 0")
          Integer offset,
      @RequestParam(name = "limit", required = false)
          @Min(value = 0, message = "limit cannot be less than 0")
          Integer limit,
      @RequestParam(name = "lon", required = false)
          @Max(value = 180, message = "longitude must be between -180 and 180 inclusive")
          @Min(value = -180, message = "longitude must be between -180 and 180 inclusive")
          Double longitude,
      @RequestParam(name = "lat", required = false)
          @Max(value = 90, message = "latitude must be between -90 and 90 inclusive")
          @Min(value = -90, message = "latitude must be between -90 and 90 inclusive")
          Double latitude,
      @RequestParam(name = "radius", required = false)
          @Min(value = 1, message = "radius must be between 1 and 200 inclusive (in kilometers)")
          @Max(value = 200, message = "radius must be between 1 and 200 inclusive (in kilometers)")
          Integer radius,
      @RequestParam(name = "sort", required = false) String sortActivity,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Integer profileId = (Integer) id;

    SortActivity sortActivityEnum = null;
    if (sortActivity != null) {
      try {
        sortActivityEnum = SortActivity.valueOf(sortActivity.toUpperCase());
      } catch (IllegalArgumentException e) {
        return new ResponseEntity<>("Sort method provided does not exist", HttpStatus.BAD_REQUEST);
      }
    }

    if (sortActivityEnum != null
        && sortActivityEnum.isLocationSort()
        && (longitude == null || latitude == null)) {
      return new ResponseEntity<>(
          "Cannot sort by location when no original location is provided", HttpStatus.BAD_REQUEST);
    }

    boolean isAdmin = UserSecurityService.checkIsAdmin();

    String[] activityTypesArray = null;
    if (activityTypes != null) {
      activityTypesArray = activityTypes.replace("%20", " ").split(" ");
    }

    String[] hashtagsArray = null;
    if (hashtags != null) {
      hashtagsArray = hashtags.replace("%20", " ").split(" ");
    }

    if (activityName != null) {
      activityName = activityName.replace("%20", " ");
    }

    if (activityTypesMethod != null) {
      activityTypesMethod = activityTypesMethod.toLowerCase();
    }

    if (hashtagsMethod != null) {
      hashtagsMethod = hashtagsMethod.toLowerCase();
    }

    LocalDateTime startDateLDT = null;
    LocalDateTime endDateLDT = null;

    if (time != null && time.equalsIgnoreCase("duration")) {

      if (startDate != null) {
        String[] startDateArray = startDate.split("-");
        String startYear = startDateArray[0];
        String startMonth = startDateArray[1];
        String startDay = startDateArray[2];
        startDateLDT =
            LocalDateTime.of(
                Integer.parseInt(startYear),
                Integer.parseInt(startMonth),
                Integer.parseInt(startDay),
                0,
                0);
      }
      if (endDate != null) {
        String[] endDateArray = endDate.split("-");

        String endYear = endDateArray[0];
        String endMonth = endDateArray[1];
        String endDay = endDateArray[2];
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

    if ((longitude != null && latitude == null) || (longitude == null && latitude != null)) {
      return new ResponseEntity<>(
          "Longitude and Latitude must both be provided for searching by location",
          HttpStatus.BAD_REQUEST);
    }

    if (radius == null) {
      radius = 1;
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
            isAdmin,
            longitude,
            latitude,
            radius,
            sortActivityEnum);
    List<SearchActivityResponse> results = new ArrayList<>();
    for (Activity activity : activityList) {
      Location location = activity.getLocation();
      if (location != null) {
        location.setName(
            locationService.getLocationAddressFromLatLng(
                location.getLatitude(), location.getLongitude(), false));
      }
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
              location,
              activity.getPath(),
              activity.getVisibilityType());
      results.add(result);
    }
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("results", results);
    return new ResponseEntity<>(resultsObject, HttpStatus.OK);
  }

  /**
   * This endpoint is used for finding how many activities will return from the specified
   * parameters. Anyone that is logged in can use this endpoint. If no request parameters are
   * provided, it will return the number of activities in the database.
   *
   * <p>API example:
   * /activities/count?name=biking&hashtags=biking_is_cool%20hotday&hashtags-method=or&types=bike%20run&types-method=and&time=duration&start-date=20200812&end-date=20201029&lon=10.0234&lat=52.23423&radius=10sort=closest_location
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
   * @param longitude the longitude coordinate of the location of the activity
   * @param latitude the latitude coordinate of the location of the activity
   * @param radius the radius of the the circle centred at the location coordinate
   * @param sortActivity the sorting method for the activities returned
   * @param session the logged in session
   * @return number of activities that will return based on the search parameters.
   */
  @GetMapping()
  @RequestMapping(value = "/count")
  public ResponseEntity<Object> getActivitiesCount(
      @RequestParam(name = "name", required = false) String activityName,
      @RequestParam(name = "types", required = false) String activityTypes,
      @RequestParam(name = "types-method", required = false)
          @Pattern(
              regexp = "(?i)and|or",
              message = "activity types method can only be 'and' or 'or'")
          String activityTypesMethod,
      @RequestParam(name = "hashtags", required = false) String hashtags,
      @RequestParam(name = "hashtags-method", required = false)
          @Pattern(regexp = "(?i)and|or", message = "hashtags method can only be 'and' or 'or'")
          String hashtagsMethod,
      @RequestParam(name = "time", required = false)
          @Pattern(
              regexp = "(?i)duration|continuous",
              message = "time can only be 'duration' or 'continuous'")
          String time,
      @RequestParam(name = "start-date", required = false)
          @Pattern(
              regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
              message = "start date must be in valid in YYYY-MM-DD format")
          String startDate,
      @RequestParam(name = "end-date", required = false)
          @Pattern(
              regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))",
              message = "end date must be in valid in YYYY-MM-DD format")
          String endDate,
      @RequestParam(name = "lon", required = false)
          @Max(value = 180, message = "longitude must be between -180 and 180 inclusive")
          @Min(value = -180, message = "longitude must be between -180 and 180 inclusive")
          Double longitude,
      @RequestParam(name = "lat", required = false)
          @Max(value = 90, message = "latitude must be between -90 and 90 inclusive")
          @Min(value = -90, message = "latitude must be between -90 and 90 inclusive")
          Double latitude,
      @RequestParam(name = "radius", required = false) Integer radius,
      @RequestParam(name = "sort", required = false) String sortActivity,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Integer profileId = (Integer) id;

    SortActivity sortActivityEnum = null;
    if (sortActivity != null) {
      try {
        sortActivityEnum = SortActivity.valueOf(sortActivity.toUpperCase());
      } catch (IllegalArgumentException e) {
        return new ResponseEntity<>("Sort method provided does not exist", HttpStatus.BAD_REQUEST);
      }
    }

    if (sortActivityEnum != null
        && sortActivityEnum.isLocationSort()
        && (longitude == null || latitude == null)) {
      return new ResponseEntity<>(
          "Cannot sort by location when no original location is provided", HttpStatus.BAD_REQUEST);
    }

    boolean isAdmin = UserSecurityService.checkIsAdmin();

    String[] activityTypesArray = null;
    if (activityTypes != null) {
      activityTypesArray = activityTypes.replace("%20", " ").split(" ");
    }

    String[] hashtagsArray = null;
    if (hashtags != null) {
      hashtagsArray = hashtags.replace("%20", " ").split(" ");
    }

    if (activityName != null) {
      activityName = activityName.replace("%20", " ");
    }

    if (activityTypesMethod != null) {
      activityTypesMethod = activityTypesMethod.toLowerCase();
    }

    if (hashtagsMethod != null) {
      hashtagsMethod = hashtagsMethod.toLowerCase();
    }

    LocalDateTime startDateLDT = null;
    LocalDateTime endDateLDT = null;

    if (time != null && time.equalsIgnoreCase("duration")) {

      if (startDate != null) {
        String[] startDateArray = startDate.split("-");
        String startYear = startDateArray[0];
        String startMonth = startDateArray[1];
        String startDay = startDateArray[2];
        startDateLDT =
            LocalDateTime.of(
                Integer.parseInt(startYear),
                Integer.parseInt(startMonth),
                Integer.parseInt(startDay),
                0,
                0);
      }
      if (endDate != null) {
        String[] endDateArray = endDate.split("-");

        String endYear = endDateArray[0];
        String endMonth = endDateArray[1];
        String endDay = endDateArray[2];
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

    if ((longitude != null && latitude == null) || (longitude == null && latitude != null)) {
      return new ResponseEntity<>(
          "Longitude and Latitude must both be provided for searching by location",
          HttpStatus.BAD_REQUEST);
    }

    if (radius == null) {
      radius = 1;
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
            isAdmin,
            longitude,
            latitude,
            radius,
            sortActivityEnum);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }
}
