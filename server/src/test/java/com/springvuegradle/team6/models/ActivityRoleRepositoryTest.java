package com.springvuegradle.team6.models;

import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityRoleRepositoryTest {

  @Autowired private TagRepository tagRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  private Activity activity;

  private Profile profile;

  @BeforeEach
  void setup() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe1@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe1");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);
  }

  void addRole(Profile profile, Activity activity, ActivityRoleType roleType) {
    ActivityRole role = new ActivityRole();
    role.setProfile(profile);
    role.setActivityRoleType(roleType);
    role.setActivity(activity);
    activityRoleRepository.save(role);
  }

  @Test
  void testGetCreator() {
    addRole(profile, activity, ActivityRoleType.Creator);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivityRoleType(ActivityRoleType.Creator);
    org.junit.jupiter.api.Assertions.assertEquals(
        "Run at Hagley Park", roles.get(0).getActivity().getActivityName());
  }

  @Test
  void testActivityRolesByProfileId() {
    addRole(profile, activity, ActivityRoleType.Participant);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, roles.size());
  }

  @Test
  void testNoActivityRolesForAParticularProfile() {
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, roles.size());
  }

  @Test
  void testMultipleActivityRolesForAParticularProfile() {
    // Profiles should not have more than one role in an activity, however just testing the function
    addRole(profile, activity, ActivityRoleType.Participant);
    addRole(profile, activity, ActivityRoleType.Creator);
    List<ActivityRole> roles =
        activityRoleRepository.findByActivity_IdAndProfile_Id(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, roles.size());
  }

  @Test
  void testFindMembersTypeOneMemberReturnOne() {
    addRole(profile, activity, ActivityRoleType.Creator);
    List<JSONObject> members =
        activityRoleRepository.findMembers(
            activity.getId(), ActivityRoleType.Creator.ordinal(), 10, 0);
    org.junit.jupiter.api.Assertions.assertEquals(1, members.size());
  }

  @Test
  void testFindMembersTypeMultipleMembersReturnMultiple() {
    Set<Email> emails1 = new HashSet<>();
    Email email1 = new Email("poly@pocket.com");
    email1.setPrimary(true);
    emails1.add(email1);
    Profile profile1 = new Profile();
    profile1.setFirstname("Poly");
    profile1.setLastname("Pocket");
    profile1.setEmails(emails1);
    profile1.setDob("2010-01-01");
    profile1.setPassword("Password1");
    profile1.setGender("female");
    profile1 = profileRepository.save(profile1);

    Set<Email> emails2 = new HashSet<>();
    Email email2 = new Email("david@pocket.com");
    email2.setPrimary(true);
    emails2.add(email2);
    Profile profile2 = new Profile();
    profile2.setFirstname("Davod");
    profile2.setLastname("Pocket");
    profile2.setEmails(emails2);
    profile2.setDob("2010-01-01");
    profile2.setPassword("Password1");
    profile2.setGender("male");
    profile2 = profileRepository.save(profile2);

    addRole(profile1, activity, ActivityRoleType.Participant);
    addRole(profile2, activity, ActivityRoleType.Participant);
    List<JSONObject> members =
        activityRoleRepository.findMembers(
            activity.getId(), ActivityRoleType.Participant.ordinal(), 10, 0);
    org.junit.jupiter.api.Assertions.assertEquals(2, members.size());
  }
}
