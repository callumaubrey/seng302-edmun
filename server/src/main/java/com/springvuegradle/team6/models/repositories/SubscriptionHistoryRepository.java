package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

  SubscriptionHistory findById(int id);

  Set<SubscriptionHistory> findByActivity_id(int activityId);

  Set<SubscriptionHistory> findByProfile_id(int profileId);

  @Query(
          value =
                  "SELECT * from subscription_history where activity_id = :activityId " +
                          "and profile_id = :profileId and end_date_time is null",
          nativeQuery = true)
  List<SubscriptionHistory> findActive(int activityId, int profileId);

  @Query(
      value =
          "SELECT COUNT(s.id) from subscription_history s LEFT JOIN activity a ON s.activity_id = a.id WHERE " +
              "a.archived = 0 AND s.profile_id = :profileId AND s.end_date_time IS NULL",
      nativeQuery = true)
  Long findUsersActiveFollowings(int profileId);

  @Modifying
  @Transactional
  @Query(
          value = "update subscription_history set end_date_time = :endTime where activity_id = :activityId and profile_id != :creatorId and end_date_time = null",
          nativeQuery = true)
   void unSubscribeAllButCreator(int activityId, int creatorId, LocalDateTime endTime);
}
