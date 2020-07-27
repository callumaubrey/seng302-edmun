package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ProfileRepository
    extends JpaRepository<Profile, Integer>, CustomizedProfileRepository {
  Profile findById(int id);

  Profile findByEmailsContains(Email email);

  boolean existsByEmailsContaining(Email email);

  Integer removeById(int id);

  Profile findByEmails_address(String email);
}
