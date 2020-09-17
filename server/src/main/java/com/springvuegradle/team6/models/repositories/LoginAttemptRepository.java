package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.LoginAttempt;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
  @Query(value = "SELECT * FROM login_attempt WHERE profile_id = :profileId", nativeQuery = true)
  List<LoginAttempt> findByProfileId(Integer profileId);

  /**
   * Finds a list of LoginAttempt where the timestamp is greater than the given time for a given
   * profile id.
   *
   * @param profileId the id of the profile
   * @param time the timestamp of login attempts must be greater than this time
   * @return
   */
  @Query(
      value = "SELECT * FROM login_attempt WHERE profile_id = :profileId and timestamp > :time",
      nativeQuery = true)
  List<LoginAttempt> findByProfileIdGreaterThanTime(Integer profileId, LocalDateTime time);

  @Query(value = "SELECT is_locked from profile where id = :profileId ", nativeQuery = true)
  boolean isProfileLocked(Integer profileId);
}
