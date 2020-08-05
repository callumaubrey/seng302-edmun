package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityResult;
import java.util.List;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Integer> {

  /**
   * Gets all a particular users results for an activity.
   *
   * <p>Note: Two tables activity_result and activity_qualification_metrics need to be joined as we
   * need both the metrics and results when displaying on the front-end. There are also many rows in
   * the select part of the query as each row had to be manually retrieved because both tables had a
   * conflicting row 'id'
   *
   * @param activityId the activity the user wants to retrive the results for
   * @param userId the user that created the result
   * @return the activity results and the metrics
   */
  @Query(
      value =
          "select result_type, special_metric, count_result, distance_result, duration_result, a.id,"
              + "result_finish, result_start, metric_id, user_id, rank_by_asc, unit, activity_id, "
              + "description, title from activity_result a JOIN activity_qualification_metrics q on "
              + "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId ",
      nativeQuery = true)
  List<JSONObject> findSingleUsersResultsOnActivity(int activityId, int userId);
}
