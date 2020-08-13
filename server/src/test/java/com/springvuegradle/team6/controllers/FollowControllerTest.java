package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.SubscriptionHistoryRepository;
import com.springvuegradle.team6.requests.LoginRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class FollowControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  private int id;

  private int otherId;

  private int profile2Id;

  private int activityId;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    String profileJson =
        "{\n"
            + "  \"lastname\": \"Pocket\",\n"
            + "  \"firstname\": \"Poly\",\n"
            + "  \"middlename\": \"Michelle\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"poly@pocket.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n}";

    // Logs in and get profile Id
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    id = Integer.parseInt(body);

    String activityJson =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";
    // Creates an activity
    String activityBody =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(activityJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    activityId = Integer.parseInt(activityBody);

    profileJson =
        "{\n"
            + "  \"lastname\": \"Pocko\",\n"
            + "  \"firstname\": \"Pols\",\n"
            + "  \"middlename\": \"Michelle\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"poly2@pocket.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n}";

    // Logs in and get profile Id
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    otherId = Integer.parseInt(body);
  }

  void addRoleToActivity(Profile profile, Activity activity, ActivityRoleType roleType) {
    ActivityRole role = new ActivityRole();
    role.setProfile(profile);
    role.setActivityRoleType(roleType);
    role.setActivity(activity);
    activityRoleRepository.save(role);
  }

  @Test
  void isSubscribedToActivityFalse() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
  }

  @Test
  void subscribeThenGetIsSubscribed() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @Test
  void subscribeThenIsFollower() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    String loginJson =
        "{\n" + "  \"email\": \"poly@pocket.com\",\n" + "  \"password\": \"Password1\"\n" + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(loginJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    String subscriberJson = "{\n" + "  \"email\": \"poly2@pocket.com\"\n" + "}";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + id + "/activities/" + activityId + "/subscriber")
                    .content(subscriberJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals("follower", obj.get("role"));
  }

  @Test
  void deleteSubscriptionFromActivity() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
  }

  @Test
  void deleteSubscriptionFromRestrictedActivityAndUserRoleIsNowAccess() throws Exception {
    Profile profile = new Profile();
    profile.setFirstname("Doe");
    profile.setLastname("John");
    profile.setPassword("Password1");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile.setEmails(email2);
    profile = profileRepository.save(profile);

    String profileJson =
        "{\n" + "  \"email\": \"doe@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Optional<Activity> activity = activityRepository.findById(activityId);
    activity.get().setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity.get());
    SubscriptionHistory subscriptionHistory =
        new SubscriptionHistory(profile, activity.get(), SubscribeMethod.SELF);
    subscriptionHistoryRepository.save(subscriptionHistory);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity.get());
    activityRole.setProfile(profile);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + profile.getId() + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    List<ActivityRole> activityRoles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profile.getId());
    ActivityRole activityRoleFound = activityRoles.get(0);
    org.junit.jupiter.api.Assertions.assertEquals(
        "Access", activityRoleFound.getActivityRoleType().toString());
  }

  @Test
  void deleteSubscriptionFromPublicActivityAndUserRoleDosentExist() throws Exception {
    Profile profile = new Profile();
    profile.setFirstname("Doe");
    profile.setLastname("John");
    profile.setPassword("Password1");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile.setEmails(email2);
    profile = profileRepository.save(profile);

    String profileJson =
        "{\n" + "  \"email\": \"doe@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Optional<Activity> activity = activityRepository.findById(activityId);
    SubscriptionHistory subscriptionHistory =
        new SubscriptionHistory(profile, activity.get(), SubscribeMethod.SELF);
    subscriptionHistoryRepository.save(subscriptionHistory);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity.get());
    activityRole.setProfile(profile);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + profile.getId() + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    List<ActivityRole> activityRoles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activityId, profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, activityRoles.size());
  }

  @Test
  void deleteSubscriptionThatDoesntExist() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void subscribeToSameActivityTwice() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void addSubscriptionWhenNotLoggedInToSpecifiedId() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + 123345667 + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteSubscriptionWhenNotLoggedInToSpecifiedId() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + 123345667 + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteSubscriptionOnNonExistingActivity() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + 123498763)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void addSubsciptionAfterDeletingSameSubscription() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @Test
  void getActivityRolesNoMembersAssociatedApartFromCreator() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    JSONArray organisers = obj.getJSONArray("Organiser");
    JSONArray creators = obj.getJSONArray("Creator");
    JSONArray followers = obj.getJSONArray("Follower");
    JSONArray accesses = obj.getJSONArray("Access");
    int total =
        participants.length()
            + organisers.length()
            + creators.length()
            + followers.length()
            + accesses.length();
    org.junit.jupiter.api.Assertions.assertEquals(1, total);
  }

  @Test
  void getActivityRolesWithOneOrganiser() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    JSONArray organisers = obj.getJSONArray("Organiser");
    JSONArray creators = obj.getJSONArray("Creator");
    JSONArray followers = obj.getJSONArray("Follower");
    JSONArray accesses = obj.getJSONArray("Access");
    int total =
        participants.length()
            + organisers.length()
            + creators.length()
            + followers.length()
            + accesses.length();
    org.junit.jupiter.api.Assertions.assertEquals(2, total);
  }

  @Test
  void getActivityRolesWithTwoMemberRoles() throws Exception {
    Activity activity = activityRepository.getOne(activityId);
    Profile profile = profileRepository.getOne(id);
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profile);
    activityRole.setActivity(activity);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    JSONArray organisers = obj.getJSONArray("Organiser");
    JSONArray creators = obj.getJSONArray("Creator");
    JSONArray followers = obj.getJSONArray("Follower");
    JSONArray accesses = obj.getJSONArray("Access");
    int total =
        participants.length()
            + organisers.length()
            + creators.length()
            + followers.length()
            + accesses.length();
    org.junit.jupiter.api.Assertions.assertEquals(2, total);
  }

  @Test
  void getActivityRolesWithThreeMemberRoles() throws Exception {
    Activity activity = activityRepository.getOne(activityId);
    Profile profile = profileRepository.getOne(id);
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profile);
    activityRole.setActivity(activity);
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setProfile(profile);
    activityRole2.setActivity(activity);
    activityRole2.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    JSONArray organisers = obj.getJSONArray("Organiser");
    JSONArray creators = obj.getJSONArray("Creator");
    JSONArray followers = obj.getJSONArray("Follower");
    JSONArray accesses = obj.getJSONArray("Access");
    int total =
        participants.length()
            + organisers.length()
            + creators.length()
            + followers.length()
            + accesses.length();
    org.junit.jupiter.api.Assertions.assertEquals(3, total);
  }

  @Test
  void getActivityRolesMemberInAllRoles() throws Exception {
    Activity activity = activityRepository.getOne(activityId);
    Profile profile = profileRepository.getOne(id);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profile);
    activityRole.setActivity(activity);
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setProfile(profile);
    activityRole2.setActivity(activity);
    activityRole2.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole2);

    ActivityRole activityRole3 = new ActivityRole();
    activityRole3.setProfile(profile);
    activityRole3.setActivity(activity);
    activityRole3.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole3);

    ActivityRole activityRole4 = new ActivityRole();
    activityRole4.setProfile(profile);
    activityRole4.setActivity(activity);
    activityRole4.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole4);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    JSONArray organisers = obj.getJSONArray("Organiser");
    JSONArray creators = obj.getJSONArray("Creator");
    JSONArray followers = obj.getJSONArray("Follower");
    JSONArray accesses = obj.getJSONArray("Access");
    int total =
        participants.length()
            + organisers.length()
            + creators.length()
            + followers.length()
            + accesses.length();
    org.junit.jupiter.api.Assertions.assertEquals(5, total);
  }

  @Test
  void getActivityCounts() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/membercount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONObject answer =
        new JSONObject(
            "{\"organisers\":1,\"followers\":0,\"accessors\":0,\"creators\":1,\"participants\":0}");
    org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
  }

  @Test
  void getActivityMultipleMembersReturnCounts() throws Exception {
    Activity activity = activityRepository.getOne(activityId);
    Profile profile = profileRepository.getOne(id);
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profile);
    activityRole.setActivity(activity);
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setProfile(profile);
    activityRole2.setActivity(activity);
    activityRole2.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole2);

    ActivityRole activityRole3 = new ActivityRole();
    activityRole3.setProfile(profile);
    activityRole3.setActivity(activity);
    activityRole3.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole3);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/membercount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONObject answer =
        new JSONObject(
            "{\"organisers\":1,\"followers\":1,\"accessors\":1,\"creators\":1,\"participants\":0}");
    org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
  }

  @Test
  void getActivityRolesWithOneOrganiserWithPagination() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members?limit=0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONObject answer =
        new JSONObject(
            "{\"Organiser\":[],\"Participant\":[],\"Access\":[],\"Follower\":[],\"Creator\":[]}");
    org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
  }

  @Test
  void checkIncorrectTypeValue() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/activities/" + activityId + "/members?type=thisIsAnIncorrectValue")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testGetActivityUsersParticipantsReturnParticipants() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/" + activityId + "/members?type=participant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Participant");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void testGetActivityUsersAccessorsReturnAccessors() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    Profile profile = profileRepository.findById(id);
    activityRole.setProfile(profile);
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members?type=accessor")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Access");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivityUsersFollowerReturnFollower() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members?type=follower")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Follower");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivityUsersCreatorReturnCreator() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members?type=creator")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Creator");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivityUsersOrganisersReturnOrganisers() throws Exception {
    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(id));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activityId + "/members?type=organiser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Organiser");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  public void createData() throws Exception {
    profile2Id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    for (int i = 0; i < 5; i++) {
      ActivityRole activityRole =
          TestDataGenerator.createActivityMemberType(
              mvc,
              mapper,
              session,
              ActivityRoleType.Participant,
              activityRepository.getOne(activityId),
              profileRepository.getOne(id));
      activityRoleRepository.save(activityRole);

      ActivityRole activityRole2 =
          TestDataGenerator.createActivityMemberType(
              mvc,
              mapper,
              session,
              ActivityRoleType.Participant,
              activityRepository.getOne(activityId),
              profileRepository.getOne(profile2Id));
      activityRoleRepository.save(activityRole2);

      ActivityRole activityRole3 =
          TestDataGenerator.createActivityMemberType(
              mvc,
              mapper,
              session,
              ActivityRoleType.Participant,
              activityRepository.getOne(activityId),
              profileRepository.getOne(profile2Id));
      activityRoleRepository.save(activityRole3);
    }
  }

  @Test
  void testGetActivityUsersLimitWorksReturnOneUser() throws Exception {
    Set<Email> emails1 = new HashSet<>();
    Email email1 = new Email("polypo@pocket.com");
    email1.setPrimary(true);
    emails1.add(email1);
    Profile profile1 = new Profile();
    profile1.setFirstname("Poly");
    profile1.setLastname("Pocket");
    profile1.setEmails(emails1);
    profile1.setDob("2010-01-01");
    profile1.setPassword("Password1");
    profile1.setGender("female");
    profile1 = profileRepository.save(profile1);

    Set<Email> emails2 = new HashSet<>();
    Email email2 = new Email("david@pocket.com");
    email2.setPrimary(true);
    emails2.add(email2);
    Profile profile2 = new Profile();
    profile2.setFirstname("Davod");
    profile2.setLastname("Pocket");
    profile2.setEmails(emails2);
    profile2.setDob("2010-01-01");
    profile2.setPassword("Password1");
    profile2.setGender("male");
    profile2 = profileRepository.save(profile2);

    Activity activity = activityRepository.findById(activityId).get();
    addRoleToActivity(profile1, activity, ActivityRoleType.Participant);
    addRoleToActivity(profile2, activity, ActivityRoleType.Participant);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/" + activityId + "/members?type=participant&offset=0&limit=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(obj);
    JSONArray arr = obj.getJSONArray("Participant");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void testUnauthorisedPrivate() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityTypeByString("private");
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testAuthorisedPrivate() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setProfile(profileRepository.findById(otherId));
    activity.setVisibilityTypeByString("private");
    activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testUnauthorisedRestricted() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityTypeByString("restricted");
    activityRepository.save(activity);
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testAuthorisedRestrictedWhenCreator() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setProfile(profileRepository.findById(otherId));
    activity.setVisibilityTypeByString("restricted");
    LoginRequest login_request = new LoginRequest();
    login_request.email = "poly@pocket.com";
    login_request.password = "Password1";
    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(mapper.writeValueAsString(login_request))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testAuthorisedRestricted() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityTypeByString("restricted");
    activity.setProfile(profileRepository.findById(otherId));
    activityRepository.save(activity);
    int authorisedId = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setProfile(profileRepository.getOne(authorisedId));
    activityRole.setActivity(activityRepository.getOne(activityId));
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(roles = {"USER", "USER_ADMIN"})
  void testAdminRestricted() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityTypeByString("restricted");
    activity.setProfile(profileRepository.findById(otherId));
    activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(roles = {"USER", "USER_ADMIN"})
  void testAdminCanGetPrivateActivityMemberData() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityTypeByString("private");
    activity.setProfile(profileRepository.findById(otherId));
    activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testGetActivityUsersPaginationNoValuesShouldDefaultToFirstTen() throws Exception {
    createData();
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/" + activityId + "/members?type=participant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("Participant");
    org.junit.jupiter.api.Assertions.assertEquals(10, arr.length());
  }

  @Test
  void testGetActivityUsersOffsetWorksReturnOneUser() throws Exception {
    Set<Email> emails1 = new HashSet<>();
    Email email1 = new Email("polypo@pocket.com");
    email1.setPrimary(true);
    emails1.add(email1);
    Profile profile1 = new Profile();
    profile1.setFirstname("Poly");
    profile1.setLastname("Pocket");
    profile1.setEmails(emails1);
    profile1.setDob("2010-01-01");
    profile1.setPassword("Password1");
    profile1.setGender("female");
    profile1 = profileRepository.save(profile1);

    Set<Email> emails2 = new HashSet<>();
    Email email2 = new Email("david@pocket.com");
    email2.setPrimary(true);
    emails2.add(email2);
    Profile profile2 = new Profile();
    profile2.setFirstname("Davod");
    profile2.setLastname("Pocket");
    profile2.setEmails(emails2);
    profile2.setDob("2010-01-01");
    profile2.setPassword("Password1");
    profile2.setGender("male");
    profile2 = profileRepository.save(profile2);

    Activity activity = activityRepository.findById(activityId).get();
    addRoleToActivity(profile1, activity, ActivityRoleType.Participant);
    addRoleToActivity(profile2, activity, ActivityRoleType.Participant);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/"
                            + activityId
                            + "/members?type=participant&limit=200&offset=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, obj.length());
  }

  @Test
  void checkCreatorIsSubscribed() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + id + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @Test
  @Disabled
  void addActivityRoleNewUserIsAccess() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    String activityJson =
        "{\n"
            + "  \"activity_name\": \"asdasdasd\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    // Creates an activity
    String activityBody =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", otherId)
                    .content(activityJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    int activityId = Integer.parseInt(activityBody);

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"access\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString2 = "{\n" + "\"email\": \"example@email.com\"\n" + "}";

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                    .content(jsonString2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject result = new JSONObject(response);
    Assert.assertEquals("access", result.getString("role"));
  }

  @Test
  void addActivityRoleNewUserAboveAccessIsGivenRoleAndIsSubscribed() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example@email.com"));
    profile1.setEmails(email1);
    profile1 = profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("asdasdadsads");
    activity.setDescription("a big nice race on a lovely asda");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(otherId));
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"organiser\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString2 = "{\n" + "\"email\": \"example@email.com\"\n" + "}";

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                    .content(jsonString2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject result = new JSONObject(response);
    Assert.assertEquals("organiser", result.getString("role"));

    List<SubscriptionHistory> active =
        subscriptionHistoryRepository.findActive(activityId, profile1.getId());
    Assert.assertEquals(1, active.size());
  }

  @Test
  void addActivityRoleDemotingUserToAccessIsNewRoleAndIsUnsubscribed() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("asdasdadsads");
    activity.setDescription("a big nice race on a lovely asda");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(otherId));
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"participant\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString3 =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"access\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString3)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString2 = "{\n" + "\"email\": \"example@email.com\"\n" + "}";

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                    .content(jsonString2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Assert.assertEquals("User is not subscribed", response);

    List<SubscriptionHistory> active =
        subscriptionHistoryRepository.findActive(activityId, profile1.getId());
    Assert.assertEquals(0, active.size());
  }

  @Test
  void addActivityRolePromotingUserIsNewRoleAndIsSubscribed() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("asdasdadsads");
    activity.setDescription("a big nice race on a lovely asda");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(otherId));
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"access\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString3 =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example@email.com\",\n"
            + "  \"role\": \"participant\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                .content(jsonString3)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString2 = "{\n" + "\"email\": \"example@email.com\"\n" + "}";

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + otherId + "/activities/" + activityId + "/subscriber")
                    .content(jsonString2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject result = new JSONObject(response);
    Assert.assertEquals("participant", result.getString("role"));

    List<SubscriptionHistory> active =
        subscriptionHistoryRepository.findActive(activityId, profile1.getId());
    Assert.assertEquals(1, active.size());
  }

  @Disabled
  @Test
  void deleteActivityRole() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    String activityJson =
        "{\n"
            + "  \"activity_name\": \"asdasdasd\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    // Creates an activity
    String activityBody =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", otherId)
                    .content(activityJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    int activityId2 = Integer.parseInt(activityBody);

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"example1@email.com\",\n"
            + "  \"role\": \"access\"\n"
            + "  }\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + otherId + "/activities/" + activityId2 + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String jsonString2 = "{\n" + "\"email\": \"example1@email.com\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/activities/" + activityId2 + "/subscriber")
                .content(jsonString2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testUserIsNotParticipantInNonExistentActivity() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/" + id + "/subscriptions/activities/123000/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testNonExistentUserIsNotParticipantInActivity() throws Exception {
    String response = mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/451200/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject response_json = new JSONObject(response);

    Assert.assertFalse(response_json.getBoolean("participant"));
  }

  @Test
  void testUserIsCreatorInActivity() throws Exception {
    String response = mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/" + id + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject response_json = new JSONObject(response);
    String result = response_json.getString("participant");

    Assert.assertEquals("null", result);
  }

  @Test
  void testUserNotParticipantInActivity() throws Exception {
    String response = mvc.perform(
        MockMvcRequestBuilders.get(
            "/profiles/" + otherId + "/subscriptions/activities/" + activityId + "/participate")
            .accept(MediaType.APPLICATION_JSON)
            .session(session))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JSONObject response_json = new JSONObject(response);
    String result = response_json.getString("participant");

    Assert.assertEquals("false", result);
  }

  @Test
  void testUserIsParticipantInActivity() throws Exception {
    // Set user to participant in activity
    String response = mvc.perform(
        MockMvcRequestBuilders.get(
            "/profiles/" + otherId + "/subscriptions/activities/" + activityId + "/participate")
            .accept(MediaType.APPLICATION_JSON)
            .session(session))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JSONObject response_json = new JSONObject(response);
    String result = response_json.getString("participant");

    Assert.assertEquals("false", result);

    ActivityRole role = new ActivityRole();
    role.setActivity(activityRepository.findById(activityId).get());
    role.setProfile(profileRepository.findById(otherId));
    role.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(role);

    response = mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();

    response_json = new JSONObject(response);
    result = response_json.getString("participant");

    Assert.assertEquals("true", result);
  }

  @Test
  void testUserUpdateRoleToParticipant() throws Exception {
    // Create Activity
    Activity activity = new Activity();
    activity.setActivityName("My Fun Activity");
    activity.setDescription("Very Fun");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(id));
    activity = activityRepository.save(activity);
    int participantActivityId = activity.getId();

    // Check 200 on participate
    mvc.perform(
      MockMvcRequestBuilders.post(
              "/profiles/" + otherId + "/subscriptions/activities/" + participantActivityId + "/participate")
              .accept(MediaType.APPLICATION_JSON)
              .session(session))
      .andExpect(status().is2xxSuccessful());

    // Check user is now a participant
    String response = mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/" + otherId + "/subscriptions/activities/" + participantActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject response_json = new JSONObject(response);

    Assert.assertTrue(response_json.getBoolean("participant"));
  }

  @Test
  void testUserUpdateRoleToParticipantAndIsSubscribed() throws Exception {
    // Create Activity
    Activity activity = new Activity();
    activity.setActivityName("My Fun Activity");
    activity.setDescription("Very Fun");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(id));
    activity = activityRepository.save(activity);
    int participantActivityId = activity.getId();

    // Check 200 on participate
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + participantActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful());

    // Check user is now a participant
    String response = mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/" + otherId + "/subscriptions/activities/" + participantActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject response_json = new JSONObject(response);

    Assert.assertTrue(response_json.getBoolean("participant"));

    // Check User is subscribed
    List<SubscriptionHistory> subscription = subscriptionHistoryRepository.findActive(participantActivityId, otherId);
    Assert.assertFalse("User has no subscription to activity", subscription.isEmpty());
  }

  @Test
  void testUserRemovesParticipantRole() throws Exception {
    // Set user to participant in activity
    ActivityRole role = new ActivityRole();
    role.setActivity(activityRepository.findById(activityId).get());
    role.setProfile(profileRepository.findById(otherId));
    role.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(role);

    // Remove participant role
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful());

    // Check user is now follower
    ActivityRole newRole = activityRoleRepository.findByProfile_IdAndActivity_Id(otherId, activityId);
    Assert.assertEquals(ActivityRoleType.Follower, newRole.getActivityRoleType());
  }

  @Test
  void testUserFollowerCannotRemoveParticipantRole() throws Exception {
    // Set user to participant in activity
    ActivityRole role = new ActivityRole();
    role.setActivity(activityRepository.findById(activityId).get());
    role.setProfile(profileRepository.findById(otherId));
    role.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(role);

    // Remove participant role
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + otherId + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testUserCannotChangeOtherParticipantRole() throws Exception {
    // Create New Profile
    Profile newProfile = new Profile();
    newProfile.setFirstname("Poly");
    newProfile.setLastname("Pocket");
    Set<Email> emailSet = new HashSet<>();
    Email email = new Email("polypo@pocket.com");
    email.setPrimary(true);
    emailSet.add(email);
    newProfile.setEmails(emailSet);
    newProfile.setDob("2010-01-01");
    newProfile.setPassword("Password1");
    newProfile.setGender("female");
    newProfile = profileRepository.save(newProfile);

    // Set user to participant in activity
    ActivityRole role = new ActivityRole();
    role.setActivity(activityRepository.findById(activityId).get());
    role.setProfile(newProfile);
    role.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(role);

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + newProfile.getId() + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testUserUnauthorisedToUpdateRoleToParticipantOnRestricted() throws Exception {
    // Create Restricted Activity
    Activity activity = new Activity();
    activity.setActivityName("My Restricted Activity");
    activity.setDescription("Very Restricted");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int restrictedActivityId = activity.getId();

    // Check 4xx error on post
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + restrictedActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testUserUnauthorisedToUpdateRoleToParticipantOnPrivate() throws Exception {
    // Create Private Activity
    Activity activity = new Activity();
    activity.setActivityName("My Private Activity");
    activity.setDescription("Very Private");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Private);
    activity = activityRepository.save(activity);
    int privateActivityId = activity.getId();

    // Check 4xx error on post
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + privateActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testCreatorUserUnableToUpdateRoleToParticipant() throws Exception {
    // Check 4xx on participate
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + id + "/subscriptions/activities/" + activityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void testOrganiserUserUnableToUpdateRoleToParticipant() throws Exception {
    // Create Activity
    Activity activity = new Activity();
    activity.setActivityName("My Organised Activity");
    activity.setDescription("Very Organised");
    activity.setContinuous(true);
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Bike);
    activity.setActivityTypes(activityTypes);
    activity.setProfile(profileRepository.findById(id));
    activity = activityRepository.save(activity);
    int organisedActivityId = activity.getId();

    // Set user to organiser in activity
    ActivityRole role = new ActivityRole();
    role.setActivity(activity);
    role.setProfile(profileRepository.findById(otherId));
    role.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(role);

    // Check 4xx on Participate
    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/" + otherId + "/subscriptions/activities/" + organisedActivityId + "/participate")
                    .accept(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

}