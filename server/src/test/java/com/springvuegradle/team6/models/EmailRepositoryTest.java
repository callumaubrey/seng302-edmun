package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class EmailRepositoryTest {
  @Autowired
  EmailRepository emailRepository;

  @Autowired
  ProfileRepository profileRepository;

  @Test
  void getEmailsByProfileIdReturnsAllEmails() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe1@gmail.com");
    Email email1 = new Email("johnydoe2@gmail.com");
    Email email2 = new Email("johndoe123@hotmail.com");
    email.setPrimary(true);
    emails.add(email);
    emails.add(email1);
    emails.add(email2);
    Profile profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe1");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);
    List<Email> emailsResult =  emailRepository.getEmailsByProfileId(profile.getId());
    org.junit.jupiter.api.Assertions.assertTrue(
        emailsResult.containsAll(emails));
  }

}
