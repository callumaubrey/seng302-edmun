package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.Assertions;


import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityQualificationMetricRepositoryTest {

    @Autowired private ActivityQualificationMetricRepository activityQualificationMetricRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ProfileRepository profileRepository;

    public int activityId;

    @BeforeEach
    @Test
    public void setup () {
        Set<Email> emails = new HashSet<>();
        Email email = new Email("johnydoe99@gmail.com");
        email.setPrimary(true);
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
        activityId = activity.getId();

    }

    @Test
    void testSingleMetricFindByActivityId() {

        ActivityQualificationMetric qualificationMetric = new ActivityQualificationMetric();
        qualificationMetric.setActivity(activityRepository.findById(activityId).get());
        activityQualificationMetricRepository.save(qualificationMetric);

        List<ActivityQualificationMetric> metrics = activityQualificationMetricRepository.findByActivity_Id(activityId);
        Assertions.assertEquals(1, metrics.size());
    }

    @Test
    void testMultipleMetricFindByActivityId() {

        ActivityQualificationMetric qualificationMetric1 = new ActivityQualificationMetric();
        qualificationMetric1.setActivity(activityRepository.findById(activityId).get());

        ActivityQualificationMetric qualificationMetric2 = new ActivityQualificationMetric();
        qualificationMetric2.setActivity(activityRepository.findById(activityId).get());

        activityQualificationMetricRepository.save(qualificationMetric1);
        activityQualificationMetricRepository.save(qualificationMetric2);

        List<ActivityQualificationMetric> metrics = activityQualificationMetricRepository.findByActivity_Id(activityId);
        Assertions.assertEquals(2, metrics.size());
    }

    @Test
    void testNoMetricsFindByActivityId() {
        List<ActivityQualificationMetric> metrics = activityQualificationMetricRepository.findByActivity_Id(activityId);
        Assertions.assertEquals(0, metrics.size());
    }



}
