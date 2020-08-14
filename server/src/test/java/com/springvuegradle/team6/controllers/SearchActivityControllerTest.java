package com.springvuegradle.team6.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import io.cucumber.java.hu.Ha;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchActivityControllerTest {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  private Profile profile;

  @AfterAll
  void tearDown(@Autowired DataSource dataSource) throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("tearDown.sql"));
    }
  }

  @BeforeAll
  void setup() throws Exception {
    session = new MockHttpSession();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("poly@pocket.com");
    email.setPrimary(true);
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("Poly");
    profile.setLastname("Pocket");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("female");
    profile = profileRepository.save(profile);
    String login_url = "/login/";
    mvc.perform(
            post(login_url)
                .content(
                    "{\n"
                        + "\t\"email\" : \"poly@pocket.com\",\n"
                        + "\t\"password\": \"Password1\"\n"
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Email> emails2 = new HashSet<>();
    Email email2 = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile2 = new Profile();
    profile2.setFirstname("John");
    profile2.setLastname("Doe");
    profile2.setEmails(emails2);
    profile2.setDob("2010-01-01");
    profile2.setPassword("Password1");
    profile2.setGender("male");
    profileRepository.save(profile2);

    Tag running = new Tag("running");
    tagRepository.save(running);

    Tag canterbury = new Tag("canterbury");
    tagRepository.save(canterbury);

    Tag walking = new Tag("walking");
    tagRepository.save(walking);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Run);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setVisibilityType(VisibilityType.Public);
    activity.setProfile(profile);
    Set<Tag> tags = new HashSet<>();
    tags.add(running);
    activity.setTags(tags);
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setActivityName("Walking at Hagley Park");
    Set<ActivityType> activityTypes1 = new HashSet<>();
    activityTypes1.add(ActivityType.Walk);
    activity1.setActivityTypes(activityTypes1);
    activity1.setContinuous(false);
    activity1.setVisibilityType(VisibilityType.Public);
    activity1.setStartTime(
        LocalDateTime.parse(
            "2020-04-01T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity1.setEndTime(
        LocalDateTime.parse(
            "2020-04-01T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity1.setProfile(profile);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(walking);
    activity1.setTags(tags1);
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setActivityName("Running at Hagley Park");
    Set<ActivityType> activityTypes2 = new HashSet<>();
    activityTypes2.add(ActivityType.Run);
    activity2.setActivityTypes(activityTypes2);
    activity2.setContinuous(false);
    activity2.setVisibilityType(VisibilityType.Public);
    activity2.setStartTime(
        LocalDateTime.parse(
            "2020-04-03T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity2.setEndTime(
        LocalDateTime.parse(
            "2020-04-07T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity2.setProfile(profile);
    Set<Tag> tags2 = new HashSet<>();
    tags2.add(running);
    activity2.setTags(tags2);
    activityRepository.save(activity2);

    Activity activity3 = new Activity();
    activity3.setActivityName("Canterbury Triathlon");
    Set<ActivityType> activityTypes3 = new HashSet<>();
    activityTypes3.add(ActivityType.Run);
    activityTypes3.add(ActivityType.Bike);
    activityTypes3.add(ActivityType.Swim);
    activity3.setActivityTypes(activityTypes3);
    activity3.setContinuous(false);
    activity3.setVisibilityType(VisibilityType.Public);
    activity3.setStartTime(
        LocalDateTime.parse(
            "2020-04-05T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity3.setEndTime(
        LocalDateTime.parse(
            "2020-04-07T15:50:41+1300", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    activity3.setProfile(profile);
    Set<Tag> tags3 = new HashSet<>();
    tags3.add(running);
    tags3.add(canterbury);
    activity3.setTags(tags3);
    activityRepository.save(activity3);

    Activity activity4 = new Activity();
    activity4.setActivityName("Private activity");
    activity4.setContinuous(true);
    activity4.setVisibilityType(VisibilityType.Private);
    activity4.setProfile(profile2);
    activityRepository.save(activity4);

    Activity activity5 = new Activity();
    activity5.setActivityName("Restricted activity");
    activity5.setContinuous(true);
    activity5.setVisibilityType(VisibilityType.Restricted);
    activity5.setProfile(profile2);
    activityRepository.save(activity5);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity5);
    activityRole.setActivityRoleType(ActivityRoleType.Access);
    activityRole.setProfile(profile);
    activityRoleRepository.save(activityRole);
  }

  @Test
  void getActivitiesCountReturnOneActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities/count?name=race").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(1, Integer.parseInt(response));
  }

  @Test
  void getActivitiesCountReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities/count?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(2, Integer.parseInt(response));
  }

  @Test
  void getActivitiesByNameReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?name=Kaikoura%20Coast%20Track%20rac")
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
  void getActivitiesByPartialNameReturnOneActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=race").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void getActivitiesByPartialNameReturnTwoActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=Hagley").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesByActivityTypesWithOrReturnFourActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void getActivitiesWithOffsetReturnTwoResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or&offset=2")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithLimitReturnThreeResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20walk&types-method=or&limit=3")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesWithLimitWithOffsetReturnOneResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?types=run%20walk&types-method=or&limit=1&offset=1")
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
  void getActivitiesByActivityTypesWithAndReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?types=run%20swim%20bike&types-method=and")
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
  void getActivitiesByHashtagsWithOrReturnFourActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?hashtags=running%20walking&hashtags-method=or")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void getActivitiesByHashtagsWithAndReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?hashtags=running%20canterbury&hashtags-method=and")
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
  void getActivitiesWithContinuousReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=continuous").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationReturnThreeActivity() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=duration").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithStartDateReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=20200403")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithEndDateReturnTwoActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?time=duration&start-date=20200403")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void getActivitiesWithDurationWithStartDateWithEndDateReturnOneActivity() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?time=duration&start-date=20200401&end-date=20200402")
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
  void getActivitiesWithDurationWithStartDateWithEndDateReturnNoActivities() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities?time=duration&start-date=20300401&end-date=20300402")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void getActivitiesCanNotViewPrivateActivitiesReturnNoActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=private").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void getActivitiesCanViewRestrictedActivitiesWithAccessReturnOneActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?name=restricted").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getActivitiesAdminCanViewAllActivitiesReturnThreeActivities() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/activities?time=continuous").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void getActivitiesSearchWithMultipleOptionsReturnOneResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities?name=hagley&types=walk").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }
}
