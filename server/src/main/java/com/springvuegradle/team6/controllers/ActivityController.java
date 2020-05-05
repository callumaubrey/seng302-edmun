package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ActivityType;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.requests.EditActivityTypeRequest;
import com.springvuegradle.team6.startup.UserSecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "://", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@RestController
@RequestMapping("")
public class ActivityController {
    private final ProfileRepository profileRepository;

    ActivityController(ProfileRepository profileRep) {
        this.profileRepository = profileRep;
    }

    /**
     * Update/replace list of user activity types
     *
     * @param profileId user id to be updated
     * @param request   EditActivityTypeRequest
     * @param session   HTTPSession
     * @return unauthorised response or 201/400 HttpStatus Response
     */
    @PutMapping("/profiles/{profileId}/activity-types")
    public ResponseEntity<String> updateActivityTypes(@PathVariable Integer profileId, @Valid @RequestBody EditActivityTypeRequest request, HttpSession session) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            Profile user = profile.get();

            ResponseEntity<String> authorisedResponse = UserSecurityService.checkAuthorised(profileId, session, profileRepository);
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



}
