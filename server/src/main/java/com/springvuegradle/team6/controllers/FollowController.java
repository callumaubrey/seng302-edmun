package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.SubscriptionHistoryRepository;
import com.springvuegradle.team6.requests.DeleteSubscriptionRequest;
import com.springvuegradle.team6.requests.EditSubscriptionRequest;
import com.springvuegradle.team6.requests.SubscriptionRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * This controller contains all end points related to adding subscriptions to the activity and
 * changing roles of users of the activity.
 */
@CrossOrigin(
    origins = "http://localhost:9500",
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
@RestController
@RequestMapping("")
public class FollowController {

  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;
  private final ActivityRoleRepository activityRoleRepository;
  private final SubscriptionHistoryRepository subscriptionHistoryRepository;
  private final EmailRepository emailRepository;

  /**
   * Constructor for FollowController class which gets the profile, email and role repository
   *
   * @param profileRepository             the profile repository
   * @param activityRepository            the activity repository
   * @param subscriptionHistoryRepository the subscriptionHistory repository
   */
  FollowController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityRoleRepository activityRoleRepository,
      SubscriptionHistoryRepository subscriptionHistoryRepository,
      EmailRepository emailRepository) {
    this.profileRepository = profileRepository;
    this.activityRepository = activityRepository;
    this.activityRoleRepository = activityRoleRepository;
    this.subscriptionHistoryRepository = subscriptionHistoryRepository;
    this.emailRepository = emailRepository;
  }

  /**
   * The user follows/subscribes to the activity. Creates new subscription history row between user
   * and activity and also adds a new activity role for user as a follower
   *
   * @param profileId  id of the user subscribing
   * @param activityId id of the activity being subscribed to
   * @param session    current http session
   * @return Response entity if successful will be ok (2xx) or (4xx) if unsuccessful
   */
  @PostMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
  public ResponseEntity<String> subscribeToActivity(
      @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {

    // Check Authorisation
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    Profile profile = profileRepository.findById(profileId);

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
    }

    // Check if already subscribed
    if (!subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty()) {
      return new ResponseEntity<>(
          "User already subscribed to this activity", HttpStatus.BAD_REQUEST);
    }

    Activity activity = optionalActivity.get();

    SubscriptionHistory subscriptionHistory =
        new SubscriptionHistory(profile, activity, SubscribeMethod.SELF);
    subscriptionHistoryRepository.save(subscriptionHistory);

    if (activityRoleRepository.findByProfile_IdAndActivity_Id(profileId, activityId) == null) {
      ActivityRole activityRole = new ActivityRole();
      activityRole.setActivity(activity);
      activityRole.setProfile(profile);
      activityRole.setActivityRoleType(ActivityRoleType.Follower);
      activityRoleRepository.save(activityRole);
    }

    return ResponseEntity.ok("User is now subscribed to the activity");
  }

  /**
   * Checks if the user is subscribed to the activity
   *
   * @param profileId  id of the user that is unsubscribing
   * @param activityId id of the activity being unsubscribed from
   * @param session    current http session
   * @return Boolean true if user is subscribed, false if the user is unsubscribed
   */
  @GetMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
  public ResponseEntity<String> getSubscribedToActivity(
      @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
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
   * The user unsubscribes themselves from the activity. Update their subscription history to have
   * subscription end date time as now. The user's activity role is changed to ACCESS if the
   * activity was restricted, and if the activity was public then the role is simply removed as all
   * users have access to a public activity.
   *
   * @param profileId  id of the user that is unsubscribing
   * @param activityId id of the activity being unsubscribed from
   * @param session    current http session
   * @return Response entity if successfull will be ok (2xx) or (4xx) if unsuccessful
   */
  @DeleteMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
  public ResponseEntity<String> unfollowAndUnsubscribeFromActivity(
      @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
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
    List<SubscriptionHistory> activeSubscriptions =
        subscriptionHistoryRepository.findActive(activityId, profileId);

    // Check if already subbed
    if (activeSubscriptions.isEmpty()) {
      return new ResponseEntity<>("User does not follow this activity", HttpStatus.BAD_REQUEST);
    }

    SubscriptionHistory activeSubscription = activeSubscriptions.get(0);
    activeSubscription.setEndDateTime(LocalDateTime.now());
    activeSubscription.setUnsubscribeMethod(UnsubscribeMethod.SELF);
    subscriptionHistoryRepository.save(activeSubscription);

    List<ActivityRole> activityRoles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profileId);
    if (!activityRoles.isEmpty()) {
      if (!activity.get().getVisibilityType().equals(VisibilityType.Public)) {
        ActivityRole activityRole = activityRoles.get(0);
        activityRole.setActivityRoleType(ActivityRoleType.Access);
        activityRoleRepository.save(activityRole);
      } else {
        activityRoleRepository.delete(activityRoles.get(0));
      }
    }

    return ResponseEntity.ok("User unsubscribed from activity");
  }

  /**
   * Checks the user is logged in, that the user exists and is the author of the activity
   *
   * @param profileId  the profileId to check
   * @param activityId the activityId to check
   * @param email      the email String to check against a users profile
   * @param session    the HttpSession
   * @return null if everything is OK. ResponseEntity if not
   */
  private ResponseEntity<String> checkUserIsAuthorAndExists(
      int profileId, int activityId, String email, HttpSession session) {
    Profile creatorProfile = profileRepository.findById(profileId);
    if (creatorProfile == null) {
      return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
    }
    Optional<Activity> activityOptional = activityRepository.findById(activityId);
    if (activityOptional.isEmpty()) {
      return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
    }

    Activity activity = activityOptional.get();
    if (!activity.getProfile().getId().equals(profileId)) {
      return new ResponseEntity<>(
          "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
    }
    Optional<Email> optionalEmail = emailRepository.findByAddress(email);
    if (optionalEmail.isEmpty()) {
      return new ResponseEntity("No such email", HttpStatus.NOT_FOUND);
    }

    Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());
    if (roleProfile == null) {
      return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
    }

    return null;
  }

  /**
   * Endpoint for creator of the activity to set activity roles for a user for their activity. If
   * user does not have a role and they are given a role higher than access then it also subscribes
   * them to the activity.
   *
   * @param profileId               id of creator of activity
   * @param activityId              if of activity
   * @param editSubscriptionRequest information to edit subscription
   * @param session                 the session of the active user
   * @return response entity, 200 if successful.
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
  public ResponseEntity editRoleOfOneUser(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @RequestBody @Valid EditSubscriptionRequest editSubscriptionRequest,
      HttpSession session) {
    SubscriptionRequest request = editSubscriptionRequest.getSubscription();

    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    ResponseEntity<String> checks =
        checkUserIsAuthorAndExists(profileId, activityId, request.getEmail(), session);
    if (checks != null) {
      return checks;
    }

    Optional<Activity> activityOptional = activityRepository.findById(activityId);
    Activity activity = activityOptional.get();
    Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
    Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());

    ActivityRole activityRoleFound =
        activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);
    String toCamelCase =
        request.getRole().substring(0, 1).toUpperCase() + request.getRole().substring(1);
    ActivityRoleType activityRoleType = ActivityRoleType.valueOf(toCamelCase);
    if (activityRoleFound == null) {
      // User doesn't have role.
      ActivityRole activityRole = new ActivityRole();
      activityRole.setProfile(roleProfile);
      activityRole.setActivity(activity);
      activityRole.setActivityRoleType(activityRoleType);
      activityRoleRepository.save(activityRole);
      // If not access. Then we check and subscribe
      if (!activityRoleType.equals(ActivityRoleType.Access)) {
        List<SubscriptionHistory> optionalSubscription =
            subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());
        if (optionalSubscription.isEmpty()) {
          SubscriptionHistory subscriptionHistory =
              new SubscriptionHistory(roleProfile, activity, SubscribeMethod.ADDED);
          subscriptionHistoryRepository.save(subscriptionHistory);
        }
      }
    } else {
      // User already has role.
      if (!activityRoleFound.getActivityRoleType().equals(ActivityRoleType.Access)
          && activityRoleType.equals(ActivityRoleType.Access)) {
        // I am demoting them to access. Change role and unsubscribe
        activityRoleFound.setActivityRoleType(activityRoleType);
        activityRoleRepository.save(activityRoleFound);
        // Now unsubscribe
        List<SubscriptionHistory> optionalSubscription =
            subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());
        if (!optionalSubscription.isEmpty()) {
          SubscriptionHistory activeSubscription = optionalSubscription.get(0);
          activeSubscription.setEndDateTime(LocalDateTime.now());
          activeSubscription.setUnsubscribeMethod(UnsubscribeMethod.REMOVED);
          subscriptionHistoryRepository.save(activeSubscription);
        }
      } else {
        // User has access and now giving them a higher role
        activityRoleFound.setActivityRoleType(activityRoleType);
        activityRoleRepository.save(activityRoleFound);
        List<SubscriptionHistory> optionalSubscription =
            subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());
        if (optionalSubscription.isEmpty()) {
          SubscriptionHistory subscriptionHistory =
              new SubscriptionHistory(roleProfile, activity, SubscribeMethod.ADDED);
          subscriptionHistoryRepository.save(subscriptionHistory);
        }
      }
    }

    return new ResponseEntity("Activity role of user was updated", HttpStatus.OK);
  }

  /**
   * Endpoint for creator of the activity to delete activity roles for a user from their activity.
   * To delete the activity role of the email in the request from the activity and deletes their
   * subscription
   *
   * @param profileId  the user who's subscription and role that is changing
   * @param activityId the id of the activity
   * @param request    the request with the email to delete
   * @param session    the session of the active user
   * @return the response, 200 if successfully deleted.
   */
  @DeleteMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
  public ResponseEntity deleteRoleAndSubscriptionOfUser(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @RequestBody @Valid DeleteSubscriptionRequest request,
      HttpSession session) {
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    ResponseEntity<String> checks =
        checkUserIsAuthorAndExists(profileId, activityId, request.getEmail(), session);
    if (checks != null) {
      return checks;
    }

    Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
    Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());

    ActivityRole activityRoleFound =
        activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);

    if (activityRoleFound == null) {
      return new ResponseEntity("User is not subscribed", HttpStatus.NOT_FOUND);
    } else {
      activityRoleRepository.delete(activityRoleFound);

      List<SubscriptionHistory> activeSubscriptions =
          subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());

      // Check if already subbed
      if (activeSubscriptions.isEmpty()) {
        return new ResponseEntity<>("User does not follow this activity", HttpStatus.BAD_REQUEST);
      }

      SubscriptionHistory activeSubscription = activeSubscriptions.get(0);
      activeSubscription.setEndDateTime(LocalDateTime.now());
      activeSubscription.setUnsubscribeMethod(UnsubscribeMethod.REMOVED);

      subscriptionHistoryRepository.save(activeSubscription);
    }

    return new ResponseEntity("Activity role deleted", HttpStatus.OK);
  }

  /**
   * This endpoint gets the role of the user associated to the email in the body.
   *
   * @param profileId  owner of the activity
   * @param activityId activity
   * @param request    the request with the email of the user we want to check
   * @param session    session
   * @return
   */
  @GetMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
  public ResponseEntity getRoleOfUser(
      @PathVariable int profileId,
      @PathVariable int activityId,
      @RequestBody @Valid DeleteSubscriptionRequest request,
      HttpSession session) {
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    ResponseEntity<String> checks =
        checkUserIsAuthorAndExists(profileId, activityId, request.getEmail(), session);
    if (checks != null) {
      return checks;
    }

    Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
    Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());

    ActivityRole activityRole =
        activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);
    if (activityRole == null) {
      return new ResponseEntity("User has no activity role", HttpStatus.NOT_FOUND);
    }

    List<SubscriptionHistory> activeSubscriptions =
        subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());
    if (activeSubscriptions.isEmpty()) {
      return new ResponseEntity<>("User is not subscribed", HttpStatus.NOT_FOUND);
    }

    JSONObject obj = new JSONObject();
    obj.appendField("role", activityRole.getRole());
    return new ResponseEntity(obj, HttpStatus.OK);
  }

  /**
   * Retrieves all users associated with the activity and their role. If the activity is private
   * only the creator or an admin can have access. If the activity is restricted only users that
   * have a role in the activity will be granted access
   *
   * @param activityId id of the activity
   * @param session    current http session
   * @return Lists with users associated to a specific role
   */
  @GetMapping("activities/{activityId}/members")
  public ResponseEntity<String> getActivityUsers(
      @PathVariable int activityId,
      @RequestParam(name = "offset", required = false) Integer offset,
      @RequestParam(name = "limit", required = false) Integer limit,
      @RequestParam(name = "type", required = false) String type,
      HttpSession session) {
    Optional<Activity> activity = activityRepository.findById(activityId);
    if (activity.isEmpty()) {
      return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
    }
    Object id = session.getAttribute("id");
    VisibilityType visibility = activity.get().getVisibilityType();
    int activityCreator = activity.get().getProfile().getId();
    if (visibility.equals(VisibilityType.Private)) {
      if (id == null || !UserSecurityService.checkIsAdminOrCreator((int) id, activityCreator)) {
        return new ResponseEntity("Unauthorised user", HttpStatus.UNAUTHORIZED);
      }
    } else if (visibility.equals(VisibilityType.Restricted)
        && !UserSecurityService.checkIsAdminOrCreator((int) id, activityCreator)) {
      List<ActivityRole> member =
          activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, (Integer) id);
      if (member.size() == 0) {
        return new ResponseEntity("Unauthorised user", HttpStatus.UNAUTHORIZED);
      }
    }
    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }
    List<String> acceptedTypes =
        Arrays.asList("organiser", "participant", "accessor", "follower", "creator");
    if (type != null && !acceptedTypes.contains(type)) {
      return new ResponseEntity("Invalid member type", HttpStatus.BAD_REQUEST);
    }

    JSONObject response = new JSONObject();

    if (type == null || type.equals("organiser")) {
      response.appendField(
          "Organiser",
          activityRoleRepository.findMembers(
              activityId, ActivityRoleType.Organiser.ordinal(), limit, offset));
    }
    if (type == null || type.equals("participant")) {
      response.appendField(
          "Participant",
          activityRoleRepository.findMembers(
              activityId, ActivityRoleType.Participant.ordinal(), limit, offset));
    }
    if (type == null || type.equals("accessor")) {
      response.appendField(
          "Access",
          activityRoleRepository.findMembers(
              activityId, ActivityRoleType.Access.ordinal(), limit, offset));
    }
    if (type == null || type.equals("follower")) {
      response.appendField(
          "Follower",
          activityRoleRepository.findMembers(
              activityId, ActivityRoleType.Follower.ordinal(), limit, offset));
    }
    if (type == null || type.equals("creator")) {
      response.appendField(
          "Creator",
          activityRoleRepository.findMembers(
              activityId, ActivityRoleType.Creator.ordinal(), limit, offset));
    }
    return new ResponseEntity(response, HttpStatus.OK);
  }

  /**
   * Retrieves count of all types of members accociated with an activity
   *
   * @param activityId id of the activity
   * @param session    current http session
   * @return Count of users accociated with on an activity
   */
  @GetMapping("activities/{activityId}/membercount")
  public ResponseEntity<String> getConnectedUsersCount(
      @PathVariable int activityId, HttpSession session) {

    Optional<Activity> activity = activityRepository.findById(activityId);
    if (activity.isEmpty()) {
      return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
    }
    JSONObject response = new JSONObject();
    response.appendField(
        "participants",
        activityRoleRepository.findMembersCount(
            activityId, ActivityRoleType.Participant.ordinal()));
    response.appendField(
        "creators",
        activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Creator.ordinal()));
    response.appendField(
        "organisers",
        activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Organiser.ordinal()));
    response.appendField(
        "followers",
        activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Follower.ordinal()));
    response.appendField(
        "accessors",
        activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Access.ordinal()));
    return new ResponseEntity(response, HttpStatus.OK);
  }

  /**
   * Checks if users is a participant of an activity. There are three states which need
   *
   * @param profileId  user in activity
   * @param activityId activity user may participate in
   * @param session    current http session
   * @return boolean if user is a participant
   */
  @GetMapping("profiles/{profileId}/subscriptions/activities/{activityId}/participate")
  public ResponseEntity<String> getIsUserParticipantInActivity(
      @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
    // Check if user has access to view activity info
    ResponseEntity<String> authorisedResponse = UserSecurityService
        .checkActivityViewingPermission(activityId, session,
            activityRepository, activityRoleRepository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    // Check if user is participant
    JSONObject response = new JSONObject();

    ActivityRole userRole = activityRoleRepository
        .findByProfile_IdAndActivity_Id(profileId, activityId);

    if (userRole != null && userRole.getActivityRoleType() == ActivityRoleType.Participant) {
      response.appendField("participant", "true");
      return new ResponseEntity(response, HttpStatus.OK);
    }
    if (userRole != null && (userRole.getActivityRoleType() == ActivityRoleType.Creator
        || userRole.getActivityRoleType() == ActivityRoleType.Organiser)) {
      response.appendField("participant", "null");
      return new ResponseEntity(response, HttpStatus.OK);
    }
    response.appendField("participant", "false");
    return new ResponseEntity(response, HttpStatus.OK);
  }

  /**
   * Sets a user to a participant in an activity if they have correct permissions and are not a
   * creator or organiser.
   * @param profileId user to set to a participant
   * @param activityId activity to set the role for
   * @param session current http session
   * @return 200 on success otherwise 4xx error for unauthorised or activity not found.
   */
  @PostMapping("profiles/{profileId}/subscriptions/activities/{activityId}/participate")
  public ResponseEntity<String> setUserToParticipantInActivity(
          @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
    // Check if user has access to activity info
    ResponseEntity<String> authorisedResponse = UserSecurityService.checkActivityViewingPermission(activityId, session,
            activityRepository, activityRoleRepository);
    if(authorisedResponse != null) {
      return authorisedResponse;
    }

    // Get Activity
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if(optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity not found", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();

    // Get Profile
    Profile profile = profileRepository.findById(profileId);
    if(profile == null) {
      return new ResponseEntity<>("Profile not found", HttpStatus.NOT_FOUND);
    }

    // Check if user is allowed to set to participant, either admin, creator or logged user
    // The session id is implicit from previous check
    Integer sessionId = (Integer) session.getAttribute("id");
    if(!(UserSecurityService.checkIsAdminOrCreator(sessionId, profileId)
            || sessionId.equals(activity.getProfile().getId()))) {
      return new ResponseEntity<>("Not allowed to modify another users role", HttpStatus.UNAUTHORIZED);
    }

    // Check users current role is not Creator or Organiser
    ActivityRole userRole = activityRoleRepository.findByProfile_IdAndActivity_Id(profileId, activityId);
    if(userRole != null && (userRole.getActivityRoleType() == ActivityRoleType.Creator
            || userRole.getActivityRoleType() == ActivityRoleType.Organiser))
    {
      return new ResponseEntity<>(String.format("A %s cannot be a participant", userRole.toString()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Set user as a participant
    if(userRole != null) {
      userRole.setActivityRoleType(ActivityRoleType.Participant);
      activityRoleRepository.save(userRole);
    } else {
      ActivityRole newRole = new ActivityRole();
      newRole.setActivity(activity);
      newRole.setProfile(profileRepository.findById(profileId));
      newRole.setActivityRoleType(ActivityRoleType.Participant);
      activityRoleRepository.save(newRole);
    }

    // Subscribe user to activity
    boolean subscribed = !subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty();
    if(!subscribed) {
      SubscriptionHistory subscription = new SubscriptionHistory(profile, activity, SubscribeMethod.SELF);
      subscriptionHistoryRepository.save(subscription);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }
  /**
   * Demotes a user in an activity to follower if participant. Otherwise return 4xx error.
   * @param profileId Profile to demote
   * @param activityId Activity to demote user in
   * @param session current http session
   * @return 200 on success otherwise 4xx. 404 on user or activity not found. 401 on unauthorized.
   */
  @DeleteMapping("profiles/{profileId}/subscriptions/activities/{activityId}/participate")
  public ResponseEntity<String> removeUserAsParticipantInActivity(
          @PathVariable int profileId, @PathVariable int activityId, HttpSession session) {
    // Check if user has access to view activity info
    ResponseEntity<String> authorisedResponse = UserSecurityService.checkActivityViewingPermission(activityId, session,
            activityRepository, activityRoleRepository);
    if(authorisedResponse != null) {
      return authorisedResponse;
    }

    // Get Activity
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if(optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity not found", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();


    // Check if user is allowed to set to participant, either admin, creator or logged user
    // The session id is implicit from previous check
    Integer sessionId = (Integer) session.getAttribute("id");
    if(!(UserSecurityService.checkIsAdminOrCreator(sessionId, profileId)
            || sessionId.equals(activity.getProfile().getId()))) {
      return new ResponseEntity<>("Not allowed to modify another users role", HttpStatus.UNAUTHORIZED);
    }

    ActivityRole role = activityRoleRepository.findByProfile_IdAndActivity_Id(profileId, activityId);

    // Check user has a role in the activity
    if (role == null || !role.getActivityRoleType().equals(ActivityRoleType.Participant)) {
      return new ResponseEntity<>("User is not a participant", HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Demote user to profile to follower
    role.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(role);

    return new ResponseEntity("Success", HttpStatus.OK);
  }

  /**
   * Gets the count of non archived activities a user follows
   * @param profileId profile ID of user who the count is for
   * @param httpSession HttpSession object
   * @return Bad Request if not logged in or URL id dosent match session ID, else OK with the count
   */
  @GetMapping("profiles/{profileId}/subscriptions/activities/following")
  public ResponseEntity getNumberOfActivitiesUserFollows(
      @PathVariable int profileId,
      HttpSession httpSession) {
    Object id = httpSession.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    }

    Profile profile = profileRepository.findById(profileId);
    if (profile.getId() != Integer.parseInt(id.toString())) {
      return new ResponseEntity("You can only view your activities followed", HttpStatus.BAD_REQUEST);
    }

    Long count = subscriptionHistoryRepository.findUsersActiveFollowings(profileId);
    return new ResponseEntity(count, HttpStatus.OK);
  }
}
