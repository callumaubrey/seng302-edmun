package com.springvuegradle.team6.steps;

import io.cucumber.java.en.Given;
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

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivitySharingFeatureSteps {
    private String activityId;
    private String profileId;
    private MockHttpSession session;

    @Autowired
    private MockMvc mvc;

    @Given("I log in with email {string} and password {string}")
    public void i_log_in_with_email_and_password(String email, String password) throws Exception {
        String jsonString =
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
        mvc.perform(
                MockMvcRequestBuilders.post(loginUrl)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());
    }


    @Given("There is an activity {string} with {string} access")
    public void there_is_an_activity_with_access(String activityName, String visibilityType) throws Exception {
        // Create another user
        String jsonString =
                "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \"creator@email.com\",\r\n  \"password\": \"SomePassword1\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
        String createProfileUrl = "/profiles";
        String creatorId =
                mvc.perform(
                        MockMvcRequestBuilders.post(createProfileUrl)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        // Log in as the other user
        MockHttpSession creatorSession = new MockHttpSession();
        jsonString =
                "{\n"
                        + "\t\"email\": \""
                        + "creator@email.com"
                        + "\",\n"
                        + "\t\"password\": \""
                        + "SomePassword1"
                        + "\"\n"
                        + "}";
        String loginUrl = "/login";
        mvc.perform(
                MockMvcRequestBuilders.post(loginUrl)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(creatorSession))
                .andExpect(status().isOk());

        // Create activity as the other user
        jsonString =
                "{\n" +
                        "  \"activity_name\": \"" + activityName + "\",\n" +
                        "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                        "  \"activity_type\":[ \n" +
                        "    \"Walk\"\n" +
                        "  ],\n" +
                        "  \"continuous\": false,\n" +
                        "  \"start_time\": \"2030-04-28T15:50:41+1300\",\n" +
                        "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n" +
                        "  \"visibility\": \"" + visibilityType + "\"\n" +
                        "}";

        ResultActions responseString =
                mvc.perform(
                        MockMvcRequestBuilders.post("/profiles/{profileId}/activities", creatorId)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(creatorSession));
        responseString.andExpect(status().isCreated());
        activityId = responseString.andReturn().getResponse().getContentAsString();
    }

    @Given("user {string} creates activity {string} with {string} access")
    public void user_creates_activity_with_access(String email, String activityName, String visibilityType) throws Exception {
        String jsonString =
                "{\n" +
                        "  \"activity_name\": \"" + activityName + "\",\n" +
                        "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                        "  \"activity_type\":[ \n" +
                        "    \"Walk\"\n" +
                        "  ],\n" +
                        "  \"continuous\": false,\n" +
                        "  \"start_time\": \"2030-04-28T15:50:41+1300\",\n" +
                        "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n" +
                        "  \"visibility\": \"" + visibilityType + "\"\n" +
                        "}";

        ResultActions responseString =
                mvc.perform(
                        MockMvcRequestBuilders.post("/profiles/{profileId}/activities", profileId)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session));
        responseString.andExpect(status().isCreated());
        activityId = responseString.andReturn().getResponse().getContentAsString();
    }


}
