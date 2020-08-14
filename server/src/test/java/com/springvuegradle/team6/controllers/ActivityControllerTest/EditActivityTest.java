package com.springvuegradle.team6.controllers.ActivityControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
class EditActivityTest {

  @Autowired private MockMvc mvc;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityHistoryRepository activityHistoryRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private ActivityQualificationMetricRepository activityQualificationMetricRepository;

  @Autowired private NamedLocationRepository namedLocationRepository;

  private int id;

  private int activityId;

  private MockHttpSession session;

  private MockHttpSession otherSession;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    String profileJson =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    // Logs in and get profile Id
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profileJson)
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

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTime(LocalDateTime.parse("2000-04-28T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity.setEndTime(LocalDateTime.parse("2030-08-28T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity.setProfile(profileRepository.findById(id));
    activity = activityRepository.save(activity);
    activityId = activity.getId();
  }

  @Test
  void editAllActivityWithoutAuthorizationReturnIsUnauthorized() throws Exception {
    otherSession = new MockHttpSession();
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(otherSession))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void editAllActivityWithoutTimeReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void editActivityAsAdminReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editAllActivityWithTimeReturnStatusIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editActivityWithStartDateBeforeNowAndUnchangedReturnStatusOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editActivityWithStartDateBeforeNowChangedReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:51:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithBothDatesBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2000-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithStartDateAfterEndDateReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithStartDateEqualEndDateReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void EditActivityNameReturnStatusBadRequest() throws Exception {
    String jsonString = "{\n" + "  \"activity_name\": \"Kaikoura Coast Track race\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void EditActivityLocationReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"state\": \"Canterbury\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void EditActivityLocationEmptyReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void EditActivityLocationNoStateReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void EditActivityWithoutLocationReturnStatusIsOkAndDeletesLocation() throws Exception {
    NamedLocation location = new NamedLocation();
    location.setCity("Christchurch");
    location.setCountry("NZ");
    location = namedLocationRepository.save(location);

    Activity activity = activityRepository.findById(activityId).get();
    activity.setLocation(location);
    activityRepository.save(activity);


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
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());

    activity = activityRepository.findById(activityId).get();

    org.junit.jupiter.api.Assertions.assertNull(activity.getLocation());

  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void editActivityAsAdminLocationNoStateReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editActivityWithHashtagReturnStatusOKAndActivityIsUpdated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    Set<String> expectedResult = new HashSet<>();

    expectedResult.add("a1");
    expectedResult.add("a2");
    expectedResult.add("a3");
    expectedResult.add("a4");
    expectedResult.add("a5");

    Set<Tag> result = activityRepository.getActivityTags(activityId);
    Set<String> resultStrings = new HashSet<>();
    for (Tag tag : result) {
      resultStrings.add(tag.getName());
    }
    org.junit.jupiter.api.Assertions.assertTrue(resultStrings.containsAll(expectedResult));
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void editActivityAsAdminWithHashtagReturnStatusOKAndActivityIsUpdated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    Set<String> expectedResult = new HashSet<>();

    expectedResult.add("a1");
    expectedResult.add("a2");
    expectedResult.add("a3");
    expectedResult.add("a4");
    expectedResult.add("a5");

    Set<Tag> result = activityRepository.getActivityTags(activityId);
    Set<String> resultStrings = new HashSet<>();
    for (Tag tag : result) {
      resultStrings.add(tag.getName());
    }
    org.junit.jupiter.api.Assertions.assertTrue(resultStrings.containsAll(expectedResult));
  }

  @Test
  void editActivityWithInvalidHashtagReturnStatusIsBadRequestAndErrorMessageIsReturned()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
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
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
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
  void editActivityWithMoreThan30HashtagReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
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
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isBadRequest());
  }

  @Test
  void editActivityReturnStatusOkAndCreatesNewEntryOfActivityHistory() throws Exception {
    Set<ActivityHistory> activityHistorySetBefore =
        activityHistoryRepository.findByActivity_id(activityId);
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<ActivityHistory> activityHistorySetAfter =
        activityHistoryRepository.findByActivity_id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(
        activityHistorySetBefore.size() + 1, activityHistorySetAfter.size());
  }

  @Test
  void editActivityWithNoChangesReturnStatusOkAndDoesNotCreateNewEntryOfActivityHistory()
      throws Exception {
    // calls the edit activity endpoint with the same json twice to ensure there is no updates to
    // the activity
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<ActivityHistory> activityHistorySetBefore =
        activityHistoryRepository.findByActivity_id(activityId);
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    Set<ActivityHistory> activityHistorySetAfter =
        activityHistoryRepository.findByActivity_id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(
        activityHistorySetBefore.size(), activityHistorySetAfter.size());
  }

  @Test
  void
      EditActivityVisibilityTypeFromPublicToPrivateReturnStatusIsOkAndActivityVisibilityTypeIsChanged()
          throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"visibility\": \"private\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assert.assertSame(
        VisibilityType.Private, activityRepository.findById(activityId).get().getVisibilityType());
  }

  @Test
  void
      EditActivityVisibilityTypeFromPublicToPrivateUsingEditActivityReturnAllRolesDeletedExceptOwner()
          throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    // adding Johnny as a follower to the activity
    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activityRepository.findById(activityId).get());
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"visibility\": \"private\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assertions.assertNull(
        activityRoleRepository.findByProfile_IdAndActivity_Id(profile1.getId(), activityId));
  }

  @Test
  void
      VisibilityTypeFromRestrictedToPublicUsingEditActivityDeletesAllAccessRolesAndKeepsAllOtherRoles()
          throws Exception {

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Profile profile2 = new Profile();
    profile2.setFirstname("Mark");
    profile2.setLastname("Zuck");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("mark@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);

    // adding Johnny as ACCESS to the activity
    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activityRepository.findById(activityId).get());
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setActivity(activityRepository.findById(activityId).get());
    activityRole2.setProfile(profile2);
    activityRole2.setActivityRoleType(ActivityRoleType.Follower);
    activityRoleRepository.save(activityRole2);

    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"visibility\": \"public\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Assertions.assertEquals(0, activityRoleRepository.findMembersCount(activityId, 4));
    Assertions.assertEquals(1, activityRoleRepository.findMembersCount(activityId, 3));
  }

  @Test
  void testEditActivityWithMaxNameLengthReturnIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + "A".repeat(Activity.NAME_MAX_LENGTH)
            + "\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void testEditActivityWithInvalidNameLengthReturnIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + "A".repeat(Activity.NAME_MAX_LENGTH + 1)
            + "\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testEditActivityWithMaxDescriptionLengthReturnIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \""
            + "A".repeat(Activity.DESCRIPTION_MAX_LENGTH)
            + "\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void testEditActivityWithInvalidDescriptionLengthReturnIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \""
            + "A".repeat(Activity.DESCRIPTION_MAX_LENGTH + 1)
            + "\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void addActivityMetricWithEditActivityRequestReturnStatusOkAndActivityMetricIsAdded()
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
            + "    {\"unit\": \"Count\", \"title\": \"Counting sizth dimensions\"},\n"
            + "    {\"unit\": \"Distance\", \"title\": \"I can go the distance\", \"description\": \"Herculees\"}\n"
            + "  ]\n"
            + "}\n";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Activity activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertEquals(2, activity.getMetrics().size());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void editExistingActivityMetricWithEditActivityRequestReturnStatusOkAndActivityMetricIsEdited()
      throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric.setTitle("Hercules");
    activityQualificationMetricRepository.save(metric);

    List<ActivityQualificationMetric> metrics = new ArrayList<>();
    metrics.add(metric);
    activity.setMetrics(metrics);

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
            + "    {\"unit\": \"Distance\", \"title\": \"I can go the distance\", \"description\": \"Herculees\", \"rank_asc\": \"true\"}\n"
            + "  ]\n"
            + "}\n";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    ActivityQualificationMetric metricAfter =
        activityRepository.findById(activityId).get().getMetrics().get(0);
    org.junit.jupiter.api.Assertions.assertEquals(Unit.Distance, metricAfter.getUnit());
    org.junit.jupiter.api.Assertions.assertEquals("I can go the distance", metricAfter.getTitle());
    org.junit.jupiter.api.Assertions.assertEquals("Herculees", metricAfter.getDescription());
    org.junit.jupiter.api.Assertions.assertTrue(metricAfter.getRankByAsc());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void
      editActivityMetricWithoutUnitByUsingEditActivityRequestReturnStatusBadRequestAndActivityMetricIsNotEdited()
          throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    activityQualificationMetricRepository.save(metric);

    List<ActivityQualificationMetric> metrics = new ArrayList<>();
    metrics.add(metric);
    activity.setMetrics(metrics);

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
            + "    {\"title\": \"I can go the distance\", \"description\": \"Herculees\"}\n"
            + "  ]\n"
            + "}\n";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());

    ActivityQualificationMetric metricAfter =
        activityRepository.findById(activityId).get().getMetrics().get(0);
    org.junit.jupiter.api.Assertions.assertEquals(Unit.Count, metricAfter.getUnit());
    Assertions.assertNull(metricAfter.getTitle());
    Assertions.assertNull(metricAfter.getDescription());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void
      editActivityMetricWithoutTitleByUsingEditActivityRequestReturnStatusBadRequestAndActivityMetricIsNotEdited()
          throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric.setTitle("Hello");
    activityQualificationMetricRepository.save(metric);

    List<ActivityQualificationMetric> metrics = new ArrayList<>();
    metrics.add(metric);
    activity.setMetrics(metrics);

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

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());

    ActivityQualificationMetric metricAfter =
        activityRepository.findById(activityId).get().getMetrics().get(0);
    org.junit.jupiter.api.Assertions.assertEquals(Unit.Count, metricAfter.getUnit());
    org.junit.jupiter.api.Assertions.assertEquals(metric.getTitle(), metricAfter.getTitle());
    Assertions.assertNull(metricAfter.getDescription());
  }

  @Transactional // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Test
  void
      editActivityMetricWithEmptyMetricListUsingEditActivityRequestReturnStatusOkAndExistingActivityMetricIsRemoved()
          throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric.setTitle("Hello");
    activityQualificationMetricRepository.save(metric);

    List<ActivityQualificationMetric> metrics = new ArrayList<>();
    metrics.add(metric);
    activity.setMetrics(metrics);

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

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    org.junit.jupiter.api.Assertions.assertTrue(
        activityRepository.findById(activityId).get().getMetrics().isEmpty());
  }


}
