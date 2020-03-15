package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface EmailRepository extends JpaRepository<Email, String>  {
    @Override
    Optional<Email> findById(String s);
}
