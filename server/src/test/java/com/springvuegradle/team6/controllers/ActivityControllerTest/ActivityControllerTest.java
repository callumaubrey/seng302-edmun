package com.springvuegradle.team6.controllers.ActivityControllerTest;

import com.springvuegradle.team6.models.*;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class ActivityControllerTest {

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private MockMvc mvc;

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
    String jsonString = "{\n" +
            "  \"activity_name\": \"Tramping\",\n" +
            "  \"description\": \"tramping iz fun\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Hike\",\n" +
            "    \"Bike\"\n" +
            "  ],\n" +
            "  \"continuous\": \"true\",\n" +
            "  \"visibility\": \"public\"\n" +
            "}";
    ResultActions responseString =
            mvc.perform(
                    MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
    responseString.andExpect(status().isCreated());
  }

  @Test
  void createActivityWithoutVisibilityTypeReturnStatusOkAndVisibilityTypeDefaultsToNone() throws Exception {
    String jsonString = "{\n" +
            "  \"activity_name\": \"Running\",\n" +
            "  \"description\": \"tramping iz fun\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Hike\",\n" +
            "    \"Bike\"\n" +
            "  ],\n" +
            "  \"continuous\": true,\n" +
            "  \"hashtags\": [\n" +
            "    \"#a1\"\n" +
            "  ]\n" +
            "}";
    ResultActions responseString =
            mvc.perform(
                    MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
    responseString.andExpect(status().isCreated());
    Assert.assertSame(VisibilityType.Public, activityRepository
            .findById(Integer.parseInt(responseString.andReturn().getResponse().getContentAsString()))
            .get().getVisibilityType());

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
    activity.setVisibilityType("private");
    activity = activityRepository.save(activity);

    String jsonString =
            "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"john@pockets.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(
            MockMvcRequestBuilders.get("/logout/")
                    .session(session))
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
    activity.setVisibilityType("private");
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
    activity.setVisibilityType("public");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());
  }
}
