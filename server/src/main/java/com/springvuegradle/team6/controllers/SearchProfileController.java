package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
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

@RestController
@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "://", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@RequestMapping("/profiles")
public class SearchProfileController {

    private final ProfileRepository profileRepository;

    SearchProfileController(
            ProfileRepository profileRepository
    ) {
        this.profileRepository = profileRepository;
    }

    @GetMapping()
    public ResponseEntity getProfileByFullName(@RequestParam(name = "fullname") String fullName, HttpSession session) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }
        String fullNameWithSpaces = fullName.replaceAll("%20", " ");
        List<Profile> profiles = profileRepository.searchFullname(fullNameWithSpaces, -1, -1);
        System.out.println(fullNameWithSpaces);
        System.out.println(profiles.size());
        List<SearchProfileResponse> results = new ArrayList<>();
        for (Profile profile: profiles){
            SearchProfileResponse result = new SearchProfileResponse(profile.getLastname(), profile.getFirstname(), profile.getMiddlename(), profile.getNickname(), profile.getEmail().getAddress());
            results.add(result);
        }
        JSONObject resultsObject = new JSONObject();
        resultsObject.put("results", results);
        return new ResponseEntity(resultsObject, HttpStatus.OK);
    }
}
