package com.springvuegradle.team6.steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
@DirtiesContext
public class AdminFeatureSteps {
    public static String profileId;
    private String jsonString;
    private MockHttpSession session;
    private ResultActions mvcResponse;

    @Autowired
    private MockMvc mvc;


    @Given("There is a normal user with email {string} registered in the database.")
    public void there_is_a_normal_user_with_email_registered_in_the_database(String email) throws Exception {
        jsonString =
                "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \"" + email + "\",\r\n  \"password\": \"Cucumber123\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
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

    @Given("I log in as the default admin with email {string} and password {string}")
    public void i_log_in_as_the_default_admin_with_email_and_password(String email, String password)
            throws Exception {
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

    @Given("I receive response status code {int}")
    public void i_receive_response_status_code(Integer statusCode) throws Exception {
        mvcResponse.andExpect(status().is(statusCode));
    }


    @When("I edit the primary email of the user to {string}")
    public void i_edit_the_primary_email_of_the_user_to(String email) throws Exception {
        String updateEmailsUrl = "/profiles/" + profileId + "/emails";
        jsonString =
                "{\n"
                        + "  \"primary_email\": \""
                        + email
                        + "\",\n"
                        + "  \"additional_email\": [\n"
                        + "  ]\n"
                        + "}\n";
        mvc.perform(
                MockMvcRequestBuilders.put(updateEmailsUrl)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Then("primary email of the user is {string}")
    public void primary_email_of_the_user_is(String email) throws Exception {
        String profileData =
                mvc.perform(MockMvcRequestBuilders.get("/profiles/{id}", profileId).session(session))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        Assert.assertTrue(profileData.contains(email));
    }

    @When("I add {string} to the user's list of additional email")
    public void i_add_to_the_user_s_list_of_additional_email(String email) throws Exception {
        String updateEmailsUrl = "/profiles/" + profileId + "/emails";
        jsonString =
                "{\n"
                        + "  \"primary_email\": \"adminwashere@test.com\",\n"
                        + "  \"additional_email\": [\n"
                        + "    \""
                        + email
                        + "\"\n"
                        + "  ]\n"
                        + "}\n";
        mvc.perform(
                MockMvcRequestBuilders.put(updateEmailsUrl)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Then("{string} is in the user's list of additional email")
    public void is_in_the_user_s_list_of_additional_email(String email) throws Exception {
        String profileData =
                mvc.perform(MockMvcRequestBuilders.get("/profiles/{id}", profileId).session(session))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        Assert.assertTrue(profileData.contains(email));
    }

    @When("I change the user's password to {string} with old password")
    public void i_change_the_user_s_password_to_with_old_password(String newPassword) throws Exception {
        jsonString =
                "{\n"
                        + "\t\"old_password\": \"Cucumber123"
                        + "\",\n"
                        + "\t\"new_password\": \""
                        + newPassword
                        + "\",\n"
                        + "\t\"repeat_password\": \""
                        + newPassword
                        + "\"\n"
                        + "}";
        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/password", profileId)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Then("I will log in successfully as the user with the changed password {string}")
    public void i_will_log_in_successfully_as_the_user_with_the_changed_password(String password) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session)).andExpect(status().isOk());
        jsonString =
                "{\n"
                        + "\t\"email\": \"cucumber@test.com\",\n"
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


    @When("I log out of the admin account")
    public void i_log_out_of_the_admin_account() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session)).andExpect(status().isOk());
    }

    @Then("I will log in successfully with the changed password {string}")
    public void i_will_log_in_successfully_with_the_changed_password(String password)
            throws Exception {
        jsonString =
                "{\n"
                        + "\t\"email\": \"adminwashere@test.com\",\n"
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

    @Given("{string} is a registered user")
    public void is_a_registered_user(String email) throws Exception {
        jsonString =
                "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \""
                        + email
                        + "\",\r\n  \"password\": \"Cucumber123\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
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

    @When("I change the password of {string} to {string}")
    public void i_change_the_password_of_to(String email, String password) throws Exception {
        String editPasswordUrl = "/admin/profiles/" + profileId + "/password";
        String jsonString =
                "{\n"
                        + "  \"new_password\": \""
                        + password
                        + "\",\n"
                        + "  \"repeat_password\": \""
                        + password
                        + "\"\n"
                        + "}";
        mvc.perform(
                MockMvcRequestBuilders.put(editPasswordUrl)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Then("I can log into {string} with password {string}")
    public void i_can_log_into_with_password(String email, String password) throws Exception {
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
}
