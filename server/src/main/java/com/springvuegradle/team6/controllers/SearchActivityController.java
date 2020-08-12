package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
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
   * This endpoint is used for searching for an activity.
   * Anyone that is logged in can use this endpoint.
   *
   * @param activityName name to search for
   * @param activityTypes activity types to search for
   * @param activityTypesMethod the method of searching activity types (OR or AND)
   * @param hashtags hashtags to search for
   * @param hashtagsMethod the method of searching hashtags (OR or AND)
   * @param session the logged in session
   * @return
   */
  @GetMapping()
  public ResponseEntity getActivity(
      @RequestParam(name = "name", required = false) String activityName,
      @RequestParam(name = "types", required = false) String activityTypes,
      @RequestParam(name = "types-method", required = false) String activityTypesMethod,
      @RequestParam(name = "hashtags", required = false) String hashtags,
      @RequestParam(name = "hashtags-method", required = false) String hashtagsMethod,
      HttpSession session) {

    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    if (activityName == null) {

    }

    String[] activityTypesArray = null;
    if (activityTypes != null) {
      activityTypesArray = activityTypes.split("%20");
    }

    String[] hashtagsArray = null;
    if (hashtags != null) {
      hashtagsArray = hashtags.split("%20");
    }


    List<Activity> activityList =
        activityRepository.searchActivity(
            activityName, activityTypesArray, hashtagsArray, activityTypesMethod, hashtagsMethod, null, null, null, 0, 0);
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("results", activityList);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }
}
