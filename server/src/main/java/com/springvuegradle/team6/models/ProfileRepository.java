package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findById(int id);
    Profile findByEmailsContains(Email email);

    boolean existsByEmailsContaining(Email email);

    Integer removeById(int id);
}
