package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.LoginAttempt;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.LoginAttemptRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginAttemptRepositoryTest {

  @Autowired
  private ProfileRepository profileRepository;
  @Autowired private LoginAttemptRepository loginAttemptRepository;

  Profile createDummyProfile() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);
    return profile;
  }

  @Test
  void checkIfLoginAttemptObjectIsCreatedCorrectly() {
    Profile profile = createDummyProfile();
    LoginAttempt loginAttempt1 = new LoginAttempt(profile);

    org.junit.jupiter.api.Assertions.assertEquals(profile.getId(), loginAttempt1.getProfile().getId());
    org.junit.jupiter.api.Assertions.assertNotNull(loginAttempt1.getTimestamp());
  }

  @Test
  void findByProfileIdMethodFindsCorrectNumberOfLoginAttempts() {
    Profile profile = createDummyProfile();
    LoginAttempt loginAttempt1 = new LoginAttempt(profile);
    LoginAttempt loginAttempt2 = new LoginAttempt(profile);
    LoginAttempt loginAttempt3 = new LoginAttempt(profile);
    loginAttemptRepository.save(loginAttempt1);
    loginAttemptRepository.save(loginAttempt2);
    loginAttemptRepository.save(loginAttempt3);

    List<LoginAttempt> attemptList = loginAttemptRepository.findByProfileId(profile.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, attemptList.size());
  }

  @Test
  void isProfileLockedMethodReturnsTrueIfProfileIsLocked() {
    Profile profile = createDummyProfile();
    boolean isLocked = loginAttemptRepository.isProfileLocked(profile.getId());
    org.junit.jupiter.api.Assertions.assertFalse(isLocked);

    profile.setLockStatus(true);
    isLocked = loginAttemptRepository.isProfileLocked(profile.getId());
    org.junit.jupiter.api.Assertions.assertTrue(isLocked);
  }

  @Test
  void findByProfileIdGreaterThanTimeReturnsLoginAttempts() {
    Profile profile = createDummyProfile();
    LocalDateTime time = LocalDateTime.now();
    LoginAttempt loginAttempt1 = new LoginAttempt(profile);
    LoginAttempt loginAttempt2 = new LoginAttempt(profile);
    LoginAttempt loginAttempt3 = new LoginAttempt(profile);
    loginAttemptRepository.save(loginAttempt1);
    loginAttemptRepository.save(loginAttempt2);
    loginAttemptRepository.save(loginAttempt3);

    List<LoginAttempt> attemptList = loginAttemptRepository.findByProfileIdGreaterThanTime(profile.getId(), time);
    org.junit.jupiter.api.Assertions.assertEquals(3, attemptList.size());
  }


  @Test
  void findByProfileIdGreaterThanTimeReturnsNoLoginAttempts() {
    Profile profile = createDummyProfile();
    LoginAttempt loginAttempt1 = new LoginAttempt(profile);
    LoginAttempt loginAttempt2 = new LoginAttempt(profile);
    LoginAttempt loginAttempt3 = new LoginAttempt(profile);
    loginAttemptRepository.save(loginAttempt1);
    loginAttemptRepository.save(loginAttempt2);
    loginAttemptRepository.save(loginAttempt3);
    LocalDateTime time = LocalDateTime.now();

    List<LoginAttempt> attemptList = loginAttemptRepository.findByProfileIdGreaterThanTime(profile.getId(), time);
    org.junit.jupiter.api.Assertions.assertEquals(0, attemptList.size());
  }


}
