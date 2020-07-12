package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

  SubscriptionHistory findById(int id);

  Set<SubscriptionHistory> findByActivity_id(int activityId);

  Set<SubscriptionHistory> findByProfile_id(int profileId);
}
