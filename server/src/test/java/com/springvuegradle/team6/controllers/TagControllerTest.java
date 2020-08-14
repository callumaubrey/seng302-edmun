package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Role;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.internal.matchers.Null;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class TagControllerTest {

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private ActivityRoleRepository activityRoleRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private MockMvc mvc;

  private MockHttpSession session;

  private Profile adminProfile;

  private int id;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();

    //Create an admin user
    adminProfile = new Profile();
    adminProfile.setFirstname("Doe");
    adminProfile.setLastname("John");
    adminProfile.setPassword("Password1");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("admin@email.com"));
    Set<Role> roles = new HashSet<Role>();
    roles.add(roleRepository.findByName("ROLE_USER_ADMIN"));
    adminProfile.setEmails(email2);
    adminProfile.setRoles(roles);
    adminProfile = profileRepository.save(adminProfile);

    //Create john doe user profile
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
  void testGetHashtagAutocompleteHashtagLengthLessThanThreeReturnInitialHashtag() throws Exception {
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=co", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals("co", arr.get(0));
  }

  @Test
  void testGetHashtagAuctocompleteReturnCorrectOrder() throws Exception {
    Tag cool = new Tag();
    cool.setName("is_cool");
    cool = tagRepository.save(cool);

    Tag cold = new Tag();
    cold.setName("is_cold");
    cold = tagRepository.save(cold);

    Tag colour = new Tag();
    colour.setName("is_colour");
    colour = tagRepository.save(colour);

    Tag awesome = new Tag();
    awesome.setName("awesome");
    awesome = tagRepository.save(awesome);

    Profile profile = profileRepository.getOne(id);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cold);
    tags.add(cool);
    activity.setTags(tags);
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cold);
    tags1.add(colour);
    activity1.setTags(tags1);
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setActivityName("Burnside Park Rugby");
    activity2.setContinuous(true);
    Set<Tag> tags2 = new HashSet<>();
    tags2.add(cold);
    tags2.add(awesome);
    tags2.add(cool);
    activity2.setTags(tags2);
    activityRepository.save(activity2);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=is_c", id)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");

    List<String> expectedResultList = new ArrayList<>();
    expectedResultList.add("is_cold");
    expectedResultList.add("is_cool");
    expectedResultList.add("is_colour");
    JSONArray expectedResult = new JSONArray(expectedResultList);

    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, arr);
  }

  @Test
  void testGetHashtagAutocompleteReturnTenResultsMax() throws Exception {
    Profile profile = profileRepository.getOne(id);
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    for (int i = 0; i < 20; i++) {
      Tag tag = new Tag();
      tag.setName("number" + i);
      tag = tagRepository.save(tag);
      tags.add(tag);
    }
    activity.setTags(tags);
    activityRepository.save(activity);

    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=number", id)
                .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(10, arr.length());
  }

  @Test
  void testGetActivitiesByHashTagReturnStatusOkReturnOneResult() throws Exception {
    // Set up data
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);

    tagRepository.save(tag);
    activityRepository.save(activity);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagReturnStatusOkReturnNoResult() throws Exception {
    Tag tag = new Tag();
    tag.setName("myrun");
    tagRepository.save(tag);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagReturnStatusOkReturnCorrectOrder() throws Exception {
    Tag cold = new Tag();
    cold.setName("is_cold");
    cold = tagRepository.save(cold);

    Profile profile = profileRepository.getOne(id);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity.setCreationDate(LocalDateTime.now());
    Set<Tag> tags = new HashSet<>();
    tags.add(cold);
    activity.setTags(tags);
    activityRepository.save(activity);

    // Wait 1 second because sometimes the second activity has LocalDateTime.now() being earlier
    // than the previous activity
    TimeUnit.SECONDS.sleep(1);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    activity1.setCreationDate(LocalDateTime.now());
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cold);
    activity1.setTags(tags1);
    activityRepository.save(activity1);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/" + cold.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());

    LocalDateTime dateTime = LocalDateTime.parse(result.getJSONObject(0).getString("creationDate"));
    LocalDateTime dateTime1 =
        LocalDateTime.parse(result.getJSONObject(1).getString("creationDate"));
    org.junit.jupiter.api.Assertions.assertTrue(dateTime.isAfter(dateTime1));
  }

  @Test
  void getActivitiesByHashTagWhenActivityArchivedReturnNoResults() throws Exception {
    // Set up data
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<Tag>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);

    tagRepository.save(tag);
    int activityId = activityRepository.save(activity).getId();

    // Delete activity
    mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}", id, activityId)
            .session(session))
        .andExpect(status().isOk());
    activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertTrue(activity.isArchived());

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void getActivitiesByHashTagWhenActivityArchivedReturnOneResult() throws Exception {
    // Set up data
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<Tag>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    tagRepository.save(tag);
    int activityId = activityRepository.save(activity).getId();

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    activity1.setCreationDate(LocalDateTime.now());
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(tag);
    activity1.setTags(tags1);
    activity1 = activityRepository.save(activity1);

    // Delete activity
    mvc.perform(
        MockMvcRequestBuilders.delete(
            "/profiles/{profileId}/activities/{activityId}", id, activityId)
            .session(session))
        .andExpect(status().isOk());
    activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertTrue(activity.isArchived());

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagReturnStatusOkReturnPrivateActivities() throws Exception {
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setVisibilityType(VisibilityType.Private);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);

    tagRepository.save(tag);
    activityRepository.save(activity);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagReturnStatusOkReturnRestrictedActivities() throws Exception {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("mista@seng302.com");
    email.setPrimary(true);
    emails.add(email);
    Profile otherProfile = new Profile();
    otherProfile.setFirstname("Mista");
    otherProfile.setLastname("Mista");
    otherProfile.setEmails(emails);
    otherProfile.setDob("1977-01-01");
    otherProfile.setPassword("Password1");
    otherProfile.setGender("male");
    otherProfile = profileRepository.save(otherProfile);

    Tag tag = new Tag();
    tag.setName("myrun");
    tagRepository.save(tag);

    // Activity that is restricted and owned by user
    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setVisibilityType(VisibilityType.Restricted);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activityRepository.save(activity);

    ActivityRole activityRole = new ActivityRole();
    activityRole.setActivity(activity);
    activityRole.setProfile(profile);
    activityRole.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(activityRole);

    // Activity that is restricted and owned by other user
    Activity activity1 = new Activity();
    activity1.setProfile(otherProfile);
    activity1.setActivityName("Run at Avonhead Park");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(tag);
    activity1.setTags(tags);
    activity1.setVisibilityType(VisibilityType.Restricted);
    activityRepository.save(activity1);

    ActivityRole activityRole1 = new ActivityRole();
    activityRole1.setActivity(activity1);
    activityRole1.setProfile(otherProfile);
    activityRole1.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(activityRole1);

    ActivityRole activityRole2 = new ActivityRole();
    activityRole2.setActivity(activity1);
    activityRole2.setProfile(profile);
    activityRole2.setActivityRoleType(ActivityRoleType.Access);
    activityRoleRepository.save(activityRole2);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagAsUserReturnStatusOkReturnThreeActivities() throws Exception {
    //Set up data
    Tag tag = new Tag();
    tag.setName("myrun");
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);

    //Create activities for normal user
    //Public (should be returned)
    Activity publicActivity = new Activity();
    publicActivity.setActivityName("Public User Activity");
    publicActivity.setTags(tags);
    publicActivity.setContinuous(false);
    publicActivity.setVisibilityType(VisibilityType.Public);
    publicActivity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    publicActivity.setProfile(profile);

    //Private (should be returned as own activity)
    Activity privateActivity = new Activity();
    privateActivity.setActivityName("Private User Activity");
    privateActivity.setTags(tags);
    privateActivity.setContinuous(false);
    privateActivity.setVisibilityType(VisibilityType.Private);
    privateActivity.setDescription("description blah blah");
    privateActivity.setProfile(profile);

    //Admin made activities
    //Public (Should be returned)
    Activity adminPublicActivity = new Activity();
    adminPublicActivity.setActivityName("Public Admin Activity");
    adminPublicActivity.setTags(tags);
    adminPublicActivity.setContinuous(false);
    adminPublicActivity.setVisibilityType(VisibilityType.Public);
    adminPublicActivity.setDescription("description blah blah");
    adminPublicActivity.setProfile(adminProfile);

    //Private (Should not be returned as is a private activity and not admin)
    Activity adminPrivateActivity = new Activity();
    adminPrivateActivity.setActivityName("Private Admin Activity");
    adminPrivateActivity.setTags(tags);
    adminPrivateActivity.setContinuous(false);
    adminPrivateActivity.setVisibilityType(VisibilityType.Private);
    adminPrivateActivity.setDescription("description blah blah");
    adminPrivateActivity.setProfile(adminProfile);

    tagRepository.save(tag);
    activityRepository.save(adminPrivateActivity);
    activityRepository.save(adminPublicActivity);
    activityRepository.save(privateActivity);
    activityRepository.save(publicActivity);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
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
  void testGetActivitiesByHashtagAsAdminReturnStatusOkReturnFourActivities() throws Exception {
    //Set up data
    //Create the tag to search by
    Tag tag = new Tag();
    tag.setName("myrun");
    Set<Tag> tags = new HashSet<>();
    tags.add(tag);
    tagRepository.save(tag);

    //Login as admin
    String loginInfo = "{\n" +
        "\t\"email\" : \"admin@email.com\",\n" +
        "\t\"password\": \"Password1\"\n" +
        "}";
    mvc.perform(MockMvcRequestBuilders
        .post("/login")
        .content(loginInfo)
        .contentType(MediaType.APPLICATION_JSON)
        .session(session)
    ).andExpect(status().isOk());

    //Create activities for normal user
    //Public (should be returned)
    Activity publicActivity = new Activity();
    publicActivity.setActivityName("Public User Activity");
    publicActivity.setTags(tags);
    publicActivity.setContinuous(false);
    publicActivity.setVisibilityType(VisibilityType.Public);
    publicActivity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    publicActivity.setProfile(profile);

    //Private (should be returned)
    Activity privateActivity = new Activity();
    privateActivity.setActivityName("Private User Activity");
    privateActivity.setTags(tags);
    privateActivity.setContinuous(false);
    privateActivity.setVisibilityType(VisibilityType.Private);
    privateActivity.setDescription("description blah blah");
    privateActivity.setProfile(profile);

    //Admin made activities
    //Public (Should be returned)
    Activity adminPublicActivity = new Activity();
    adminPublicActivity.setActivityName("Public Admin Activity");
    adminPublicActivity.setTags(tags);
    adminPublicActivity.setContinuous(false);
    adminPublicActivity.setVisibilityType(VisibilityType.Public);
    adminPublicActivity.setDescription("description blah blah");
    adminPublicActivity.setProfile(adminProfile);

    //Private (Should be returned)
    Activity adminPrivateActivity = new Activity();
    adminPrivateActivity.setActivityName("Private Admin Activity");
    adminPrivateActivity.setTags(tags);
    adminPrivateActivity.setContinuous(false);
    adminPrivateActivity.setVisibilityType(VisibilityType.Private);
    adminPrivateActivity.setDescription("description blah blah");
    adminPrivateActivity.setProfile(adminProfile);

    activityRepository.save(adminPrivateActivity);
    activityRepository.save(adminPublicActivity);
    activityRepository.save(privateActivity);
    activityRepository.save(publicActivity);

    // Actual MVC response
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get("/activities/hashtag/myrun")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(4, result.length());
  }
}
