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
import com.springvuegradle.team6.models.entities.ActivityResultCount;
import com.springvuegradle.team6.models.entities.ActivityResultDistance;
import com.springvuegradle.team6.models.entities.ActivityResultDuration;
import com.springvuegradle.team6.models.entities.ActivityResultStartFinish;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

  @Test
  void getAllActivityResultsJustOne() throws Exception {
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
                        "/profiles/activities/{activityId}/result", activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
    // Added to make sure result set actually contains data
    String valueString = result.getJSONObject(0).get("value").toString();
    Assert.assertEquals("5.2", valueString);
  }

  @Test
  void getAllActivityResultsTwo() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityQualificationMetricRepository);

    Profile profile = profileRepository.findById(id);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance1 =
        new ActivityResultDistance(metric, profile, 4.5f);
    activityResultRepository.save(activityResultDistance1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/activities/{activityId}/result", activity.getId())
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
  void getAllActivityResultsTwoFromDifferentProfiles() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityQualificationMetricRepository);

    Profile profile = profileRepository.findById(id);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);

    Profile profile1 = TestDataGenerator.createExtraProfile(profileRepository);

    ActivityResultDistance activityResultDistance1 =
        new ActivityResultDistance(metric, profile1, 4.5f);
    activityResultRepository.save(activityResultDistance1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/activities/{activityId}/result", activity.getId())
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
  void testCreatorGetAllActivityResultsPrivateActivity() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);
    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testCreatorGetAllActivityResultsRestrictedActivity() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void testAdminGetAllActivityResultsRestrictedActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void testAdminGetAllActivityResultsPrivateActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testGuestGetAllActivityResultsPrivateActivityAndExpectForbidden() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGuestGetAllActivityResultsRestrictedActivityAndExpectForbidden() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetAllActivityResultsRestrictedActivityWhenAuthorised() throws Exception {
    Profile profile = profileRepository.findById(id);
    int id2 = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

    Activity activity = activityRepository.findById(activityId).get();
    activity.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity);
    TestDataGenerator.addActivityRole(
        profileRepository.findById(id2), activity, ActivityRoleType.Access, activityRoleRepository);

    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testGetAllActivityResultsWithUnidentifiedPathVariable() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", 342425)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isNotFound());

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", 9365859)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetAllActivityResultsNoResults() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/activities/{activityId}/result", activity.getId())
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
  void testGuestGetAllActivityResultsPublicActivity() throws Exception {
    TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    Activity activity = activityRepository.findById(activityId).get();
    Profile profile = profileRepository.findById(id);
    createDummyMetricAndResult(activity, profile);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updateActivityResultCountAndExpectOK() throws Exception {
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
        "{\n" + "  \"metric_id\": \"" + metric.getId() + "\",\n" + "  \"value\": \"20\"\n" + "}";

    String jsonStringUser =
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(jsonStringUser)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 10);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    Assert.assertEquals(20, result.get().getValue());
  }

  @Test
  void updateActivityResultDistanceAndExpectOK() throws Exception {
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

    float distanceFloat = (float) 20.34;
    ActivityResultDistance distanceResult =
        new ActivityResultDistance(metric, profile1, distanceFloat);
    distanceResult = activityResultRepository.save(distanceResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        distanceResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultDistance> result =
        activityResultRepository.findSpecificDistanceResult(Integer.parseInt(resultId));
    Assert.assertEquals(10.23, result.get().getValue(), 0.01);
  }

  @Test
  void updateActivityResultStartEndDateAndExpectOK() throws Exception {
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

    LocalDateTime start = LocalDateTime.parse("2015-08-04T10:11:30");
    LocalDateTime end = LocalDateTime.parse("2015-08-04T10:11:40");
    ActivityResultStartFinish result = new ActivityResultStartFinish(metric, profile1, start, end);
    result = activityResultRepository.save(result);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        result.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LocalDateTime expectedStart = LocalDateTime.parse("2019-06-06T14:00:00");
    LocalDateTime expectedEnd = LocalDateTime.parse("2019-06-06T16:00:00");
    Optional<ActivityResultStartFinish> result1 =
        activityResultRepository.findSpecificStartFinishResult(Integer.parseInt(resultId));
    Assert.assertEquals(expectedStart, result1.get().getStart());
    Assert.assertEquals(expectedEnd, result1.get().getFinish());
  }

  @Test
  void updateActivityResultDurationAndExpectOK() throws Exception {
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

    Duration duration = Duration.parse("PT20.345S");
    Duration expectedDuration = Duration.parse("PT1H");
    ActivityResultDuration result = new ActivityResultDuration(metric, profile1, duration);
    result = activityResultRepository.save(result);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        result.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultDuration> result1 =
        activityResultRepository.findSpecificDurationResult(Integer.parseInt(resultId));
    Assert.assertEquals(expectedDuration, result1.get().getValue());
  }

  @Test
  void updateOwnActivityResultWhileNotParticipantAndExpect4xx() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 10);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    profile1.getId(),
                    activity.getId(),
                    countResult.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void ownerUpdateResultForNonParticipantReturns4xx() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 10);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/result/{result}",
                    profile1.getId(),
                    activity.getId(),
                    countResult.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void ownerUpdateResultForParticipantExpectOK() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result1 =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    Assert.assertEquals(10, result1.get().getValue());
  }

  @Test
  void ownerUpdateResultForSelfAndExpectOk() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile, 20);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{result}",
                        profile.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result1 =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    System.err.println(result1.isPresent());
    Assert.assertEquals(10, result1.get().getValue());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminUpdatesResultForOwnerAndExpectOK() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile, 20);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result1 =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    Assert.assertEquals(10, result1.get().getValue());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminUpdateResultForParticipantAnExpectOk() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile1.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result1 =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    Assert.assertEquals(10, result1.get().getValue());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void adminUpdateResultForNonParticipantAndExpect4xx() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    profile1.getId(),
                    activity.getId(),
                    countResult.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void participantUpdatesResultForAnotherParticipantAndExpect4xx() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile2, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    profile2.getId(),
                    activity.getId(),
                    countResult.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Creates an activity metric that is associated to a particular activity result Useful to test
   * get requests to make sure the right amount of results are returned
   *
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
                        "/profiles/{profileId}/activities/{activityId}/metrics",
                        activity.getProfile().getId(),
                        activityId)
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
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

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
                    "/profiles/{profileId}/activities/{activityId}/metrics",
                    activity.getProfile().getId(),
                    activityId)
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
        "{\n" + "  \"email\": \"example1@email.com\",\n" + "  \"password\": \"Password1\"\n" + "}";

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
                    "/profiles/{profileId}/activities/{activityId}/metrics",
                    activity.getProfile().getId(),
                    activityId)
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
                        "/profiles/{profileId}/activities/{activityId}/metrics",
                        activity.getProfile().getId(),
                        activityId)
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
  void organiserUpdatesResultForAnotherOrganiserAndExpect4xx() throws Exception {
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
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
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
    activityRole2.setActivityRoleType(ActivityRoleType.Organiser);
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile2, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    profile2.getId(),
                    activity.getId(),
                    countResult.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void organiserUpdatesResultForParticipantAndExpectOK() throws Exception {
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
    activityRole.setActivityRoleType(ActivityRoleType.Organiser);
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
    activityRole2.setProfile(profile2);
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile2, 20);
    countResult = activityResultRepository.save(countResult);

    String resultId =
        mvc.perform(
                MockMvcRequestBuilders.put(
                        "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                        profile2.getId(),
                        activity.getId(),
                        countResult.getId())
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Optional<ActivityResultCount> result1 =
        activityResultRepository.findSpecificCountResult(Integer.parseInt(resultId));
    Assert.assertEquals(10, result1.get().getValue());
  }

  @Test
  void deleteActivityResultIsParticipantReturnOK() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

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
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    countResult.getId())
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> activityResults =
        activityResultRepository.findSingleUsersResultsOnActivity(activityId, profile1.getId());
    Assert.assertEquals(0, activityResults.size());
  }

  @Test
  void deleteActivityResultIsNotParticipantReturn4xxError() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

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
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    countResult.getId())
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteActivityResultDoesNotExistReturn4xxError() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

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
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    100)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteActivityResultAsOwnerReturnOk() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    countResult.getId())
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> activityResults =
        activityResultRepository.findSingleUsersResultsOnActivity(activityId, profile1.getId());
    Assert.assertEquals(0, activityResults.size());
  }

  @Test
  void deleteAnotherProfilesActivityResultReturn4xx() throws Exception {
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
    profile2.setFirstname("Johnny");
    profile2.setLastname("Dong");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("example2@email.com"));
    profile2.setEmails(email2);
    profile2.setPassword("Password1");
    profile2 = profileRepository.save(profile2);

    ActivityRole activityRole1 = new ActivityRole();
    activityRole1.setActivity(activity);
    activityRole1.setProfile(profile2);
    activityRole1.setActivityRoleType(ActivityRoleType.Participant);
    activityRoleRepository.save(activityRole1);

    ActivityResultCount countResult1 = new ActivityResultCount(metric, profile1, 20);
    countResult1 = activityResultRepository.save(countResult1);

    ActivityResultCount countResult2 = new ActivityResultCount(metric, profile2, 30);
    countResult2 = activityResultRepository.save(countResult2);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

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
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    countResult2.getId())
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"USER", "ADMIN"})
  void deleteActivityResultAsAdminReturnOk() throws Exception {
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

    ActivityResultCount countResult = new ActivityResultCount(metric, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
                    activity.getProfile().getId(),
                    activity.getId(),
                    countResult.getId())
                .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> activityResults =
        activityResultRepository.findSingleUsersResultsOnActivity(activityId, profile1.getId());
    Assert.assertEquals(0, activityResults.size());
  }

  @Test
  void getSingleMetricActivityResultsTestReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/{activityId}/result/{metricId}", activityId, metric1.getId())
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
  void getSingleMetricActivityResultsSortedAscTestReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1.setRankByAsc(true);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultCount countResult2 = new ActivityResultCount(metric1, profile1, 30);
    countResult2 = activityResultRepository.save(countResult2);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/{activityId}/result/{metricId}",
                activityId,
                metric1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONArray result = new JSONArray(response);
    JSONObject object = result.getJSONObject(0);
    String value = object.getString("value");
    org.junit.jupiter.api.Assertions.assertEquals(20, Integer.parseInt(value));
  }

  @Test
  void getSingleMetricActivityResultsSortedDescTestReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1.setRankByAsc(false);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultCount countResult2 = new ActivityResultCount(metric1, profile1, 30);
    countResult2 = activityResultRepository.save(countResult2);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/{activityId}/result/{metricId}",
                activityId,
                metric1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONArray result = new JSONArray(response);
    JSONObject object = result.getJSONObject(0);
    String value = object.getString("value");
    org.junit.jupiter.api.Assertions.assertEquals(30, Integer.parseInt(value));
  }

  @Test
  void getSingleMetricActivityResultMetricNotFoundReturn4xx() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}/result/{metricId}", activityId, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getSingleMetricActivityResultsForUserTestReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/{activityId}/result/{metricId}/{profileId}",
                activityId,
                metric1.getId(),
                profile1.getId())
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
  void getSingleMetricActivityResultsForUserTestInSortedAscOrderReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1.setRankByAsc(true);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultCount countResult2 = new ActivityResultCount(metric1, profile1, 30);
    countResult2 = activityResultRepository.save(countResult2);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/{activityId}/result/{metricId}/{profileId}",
                activityId,
                metric1.getId(),
                profile1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONArray result = new JSONArray(response);
    JSONObject object = result.getJSONObject(0);
    String value = object.getString("value");
    org.junit.jupiter.api.Assertions.assertEquals(20, Integer.parseInt(value));
  }

  @Test
  void getSingleMetricActivityResultsForUserTestInSortedDescOrderReturnsOk() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric1 = new ActivityQualificationMetric();
    metric1.setTitle("title");
    metric1.setActivity(activity);
    metric1.setUnit(Unit.Count);
    metric1.setRankByAsc(false);
    metric1 = activityQualificationMetricRepository.save(metric1);

    ActivityQualificationMetric metric2 = new ActivityQualificationMetric();
    metric2.setTitle("title");
    metric2.setActivity(activity);
    metric2.setUnit(Unit.Distance);
    metric2 = activityQualificationMetricRepository.save(metric2);

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

    ActivityResultCount countResult = new ActivityResultCount(metric1, profile1, 20);
    countResult = activityResultRepository.save(countResult);

    ActivityResultCount countResult2 = new ActivityResultCount(metric1, profile1, 30);
    countResult2 = activityResultRepository.save(countResult2);

    ActivityResultDistance distanceResult = new ActivityResultDistance(metric2, profile1, 10f);
    distanceResult = activityResultRepository.save(distanceResult);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/activities/{activityId}/result/{metricId}/{profileId}",
                activityId,
                metric1.getId(),
                profile1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONArray result = new JSONArray(response);
    JSONObject object = result.getJSONObject(0);
    String value = object.getString("value");
    org.junit.jupiter.api.Assertions.assertEquals(30, Integer.parseInt(value));
  }

  @Test
  void createActivityResultCountMetricIsNotEditable() throws Exception {
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

    ActivityQualificationMetric newMetric = activityQualificationMetricRepository.findById(metric.getId()).get();
    Assert.assertEquals(true, metric.getEditable());
    Assert.assertEquals(false, newMetric.getEditable());
  }

  @Test
  void addMultipleResultsAndDeleteAllMetricIsEditable() throws Exception {
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

    mvc.perform(
        MockMvcRequestBuilders.post(
            "/profiles/{profileId}/activities/{activityId}/result",
            profile1.getId(),
            activity.getId())
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());

    mvc.perform(
        MockMvcRequestBuilders.post(
            "/profiles/{profileId}/activities/{activityId}/result",
            profile1.getId(),
            activity.getId())
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(status().isOk());

    List<ActivityResult> results = activityResultRepository.findSingleUsersResultsOnActivity(activityId,profile1.getId());
    for (ActivityResult result : results) {
      mvc.perform(
          MockMvcRequestBuilders.delete(
              "/profiles/{profileId}/activities/{activityId}/result/{resultId}",
              activity.getProfile().getId(),
              activity.getId(),
              result.getId())
              .session(session))
          .andExpect(status().isOk());
    }

    ActivityQualificationMetric newMetric = activityQualificationMetricRepository.findById(metric.getId()).get();
    Assert.assertEquals(true, newMetric.getEditable());
  }

  @Test
  void deleteActivityMetricIsEditable() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric = activityQualificationMetricRepository.save(metric);

    mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}/{metricId}",
            id,
            activity.getId(),
            metric.getId())
            .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void deleteActivityMetricNotEditable() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric.setEditable(false);
    metric = activityQualificationMetricRepository.save(metric);

    mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}/{metricId}",
            id,
            activity.getId(),
            metric.getId())
            .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteActivityMetricNotOwner() throws Exception {
    Activity activity = activityRepository.findById(activityId).get();

    // Create metric
    ActivityQualificationMetric metric = new ActivityQualificationMetric();
    metric.setTitle("title");
    metric.setActivity(activity);
    metric.setUnit(Unit.Count);
    metric.setEditable(false);
    metric = activityQualificationMetricRepository.save(metric);

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("example1@email.com"));
    profile1.setEmails(email1);
    profile1.setPassword("Password1");
    profile1 = profileRepository.save(profile1);

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
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}/{metricId}",
            id,
            activity.getId(),
            metric.getId())
            .session(session))
        .andExpect(status().is4xxClientError());
  }
}
