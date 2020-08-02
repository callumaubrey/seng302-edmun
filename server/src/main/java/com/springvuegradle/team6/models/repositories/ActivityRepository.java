package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
  /**
   * Find activity by activity id
   *
   * @param id activity id
   * @return object of activity class
   */
  Optional<Activity> findById(int id);

  /**
   * Used for searching for a profile's activities by hash-tag
   * returning only activities which are not archived
   *
   * @param id profile id
   * @return list of not archived activities
   */
  List<Activity> findByProfile_IdAndArchivedFalse(int id);

  List<Activity> findByProfile_IdAndArchivedFalseAndVisibilityTypeNotLike(
      Integer profile_id, VisibilityType visibilityType);

  List<Activity> findByTags_NameOrderByCreationDateDesc(String name);
  /**
   * Used for searching activities by hash-tag returning
   * only activities which are not archived. Results are ordered
   * by creationDate.
   *
   * @param name tag name to search for
   * @return list of not archived activities
   */
  List<Activity> findByTags_NameAndArchivedIsFalseOrderByCreationDateDesc(String name);

  List<Activity> findAll();

  Activity findByActivityName(String activityName);

  @Query(value = "select t from Activity a left join a.tags t WHERE a.id = :activityId")
  Set<Tag> getActivityTags(int activityId);

  /**
   * Returns all activities sorted in descending order by the activities creation date that contain
   * the given hashtag and are public or the user is author of the activity or the user was given
   * access to the activity.
   *
   * <p>Note: visibility_type = 1 and visibility_type = 2 do not need to be checked as the activity
   * is returned if the author is the user or if the user has a row in the table for the
   * activity_role regardless of the visibility.
   *
   * @param hashtagName the name of the hashtag
   * @param profileId the id of the user that is calling the query
   * @return the activities
   */
  @Query(
      value =
          "select * from activity a left join activity_tags b on a.id = b.activity_id"
              + " left join tag t on b.tag_id = t.id left join activity_role r on a.id = r.activity_id and a.archived is FALSE"
              + " and r.profile_id = :profileId WHERE t.name = :hashtagName and"
              + " (a.visibility_type = 0 or a.author_id = :profileId or r.profile_id = :profileId)"
              + " order by a.creation_date desc",
      nativeQuery = true)
  List<Activity> getActivitiesByHashTag(String hashtagName, int profileId);
}
