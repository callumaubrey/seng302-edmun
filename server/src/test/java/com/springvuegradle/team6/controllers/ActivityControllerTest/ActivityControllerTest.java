package com.springvuegradle.team6.controllers.ActivityControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.SubscribeMethod;
import com.springvuegradle.team6.models.entities.SubscriptionHistory;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.SubscriptionHistoryRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class ActivityControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;

  private int id;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
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
  void createActivityWithLocationReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"state\": \"Canterbury\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithLocationNoStateReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithLocationEmptyReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithHashtagReturnStatusIsCreatedAndActivityIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
    Assert.assertTrue(
        activityRepository
            .findById(
                Integer.parseInt(responseString.andReturn().getResponse().getContentAsString()))
            .isPresent());
  }

  @Test
  void createActivityWithInvalidHashtagReturnStatusIsBadRequestAndErrorMessageIsReturned()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1 123\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isBadRequest());
    Assert.assertTrue(
        responseString
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("contains characters other than alphanumeric characters and underscores"));
  }

  @Test
  void createActivityWithMoreThan30HashtagReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\",\n"
            + "    \"#a6\",\n"
            + "    \"#a7\",\n"
            + "    \"#a8\",\n"
            + "    \"#a9\",\n"
            + "    \"#a10\",\n"
            + "    \"#a11\",\n"
            + "    \"#a12\",\n"
            + "    \"#a13\",\n"
            + "    \"#a14\",\n"
            + "    \"#a15\",\n"
            + "    \"#a16\",\n"
            + "    \"#a17\",\n"
            + "    \"#a18\",\n"
            + "    \"#a19\",\n"
            + "    \"#a20\",\n"
            + "    \"#a21\",\n"
            + "    \"#a22\",\n"
            + "    \"#a23\",\n"
            + "    \"#a24\",\n"
            + "    \"#a25\",\n"
            + "    \"#a26\",\n"
            + "    \"#a27\",\n"
            + "    \"#a28\",\n"
            + "    \"#a29\",\n"
            + "    \"#a30\",\n"
            + "    \"#a31\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithVisibilityTypePublicReturnStatusOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Tramping\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": \"true\",\n"
            + "  \"visibility\": \"public\"\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
  }

  @Test
  void createActivityWithoutVisibilityTypeReturnStatusOkAndVisibilityTypeDefaultsToPublic()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
    Assert.assertSame(
        VisibilityType.Public,
        activityRepository
            .findById(
                Integer.parseInt(responseString.andReturn().getResponse().getContentAsString()))
            .get()
            .getVisibilityType());
  }

  @Test
  void createActivityReturnStatusIsCreatedCreatorRoleCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    List<ActivityRole> activityRoles =
        activityRoleRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityRole creator = activityRoles.get(0);
    org.junit.jupiter.api.Assertions.assertEquals(
        ActivityRoleType.Creator, creator.getActivityRoleType());
    org.junit.jupiter.api.Assertions.assertEquals(id, creator.getProfile().getId());
  }

  @Test
  void getActivityCreatorReturnStatusOk() throws Exception {
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
                MockMvcRequestBuilders.get("/activities/" + activity1Id + "/creatorId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(((Integer) id).toString(), response);
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
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

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/3")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getActivitiesByUserIdReturnActivities() throws Exception {
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void getActivitiesWhenUserHasNoActivitiesReturnNoActivities() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string("[]"));
  }

  @Test
  void getActivitiesWhereUserDoesNotExistReturnStatusNotFound() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/{profileId}/activities", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testAuthorisedUserViewsCorrectPrivateActivitiesAndPublicActivities() throws Exception {
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("private");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setVisibilityTypeByString("public");
    testActivity2.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void testUnauthorisedUserCanNotGetPrivateActivities() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("private");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(0, result.length());
  }

  @Test
  void testUnauthorisedUserCanNotGetPrivateActivitiesButCanViewPublic() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("private");
    testActivity1.setProfile(profileRepository.findById(id));
    Activity testActivity2 = new Activity();
    testActivity2.setVisibilityTypeByString("public");
    testActivity2.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);
    activityRepository.save(testActivity2);
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  @WithMockUser(roles = {"USER", "USER_ADMIN"})
  void testAdminCanViewPrivateActivities() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("private");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setVisibilityTypeByString("public");
    testActivity2.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void testAuthorisedCanGetRestrictedActivities() throws Exception {
    int user = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("restricted");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    TestDataGenerator.addActivityRole(
        profileRepository.findById(user),
        testActivity1,
        ActivityRoleType.Access,
        activityRoleRepository);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void testUnauthorisedCanNotGetRestrictedActivityList() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("restricted");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setVisibilityTypeByString("public");
    testActivity2.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void testCreatorCanGetRestrictedActivities() throws Exception {
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("restricted");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void testOnlyActivitiesFromOneUserAreReceived() throws Exception {
    int user = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Profile profile = profileRepository.findById(id);
    Profile profile2 = profileRepository.findById(user);
    TestDataGenerator.createExtraActivity(profile, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void testGetActivitiesArchivedActivitiesAreNotReceived() throws Exception {
    int user = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Profile profile = profileRepository.findById(id);
    Profile profile2 = profileRepository.findById(user);
    Activity activity =
        TestDataGenerator.createExtraActivity(profile, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);
    TestDataGenerator.createExtraActivity(profile2, activityRepository, VisibilityType.Public);
    activity.setArchived(true);
    activityRepository.save(activity);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void testAuthorisedCanGetMultipleRestrictedActivities() throws Exception {
    int user = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("restricted");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setVisibilityTypeByString("restricted");
    testActivity2.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity2);

    Activity testActivity3 = new Activity();
    testActivity3.setVisibilityTypeByString("private");
    testActivity3.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity3);

    TestDataGenerator.addActivityRole(
        profileRepository.findById(user),
        testActivity1,
        ActivityRoleType.Access,
        activityRoleRepository);
    TestDataGenerator.addActivityRole(
        profileRepository.findById(user),
        testActivity2,
        ActivityRoleType.Participant,
        activityRoleRepository);
    TestDataGenerator.addActivityRole(
        profileRepository.findById(user),
        testActivity3,
        ActivityRoleType.Organiser,
        activityRoleRepository);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(3, result.length());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void testAdminCanGetRestrictedActivities() throws Exception {
    Activity testActivity1 = new Activity();
    testActivity1.setVisibilityTypeByString("restricted");
    testActivity1.setProfile(profileRepository.findById(id));
    activityRepository.save(testActivity1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void getActivityThatIsArchivedReturnStatusOKAndNoData() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setArchived(true);
    activity = activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string("Activity is archived"));
  }

  @Test
  void getActivityPrivateAccessAnotherUserTriesToView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("private");
    activity = activityRepository.save(activity);

    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"john@pockets.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("Activity is private"));
  }

  @Test
  void getPrivateActivityOwnerCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("private");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void getPrivateActivityAdminCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("private");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void getPublicActivityAnyoneCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("public");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void getPublicActivityAdminCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("public");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void getRestrictedActivityAdminCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("restricted");
    activity = activityRepository.save(activity);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    String jsonString1 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"doe@email.com\",\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString1)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    System.out.println(session.getAttribute("id"));

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void getRestrictedActivityWithAccessRoleReturnStatusOk() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("restricted");
    activity = activityRepository.save(activity);

    Profile profile1 = new Profile();
    profile1.setFirstname("Martin");
    profile1.setLastname("Johns");
    profile1.setPassword("Password1");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("martin@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    ActivityRole activityRole1 = new ActivityRole();
    activityRole1.setActivity(activity);
    activityRole1.setProfile(profile1);
    activityRole1.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole1);
    String jsonString =
        "{\r\n  \"email\": \"martin@email.com\",\r\n  \"password\": \"Password1\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void getRestrictedActivityWithoutAccess() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityTypeByString("restricted");
    activity = activityRepository.save(activity);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString1 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"doe@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString1)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"johnny@email.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void putActivityVisibilityAdd() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    {\n"
            + "      \"email\": \"johnny@email.com\",\n"
            + "      \"role\": \"access\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"email\" : \"doe@email.com\",\n"
            + "      \"role\": \"access\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"email\" : \"martin@email.com\",\n"
            + "      \"role\": \"access\"\n"
            + "    }\n"
            + "  ]         \n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility",
                    id,
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, result.size());
  }

  @Test
  void testChangeActivityVisibilityRestrictedReturnsStatusOkRemovesUsers() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity.setVisibilityType(VisibilityType.Public);
    activity = activityRepository.save(activity);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivityRoleType(ActivityRoleType.Creator);
    activityRole.setActivity(activity);
    activityRole.setProfile(owner);
    activityRoleRepository.save(activityRole);

    ActivityRole activityRole1 = new ActivityRole();
    activityRole1.setActivityRoleType(ActivityRoleType.Access);
    activityRole1.setActivity(activity);
    activityRole1.setProfile(profile1);
    activityRoleRepository.save(activityRole1);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setActivityRoleType(ActivityRoleType.Access);
    activityRole2.setActivity(activity);
    activityRole2.setProfile(profile2);
    activityRoleRepository.save(activityRole2);

    ActivityRole activityRole3 = new ActivityRole();
    activityRole3.setActivityRoleType(ActivityRoleType.Access);
    activityRole3.setActivity(activity);
    activityRole3.setProfile(profile3);
    activityRoleRepository.save(activityRole3);

    String jsonString2 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    {\n"
            + "      \"email\": \"johnny@email.com\",\n"
            + "      \"role\": \"access\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"email\": \"martin@email.com\",\n"
            + "      \"role\": \"access\"\n"
            + "    }\n"
            + "\n"
            + "  ]         \n"
            + "}\n";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility",
                    id,
                    activity.getId())
                .content(jsonString2)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, result.size());
  }

  @Test
  void putVisibilityTypeAsPublicWillIgnoreAccessorList() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    String jsonString =
        "{\n"
            + "  \"visibility\": \"public\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, result.size());
  }

  @Test
  void testUserIsAFollowerUponCreatingActivity() throws Exception {
    String activityJson =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track racey\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";
    // Creates an activity
    String activityBody =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(activityJson)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    int activityId = Integer.parseInt(activityBody);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/" + id + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @Test
  void addActivityHashtagsReturnStatusOkAndHashtagSetSizeIsIncreased() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    String jsonString =
        "{\n"
            + "  \"hashtags\": [\n"
            + "    \"#nosweatnoglory\",\n"
            + "    \"#whalewatching\"\n"
            + "  ]\n"
            + "}";

    Set<Tag> hashtagSetBefore = activityRepository.findById(activity.getId()).get().getTags();
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/hashtags", id, activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Tag> hashtagSetAfter = activityRepository.findById(activity.getId()).get().getTags();
    org.junit.jupiter.api.Assertions.assertEquals(
        hashtagSetBefore.size() + 2, hashtagSetAfter.size());
  }

  @Test
  void removeActivityHashtagsReturnStatusOkAndHashtagSetSizeIsDecreased() throws Exception {
    Tag tag1 = new Tag("#nosweatnoglory");
    tagRepository.save(tag1);
    Tag tag2 = new Tag("#whalewatching");
    tagRepository.save(tag2);
    Set<Tag> tagSet = new HashSet<>();
    tagSet.add(tag1);
    tagSet.add(tag2);

    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setTags(tagSet);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    String jsonString = "{\n" + "  \"hashtags\": [\n" + "    \"#nosweatnoglory\"\n" + "  ]\n" + "}";

    Set<Tag> hashtagSetBefore = activityRepository.findById(activity.getId()).get().getTags();
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/hashtags", id, activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Tag> hashtagSetAfter = activityRepository.findById(activity.getId()).get().getTags();
    org.junit.jupiter.api.Assertions.assertEquals(
        hashtagSetBefore.size() - 1, hashtagSetAfter.size());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void createActivityForAnotherUserAsAdminAndExpectStatusIsCreated() throws Exception {
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
    List<Activity> otherUsersActivities = activityRepository.findByProfile_IdAndArchivedFalse(id);
    org.junit.jupiter.api.Assertions.assertFalse(otherUsersActivities.isEmpty());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void createActivityForAnotherUserThatDoesNotExistAsAdminAndExpectClientError() throws Exception {
    int nonExistingProfileId = 9381849;
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
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", nonExistingProfileId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createActivityWithInvalidDescriptionLengthReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Blabber mouth\",\n"
            + "  \"description\": \""
            + "A".repeat(Activity.DESCRIPTION_MAX_LENGTH + 1)
            + "\",\n"
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
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createActivityWithMaxDescriptionLengthReturnStatusIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Blabber mouth\",\n"
            + "  \"description\": \""
            + "A".repeat(Activity.DESCRIPTION_MAX_LENGTH)
            + "\",\n"
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
  void createActivityWithInvalidNameLengthReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + "A".repeat(Activity.NAME_MAX_LENGTH + 1)
            + "\",\n"
            + "  \"description\": \"String madness\",\n"
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
        .andExpect(status().is4xxClientError());
  }

  @Test
  void createActivityWithMaxNameLengthReturnStatusIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + "A".repeat(Activity.NAME_MAX_LENGTH)
            + "\",\n"
            + "  \"description\": \"String madness\",\n"
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
  void excludingPreviouslySubscribedUserFromRestrictedAccessorsUnsubscribesUser() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    String removeAccess =
        "{\n" + "  \"visibility\": \"restricted\",\n" + "  \"accessors\":[ \n" + "  ]\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(removeAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertEquals(
        0, subscriptionHistoryRepository.findActive(activityId, profile1.getId()).size());
  }

  @Test
  void excludingFollowingUserFromRestrictedAccessorsDeletesActivityRole() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    String removeAccess =
        "{\n" + "  \"visibility\": \"restricted\",\n" + "  \"accessors\":[ \n" + "  ]\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(removeAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertEquals(
        activityRoleRepository.findByProfile_IdAndActivity_Id(profile1.getId(), activityId), null);
  }

  @Test
  void includingPreviouslySubscribedUserInRestrictedAccessorsRetainsSubscription()
      throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Email email1 = new Email("example1@email.com");
    Set<Email> emails = new HashSet<Email>();
    emails.add(email1);
    profile1.setEmails(emails);
    profile1.setPrimaryEmail(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    Assert.assertEquals(
        1, subscriptionHistoryRepository.findActive(activityId, profile1.getId()).size());

    String retainAccess =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    {\n"
            + "      \"email\": \"example1@email.com\",\n"
            + "      \"role\": \"follower\"\n"
            + "    }\n"
            + "  ]         \n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(retainAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertEquals(
        1, subscriptionHistoryRepository.findActive(activityId, profile1.getId()).size());
  }

  @Test
  void includingFollowingUserInRestrictedAccessorsRetainsActivityRole() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Email email1 = new Email("example1@email.com");
    Set<Email> emails = new HashSet<Email>();
    emails.add(email1);
    profile1.setEmails(emails);
    profile1.setPrimaryEmail(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Public);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    String retainAccess =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    {\n"
            + "      \"email\": \"example1@email.com\",\n"
            + "      \"role\": \"organiser\"\n"
            + "    }\n"
            + "  ]         \n"
            + "}\n";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(retainAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertEquals(
        ActivityRoleType.Organiser,
        activityRoleRepository
            .findByProfile_IdAndActivity_Id(profile1.getId(), activityId)
            .getActivityRoleType());
  }

  @Test
  void settingVisibilityToPrivateDeletesAllActivityRolesExceptCreator() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    String removeAccess =
        "{\n" + "  \"visibility\": \"private\",\n" + "  \"accessors\":[ \n" + "  ]\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(removeAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertEquals(0, activityRoleRepository.findMembersCount(activityId, 4));
  }

  @Test
  void includingAccessorsWhenMakingPrivateReturnsBadRequest() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTimeByString("2000-04-28T15:50:41+1300");
    activity.setEndTimeByString("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity.setVisibilityType(VisibilityType.Restricted);
    activity = activityRepository.save(activity);
    int activityId = activity.getId();

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile1);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory.setSubscribeMethod(SubscribeMethod.ADDED);
    subscriptionHistoryRepository.save(subscriptionHistory);

    String removeAccess =
        "{\n"
            + "  \"visibility\": \"private\",\n"
            + "  \"accessors\":[ \n"
            + "  \"example1@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/visibility", id, activityId)
                .content(removeAccess)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void createActivityWithMetricsReturnStatusCreatedAndMetricsAreAdded() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"metrics\": [\n"
            + "    {\"unit\": \"Count\", \"title\": \"Counting sizth dimensions\"},\n"
            + "    {\"unit\": \"Distance\", \"title\": \"I can go the distance\", \"description\": \"Herculees\"}\n"
            + "  ]\n"
            + "}\n";

    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Activity activity = activityRepository.findById(Integer.parseInt(activityId)).get();
    org.junit.jupiter.api.Assertions.assertEquals(2, activity.getMetrics().size());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void createActivityAndMetricHasNoTitleReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"metrics\": [\n"
            + "    {\"unit\": \"Distance\", \"description\": \"Herculees\"}\n"
            + "  ]\n"
            + "}\n";

    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void createActivityAndMetricHasNoUnitReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"metrics\": [\n"
            + "    {\"description\": \"Herculees\", \"title\": \"Hmmmmmmmm\"}\n"
            + "  ]\n"
            + "}\n";

    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void createActivityWithEmptyMetricListReturnStatusCreatedAndActivityHasNoMetric()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"metrics\": [\n"
            + "  ]\n"
            + "}\n";

    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    org.junit.jupiter.api.Assertions.assertTrue(
        activityRepository.findById(Integer.parseInt(activityId)).get().getMetrics().isEmpty());
  }
}
