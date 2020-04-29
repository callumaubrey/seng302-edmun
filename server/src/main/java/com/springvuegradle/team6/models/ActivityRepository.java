package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, String> {
    /**
     * Find activity by activity id
     *
     * @param id activity id
     * @return object of activity class
     */

    Optional<Activity> findById(int id);
}
