package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PathRepository extends JpaRepository<Path, Integer> {

  Path findById(int id);
}
