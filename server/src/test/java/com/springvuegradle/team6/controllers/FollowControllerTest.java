package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.*;
import io.cucumber.core.internal.gherkin.deps.com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class FollowControllerTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired private ProfileRepository profileRepository;

    @Autowired private ActivityRoleRepository activityRoleRepository;

    @Autowired private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private int id;

    private int activityId;

    private MockHttpSession session;

    @BeforeEach
    void setup() throws Exception {
        session = new MockHttpSession();
        String profileJson =
                "{\n" +
                        "  \"lastname\": \"Pocket\",\n" +
                        "  \"firstname\": \"Poly\",\n" +
                        "  \"middlename\": \"Michelle\",\n" +
                        "  \"nickname\": \"Pino\",\n" +
                        "  \"primary_email\": \"poly@pocket.com\",\n" +
                        "  \"password\": \"Password1\",\n" +
                        "  \"bio\": \"Poly Pocket is so tiny.\",\n" +
                        "  \"date_of_birth\": \"2000-11-11\",\n" +
                        "  \"gender\": \"female\"\n}";

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
    }

    @Test
    void isSubscribedToActivityFalse() throws Exception {
        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
    }

    @Test
    void subscribeThenGetIsSubscribed() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
    }

    @Test
    void deleteSubsriptionFromActivity() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
    }

  @Test
  void deleteSubsriptionThatDosentExist() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/" + id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
    }

  @Test
  void subscribeToSameActivityTwice() throws Exception {
      mvc.perform(MockMvcRequestBuilders
              .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session)
      )
              .andExpect(status().is2xxSuccessful());
      mvc.perform(MockMvcRequestBuilders
              .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session)
      )
              .andExpect(status().is4xxClientError());
  }

    @Test
    void addSubscriptionWhenNotLoggedInToSpecifiedId() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/"+ 123345667 + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteSubscriptionWhenNotLoggedInToSpecifiedId() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/"+ 123345667 + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is4xxClientError());
    }
    @Test
    void deleteSubscriptionOnNonExistingActivity() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/"+ id + "/subscriptions/activities/" + 123498763)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is4xxClientError());
    }
    @Test
    void addSubsciptionAfterDeletingSameSubscription() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
    }

    @Test
    void getActivityRolesNoMemebersAccociated() throws Exception {
        String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/activities/" + activityId + "/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[],\"Participant\":[],\"Access\":[],\"Follower\":[],\"Creator\":[]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getActivityRolesWithOneOrganiser() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Organiser);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}],\"Participant\":[],\"Access\":[],\"Follower\":[],\"Creator\":[]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }
    @Test
    void getActivityRolesWithTwoMemeberRoles() throws Exception {
        Activity activity = activityRepository.getOne(activityId);
        Profile profile = profileRepository.getOne(id);
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profile);
        activityRole.setActivity(activity);
        activityRole.setActivityRoleType(ActivityRoleType.Participant);
        activityRoleRepository.save(activityRole);

        ActivityRole activityRole2 = new ActivityRole();
        activityRole2.setProfile(profile);
        activityRole2.setActivity(activity);
        activityRole2.setActivityRoleType(ActivityRoleType.Access);
        activityRoleRepository.save(activityRole2);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[],\"Participant\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}],\"Access\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}],\"Follower\":[],\"Creator\":[]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }
    @Test
    void getActivityRolesWithThreeMemberRoles() throws Exception {
        Activity activity = activityRepository.getOne(activityId);
        Profile profile = profileRepository.getOne(id);
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profile);
        activityRole.setActivity(activity);
        activityRole.setActivityRoleType(ActivityRoleType.Creator);
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
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[],\"Participant\":[],\"Access\":[{\"PROFILE_ID\":6," +
                "\"FULL_NAME\":\"Poly Pocket\"}],\"Follower\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]," +
                "\"Creator\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }
    @Test
    void getActivityRolesMemberInAllRoles() throws Exception {
        Activity activity = activityRepository.getOne(activityId);
        Profile profile = profileRepository.getOne(id);

        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profile);
        activityRole.setActivity(activity);
        activityRole.setActivityRoleType(ActivityRoleType.Creator);
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

        ActivityRole activityRole5 = new ActivityRole();
        activityRole5.setProfile(profile);
        activityRole5.setActivity(activity);
        activityRole5.setActivityRoleType(ActivityRoleType.Organiser);
        activityRoleRepository.save(activityRole5);
        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]," +
                "\"Participant\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}],\"Access\":[{\"PROFILE_ID\":6," +
                "\"FULL_NAME\":\"Poly Pocket\"}],\"Follower\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]," +
                "\"Creator\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getActivityCounts() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Organiser);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/membercount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"organisers\":1,\"followers\":0,\"accessors\":0,\"creators\":0,\"participants\":0}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getActivityMultipleMembers() throws Exception {
        Activity activity = activityRepository.getOne(activityId);
        Profile profile = profileRepository.getOne(id);
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profile);
        activityRole.setActivity(activity);
        activityRole.setActivityRoleType(ActivityRoleType.Creator);
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
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/membercount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"organisers\":0,\"followers\":1,\"accessors\":1,\"creators\":1,\"participants\":0}");
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
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?limit=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[],\"Participant\":[],\"Access\":[],\"Follower\":[],\"Creator\":[]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void checkIncorrectTypeValue() throws Exception {
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=thisIsAnIncorrectValue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is4xxClientError());
    }
    @Test
    void getJustParticipants() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Participant);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Participant\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }
    @Test
    void getJustAccessors() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Access);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=accessor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Access\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getJustFollowers() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Follower);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=follower")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Follower\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getJustCreators() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Creator);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=creator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Creator\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }

    @Test
    void getJustOrganisers() throws Exception {
        ActivityRole activityRole = new ActivityRole();
        activityRole.setProfile(profileRepository.getOne(id));
        activityRole.setActivity(activityRepository.getOne(activityId));
        activityRole.setActivityRoleType(ActivityRoleType.Organiser);
        activityRoleRepository.save(activityRole);

        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=organiser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Organiser\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(obj.toString(), answer.toString());
    }
    public void createData() throws Exception {
        int profile2Id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        for (int i = 0 ; i < 5 ; i++) {
            ActivityRole activityRole = TestDataGenerator.createActivityMemberType(mvc, mapper, session,
                    ActivityRoleType.Participant, activityRepository.getOne(activityId), profileRepository.getOne(id));
            activityRoleRepository.save(activityRole);

            ActivityRole activityRole2 = TestDataGenerator.createActivityMemberType(mvc, mapper, session,
                    ActivityRoleType.Participant, activityRepository.getOne(activityId), profileRepository.getOne(profile2Id));
            activityRoleRepository.save(activityRole2);

            ActivityRole activityRole3 = TestDataGenerator.createActivityMemberType(mvc, mapper, session,
                    ActivityRoleType.Participant, activityRepository.getOne(activityId), profileRepository.getOne(profile2Id));
            activityRoleRepository.save(activityRole3);
        }
    }
    @Test
    void testPaginationThreeElements() throws Exception {
        createData();
        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=participant&offset=4&limit=3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Participant\":[{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"},{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"},{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(answer.toString(), obj.toString());
    }
    @Test
    void testPaginationNoValuesShouldDefaultToFirstTen() throws Exception {
        createData();
        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);

        JSONObject answer = new JSONObject("{\"Participant\":[{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}," +
                "{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"},{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"}," +
                "{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"},{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"}," +
                "{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"},{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}," +
                "{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"},{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"}," +
                "{\"PROFILE_ID\":6,\"FULL_NAME\":\"Poly Pocket\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(answer.toString(), obj.toString());
    }
    @Test
    void testPaginationPastMaxValue() throws Exception {
        createData();
        String response =
                mvc.perform(MockMvcRequestBuilders
                        .get("/activities/" + activityId + "/members?type=participant&limit=200&offset=13")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                )
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        JSONObject obj = new JSONObject(response);
        JSONObject answer = new JSONObject("{\"Participant\":[{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"}," +
                "{\"PROFILE_ID\":9,\"FULL_NAME\":\"John Doe\"}]}");
        org.junit.jupiter.api.Assertions.assertEquals(answer.toString(), obj.toString());
    }
}
