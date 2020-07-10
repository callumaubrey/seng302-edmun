package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProfileRepository
    extends JpaRepository<Profile, Integer>, CustomizedProfileRepository {
  Profile findById(int id);

  Profile findByEmailsContains(Email email);

  boolean existsByEmailsContaining(Email email);

  Integer removeById(int id);

  @Query(
      value =
          "SELECT p.activity_id as id, a.message as message, a.time_date as dateTime FROM profile_subscriptions p left join activity_history a on p.activity_id = a.activity_id where profile_id = :id",
      nativeQuery = true)
  List<ActivityUpdateNotification> getActivityFeed(int id);
}
