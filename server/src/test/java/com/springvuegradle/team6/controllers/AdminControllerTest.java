package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void createJohnDoeUser() throws Exception {
        CreateProfileRequest validRequest = new CreateProfileRequest();
        String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @AfterEach
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteProfile() throws Exception {

        String deleteProfileUrl = "/admin/profiles";

        //Delete existing primary email
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteProfileUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Incorrect email associated to user
        jsonString = "{\n" +
                "\t\"primary_email\": \"incorrect@gmail.com\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteProfileUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addRole() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Add new role to non-existing user
        jsonString = "{\n" +
                "\t\"primary_email\": \"user@doesnotexist.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Add duplicate role to existing user
        jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());

        // Add invalid role to existing user
        jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_INVALID\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteRole() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete existing role from existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Delete non-existing role from existing user
        jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());

        // Delete invalid role
        jsonString = "{\n" +
                "\t\"primary_email\": \"poly@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_INVALID\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());


        // Delete role from non=existing user
        jsonString = "{\n" +
                "\t\"primary_email\": \"incorrect@gmail.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}
