package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByEmail(String email);
    Profile findById(int id);

    Integer removeByEmail(String email);
    boolean existsByEmail(String email);
}
