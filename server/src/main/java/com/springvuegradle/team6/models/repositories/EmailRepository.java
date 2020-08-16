package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Email;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface EmailRepository extends JpaRepository<Email, String> {
  Optional<Email> findByAddress(String s);

  /** Gets all the emails based on the profileId
   * @param profileId the profileId of the emails you want
   * @return A list of Email objects
   */
  @Query(
      value =
          "select * from profile_emails pe left join email e on pe.emails_id = e.id where pe.profile_id=:profileId",
      nativeQuery = true)
  List<Email> getEmailsByProfileId(Integer profileId);

  @Override
  void delete(Email email);
}
