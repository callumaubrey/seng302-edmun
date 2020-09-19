package com.springvuegradle.team6.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.ActivityHistoryRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.SubscriptionHistoryRepository;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIService;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIServiceMocking;
import com.springvuegradle.team6.services.LocationService;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class LocationControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private LocationController locationController;

  @Autowired private LocationService locationService;

  @Autowired private GoogleAPIServiceMocking googleAPIService;

  private MockHttpSession session;

  private Profile profile;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);
    String login_url = "/login/";
    mvc.perform(
            post(login_url)
                .content(
                    "{\n"
                        + "\t\"email\" : \"johnydoe99@gmail.com\",\n"
                        + "\t\"password\": \"Password1\"\n"
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    googleAPIService.mockReverseGeocode("controllers/46BalgaySt_OK.json");
    googleAPIService.mockAutoCompleteLocationName("controllers/christchurchPlaces_OK.json");
  }

  @Test
  void getLocationAutocompleteWithLatLonReturnLocationNameReturnStatusOK() throws Exception {
    String lat = "-45.53000";
    String lon = "172.62028";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getLocationAutocompleteWithLatitudeNoLongitudeReturnBadRequest() throws Exception {
    String lat = "-45.53000";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat).session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getLocationAutocompleteWithLongitudeNoLatitudeReturnBadRequest() throws Exception {
    String lon = "172.62028";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lon=" + lon).session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getLocationAutocompleteInvalidLongitudeOverMaxReturnBadRequest() throws Exception {
    String lat = "-45.53000";
    String lon = "190.62028";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getLocationAutocompleteInvalidLongitudeUnderMinReturnBadRequest() throws Exception {
    String lat = "-45.53000";
    String lon = "-190.62028";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getLocationAutocompleteLongitudeOnMaxReturnOk() throws Exception {
    String lat = "-45.53000";
    String lon = "180";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationLongitudeOnMinReturnOk() throws Exception {
    String lat = "-45.53000";
    String lon = "-180";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeOverMaxReturnBadRequest() throws Exception {
    String lat = "91.53000";
    String lon = "0";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationInvalidLatitudeUnderMinReturnBadRequest() throws Exception {
    String lat = "-90.53000";
    String lon = "0";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationLatitudeOnMaxReturnOk() throws Exception {
    String lat = "90";
    String lon = "0";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationLatitudeOnMinReturnOk() throws Exception {
    String lat = "-90";
    String lon = "0";
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?lat=" + lat + "&lon=" + lon)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Test
  void getActivitiesByLocationNameReturnOk() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/location/autocomplete?name=" + "Christchurch")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(5, arr.length());
  }
}
