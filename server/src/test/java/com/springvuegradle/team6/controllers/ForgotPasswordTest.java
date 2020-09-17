package com.springvuegradle.team6.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.PasswordToken;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.PasswordTokenRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
    "ADMIN_PASSWORD=test"})
class ForgotPasswordTest {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private PasswordTokenRepository passwordTokenRepository;

  @Autowired
  private MockMvc mvc;

  private int id;

  private MockHttpSession session;


  @BeforeEach
  void createJohnDoeUser() throws Exception {
    session = new MockHttpSession();

    String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly1@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders
        .post("/profiles")
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session)
    ).andExpect(status().isCreated());

    String body = mvc.perform(
        get("/profiles/id").session(session)
    ).andReturn().getResponse().getContentAsString();
    id = Integer.parseInt(body);

    mvc.perform(MockMvcRequestBuilders
        .get("/logout/")
        .session(session)
    ).andExpect(status().isOk());

  }

  @Test
  void changeUserPasswordWithoutPasswordReturnStatusOk() throws Exception {
    Profile profile = profileRepository.findById(id);
    PasswordToken token = new PasswordToken(profile);
    passwordTokenRepository.save(token);

    String url = "/profiles/forgotpassword/" + token.getToken();

    String jsonString =
        "{\n"
            + "  \"new_password\": \"mynewpwdA1\",\n"
            + "  \"repeat_password\": \"mynewpwdA1\"\n"
            + "}";

    mvc.perform(MockMvcRequestBuilders
        .put(url)
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    profile = profileRepository.findById(id);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
    org.junit.jupiter.api.Assertions.assertTrue(passwordMatch);
  }

  @Test
  void changePasswordAndPasswordDoNotMatchReturnStatusBadRequest() throws Exception {
    Profile profile = profileRepository.findById(id);
    PasswordToken token = new PasswordToken(profile);
    passwordTokenRepository.save(token);
    profileRepository.save(profile);

    String url = "/profiles/forgotpassword/" + token.getToken();

    String jsonString =
        "{\n"
            + "  \"new_password\": \"mynewpwdA12\",\n"
            + "  \"repeat_password\": \"mynewpwdA1\"\n"
            + "}";

    mvc.perform(MockMvcRequestBuilders
        .put(url)
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isBadRequest());

    profile = profileRepository.findById(id);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
    org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
  }

  @Test
  void changePasswordAndPasswordInvalidReturnStatusBadRequest() throws Exception {
    Profile profile = profileRepository.findById(id);
    PasswordToken token = new PasswordToken(profile);
    passwordTokenRepository.save(token);
    profileRepository.save(profile);

    String url = "/profiles/forgotpassword/" + token.getToken();

    String jsonString =
        "{\n"
            + "  \"new_password\": \"mynewpwd\",\n"
            + "  \"repeat_password\": \"mynewpwd\"\n"
            + "}";

    mvc.perform(MockMvcRequestBuilders
        .put(url)
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isBadRequest());

    profile = profileRepository.findById(id);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean passwordMatch = passwordEncoder.matches("mynewpwd", profile.getPassword());
    org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
  }

  @Test
  void changePasswordAndPasswordTooShortReturnStatusBadRequest() throws Exception {
    Profile profile = profileRepository.findById(id);
    PasswordToken token = new PasswordToken(profile);
    passwordTokenRepository.save(token);
    profileRepository.save(profile);

    String url = "/profiles/forgotpassword/" + token.getToken();

    String jsonString =
        "{\n"
            + "  \"new_password\": \"mA1\",\n"
            + "  \"repeat_password\": \"mA1\"\n"
            + "}";

    mvc.perform(MockMvcRequestBuilders
        .put(url)
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isBadRequest());

    profile = profileRepository.findById(id);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean passwordMatch = passwordEncoder.matches("mA1", profile.getPassword());
    org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
  }

  @Test
  void changePasswordWithIncorrectTokenReturnStatusNotFound() throws Exception {
    String tokenString = "213879879AHSBSIUHBIBI";

    String url = "/profiles/forgotpassword/" + tokenString;

    String jsonString =
        "{\n"
            + "  \"new_password\": \"mynewpwdA1\",\n"
            + "  \"repeat_password\": \"mynewpwdA1\"\n"
            + "}";

    mvc.perform(MockMvcRequestBuilders
        .put(url)
        .content(jsonString)
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isNotFound());

    Profile profile = profileRepository.findById(id);

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
    org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
  }


}
