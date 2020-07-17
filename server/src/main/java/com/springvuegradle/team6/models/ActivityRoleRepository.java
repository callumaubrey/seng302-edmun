package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.springvuegradle.team6.models.ActivityRoleType;

import java.util.List;

@RepositoryRestResource
public interface ActivityRoleRepository extends JpaRepository<ActivityRole, Integer> {

  List<ActivityRole> findByActivityRoleType(ActivityRoleType type);

  ActivityRole findByProfile_IdAndActivity_Id(int profileId, int activityId);

  List<ActivityRole> findByActivity_Id(int activityId);
}
