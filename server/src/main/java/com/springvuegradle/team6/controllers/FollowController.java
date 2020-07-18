package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.exceptions.DuplicateSubscriptionException;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.security.UserSecurityService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(
        origins = "http://localhost:9500",
        allowCredentials = "true",
        allowedHeaders = "://",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.DELETE
        })
@RestController
@RequestMapping("")
public class FollowController {
    private ProfileRepository profileRepository;
    private ActivityRepository activityRepository;
    private SubscriptionHistoryRepository subscriptionHistoryRepository;

    /**
     * Constructor for FollowController class which gets the profile, email and role repository
     *
     * @param profileRepository             the profile repository
     * @param activityRepository            the activity repository
     * @param subscriptionHistoryRepository the subscriptionHistory repository
     */
    FollowController(ProfileRepository profileRepository,
                     ActivityRepository activityRepository,
                     SubscriptionHistoryRepository subscriptionHistoryRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.subscriptionHistoryRepository = subscriptionHistoryRepository;
    }

    /**
     * Creates new subscriptionHistory between user and activity
     * @param profileId id of the user subscribing
     * @param activityId id of the activity being subscribed to
     * @param session current http session
     * @return Response entity if successful will be ok (2xx) or (4xx) if unsuccessful
     */
    @PostMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> followActivity(@PathVariable int profileId,
                                                 @PathVariable int activityId,
                                                 HttpSession session) {
        // Check Authorisation
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        // Check if already subbed
        if (!subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty()) {
            return new ResponseEntity<>("User already follows this activity", HttpStatus.BAD_REQUEST);
        }

        Activity subTo = activity.get();

        // Create subscriptionHistory object
        SubscriptionHistory subscriptionHistory = new SubscriptionHistory(profile, subTo);

        subscriptionHistoryRepository.save(subscriptionHistory);

        return ResponseEntity.ok("User is now subscribed");
    }

    /**
     * Checks if the user is subscribed to the activity
     * @param profileId id of the user that is unsubscribing
     * @param activityId id of the activity being unsubscribed from
     * @param session current http session
     * @return Boolean true if user is subscribed, false if the user is unsubscribed
     */
    @GetMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> getFollowingActivity(@PathVariable int profileId,
                                                 @PathVariable int activityId,
                                                 HttpSession session) {
        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }
        boolean subscribed;
        // Check if already subbed
        subscribed = !subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty();
        JSONObject obj = new JSONObject();
        obj.appendField("subscribed", subscribed);
        return new ResponseEntity(obj, HttpStatus.OK);
    }

    /**
     * Unsubscribes user from activity
     * @param profileId id of the user that is unsubscribing
     * @param activityId id of the activity being unsubscribed from
     * @param session current http session
     * @return Response entity if successfull will be ok (2xx) or (4xx) if unsuccessful
     */
    @DeleteMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> unfollowActivity(@PathVariable int profileId,
                                                       @PathVariable int activityId,
                                                       HttpSession session) {
        // Check Authorisation
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        if (activity.get().getProfile().getId() == profile.getId()) {
            return new ResponseEntity("Cannot unfollow an activity you created", HttpStatus.BAD_REQUEST);
        }

        // Gets the active subscription history object for this user and activity if one exists
        List<SubscriptionHistory> activeSubscriptions = subscriptionHistoryRepository.findActive(activityId, profileId);

        // Check if already subbed
        if (activeSubscriptions.isEmpty()) {
            return new ResponseEntity<>("User does not follow this activity", HttpStatus.BAD_REQUEST);
        }

        SubscriptionHistory activeSubscription = activeSubscriptions.get(0);
        activeSubscription.setEndDateTime(LocalDateTime.now());

        subscriptionHistoryRepository.save(activeSubscription);

        return ResponseEntity.ok("User unsubscribed from activity");
    }
}
