package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProfileRepository extends JpaRepository<Profile, Integer>, CustomizedProfileRepository{
    Profile findByEmail(Email email);
    Profile findById(int id);
    boolean existsByEmail(Email email);
    Integer removeByEmail(Email email);
}
