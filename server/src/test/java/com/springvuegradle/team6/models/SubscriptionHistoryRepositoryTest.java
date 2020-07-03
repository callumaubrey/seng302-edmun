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

    private static boolean dataLoaded = false;

    @BeforeEach
    void setup() {
        if (!dataLoaded) {
            Set<Email> emails = new HashSet<>();
            Email email = new Email("johnydoe99@gmail.com");
            emails.add(email);
            Profile profile = new Profile();
            profile.setId(1);
            profile.setFirstname("John");
            profile.setLastname("Doe");
            profile.setEmails(emails);
            profile.setDob("2010-01-01");
            profile.setPassword("Password1");
            profile.setGender("male");
            profile = profileRepository.save(profile);

            Set<Email> emails1 = new HashSet<>();
            Email email1 = new Email("thesecondemail@gmail.com");
            emails1.add(email1);
            Profile profile1 = new Profile();
            profile.setId(2);
            profile1.setFirstname("Gon");
            profile1.setLastname("Freecss");
            profile1.setEmails(emails1);
            profile1.setDob("2010-01-01");
            profile1.setPassword("Password1");
            profile1.setGender("male");
            profile1 = profileRepository.save(profile1);

            activityRepository.deleteAll();

            Activity activity = new Activity();
            activity.setProfile(profile);
            activity.setActivityName("Run at Hagley Park");
            activity.setContinuous(true);
            activity = activityRepository.save(activity);

            Activity activity1 = new Activity();
            activity1.setProfile(profile);
            activity1.setActivityName("Avonhead Park Walk");
            activity1.setContinuous(true);
            activity1 = activityRepository.save(activity1);

            Activity activity2 = new Activity();
            activity2.setProfile(profile);
            activity2.setActivityName("Burnside Park Rugby");
            activity2.setContinuous(true);
            activity2 = activityRepository.save(activity2);

            SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
            subscriptionHistory.setActivity(activity);
            subscriptionHistory.setProfile(profile);
            subscriptionHistory.setSubscribe(true);
            subscriptionHistory.setTimeDate(new Date());
            subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

            dataLoaded = true;
        }
    }

    @Test
    void testSingleSubscriptionFindByProfile() {
        Set<SubscriptionHistory> subscriptionHistories = subscriptionHistoryRepository.findByProfile_id(1);
        org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
    }


}
