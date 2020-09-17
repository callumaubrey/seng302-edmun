package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.LoginAttempt;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Role;
import com.springvuegradle.team6.models.repositories.LoginAttemptRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import com.springvuegradle.team6.requests.LoginRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class LoginControllerTest {
  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  @Autowired private LoginAttemptRepository loginAttemptRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private RoleRepository roleRepository;

  private Integer profileId;

  @BeforeEach
  void setup() throws Exception {
    MockHttpSession session = new MockHttpSession();
    profileId = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
  }

  @Test
  void loginTestEmptyDataFail() throws Exception {
    mvc.perform(post("/login/").content("{}").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestValidEmailAndPasswordSuccess() throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "johndoe@uclive.ac.nz";
    loginRequestCorrectPass.password = "SuperSecurePassword123";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginRequestCorrectPass))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void loginTestIncorrectPasswordFail() throws Exception {
    LoginRequest loginRequestIncorrectPass = new LoginRequest();
    loginRequestIncorrectPass.email = "johndoe@uclive.ac.nz";
    loginRequestIncorrectPass.password = "hi";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginRequestIncorrectPass))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestNoSuchUserFail() throws Exception {
    LoginRequest loginRequestIncorrectUser = new LoginRequest();
    loginRequestIncorrectUser.email = "iDoNotExist@uclive.ac.nz";
    loginRequestIncorrectUser.password = "tester";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginRequestIncorrectUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestInvalidEmailFail() throws Exception {
    LoginRequest loginInvalidEmail = new LoginRequest();
    loginInvalidEmail.email = "invalidEmail.com";
    loginInvalidEmail.password = "tester";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginInvalidEmail))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestEmptyPasswordFail() throws Exception {
    LoginRequest loginEmptyPass = new LoginRequest();
    loginEmptyPass.email = "johndoe@uclive.ac.nz";
    loginEmptyPass.password = "";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginEmptyPass))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestEmptyEmailFail() throws Exception {
    LoginRequest loginEmptyUser = new LoginRequest();
    loginEmptyUser.email = "";
    loginEmptyUser.password = "SuperSecurePassword123";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginEmptyUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginTestFourthFailedLoginAttemptAccountLocked() throws Exception {
    LoginRequest loginUser = new LoginRequest();
    loginUser.email = "johndoe@uclive.ac.nz";
    loginUser.password = "Password1";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    String response =
        mvc.perform(
                post("/login/")
                    .content(mapper.writeValueAsString(loginUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals("This account is locked", response);
    org.junit.jupiter.api.Assertions.assertTrue(loginAttemptRepository.isProfileLocked(profileId));
  }

  @Test
  void loginTestThirdFailedLoginAttemptAccountNotLocked() throws Exception {
    LoginRequest loginUser = new LoginRequest();
    loginUser.email = "johndoe@uclive.ac.nz";
    loginUser.password = "Password1";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    String response =
        mvc.perform(
                post("/login/")
                    .content(mapper.writeValueAsString(loginUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals(
        "No associated user with email and password", response);
    org.junit.jupiter.api.Assertions.assertFalse(loginAttemptRepository.isProfileLocked(profileId));
  }

  @Test
  void loginTestLoggingIntoLockedAccountWithCorrectCredentialsReturnBadRequest() throws Exception {
    LoginRequest loginUser = new LoginRequest();
    loginUser.email = "johndoe@uclive.ac.nz";
    loginUser.password = "SuperSecurePassword123";
    Profile profile = profileRepository.findById(profileId).get();
    profile.setLockStatus(true);
    profileRepository.save(profile);
    String response =
        mvc.perform(
                post("/login/")
                    .content(mapper.writeValueAsString(loginUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals("This account is locked", response);
    org.junit.jupiter.api.Assertions.assertTrue(loginAttemptRepository.isProfileLocked(profileId));
  }

  @Test
  void loginTestGlobalAdminLoginAttemptsDoesNotLockAccount() throws Exception {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("admin@edmun.com");
    email.setPrimary(true);
    emails.add(email);
    Profile adminProfile = new Profile();
    adminProfile.setFirstname("John");
    adminProfile.setLastname("Doe");
    adminProfile.setEmails(emails);
    adminProfile.setDob("2010-01-01");
    adminProfile.setPassword("AdminPassword123");
    adminProfile.setGender("male");
    List<Role> roles = new ArrayList<>();
    Role role = new Role("ROLE_ADMIN");
    roleRepository.save(role);
    roles.add(role);
    adminProfile.setRoles(roles);
    profileRepository.save(adminProfile);

    LoginRequest loginEmptyUser = new LoginRequest();
    loginEmptyUser.email = "admin@edmun.com";
    loginEmptyUser.password = "Password1";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginEmptyUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginEmptyUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginEmptyUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    String response =
        mvc.perform(
                post("/login/")
                    .content(mapper.writeValueAsString(loginEmptyUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals(
        "No associated user with email and password", response);
    org.junit.jupiter.api.Assertions.assertFalse(
        loginAttemptRepository.isProfileLocked(adminProfile.getId()));
  }

  @Test
  void loginTestUserAdminLoginAttemptsDoesLock() throws Exception {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("poly@pocket.com");
    email.setPrimary(true);
    emails.add(email);
    Profile userAdminProfile = new Profile();
    userAdminProfile.setFirstname("John");
    userAdminProfile.setLastname("Doe");
    userAdminProfile.setEmails(emails);
    userAdminProfile.setDob("2010-01-01");
    userAdminProfile.setPassword("AdminPassword123");
    userAdminProfile.setGender("male");
    List<Role> roles = new ArrayList<>();
    Role role = new Role("ROLE_USER_ADMIN");
    roleRepository.save(role);
    roles.add(role);
    userAdminProfile.setRoles(roles);
    profileRepository.save(userAdminProfile);

    LoginRequest loginUser = new LoginRequest();
    loginUser.email = "poly@pocket.com";
    loginUser.password = "Password1";
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    mvc.perform(
            post("/login/")
                .content(mapper.writeValueAsString(loginUser))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
    String response =
        mvc.perform(
                post("/login/")
                    .content(mapper.writeValueAsString(loginUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals("This account is locked", response);
    org.junit.jupiter.api.Assertions.assertTrue(
        loginAttemptRepository.isProfileLocked(userAdminProfile.getId()));
  }
}
