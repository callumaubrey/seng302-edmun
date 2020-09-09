package com.springvuegradle.team6.permission;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.requests.LoginRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserAuthorisationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ObjectMapper mapper;

  private MockHttpSession session;

  private Activity activity;

  /**
   * Function to mock Spring authority manually
   *
   * @param email    user email
   * @param password user password
   * @param role     user role
   */
  private void mockAuthority(String email, String password, String role) {
    List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
    updatedAuthorities.add(authority);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(email, password, updatedAuthorities));
  }

  @BeforeEach
  void setup() {
    session = new MockHttpSession();
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profileRepository.save(profile1);

    Profile profile2 = new Profile();
    profile2.setFirstname("Johnny");
    profile2.setLastname("Dong");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("useradmin@email.com"));
    profile2.setEmails(email2);
    profile2.setPassword("Password1");
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Johnny");
    profile3.setLastname("Dong");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("unauthorised@email.com"));
    profile3.setEmails(email3);
    profile3.setPassword("Password1");
    profileRepository.save(profile3);

    activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(profile1);
    activityRepository.save(activity);
  }

  @Test
  void checkIsAdminOrCreatorFunctionReturnsTrueWhenSessionIdEqualsCreatorId() throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "johnny@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    org.junit.jupiter.api.Assertions.assertTrue(
        UserSecurityService.checkIsAdminOrCreator(
            (Integer) session.getAttribute("id"), activity.getProfile().getId()));
  }

  @Test
  void checkIsAdminOrCreatorFunctionReturnsTrueWhenSessionIdIsNotEqualToCreatorIdAndUserIsAdmin()
      throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "useradmin@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_ADMIN");

    org.junit.jupiter.api.Assertions.assertTrue(
        UserSecurityService.checkIsAdminOrCreator(
            (Integer) session.getAttribute("id"), activity.getProfile().getId()));
  }

  @Test
  void
  checkIsAdminOrCreatorFunctionReturnsFalseWhenSessionIdIsNotEqualToCreatorIdAndUserIsNotAdmin()
      throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "unauthorised@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    org.junit.jupiter.api.Assertions.assertFalse(
        UserSecurityService.checkIsAdminOrCreator(
            (Integer) session.getAttribute("id"), activity.getProfile().getId()));
  }

  @Test
  void checkIsAuthorisedFunctionReturns401HttpStatusIfUserIsNotLoggedIn() {
    org.junit.jupiter.api.Assertions.assertEquals(
        "401 UNAUTHORIZED",
        Objects.requireNonNull(
            UserSecurityService.checkAuthorised(
                activity.getProfile().getId(), session, profileRepository))
            .getStatusCode()
            .toString());
  }

  @Test
  void checkIsAuthorisedFunctionReturns401HttpStatusIfUserIsNotRegisteredInTheSystem()
      throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "randomdude@email.com";
    loginRequestCorrectPass.password = "Trespasser1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    org.junit.jupiter.api.Assertions.assertEquals(
        "401 UNAUTHORIZED",
        Objects.requireNonNull(
            UserSecurityService.checkAuthorised(
                activity.getProfile().getId(), session, profileRepository))
            .getStatusCode()
            .toString());
  }

  @Test
  void checkIsAuthorisedFunctionReturnsNullIfUserIsAuthorisedToAccessRequestedInformation()
      throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "johnny@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    Assertions.assertNull(
        UserSecurityService.checkAuthorised(
            activity.getProfile().getId(), session, profileRepository));
  }

  @Test
  void checkIsAuthorisedFunctionReturns401HttpStatusIfUserIsNotAuthorisedToAccessRequestedInformation()
      throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "unauthorised@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    org.junit.jupiter.api.Assertions.assertEquals(
        "401 UNAUTHORIZED",
        Objects.requireNonNull(
            UserSecurityService.checkAuthorised(
                activity.getProfile().getId(), session, profileRepository))
            .getStatusCode()
            .toString());
  }

  @Test
  void checkViewingPermissionReturns401HttpStatusIfUserIsNotLoggedIn() {
    org.junit.jupiter.api.Assertions.assertEquals(
        "401 UNAUTHORIZED",
        Objects.requireNonNull(
            UserSecurityService.checkViewingPermission(
                activity.getProfile().getId(), session, profileRepository))
            .getStatusCode()
            .toString());
  }

  @Test
  void checkViewingPermissionReturns401HttpStatusIfUserIsRegisteredInTheSystem() throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "randomdude@email.com";
    loginRequestCorrectPass.password = "Trespasser1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    org.junit.jupiter.api.Assertions.assertEquals(
        "401 UNAUTHORIZED",
        Objects.requireNonNull(
            UserSecurityService.checkViewingPermission(
                activity.getProfile().getId(), session, profileRepository))
            .getStatusCode()
            .toString());
  }

  @Test
  void checkViewingPermissionReturnsNullIfUserCanViewRequestedInformation() throws Exception {
    LoginRequest loginRequestCorrectPass = new LoginRequest();
    loginRequestCorrectPass.email = "useradmin@email.com";
    loginRequestCorrectPass.password = "Password1";
    mvc.perform(
        post("/login/")
            .content(mapper.writeValueAsString(loginRequestCorrectPass))
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));
    mockAuthority(loginRequestCorrectPass.email, loginRequestCorrectPass.password, "ROLE_USER");

    Assertions.assertNull(
        UserSecurityService.checkViewingPermission(
            activity.getProfile().getId(), session, profileRepository));
  }
}
