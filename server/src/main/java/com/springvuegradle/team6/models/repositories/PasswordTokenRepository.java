package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.PasswordToken;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Integer> {

     PasswordToken findByProfileId(Integer profileId);

     Profile findProfileById(PasswordToken token);
}
