package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Tag;
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
   *
   * @param id profile id
   * @return list of activities
   */
  List<Activity> findByProfile_IdAndArchivedFalse(int id);

  /**
   * Used for searching activities by hash-tag
   *
   * @param name tag name to search for
   * @return list of activities
   */
  List<Activity> findByTags_NameAndArchivedIsFalseOrderByCreationDateDesc(String name);

  List<Activity> findAll();

  Activity findByActivityName(String activityName);

  @Query(value = "select t from Activity a left join a.tags t WHERE a.id = :activityId")
  Set<Tag> getActivityTags(int activityId);
}
