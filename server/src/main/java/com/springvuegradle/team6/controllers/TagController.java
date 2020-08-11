package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import com.springvuegradle.team6.security.UserSecurityService;
import java.security.Security;
import net.minidev.json.JSONObject;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TagController {

  private final ActivityRepository activityRepository;
  private final TagRepository tagRepository;

  TagController(ActivityRepository activityRepository, TagRepository tagRepository) {
    this.activityRepository = activityRepository;
    this.tagRepository = tagRepository;
  }

  /**
   * Search for hashtags in the database that starts with the given hashtag e.g searching for cool,
   * would return coolor and coolstory if they exist in the database.
   *
   * @param hashtag the hashtag to search for, does not contain # at the start
   * @param session the current session
   * @return the list of hashtags that match the hashtag
   */
  @GetMapping
  @RequestMapping(value = "/hashtag/autocomplete")
  public ResponseEntity getHashtagsAutocomplete(
      @RequestParam(name = "hashtag") String hashtag, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    hashtag = hashtag.toLowerCase();

    if (!hashtag.matches("^[a-zA-Z0-9_]*$")) {
      return new ResponseEntity(
          "Is not a hashtag, can only contain alphanumeric characters and underscore",
          HttpStatus.BAD_REQUEST);
    }

    List<String> resultList;
    if (hashtag.length() < 3) {
      resultList = new ArrayList<>();
      resultList.add(hashtag);
    } else {
      hashtag = hashtag.replace("_", "\\_");
      resultList = tagRepository.findTagsMatchingSearch(hashtag, PageRequest.of(0, 10));
    }

    Map<String, List<String>> result = new HashMap<>();
    result.put("results", resultList);
    JSONObject obj = new JSONObject(result);
    return new ResponseEntity(obj, HttpStatus.OK);
  }

  /**
   * Find all activities containing the given hashtag that the user has permission to view. If a
   * user is of role ROLE_ADMIN or ROLE_USER_ADMIN then activities of any visibility will be
   * returned which contain the hashtag. If a user is not an admin then all public, private or
   * restricted activities which contain the hashtag that they own and any public activities which
   * also contain the hashtag will be returned. The activities are returned in descending order of
   * the activities creation date.
   *
   * @param hashTag the name of the hashtag
   * @param session the current session logged in
   * @return the activities
   */
  @GetMapping("/activities/hashtag/{hashTag}")
  public ResponseEntity getActivitiesByHashtag(@PathVariable String hashTag, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    //Check if user is an admin, if so return activities regardless of their visibility.
    if (UserSecurityService.checkIsAdmin()) {
      hashTag = hashTag.toLowerCase();
      List<Activity> activities = activityRepository
          .getActivitiesByHashTagAnyVisibilityType(hashTag, (int) id);
      return new ResponseEntity(activities, HttpStatus.OK);
    } else {
      hashTag = hashTag.toLowerCase();
      List<Activity> activities = activityRepository.getActivitiesByHashTag(hashTag, (Integer) id);
      return new ResponseEntity(activities, HttpStatus.OK);
    }
  }
}
