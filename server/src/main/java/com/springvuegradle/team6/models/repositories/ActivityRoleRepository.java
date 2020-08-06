package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.Profile;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource
public interface ActivityRoleRepository extends JpaRepository<ActivityRole, Integer> {

  List<ActivityRole> findByActivityRoleType(ActivityRoleType type);

  ActivityRole findByProfile_IdAndActivity_Id(int profileId, int activityId);

  @Query(value = "select * from activity_role where activity_id = :activityId", nativeQuery = true)
  List<ActivityRole> findByActivity_Id(int activityId);

  @Query(
      value =
          "select CONCAT(firstname, ' ' , lastname) as full_name, ar.profile_id, e.address as primary_email"
              + " from activity_role ar left join profile p on ar.profile_id = p.id"
              + " left join profile_emails pe on pe.profile_id = p.id"
              + " left join email e on pe.emails_id = e.id"
              + " where ar.activity_id = :activityId and ar.activity_role_type = :type and e.is_primary = 1 "
              + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  List<JSONObject> findMembers(int activityId, int type, int limit, int offset);

  List<ActivityRole> findByActivity_IdAndProfile_Id(int activityId, int profileId);

  @Query(
      value =
          "select COUNT(*) as Count from activity_role where activity_id = :activityId and activity_role_type = :type",
      nativeQuery = true)
  int findMembersCount(int activityId, int type);

  @Modifying
  @Transactional
  @Query(
      value =
          "delete from activity_role where activity_id = :activityId and profile_id != :profileId",
      nativeQuery = true)
  void deleteAllActivityRolesExceptOwner(int activityId, int profileId);

  @Modifying
  @Transactional
  @Query(
      value =
          "delete from activity_role where activity_id = :activityId and activity_role_type = 4",
      nativeQuery = true)
  void deleteAllAccessRolesOfActivity(int activityId);
}
