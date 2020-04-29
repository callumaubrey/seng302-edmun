package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    @Query("SELECT e.profile FROM Email e WHERE e=?1")
    Profile findByEmail(Email email);
    Profile findById(int id);

    @Query("SELECT COUNT(e)>0 FROM Email e WHERE e=?1")
    boolean existsByEmail(Email email);

    Integer removeById(int id);
}
