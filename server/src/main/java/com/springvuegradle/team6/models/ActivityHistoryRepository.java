package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;


@RepositoryRestResource
public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Integer> {
    ActivityHistory findById(int id);

    Set<ActivityHistory> findByActivity_id(int id);
}
