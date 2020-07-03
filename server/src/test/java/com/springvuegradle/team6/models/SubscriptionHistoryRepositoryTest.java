package com.springvuegradle.team6.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.*;


@SpringBootTest
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class SubscriptionHistoryRepositoryTest {

    @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ProfileRepository profileRepository;

    @BeforeEach
    void setup() {
        subscriptionHistoryRepository.deleteAll();
        activityRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    void testSingleSubscriptionFindByProfile() {
        Set<Email> emails = new HashSet<>();
        Email email = new Email("johnydoe99@gmail.com");
        emails.add(email);
        Profile profile = new Profile();
        profile.setFirstname("John");
        profile.setLastname("Doe");
        profile.setEmails(emails);
        profile.setDob("2010-01-01");
        profile.setPassword("Password1");
        profile.setGender("male");
        profile = profileRepository.save(profile);

        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("Run at Hagley Park");
        activity.setContinuous(true);
        activity = activityRepository.save(activity);

        SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
        subscriptionHistory.setActivity(activity);
        subscriptionHistory.setProfile(profile);
        subscriptionHistory.setSubscribe(true);
        subscriptionHistory.setTimeDate(new Date());
        subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

        Profile profile1 = profileRepository.findByEmailsContains(email);
        Set<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository.findByProfile_id(profile1.getId());
        org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
    }

    @Test
    void testSingleSubscriptionFindByActivity() {
        Set<Email> emails = new HashSet<>();
        Email email = new Email("johnydoe99@gmail.com");
        emails.add(email);
        Profile profile = new Profile();
        profile.setFirstname("John");
        profile.setLastname("Doe");
        profile.setEmails(emails);
        profile.setDob("2010-01-01");
        profile.setPassword("Password1");
        profile.setGender("male");
        profile = profileRepository.save(profile);

        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("Run at Hagley Park");
        activity.setContinuous(true);
        activity = activityRepository.save(activity);

        SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
        subscriptionHistory.setActivity(activity);
        subscriptionHistory.setProfile(profile);
        subscriptionHistory.setSubscribe(true);
        subscriptionHistory.setTimeDate(new Date());
        subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

        Profile profile1 = profileRepository.findByEmailsContains(email);
        Set<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository
                .findByActivity_id(activityRepository.findByProfile_Id(profile1.getId()).get(0).getId());
        org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
    }
}
