package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivityHashtagFeatureSteps {
    private String jsonString;
    private MockHttpSession session;
    private ResultActions mvcResponse;
    private String profileId;
    private String activityId;


    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MockMvc mvc;

    @Given("I registered a user with email {string} and password {string}")
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

    @Given("I log in as a user with email {string} and password {string}")
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

    @Given("there are no activities in the database")
    public void there_are_no_activities_in_the_database() {
        List<Activity> activities = activityRepository.findAll();
        Assert.assertTrue(activities.size() == 0);
    }

    @When("I create an activity {string} with hashtags")
    public void i_create_an_activity_with_hashtags(String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<String> hashTags = new ArrayList<>();
        for (Map<String, String> hashTagMapping : dataTable.asMaps()) {
            String hashTag = '"' + hashTagMapping.get("Hashtag") + '"';
            hashTags.add(hashTag);
        }
        jsonString = "{\n" +
                "  \"activity_name\": \"" + activityName + "\",\n" +
                "  \"description\": \"tramping iz fun\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Hike\",\n" +
                "    \"Bike\"\n" +
                "  ],\n" +
                "  \"continuous\": true,\n" +
                "  \"hashtags\": " + hashTags + "\n" +
                "}";
        String createActivityUrl = "/profiles/" + profileId + "/activities";
        mvcResponse =
                mvc.perform(
                        MockMvcRequestBuilders.post(createActivityUrl)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session));

    }

    @Then("I have an activity {string} with hashtags")
    public void i_have_an_activity_with_hashtags(String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
        Assert.assertTrue(activityRepository.findByActivityName(activityName) != null);

        activityId = mvcResponse.andReturn().getResponse().getContentAsString();
        String getActivityUrl = "/activities/" + activityId;
        String responseString =
                mvc.perform(
                        MockMvcRequestBuilders.get(getActivityUrl)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        for (Map<String, String> hashTagMapping : dataTable.asMaps()) {
            String hashTag = hashTagMapping.get("Hashtag").toLowerCase().substring(1);
            Assert.assertTrue(responseString.contains(hashTag));
        }
    }

    @Then("I do not have an activity {string} and receive {string} status code")
    public void i_do_not_have_an_activity_and_receive_status_code(String activityName, String statusCode) throws Exception {
        Assert.assertTrue(activityRepository.findByActivityName(activityName) == null);
        mvcResponse.andExpect(status().is(Integer.parseInt(statusCode)));
    }


}
