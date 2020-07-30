package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import org.junit.jupiter.api.Assertions;
import io.cucumber.java.en_old.Ac;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class ActivityRepositoryTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private TagRepository tagRepository;

  private Profile profile;

  @BeforeEach
  void setup() {
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
    activityRepository.save(activity);
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
    activity.setVisibilityTypeByString("private");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityTypeByString("public");
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityTypeByString("private");
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
    activity.setVisibilityTypeByString("private");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityTypeByString("private");
    activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityTypeByString("private");
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
    activity.setVisibilityTypeByString("public");
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setVisibilityTypeByString("public");
    activityRepository.save(activity1);

    Activity activity3 = new Activity();
    activity3.setProfile(profile);
    activity3.setVisibilityTypeByString("private");
    activityRepository.save(activity3);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setVisibilityTypeByString("public");
    activityRepository.save(activity2);

    List<Activity> result =
        activityRepository.findByProfile_IdAndArchivedFalseAndVisibilityTypeNotLike(
            profile.getId(), VisibilityType.Private);
    Assertions.assertEquals(3, result.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnPublicActivityOwned() {
    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Public);
    activityRepository.save(activity);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnPublicActivityNotOwned() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("polypocket@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile otherProfile = new Profile();
    otherProfile.setFirstname("Poly");
    otherProfile.setLastname("Pocket");
    otherProfile.setEmails(emails);
    otherProfile.setDob("2010-10-10");
    otherProfile.setPassword("Password1");
    otherProfile.setGender("female");
    otherProfile = profileRepository.save(otherProfile);

    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(otherProfile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Public);
    activityRepository.save(activity);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnPrivateActivityOwned() {
    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagDoesNotReturnPrivateActivityNotOwned() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("polypocket@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile otherProfile = new Profile();
    otherProfile.setFirstname("Poly");
    otherProfile.setLastname("Pocket");
    otherProfile.setEmails(emails);
    otherProfile.setDob("2010-10-10");
    otherProfile.setPassword("Password1");
    otherProfile.setGender("female");
    otherProfile = profileRepository.save(otherProfile);

    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(otherProfile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnNoActivities() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("polypocket@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile otherProfile = new Profile();
    otherProfile.setFirstname("Poly");
    otherProfile.setLastname("Pocket");
    otherProfile.setEmails(emails);
    otherProfile.setDob("2010-10-10");
    otherProfile.setPassword("Password1");
    otherProfile.setGender("female");
    otherProfile = profileRepository.save(otherProfile);

    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(otherProfile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Public);
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Run at Avonhead Park");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cool);
    activity1.setTags(tags);
    activity1.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity1);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("hot", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnMultipleActivities() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("polypocket@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile otherProfile = new Profile();
    otherProfile.setFirstname("Poly");
    otherProfile.setLastname("Pocket");
    otherProfile.setEmails(emails);
    otherProfile.setDob("2010-10-10");
    otherProfile.setPassword("Password1");
    otherProfile.setGender("female");
    otherProfile = profileRepository.save(otherProfile);

    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(otherProfile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Public);
    activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Run at Avonhead Park");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cool);
    activity1.setTags(tags);
    activity1.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity1);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, activities.size());
  }

  @Test
  void testGetActivitiesByHashTagReturnDateDescendingOrder() throws InterruptedException {
    Tag cool = new Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cool);
    activity.setTags(tags);
    activity.setVisibilityType(VisibilityType.Public);
    activityRepository.save(activity);

    TimeUnit.SECONDS.sleep(1);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Run at Avonhead Park");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cool);
    activity1.setTags(tags);
    activity1.setVisibilityType(VisibilityType.Private);
    activityRepository.save(activity1);

    List<Activity> activities = activityRepository.getActivitiesByHashTag("cool", profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, activities.size());

    Activity resultActivity0 = activities.get(0);
    Activity resultActivity1 = activities.get(1);
    org.junit.jupiter.api.Assertions.assertTrue(
        resultActivity0.getCreationDate().isAfter(resultActivity1.getCreationDate()));
  }
}
