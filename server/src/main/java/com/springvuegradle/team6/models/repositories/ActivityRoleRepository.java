package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.Profile;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.springvuegradle.team6.models.entities.ActivityRoleType;

import java.util.List;

@RepositoryRestResource
public interface ActivityRoleRepository extends JpaRepository<ActivityRole, Integer> {

  List<ActivityRole> findByActivityRoleType(ActivityRoleType type);

  ActivityRole findByProfile_IdAndActivity_Id(int profileId, int activityId);

  List<ActivityRole> findByActivity_Id(int activityId);

  @Query(
      value =
          "select CONCAT(firstname, ' ' , lastname) as full_name, profile_id from activity_role JOIN profile on "
              + "activity_role.profile_id = profile.id where activity_id = :activityId and activity_role_type = :type "
              + "LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  List<JSONObject> findMembers(int activityId, int type, int limit, int offset);

  List<ActivityRole> findByActivity_IdAndProfile_Id(int activityId, int profileId);

  @Query(
      value =
          "select COUNT(*) as Count from activity_role where activity_id = :activityId and activity_role_type = :type",
      nativeQuery = true)
  int findMembersCount(int activityId, int type);
}
