package com.springvuegradle.team6.models.repositories;
import com.springvuegradle.team6.models.entities.NamedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface NamedLocationRepository extends JpaRepository<NamedLocation, Long> {

    Optional<NamedLocation> findByCountryAndStateAndCity(String country, String state, String city);
}
