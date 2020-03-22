package com.springvuegradle.team6.models.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface OSMLocationRepository extends JpaRepository<OSMLocation, OSMElementID> {
}
