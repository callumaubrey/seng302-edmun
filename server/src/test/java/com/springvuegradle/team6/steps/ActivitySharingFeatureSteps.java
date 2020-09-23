package com.springvuegradle.team6.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class ActivitySharingFeatureSteps {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired
  private ActivityRoleRepository activityRoleRepository;

  @Autowired private MockMvc mvc;

  @Autowired
  private LoginSteps loginSteps;

  private ResultActions mvcResponse;

  private String jsonString;

  private String activityId;

  @Given("There is an activity {string} with {string} access")
  public void there_is_an_activity_with_access(String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"activity_name\": \""
                    + activityName
                    + "\",\n"
                    + "  \"description\": \"tramping iz fun\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Hike\",\n"
                    + "    \"Bike\"\n"
                    + "  ],\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\",\n"
                    + "  \"continuous\": true"
                    + "}";
    String createActivityUrl = "/profiles/" + loginSteps.profileId + "/activities";
    activityId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createActivityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }

  @Given("user {string} creates activity {string} with {string} access")
  public void user_creates_activity_with_access(String email, String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"activity_name\": \""
                    + activityName
                    + "\",\n"
                    + "  \"description\": \"tramping iz fun\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Hike\",\n"
                    + "    \"Bike\"\n"
                    + "  ],\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\",\n"
                    + "  \"continuous\": true"
                    + "}";
    String createActivityUrl = "/profiles/" + loginSteps.profileId + "/activities";
    activityId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createActivityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }

  @When("user {string} sets activity {string} to {string} access")
  public void user_sets_activity_to_access(String email, String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\""
                    + "}";
    String changeVisibilityUrl = "/profiles/" + loginSteps.profileId + "/activities/" + activityId + "/visibility";
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.put(changeVisibilityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
  }

  @When("user {string} views {string}")
  public void user_views(String email, String activityName) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.post(loginUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
    .andExpect(status().isOk());

    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get("/activities/" + activityId)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
  }

  @Then("I have response status {string} on activity {string}")
  public void i_have_response_status_on_activity(String statusCode, String activityName) throws Exception {
    mvcResponse.andExpect(status().is(Integer.parseInt(statusCode)));
  }

  @Given("user {string} has role {string} on activity {string}")
  public void user_has_role_on_activity(String email, String role, String activityName)
      throws Exception {
    Activity activity = activityRepository.findByActivityName(activityName);
    Profile profile = profileRepository.findByEmails_address(email);

    // Creator assigns user 'Access' role
    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"" + email + "\",\n"
            + "  \"role\": \"" + role + "\"\n"
            + "  }\n"
            + "}";

    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.put(
                "/profiles/"
                    + loginSteps.profileId
                    + "/activities/"
                    + activity.getId()
                    + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session));

    List<ActivityRole> activityRole = activityRoleRepository
        .findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    ActivityRole activityRoleFound = activityRole.get(0);
    Assert.assertEquals(role, activityRoleFound.getActivityRoleType().toString());
  }

  @When("user {string} adds user roles")
  public void user_adds_user_roles(String string, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
    jsonString =
        "{\n"
            + "\t\"email\": \""
            + string
            + "\",\n"
            + "\t\"password\": \""
            + password
            + "\"\n"
            + "}";
    String loginUrl = "/login";
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(string);

    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      String email = activityMapping.get("UserEmail");
      String role = activityMapping.get("Role");

      String jsonString =
          "{\n"
              + "  \"subscriber\": { \n"
              + "  \"email\": \"" + email + "\",\n"
              + "  \"role\": \"" + role +  "\"\n"
              + "  }\n"
              + "}";

      mvcResponse = mvc.perform(
          MockMvcRequestBuilders.put(
              "/profiles/" + profile.getId() + "/activities/" + activityId + "/subscriber")
              .content(jsonString)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session));
    }
  }

  @Then("user {string} gets users of role {string} for activity {string} should show")
  public void user_gets_users_of_role_for_activity_should_show(
      String email,
      String role,
      String activityName,
      io.cucumber.datatable.DataTable dataTable) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(email);
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/" + activityId + "/members")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    int tableSize = dataTable.asList().size();

    JSONObject obj = new JSONObject(response);
    JSONArray participants = obj.getJSONArray("Participant");
    if (role.equals("participant")) {
      Assert.assertEquals(tableSize, participants.length());
    }
    JSONArray organisers = obj.getJSONArray("Organiser");
    if (role.equals("organiser")) {
      Assert.assertEquals(tableSize, organisers.length());
    }
    JSONArray creators = obj.getJSONArray("Creator");
    if (role.equals("creator")) {
      Assert.assertEquals(tableSize, creators.length());
    }
    JSONArray followers = obj.getJSONArray("Follower");
    if (role.equals("follower")) {
      Assert.assertEquals(tableSize, followers.length());
    }
    JSONArray accesses = obj.getJSONArray("Access");
    if (role.equals("access")) {
      Assert.assertEquals(tableSize, accesses.length());
    }
  }

  @When("user {string} removes user")
  public void user_removes_user(String email, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(email);

    String userEmail = "";
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      userEmail = activityMapping.get("UserEmail");
    }

    String jsonString = "{\n" + "\"email\": \"" + userEmail + "\"\n" + "}";

    mvcResponse = mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/" + profile.getId() + "/activities/" + activityId + "/subscriber")
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @When("user {string} removes user roles")
  public void user_removes_user_roles(String email, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(email);

    String userEmail = "";
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      userEmail = activityMapping.get("UserEmail");
    }

    String jsonString = "{\n" + "\"email\": \"" + userEmail + "\"\n" + "}";

    mvcResponse = mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/" + profile.getId() + "/activities/" + activityId + "/subscriber")
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @When("user {string} changes user roles")
  public void user_changes_user_roles(String string, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String email = "";
    String oldRole = "";
    String newRole = "";
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      email = activityMapping.get("UserEmail");
      oldRole = activityMapping.get("Role");
      newRole = activityMapping.get("NewRole");
    }

    String jsonString =
        "{\n"
            + "  \"subscriber\": { \n"
            + "  \"email\": \"" + email + "\",\n"
            + "  \"role\": \"" + newRole +"\"\n"
            + "  }\n"
            + "}";

    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/" + loginSteps.profileId + "/activities/" + activityId + "/subscriber")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session));
  }

  @When("user {string} deletes activity {string}")
  public void user_deletes_activity(String email, String activityName) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(email);
    Activity activity = activityRepository.findByActivityName(activityName);

    mvcResponse = mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}", profile.getId(), activity.getId())
            .session(session));
  }

  @Then("activity {string} exists")
  public void activity_exists(String string) {
    Activity activity = activityRepository.findByActivityName(string);
    Assert.assertEquals(string, activity.getActivityName());
  }

  @When("the user {string} edits activity {string} description to {string}")
  public void the_user_edits_activity_description_to(
      String email,
      String activityName,
      String description) throws Exception {
    String password = "Password1";
    MockHttpSession session = new MockHttpSession();
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
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

    Profile profile = profileRepository.findByEmails_address(email);
    Activity activity = activityRepository.findByActivityName(activityName);
    jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + activityName
            + "\",\n"
            + "  \"description\": \"" + description  + "\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"visibility\": \""
            + activity.getVisibilityType()
            + "\",\n"
            + "  \"continuous\": true"
            + "}";
    String editActivityUrl = "/profiles/" + profile.getId() + "/activities/" + activity.getId();
    mvcResponse = mvc.perform(
        MockMvcRequestBuilders.put(editActivityUrl)
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));
  }

  @Then("the activity {string} should have description {string}")
  public void the_activity_should_have_description(String activityName, String description) {
    Activity activity = activityRepository.findByActivityName(activityName);
    Assert.assertEquals(description, activity.getDescription());
  }
}
