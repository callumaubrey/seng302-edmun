package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.repositories.ActivityHistoryRepository;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.SubscriptionHistoryRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import com.springvuegradle.team6.services.LocationService;
import com.sun.mail.iap.Response;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@RequestMapping("/location")
@Validated
public class LocationController {

  private final LocationRepository locationRepository;
  private final LocationService locationService;

  LocationController(LocationRepository locationRepository, LocationService locationService) {
    this.locationRepository = locationRepository;
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
   * This endpoint gets a list of location name through coordinates and short name. The endpoint can
   * only handle either coordinates or name query at once. This method will return a response entity
   * of bad request if coordinates are invalid, or all/none query parameters are given
   *
   * <p>JSONObject of {name: "location"} is returned if coordinates is queried JSONObject of {name:
   * "location", place_id: "place_id"} is returned if short name is queried
   *
   * @param longitude coordinate longitude
   * @param latitude coordinate latitude
   * @param name location short name
   * @param session HttpSession
   * @return ResponseEntity OK if successful, or BAD REQUEST otherwise
   */
  @GetMapping("/autocomplete")
  public ResponseEntity<?> getLocationAutocomplete(
      @RequestParam(name = "lon", required = false)
          @Max(value = 180, message = "longitude must be between -180 and 180 inclusive")
          @Min(value = -180, message = "longitude must be between -180 and 180 inclusive")
          Double longitude,
      @RequestParam(name = "lat", required = false)
          @Max(value = 90, message = "latitude must be between -90 and 90 inclusive")
          @Min(value = -90, message = "latitude must be between -90 and 90 inclusive")
          Double latitude,
      @RequestParam(name = "name", required = false) String name,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    if (longitude != null && latitude != null && name != null) {
      return new ResponseEntity<>(
          "Cannot specify both coordinates and name. Please specify only one",
          HttpStatus.BAD_REQUEST);
    }

    List results = new ArrayList();
    if (longitude != null && latitude != null) {
      String locationName =
          locationService.getLocationAddressFromLatLng(latitude, longitude, false);
      if (locationName != null) {
        results.add(locationName);
        JSONObject resultsObject = new JSONObject();
        resultsObject.put("results", results);
        return new ResponseEntity<>(resultsObject, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("No location at this coordinate", HttpStatus.BAD_REQUEST);
      }
    }
    if (name != null) {
      results = locationService.getLocationAddressListFromName(name);
      if (results.size() > 10) {
        results = results.subList(0, 10);
      }
      JSONObject resultsObject = new JSONObject();
      resultsObject.put("results", results);
      return new ResponseEntity<>(resultsObject, HttpStatus.OK);
    }
    return new ResponseEntity<>(
        "Either latitude or longitude is specified or name to search for is specified",
        HttpStatus.BAD_REQUEST);
  }
}
