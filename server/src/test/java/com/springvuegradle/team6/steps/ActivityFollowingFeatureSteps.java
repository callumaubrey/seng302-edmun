package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivityFollowingFeatureSteps {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private MockMvc mvc;

  private ResultActions mvcResponse;

  private MockHttpSession session;

  private String jsonString;

  private String profileId;

  private String activityId;


  @Given("I create a user with email {string} and password {string}")
  public void i_registered_a_user_with_email_and_password(String email, String password) throws Exception {
    jsonString =
            "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \"" + email + "\",\r\n  \"password\": \"" + password + "\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
    String createProfileUrl = "/profiles";
    profileId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createProfileUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }


  @Given("I log in with email {string} and password {string}")
  public void i_log_in_as_a_user_with_email_and_password(String email, String password) throws Exception {
    session = new MockHttpSession();
    jsonString =
            "{\n"
                    + "\t\"email\": \""
                    + email
                    + "\",\n"
                    + "\t\"password\": \""
                    + password
                    + "\"\n"
                    + "}";
    String loginUrl = "/login";
    mvcResponse = mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session));
  }

  @Given("I create an activity {string}")
  public void i_create_an_activity(String activityName) throws Exception {
    String jsonString;
    jsonString = "{\n" +
            "  \"activity_name\": \"" + activityName + "\",\n" +
            "  \"description\": \"tramping iz fun\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Hike\",\n" +
            "    \"Bike\"\n" +
            "  ],\n" +
            "  \"continuous\": true\n" +
            "}";
    String createActivityUrl = "/profiles/" + profileId + "/activities";
    activityId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createActivityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

  }

  @Then("User with email {string} home feed shows")
  public void user_with_email_home_feed_shows(
      String emailStr, io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<String> activityNames = new ArrayList<>();
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      String activityName = activityMapping.get("Activity Name");
      activityNames.add(activityName);
    }

    String url = "/feed/homefeed/" + profileId;
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));

    String response = mvcResponse.andReturn().getResponse().getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(activityNames.size(), arr.length());

    List<String> responseActivityNames = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      int activityId = arr.getJSONObject(i).getInt("activity_id");
      String name = activityRepository.findById(activityId).get().getActivityName();
      responseActivityNames.add(name);
    }
    org.junit.jupiter.api.Assertions.assertTrue(responseActivityNames.containsAll(activityNames));
  }

  @When("User follows the activity {string}")
  public void user_follows_the_activity(String activityName) throws Exception{
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
  }
  @Then("User is a follower of {string}")
  public void user_is_a_follower_of(String activityName) throws Exception{
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @When("User unfollows the activity {string}")
  public void userUnfollowsTheActivity(String activityName) throws Exception {
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
  }

  @Then("User is not a follower of {string}")
  public void userIsNotAFollowerOf(String activityName) throws Exception {
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/profiles/"+ profileId + "/subscriptions/activities/" + activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
  }
}
