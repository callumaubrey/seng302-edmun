package com.springvuegradle.team6.models;

import jdk.jshell.JShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityHistoryRepository activityHistoryRepository;

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
    void testGetActivityFeed() {
        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("Run at Hagley Park");
        activity.setContinuous(true);
        List<Profile> subscribers = new ArrayList<>();
        subscribers.add(profile);
        activity.setSubscribers(subscribers);
        activity = activityRepository.save(activity);

        List<Activity> subscribedActivities = new ArrayList<>();
        subscribedActivities.add(activity);
        profile.setSubscriptions(subscribedActivities);
        profile = profileRepository.save(profile);

        ActivityHistory activityHistory = new ActivityHistory();
        activityHistory.setActivity(activity);
        activityHistory.setMessage("Activity created");
        activityHistoryRepository.save(activityHistory);

        List<ActivityUpdateNotification> results = profileRepository.getActivityFeed(profile.getId());
        org.junit.jupiter.api.Assertions.assertEquals("Activity created", results.get(0).getMessage());
    }

}
