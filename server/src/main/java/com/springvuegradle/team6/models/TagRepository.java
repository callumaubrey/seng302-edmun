package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface TagRepository extends JpaRepository<Tag, Integer> {

  List<Tag> findByNameContaining(String name);

  List<Activity> findActivitiesByName(String name);
}
