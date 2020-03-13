package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.requests.LoginRequest;
import com.springvuegradle.team6.controllers.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LoginControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void loginTest() throws Exception {
        TestDataGenerator.createJohnDoeUser(mvc, mapper);

        String login_url = "/account/login/";
        LoginRequest loginRequestIncorrectPass = new LoginRequest();
        LoginRequest loginRequestCorrectPass = new LoginRequest();
        LoginRequest loginRequestIncorrectUser = new LoginRequest();


        mvc.perform(
                post(login_url)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        loginRequestCorrectPass.email = "johndoe@uclive.ac.nz";
        loginRequestCorrectPass.password = "SuperSecurePassword123";
        mvc.perform(
                post(login_url)
                        .content(mapper.writeValueAsString(loginRequestCorrectPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        loginRequestIncorrectPass.email = "johndoe@uclive.ac.nz";
        loginRequestIncorrectPass.password = "hi";
        mvc.perform(
                post(login_url)
                        .content(mapper.writeValueAsString(loginRequestIncorrectPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

        loginRequestIncorrectUser.email = "iDoNotExist@uclive.ac.nz";
        loginRequestIncorrectUser.password = "tester";
        mvc.perform(
                post(login_url)
                        .content(mapper.writeValueAsString(loginRequestIncorrectUser))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());

    }
}

