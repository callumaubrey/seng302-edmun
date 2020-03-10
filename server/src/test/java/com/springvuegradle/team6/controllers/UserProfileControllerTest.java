package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.EditPasswordRequest;
import com.springvuegradle.team6.requests.EditProfileRequest;
import com.springvuegradle.team6.requests.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void createProfileFailCases() throws Exception {
        String create_profile_url = "/profile/";
        CreateProfileRequest valid_request = new CreateProfileRequest();
        valid_request.firstname = "John";
        valid_request.middlename = "S";
        valid_request.lastname = "Doe";
        valid_request.nickname = "Big J";
        valid_request.bio = "Just another plain jane";
        valid_request.email = "johndoe@uclive.ac.nz";
        valid_request.password = "SuperSecurePassword123";
        valid_request.dob = "12-03-2000";
        valid_request.gender = "male";
        valid_request.fitness = 0;

        // Empty test
        mvc.perform(
                post(create_profile_url)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is4xxClientError());

        // Success Case
        mvc.perform(
                post(create_profile_url)
                        .content(mapper.writeValueAsString(valid_request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }

    @Test
    void editPasswordFailCases() throws Exception {
        TestDataGenerator.createJohnDoeUser(mvc, mapper);

        String editPassUrl = "/profile/editpassword";
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
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

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
        request.id = 1;
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
        String updateUrl = "/profile/1";

        MockHttpSession session = new MockHttpSession();
        TestDataGenerator.createJohnDoeUser(mvc, mapper);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

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
}