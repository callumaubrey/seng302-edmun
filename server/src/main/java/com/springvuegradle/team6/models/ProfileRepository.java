package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface ProfileRepository
    extends JpaRepository<Profile, Integer>, CustomizedProfileRepository {
    Profile findById(int id);

    Profile findByEmailsContains(Email email);

    boolean existsByEmailsContaining(Email email);

    Integer removeById(int id);

    Optional<Profile> findByAdditionalemail_AddressOrEmail_Address(String address1, String address2);
}
