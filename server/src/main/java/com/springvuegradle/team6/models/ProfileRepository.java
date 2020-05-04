package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface ProfileRepository
    extends JpaRepository<Profile, Integer>, CustomizedProfileRepository {
  Profile findByEmail(Email email);

  Profile findById(int id);

  boolean existsByEmail(Email email);

  Integer removeByEmail(Email email);

  Optional<Profile> findByAdditionalemail_AddressOrEmail_Address(String address1, String address2);

  Optional<Profile> findByActivityTypes(ActivityType type);
}
