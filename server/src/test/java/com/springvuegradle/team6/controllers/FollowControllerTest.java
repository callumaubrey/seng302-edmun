package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import io.cucumber.core.internal.gherkin.deps.com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

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

    @Autowired
    private ActivityRoleRepository activityRoleRepository;

    @Autowired private MockMvc mvc;

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
  void deleteSubscriptionThatDoesntExist() throws Exception {
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
    void addActivityRoleAccess() throws Exception {
        Profile profile1 = new Profile();
        profile1.setFirstname("Johnny");
        profile1.setLastname("Dong");
        Set<Email> email1 = new HashSet<Email>();
        email1.add(new Email("example@email.com"));
        profile1.setEmails(email1);
        profileRepository.save(profile1);

        String jsonString = "{\n" +
                "  \"subscriber\": { \n" +
                "  \"email\": \"example@email.com\"\n" +
                "  \"role\": \"access\"\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/"+ id + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        String jsonString2 = "{\n" +
                "  \"email\": example@email.com\n" +
                "}";

        String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/"+ id + "/activities/" + activityId + "/subscriber")
                        .content(jsonString2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject result = new JSONObject(response);
        Assert.assertSame("access", result.getString("role"));
    }

    @Test
    void deleteActivityRole() throws Exception {
        Profile profile1 = new Profile();
        profile1.setFirstname("Johnny");
        profile1.setLastname("Dong");
        Set<Email> email1 = new HashSet<Email>();
        email1.add(new Email("example@email.com"));
        profile1.setEmails(email1);
        profileRepository.save(profile1);

        String jsonString = "{\n" +
                "  \"subscriber\": { \n" +
                "  \"email\": \"example@email.com\"\n" +
                "  \"role\": \"access\"\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/"+ id + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());

        String jsonString2 = "{\n" +
                "  \"email\": example@email.com\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/"+ id + "/activities/" + activityId + "/subscriber")
                .content(jsonString2)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        )
                .andExpect(status().is2xxSuccessful());
    }

}
