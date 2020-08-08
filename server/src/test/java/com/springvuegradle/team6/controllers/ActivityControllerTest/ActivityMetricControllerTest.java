package com.springvuegradle.team6.controllers.ActivityControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityHistory;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityResult;
import com.springvuegradle.team6.models.entities.ActivityResultDistance;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Unit;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityHistoryRepository;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityMetricControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityQualificationMetricRepository activityQualificationMetricRepository;

  @Autowired private ActivityResultRepository activityResultRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private ActivityHistoryRepository activityHistoryRepository;

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  private int id;

  private int activityId;

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

    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);
    activityId = activity.getId();
  }

  @Test
  void createActivityResultCount() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  void createActivityResultDistance() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Distance);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10.23\"\n" + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  void createActivityResultStartEndDate() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.TimeStartFinish);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n"
            + "  \"metric_id\": \""
            + metric.getId()
            + "\",\n"
            + "  \"start\": \"2019-06-06T14:00:00\",\n"
            + "  \"end\": \"2019-06-06T16:00:00\"\n"
            + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  void createActivityResultDurationReturnsOK() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.TimeDuration);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n"
            + "  \"metric_id\": \""
            + metric.getId()
            + "\",\n"
            + "  \"value\": \"01:00:00\"\n"
            + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  void createOwnActivityResultNotParticipantReturns4xx() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void ownerCreateResultForNonParticipantReturns4xx() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void ownerCreateResultForParticipantReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  void ownerCreatesResultForSelfReturnsOk() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile);
    activityRole.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory =
        activityHistoryRepository.findByActivity_id(activity.getId());
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminCreatesResultForOwnerIsOK() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile);
    activityRole.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory =
        activityHistoryRepository.findByActivity_id(activity.getId());
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminCreateResultForParticipantReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile1.getId());
    Assert.assertEquals(1, results.size());

    Set<ActivityHistory> foundHistory = activityHistoryRepository.findByActivity_id(activityId);
    Assert.assertEquals(1, foundHistory.size());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminCreateResultForNonParticipantReturns4xx() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile1.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void participantCreatesResultForAnotherParticipantReturns4xx() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole);

    Profile profile2 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("example12@email.com"));
    profile2.setEmails(email2);
    profile2.setPassword("Password1");
    profile2 = profileRepository.save(profile2);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setActivity(activity);
    activityRole2.setProfile(profile1);
    activityRole2.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole2);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonString =
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"10\"\n" + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile2.getId(),
                    activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Creates an activity metric that is associated to a particular activity result
   * Useful to test get requests to make sure the right amount of results are returned
   * @param activity the activity the metric is associated with
   * @param profile the profile that the result is associated with
   */
  private void createDummyMetricAndResult(Activity activity, Profile profile) {
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityQualificationMetricRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
  }

  @Test
  void testGetASingleActivityResult() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityQualificationMetricRepository);

    Profile profile = profileRepository.findById(id);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        profile.getId(),
                        activity.getId())
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
  void testGetASingleActivityMultipleResults() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    Profile profile = profileRepository.findById(id);

    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityQualificationMetricRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(activityMetrics, profile, 4.0f);
    activityResultRepository.save(activityResultDistance2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        profile.getId(),
                        activity.getId())
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
  void testResultReturnedIsNotEmptyList() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/profiles/{profileId}/activities/{activityId}/result",
                profile.getId(),
                activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    String activityResult = result.getJSONObject(0).get("result").toString();
    org.junit.jupiter.api.Assertions.assertEquals("5.2", activityResult);
  }

  @Test
  void testCreatorGetActivityResultPrivateActivity() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);
    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testCreatorGetActivityResultRestrictedActivity() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void testAdminGetActivityResultRestrictedActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void testAdminGetActivityResultPrivateActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testGuestGetActivityResultPrivateActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGuestGetActivityResultRestrictedActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetActivityResultRestrictedActivityAuthorised() throws Exception {
    Profile profile = profileRepository.findById(id);
    int id2 = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    TestDataGenerator.addActivityRole(
        profileRepository.findById(id2), activity, ActivityRoleType.Access, activityRoleRepository);

    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result", id, activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testGetActivityResultActivityBadValues() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result", id, 342425)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isNotFound());

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result", 9999, activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetActivityResultNoActivities() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    Profile profile = profileRepository.findById(id);
    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGuestViewPublicActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/result",
                    profile.getId(),
                    activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void ownerGetActivityMetricReturnsStatusOKAndMetric() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    String response =
    mvc.perform(
        MockMvcRequestBuilders.get(
            "/profiles/{profileId}/activities/{activityId}/metrics", activity.getProfile().getId(), activityId)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void userWithoutAccessGetActivityMetricReturnStatus4xxError() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);

    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    String jsonStringUser =
            "{\n" +
                    "  \"email\": \"example1@email.com\",\n" +
                    "  \"password\": \"Password1\"\n" +
                    "}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
            .andExpect(status().isOk())
            .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                    .content(jsonStringUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/metrics", activity.getProfile().getId(), activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is4xxClientError());

  }

  @Test
  void userWithAccessGetActivityMetricReturnStatusOkAndMetric() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);

    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile1);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole);

    String jsonStringUser =
            "{\n" +
                    "  \"email\": \"example1@email.com\",\n" +
                    "  \"password\": \"Password1\"\n" +
                    "}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
            .andExpect(status().isOk())
            .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                    .content(jsonStringUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get(
                    "/profiles/{profileId}/activities/{activityId}/metrics", activity.getProfile().getId(), activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
          username = "admin",
          roles = {"USER", "ADMIN"})
  void adminGetActivityMetricReturnsStatusOKAndMetric() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get(
                            "/profiles/{profileId}/activities/{activityId}/metrics", activity.getProfile().getId(), activityId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }
}
