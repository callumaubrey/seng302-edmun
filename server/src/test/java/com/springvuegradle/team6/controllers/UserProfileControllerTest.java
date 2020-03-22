package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.EditPasswordRequest;
import com.springvuegradle.team6.requests.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserProfileControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private CreateProfileRequest getDummyProfile() {
        CreateProfileRequest validRequest = new CreateProfileRequest();
        validRequest.firstname = "John";
        validRequest.middlename = "S";
        validRequest.lastname = "Doe";
        validRequest.nickname = "Big J";
        validRequest.bio = "Just another plain jane";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "1999-12-20";
        validRequest.gender = "male";
        validRequest.fitness = 0;
        return validRequest;
    }

    @Test
    void createProfileEmptyFailCases() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();

        // Empty test
        mvc.perform(
                post(createProfileUrl)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is4xxClientError());

        // Success Case
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        // Empty lastname
        validRequest.lastname = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Empty firstname
        validRequest.lastname = "Doe";
        validRequest.firstname = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Empty primary email
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Empty password
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Empty date of birth
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Empty Gender
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "12-03-2000";
        validRequest.gender = "male";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileEmailExists() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();

        // Success Case
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        // Email already exists because we just added it
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidPassword() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.password = "jacky";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidDateFormat() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.dob = "1985/12/20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidDateRange() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.dob = "2021-12-20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        request.email = "anotheremail@gmail.com";
        request.dob = "1800-12-20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidEmail() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.email = "test.com";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidNames() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        // Invalid nickname
        request.nickname = "#mynickname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Invalid first name
        request.nickname = "nick";
        request.firstname = "#firstname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Invalid last name
        request.firstname = "firstname";
        request.lastname = "@lastname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void editPasswordFailCases() throws Exception {
        TestDataGenerator.createJohnDoeUser(mvc, mapper);

        String editPassUrl = "/profiles/editpassword";
        EditPasswordRequest request = new EditPasswordRequest();
        MockHttpSession session = new MockHttpSession();

        // Try a case when not logged in...
        request.id = 1;
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());

        // Login
        request.id = TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        // Passwords don't match
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1235";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        // Old password isn't correct
        request.oldpassword = "SuperSecurePassword12";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        // Password dosent have uppercase
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "supersecurepassword1234";
        request.repeatedpassword = "supersecurepassword1234";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        // Password dosent have number
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "supersecurepassworD";
        request.repeatedpassword = "supersecurepassworD";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        // Everything is correct
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                patch(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editPassports() throws Exception {
        MockHttpSession session = new MockHttpSession();
        TestDataGenerator.createJohnDoeUser(mvc, mapper);
        int id = TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d";
        updateUrl = String.format(updateUrl, id);

        EditProfileRequest request = new EditProfileRequest();
        request.passports = new ArrayList<>();
        request.passports.add("NZD");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editEmails() throws Exception {
        MockHttpSession session = new MockHttpSession();
        TestDataGenerator.createJohnDoeUser(mvc, mapper);
        int id = TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d";
        updateUrl = String.format(updateUrl, id);

        // Sets Primary email and
        EditProfileRequest request = new EditProfileRequest();
        request.primaryemail = "valid@email.com";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworld@gmail.com");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());

        // Bad primary
        request = new EditProfileRequest();
        request.primaryemail = "validemailcom";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworld@gmail.com");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());

        //  Bad Additional
        request = new EditProfileRequest();
        request.primaryemail = "valid@email.com";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworldgmailcom");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }
}