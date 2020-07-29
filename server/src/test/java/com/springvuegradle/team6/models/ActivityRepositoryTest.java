package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class ActivityRepositoryTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private TagRepository tagRepository;

  private Profile profile;

  @BeforeEach
  void setup() throws Exception {
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
  }

  @Test
  void testFindByProfile_IdAndArchivedFalseReturnOneActivity() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity = activityRepository.save(activity);
    List<Activity> result = activityRepository.findByProfile_IdAndArchivedFalse(profile.getId());
    List<Activity> expectedResult = new ArrayList<>();
    expectedResult.add(activity);
    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testFindByProfile_IdAndArchivedFalseWithArchivedActivityReturnNoActivities() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setArchived(true);
    activity = activityRepository.save(activity);
    List<Activity> result = activityRepository.findByProfile_IdAndArchivedFalse(profile.getId());
    List<Activity> expectedResult = new ArrayList<>();
    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testFindByProfile_IdAndArchivedFalseReturnMultipleActivities() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1 = activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2 = activityRepository.save(activity2);

    List<Activity> result = activityRepository.findByProfile_IdAndArchivedFalse(profile.getId());
    List<Activity> expectedResult = new ArrayList<>();
    expectedResult.add(activity);
    expectedResult.add(activity1);
    expectedResult.add(activity2);
    org.junit.jupiter.api.Assertions.assertTrue(expectedResult.containsAll(result));
  }

  @Test
  void testGetActivityTags() {
    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Tag cold = new Tag();
    cold.setName("cold");
    cold = tagRepository.save(cold);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cold);
    tags.add(cool);
    activity.setTags(tags);
    activity = activityRepository.save(activity);

    Set<String> expectedResult = new HashSet<>();

    expectedResult.add("cold");
    expectedResult.add("cool");
    Set<Tag> result = activityRepository.getActivityTags(activity.getId());
    Set<String> resultStrings = new HashSet<>();
    for (Tag tag : result) {
      resultStrings.add(tag.getName());
    }
    org.junit.jupiter.api.Assertions.assertTrue(resultStrings.containsAll(expectedResult));
  }

  @Test
  void testFindActivitiesThatAreNotPrivate() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setVisibilityType("private");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityType("public");
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityType("private");
    activityRepository.save(activity2);

    List<Activity> result =
        activityRepository.findByProfile_IdAndArchivedFalseAndVisibilityTypeNotLike(
            profile.getId(), VisibilityType.Private);
    Assertions.assertEquals(1, result.size());
  }

  @Test
  void testAllActivitiesPrivateNoActivitiesReturned() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setVisibilityType("private");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityType("private");
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityType("private");
    activityRepository.save(activity2);

    List<Activity> result =
        activityRepository.findByProfile_IdAndArchivedFalseAndVisibilityTypeNotLike(
            profile.getId(), VisibilityType.Private);
    Assertions.assertEquals(0, result.size());
  }

  @Test
  void testFindActivitiesMultipleActivitiesNotPrivate() {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setVisibilityType("public");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityType("public");
    activityRepository.save(activity1);

    Activity activity3 = new Activity();
    activity3.setProfile(profile);
    activity3.setVisibilityType("private");
    activityRepository.save(activity3);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityType("public");
    activityRepository.save(activity2);

    List<Activity> result =
        activityRepository.findByProfile_IdAndArchivedFalseAndVisibilityTypeNotLike(
            profile.getId(), VisibilityType.Private);
    Assertions.assertEquals(3, result.size());
  }
}
