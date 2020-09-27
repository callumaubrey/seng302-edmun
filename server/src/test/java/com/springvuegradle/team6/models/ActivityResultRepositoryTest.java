package com.springvuegradle.team6.models;

import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityResult;
import com.springvuegradle.team6.models.entities.ActivityResultCount;
import com.springvuegradle.team6.models.entities.ActivityResultDistance;
import com.springvuegradle.team6.models.entities.ActivityResultDuration;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Unit;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityResultRepositoryTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityQualificationMetricRepository activityMetricsRepository;

  @Autowired private ActivityResultRepository activityResultRepository;

  private Activity activity;

  private Profile profile;

  @BeforeEach
  void setup() {
    profile = TestDataGenerator.createExtraProfile(profileRepository);

    activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activityRepository.save(activity);
  }

  @Test
  void testFindActivityResultOfUserOnActivityOneActivityReturnSingleActivityResult() {
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityTwoActivityResultReturnTwoActivityResult() {
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(activityMetrics, profile, 4.0d);
    activityResultRepository.save(activityResultDistance2);

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityManyActivityResultReturnManyActivityResult() {
    Profile anotherProfile = TestDataGenerator.createExtraProfile(profileRepository);
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(activityMetrics, profile, 4.0d);
    activityResultRepository.save(activityResultDistance2);
    ActivityResultDistance activityResultDistance3 =
        new ActivityResultDistance(activityMetrics, anotherProfile, 4.0d);
    activityResultRepository.save(activityResultDistance3);
    ActivityResultDistance activityResultDistance4 =
        new ActivityResultDistance(activityMetrics, profile, 2.0d);
    activityResultRepository.save(activityResultDistance4);

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityDifferentActivityMetrics() {
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityQualificationMetric activityMetrics2 =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.TimeDuration, activityMetricsRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    Duration duration = Duration.ofSeconds(50);
    ActivityResultDuration activityResultDuration =
        new ActivityResultDuration(activityMetrics2, profile, duration);
    activityResultRepository.save(activityResultDuration);

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityTwoDifferentActivities() {
    ActivityQualificationMetric activityMetrics =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityQualificationMetric activityMetrics2 =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.TimeDuration, activityMetricsRepository);

    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(activityMetrics, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(activityMetrics2, profile, 4.0d);
    activityResultRepository.save(activityResultDistance2);

    List<ActivityResult> results =
        activityResultRepository.findSingleUsersResultsOnActivity(
            activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindAllActivityResultsWithOneResult() {
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);

    List<ActivityResult> results =
        activityResultRepository.findAllResultsForAnActivity(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, results.size());
  }

  @Test
  void testFindAllActivityResultsWithNoResults() {
    List<ActivityResult> results =
        activityResultRepository.findAllResultsForAnActivity(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, results.size());
  }

  @Test
  void testFindAllActivityResultsWithTwoResults() {
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(metric, profile, 5.5d);
    activityResultRepository.save(activityResultDistance2);

    List<ActivityResult> results =
        activityResultRepository.findAllResultsForAnActivity(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindAllActivityResultsWithTwoDifferentUsersResults() {
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    Profile profile2 = TestDataGenerator.createExtraProfile(profileRepository);
    ActivityResultDistance activityResultDistance2 =
        new ActivityResultDistance(metric, profile2, 5.5d);
    activityResultRepository.save(activityResultDistance2);

    List<ActivityResult> results =
        activityResultRepository.findAllResultsForAnActivity(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindAllActivityResultsWithTwoResultsForTwoDifferentMetrics() {
    ActivityQualificationMetric metric =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Distance, activityMetricsRepository);
    ActivityQualificationMetric metric2 =
        TestDataGenerator.createDummyActivityMetric(
            activity, Unit.Count, activityMetricsRepository);
    ActivityResultDistance activityResultDistance =
        new ActivityResultDistance(metric, profile, 5.2d);
    activityResultRepository.save(activityResultDistance);
    ActivityResultCount activityResultCount = new ActivityResultCount(metric2, profile, 5);
    activityResultRepository.save(activityResultCount);

    List<ActivityResult> results =
        activityResultRepository.findAllResultsForAnActivity(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }
}
