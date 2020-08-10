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
          "select * from activity_result a LEFT JOIN activity_qualification_metric q on "
              + "a.metric_id = q.id where q.activity_id = :activityId and q.id = :metricId ",
      nativeQuery = true)
  List<ActivityResult> findSingleMetricResultsOnActivity(int activityId, int metricId);

  @Query(value = "select * from activity_result where id = :resultId", nativeQuery = true)
  Optional<ActivityResultCount> findSpecificCountResult(int resultId);

  @Query(value = "select * from activity_result where id = :resultId", nativeQuery = true)
  Optional<ActivityResultDistance> findSpecificDistanceResult(int resultId);

  @Query(value = "select * from activity_result where id = :resultId", nativeQuery = true)
  Optional<ActivityResultDuration> findSpecificDurationResult(int resultId);

  @Query(value = "select * from activity_result where id = :resultId", nativeQuery = true)
  Optional<ActivityResultStartFinish> findSpecificStartFinishResult(int resultId);

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
