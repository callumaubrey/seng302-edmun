package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ActivityType;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.requests.EditActivityTypeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "://", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@RestController
@RequestMapping("")
public class ActivityController {
    private final ProfileRepository profileRepository;

    ActivityController(ProfileRepository profileRep) {
        this.profileRepository = profileRep;
    }

    /**
     * Check if user is authorised to edit activity type
     *
     * @param requestId userid in endpoint
     * @param session   http session
     * @return ResponseEntity or null
     */
    private ResponseEntity<String> checkAuthorised(Integer requestId, HttpSession session) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        if (!(id.toString().equals(requestId.toString()))) {
            return new ResponseEntity<>("You can only edit you're own profile", HttpStatus.UNAUTHORIZED);
        }

        if (!profileRepository.existsById(requestId)) {
            return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
        }
        return null;
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

            ResponseEntity<String> authorisedResponse = this.checkAuthorised(profileId, session);
            if (authorisedResponse != null) {
                return authorisedResponse;
            }

            Set<ActivityType> activities = user.getActivityTypes();
            activities.addAll(request.getActivities());
            user.setActivityTypes(activities);
            profileRepository.save(user);
        }
        return ResponseEntity.ok("User activity types updated");
    }


}
