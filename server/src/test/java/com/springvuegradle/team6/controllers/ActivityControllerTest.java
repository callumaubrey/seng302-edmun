package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.*;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ActivityControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    activityRepository.deleteAll();
    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    id = Integer.parseInt(body);
  }

  @Test
  void createActivityWithTimeReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithDateOnlyReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2030-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithStartDateBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithBothDateBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2010-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithStartDateAfterEndDateNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2060-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithStartDateEqualEndDateReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2050-08-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithOnlyMandatoryValuesReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithNoActivityNameReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithNoActivityTypeReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                + "  \"activity_type\":[ \n"
                + "  ]\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithActivityTypeIsNullReturnStatusBadRequest() throws Exception {
    String jsonString =
            "{\n"
                    + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                    + "  \"continuous\": true\n"
                    + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithNoContinuousReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ]\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getActivityById() throws Exception {
    Profile profile1 = profileRepository.findById(id);
    Activity testActivity1 = new Activity();
    testActivity1.setActivityName("Test");
    testActivity1.setProfile(profile1);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    // ID to check for
    String activity1Id = testActivity1.getId().toString();

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activity1Id)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject result = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(activity1Id, result.getString("id"));
  }

  @Test
  void getActivityByIdWhenIdDoesntExists() throws Exception {
    Profile profile1 = profileRepository.findById(id);
    Activity testActivity1 = new Activity();
    testActivity1.setId(1000);
    testActivity1.setProfile(profile1);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setId(2000);
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    mvc.perform(MockMvcRequestBuilders.get("/activities/3").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getActivitiesByUserId() throws Exception {
    Activity testActivity1 = new Activity();
    Profile profile1 = profileRepository.findById(id);
    Profile profile2 = new Profile();
    profileRepository.save(profile2);

    testActivity1.setProfile(profile1);

    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    Activity testActivity3 = new Activity();
    testActivity3.setProfile(profile2);
    activityRepository.save(testActivity3);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", profile1.getId())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void getActivitiesWhenUserHasNoActivities() throws Exception {
    Activity testActivity1 = new Activity();
    Profile profile1 = profileRepository.findById(id);
    Profile profile2 = new Profile();
    profileRepository.save(profile2);

    testActivity1.setProfile(profile2);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile2);
    activityRepository.save(testActivity2);

    Activity testActivity3 = new Activity();
    testActivity3.setProfile(profile2);
    activityRepository.save(testActivity3);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/{profileId}/activities", profile1.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string("[]"));
  }
}
