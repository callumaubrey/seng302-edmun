package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.*;

import java.util.List;
import java.util.Optional;

import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Integer> {

  /**
   * Gets all a particular users results for an activity.
   *
   * @param activityId the activity the user wants to retrieve the results for
   * @param userId the user that created the result
   * @return the activity results for that user
   */
  @Query(
      value =
          "select * from activity_result a LEFT JOIN activity_qualification_metric q on "
              + "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId ",
      nativeQuery = true)
  List<ActivityResult> findSingleUsersResultsOnActivity(int activityId, int userId);

  @Query(
          value =
                  "select * from activity_result a JOIN activity_qualification_metric q on " +
                          "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId " +
                          "and a.metric_id = :metricId",
          nativeQuery = true)
  Optional<ActivityResultCount> findUsersCountResultForSpecificActivityAndMetric(int activityId, int userId, int metricId);

  @Query(
          value =
                  "select * from activity_result a JOIN activity_qualification_metric q on " +
                          "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId " +
                          "and a.metric_id = :metricId",
          nativeQuery = true)
  Optional<ActivityResultDistance> findUsersDistanceResultForSpecificActivityAndMetric(int activityId, int userId, int metricId);

  @Query(
          value =
                  "select * from activity_result a JOIN activity_qualification_metric q on " +
                          "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId " +
                          "and a.metric_id = :metricId",
          nativeQuery = true)
  Optional<ActivityResultDuration> findUsersDurationResultForSpecificActivityAndMetric(int activityId, int userId, int metricId);

  @Query(
          value =
                  "select * from activity_result a JOIN activity_qualification_metric q on " +
                          "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId " +
                          "and a.metric_id = :metricId",
          nativeQuery = true)
  Optional<ActivityResultStartFinish> findUsersStartFinishResultForSpecificActivityAndMetric(int activityId, int userId, int metricId);

  /**
   * Gets all results for an activity.
   *
   * @param activityId the activity the user wants to retrieve all results for
   * @return the activity results
   */
  @Query(
      value =
          "select * from activity_result a LEFT JOIN activity_qualification_metric q on "
              + "a.metric_id = q.id where q.activity_id = :activityId",
      nativeQuery = true)
  List<ActivityResult> findAllResultsForAnActivity(int activityId);
}
