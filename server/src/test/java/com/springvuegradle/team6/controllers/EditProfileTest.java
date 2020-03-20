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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EditProfileTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    private void setUp() throws Exception {
        LoginRequest loginRequestCorrectPass = new LoginRequest();
        loginRequestCorrectPass.email = "johndoe@uclive.ac.nz";
        loginRequestCorrectPass.password = "SuperSecurePassword123";
        mvc.perform(
                post("/account/login")
                        .content(mapper.writeValueAsString(loginRequestCorrectPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());


    }

    @Test
    void createEmptyProfileEdit() throws Exception {
        String editProfileUrl = "/profile/1";

        // Empty test
        mvc.perform(
                post(editProfileUrl)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        EditProfileRequest request = new EditProfileRequest();
        request.firstname = "John";
        //Change the name
        mvc.perform(
                post(editProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        request.firstname = "";
        //Empty name
        mvc.perform(
                post(editProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        request.lastname = "22";
        //Empty name
        mvc.perform(
                post(editProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());


    }
}


