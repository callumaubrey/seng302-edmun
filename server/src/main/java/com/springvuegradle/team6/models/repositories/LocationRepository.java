package com.springvuegradle.team6.models.repositories;
import com.springvuegradle.team6.models.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatitudeAndLongitude(double latitude, double longitude);

    Optional<Location> findById(int id);
}
