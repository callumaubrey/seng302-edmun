package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityQualificationMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ActivityQualificationMetricsRepository extends
    JpaRepository<ActivityQualificationMetrics, Integer> {

}
