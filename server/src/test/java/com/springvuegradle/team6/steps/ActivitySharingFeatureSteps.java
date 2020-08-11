package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
public class ActivitySharingFeatureSteps {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

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
  public void user_has_role_on_activity(String string, String string2, String string3) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @When("user {string} adds user roles")
  public void user_adds_user_roles(String string, io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    throw new io.cucumber.java.PendingException();
  }

  @Then("user {string} gets users of role {string} for activity {string} should show")
  public void user_gets_users_of_role_for_activity_should_show(String string, String string2, String string3, io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    throw new io.cucumber.java.PendingException();
  }

  @When("user {string} removes user")
  public void user_removes_user(String string, io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    throw new io.cucumber.java.PendingException();
  }

  @When("user {string} removes user roles")
  public void user_removes_user_roles(String string, io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    throw new io.cucumber.java.PendingException();
  }

  @When("user {string} changes user roles")
  public void user_changes_user_roles(String string, io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    throw new io.cucumber.java.PendingException();
  }

  @When("user {string} deletes activity {string}")
  public void user_deletes_activity(String string, String string2) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("activity {string} exists")
  public void activity_exists(String string) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @When("the user {string} edits activity {string} description to {string}")
  public void the_user_edits_activity_description_to(String string, String string2, String string3) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("the activity {string} should have description {string}")
  public void the_activity_should_have_description(String string, String string2) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}
