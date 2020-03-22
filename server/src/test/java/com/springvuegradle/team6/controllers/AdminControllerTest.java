package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.AddRoleRequest;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.DeleteProfileRequest;
import com.springvuegradle.team6.requests.DeleteRoleRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        validRequest.firstname = "John";
        validRequest.middlename = "S";
        validRequest.lastname = "Doe";
        validRequest.nickname = "Big J";
        validRequest.bio = "Just another plain jane";
        validRequest.email = "john@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "1999-12-20";
        validRequest.gender = "male";
        validRequest.fitness = 0;

        String createProfileUrl = "/profiles";

        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @AfterEach
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteProfile() throws Exception {

        String deleteProfileUrl = "/admin/profiles";

        //Delete existing primary email
        DeleteProfileRequest request = new DeleteProfileRequest();
        request.primary_email = "john@uclive.ac.nz";

        mvc.perform(
                delete(deleteProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk());

        // Incorrect email associated to user
        request = new DeleteProfileRequest();
        request.primary_email = "incorrect@email.com";

        mvc.perform(
                delete(deleteProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addRole() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        // Add new role to existing user
        AddRoleRequest request = new AddRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_ADMIN";

        mvc.perform(
                post(addRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Add new role to non-existing user
        request = new AddRoleRequest();
        request.primary_email = "user@doesnotexist.com";
        request.role_name = "ROLE_ADMIN";

        mvc.perform(
                post(addRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Add duplicate role to existing user
        request = new AddRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_USER";

        mvc.perform(
                post(addRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());

        // Add invalid role to existing user
        request = new AddRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_INVALID";

        mvc.perform(
                post(addRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteRole() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete existing role from existing user
        DeleteRoleRequest request = new DeleteRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_USER";

        mvc.perform(
                delete(deleteRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // Delete non-existing role from existing user
        request = new DeleteRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_ADMIN";

        mvc.perform(
                delete(deleteRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());

        // Delete invalid role
        request = new DeleteRoleRequest();
        request.primary_email = "john@uclive.ac.nz";
        request.role_name = "ROLE_INVALID";

        mvc.perform(
                delete(deleteRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Delete role from non=existing user
        request = new DeleteRoleRequest();
        request.primary_email = "incorrect@email.com";
        request.role_name = "ROLE_USER";

        mvc.perform(
                delete(deleteRoleUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}
