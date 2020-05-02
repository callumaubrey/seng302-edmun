package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.responses.SearchProfileResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.coyote.Response;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
    origins = "http://localhost:9500",
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RequestMapping("/profiles")
public class SearchProfileController {

  private final ProfileRepository profileRepository;
  private final EmailRepository emailRepository;

  SearchProfileController(ProfileRepository profileRepository, EmailRepository emailRepository) {
    this.profileRepository = profileRepository;
    this.emailRepository = emailRepository;
  }

  /**
   * Searches for the user based on the full name given as best as possible using the implementation
   * of hibernate search
   *
   * @param fullName the full name to search for
   * @param session the current logged in user session
   * @return the results of the search containing profiles that match the full name roughly
   */
  @GetMapping()
  @RequestMapping(params = "fullname")
  public ResponseEntity getProfileByFullName(
      @RequestParam(name = "fullname") String fullName, @RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    String fullNameWithSpaces = fullName.replaceAll("%20", " ");
    List<Profile> profiles = profileRepository.searchFullname(fullNameWithSpaces, limit, offset);
    System.out.println(fullNameWithSpaces);
    System.out.println(profiles.size());
    List<SearchProfileResponse> results = new ArrayList<>();
    for (Profile profile : profiles) {
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getEmail().getAddress());
      results.add(result);
    }
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * Given the email search for a profile that has this email
   *
   * @param searchedEmail the email to search for
   * @param session the current logged in user session
   * @return the results of the search that matches the email exactly
   */
  @GetMapping
  @RequestMapping(params = "email")
  public ResponseEntity getProfileByEmail(
      @RequestParam(name = "email") String searchedEmail, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    JSONObject resultsObject = new JSONObject();
    List<SearchProfileResponse> results = new ArrayList<>();

    Optional<Profile> optionalProfile =
        profileRepository.findByAdditionalemail_AddressOrEmail_Address(
            searchedEmail, searchedEmail);
    if (optionalProfile.isPresent()) {
      Profile profile = optionalProfile.get();
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getEmail().getAddress());
      results.add(result);
    }

    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * Given the nickname search for profiles that have this nickname
   *
   * @param searchedNickname the nickname to search for
   * @param session the current logged in user session
   * @return the results of the search that matches the nickname exactly
   */
  @GetMapping
  @RequestMapping(params = "nickname")
  public ResponseEntity getProfileByNickname(
      @RequestParam(name = "nickname") String searchedNickname, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    JSONObject resultsObject = new JSONObject();

    List<Profile> profiles = profileRepository.findByNickname(searchedNickname);
    List<SearchProfileResponse> results = new ArrayList<>();
    for (Profile profile : profiles) {
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getEmail().getAddress());
      results.add(result);
    }

    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }
}
