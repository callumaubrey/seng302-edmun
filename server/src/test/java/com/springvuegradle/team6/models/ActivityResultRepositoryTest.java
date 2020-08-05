package com.springvuegradle.team6.models;

import com.springvuegradle.team6.controllers.TestDataGenerator;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
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
import net.minidev.json.JSONObject;
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
    ActivityQualificationMetric activityMetrics = new ActivityQualificationMetric();
    activityMetrics.setActivity(activity);
    activityMetrics.setUnit(Unit.Distance);
    activityMetrics.setTitle("Hercules");
    activityMetrics.setDescription("Time to run up hill");
    activityMetricsRepository.save(activityMetrics);

    ActivityResultDistance activityResultDistance = new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);

    List<JSONObject> results =
        activityResultRepository.findSingleUsersResultsOnActivity(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(
        "Time to run up hill", results.get(0).get("DESCRIPTION"));
    org.junit.jupiter.api.Assertions.assertEquals(1, results.size());
    org.junit.jupiter.api.Assertions.assertEquals(profile.getId(), results.get(0).get("USER_ID"));
    org.junit.jupiter.api.Assertions.assertNotNull(results.get(0).get("DISTANCE_RESULT"));
  }

  @Test
  void testFindActivityResultOfUserOnActivityTwoActivityResultReturnTwoActivityResult() {
    ActivityQualificationMetric activityMetrics = new ActivityQualificationMetric();
    activityMetrics.setActivity(activity);
    activityMetrics.setUnit(Unit.Distance);
    activityMetrics.setTitle("Hercules");
    activityMetrics.setDescription("Time to run up hill");
    activityMetricsRepository.save(activityMetrics);

    ActivityResultDistance activityResultDistance = new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 = new ActivityResultDistance(activityMetrics, profile, 4.0f);
    activityResultRepository.save(activityResultDistance2);

    List<JSONObject> results =
        activityResultRepository.findSingleUsersResultsOnActivity(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityManyActivityResultReturnManyActivityResult() {
    Profile anotherProfile = TestDataGenerator.createExtraProfile(profileRepository);
    ActivityQualificationMetric activityMetrics = new ActivityQualificationMetric();
    activityMetrics.setActivity(activity);
    activityMetrics.setUnit(Unit.Distance);
    activityMetrics.setTitle("Hercules");
    activityMetrics.setDescription("Time to run up hill");
    activityMetricsRepository.save(activityMetrics);

    ActivityResultDistance activityResultDistance = new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 = new ActivityResultDistance(activityMetrics, profile, 4.0f);
    activityResultRepository.save(activityResultDistance2);
    ActivityResultDistance activityResultDistance3 = new ActivityResultDistance(activityMetrics, anotherProfile, 4.0f);
    activityResultRepository.save(activityResultDistance3);
    ActivityResultDistance activityResultDistance4 = new ActivityResultDistance(activityMetrics, profile, 2.0f);
    activityResultRepository.save(activityResultDistance4);

    List<JSONObject> results =
        activityResultRepository.findSingleUsersResultsOnActivity(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityDifferentActivityMetrics() {
    ActivityQualificationMetric activityMetrics = new ActivityQualificationMetric();
    activityMetrics.setActivity(activity);
    activityMetrics.setUnit(Unit.Distance);
    activityMetrics.setTitle("Hercules");
    activityMetrics.setDescription("Distance to run up hill");
    activityMetricsRepository.save(activityMetrics);

    ActivityQualificationMetric activityMetrics2 = new ActivityQualificationMetric();
    activityMetrics2.setActivity(activity);
    activityMetrics2.setUnit(Unit.TimeDuration);
    activityMetrics2.setTitle("Hercules");
    activityMetrics2.setDescription("Time to run up hill");
    activityMetricsRepository.save(activityMetrics2);

    ActivityResultDistance activityResultDistance = new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    Duration duration = Duration.ofSeconds(50);
    ActivityResultDuration activityResultDuration = new ActivityResultDuration(activityMetrics2, profile, duration);
    activityResultRepository.save(activityResultDuration);

    List<JSONObject> results =
        activityResultRepository.findSingleUsersResultsOnActivity(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

  @Test
  void testFindActivityResultOfUserOnActivityTwoDifferentActivities() {
    ActivityQualificationMetric activityMetrics = new ActivityQualificationMetric();
    activityMetrics.setActivity(activity);
    activityMetrics.setUnit(Unit.Distance);
    activityMetrics.setTitle("Hercules");
    activityMetrics.setDescription("Distance to run up hill");
    activityMetricsRepository.save(activityMetrics);

    ActivityQualificationMetric activityMetrics2 = new ActivityQualificationMetric();
    activityMetrics2.setActivity(activity);
    activityMetrics2.setUnit(Unit.TimeDuration);
    activityMetrics2.setTitle("Hercules");
    activityMetrics2.setDescription("Time to run up hill");
    activityMetricsRepository.save(activityMetrics2);

    ActivityResultDistance activityResultDistance = new ActivityResultDistance(activityMetrics, profile, 5.2f);
    activityResultRepository.save(activityResultDistance);
    ActivityResultDistance activityResultDistance2 = new ActivityResultDistance(activityMetrics2, profile, 4.0f);
    activityResultRepository.save(activityResultDistance2);

    List<JSONObject> results =
        activityResultRepository.findSingleUsersResultsOnActivity(activity.getId(), profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, results.size());
  }

}
