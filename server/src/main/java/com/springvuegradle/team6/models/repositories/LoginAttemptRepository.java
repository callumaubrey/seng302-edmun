package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.LoginAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
  @Query(
      value =
          "SELECT * FROM login_attempt WHERE profile_id = :profileId",
      nativeQuery = true)
  List<LoginAttempt> findByProfileId(Integer profileId);

  @Query(
      value =
          "SELECT is_locked from profile where id = :profileId ",
      nativeQuery = true)
  boolean isProfileLocked(Integer profileId);


}
