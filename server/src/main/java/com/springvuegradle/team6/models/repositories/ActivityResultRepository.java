package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Integer> {

  /**
   * Gets all a particular users results for an activity.
   *
   * @param activityId the activity the user wants to retrive the results for
   * @param userId the user that created the result
   * @return the activity results and the metrics
   */
  @Query(
      value =
          "select * from activity_result a LEFT JOIN activity_qualification_metric q on "
              + "a.metric_id = q.id where q.activity_id = :activityId and a.user_id = :userId ",
      nativeQuery = true)
  List<ActivityResult> findSingleUsersResultsOnActivity(int activityId, int userId);
}
