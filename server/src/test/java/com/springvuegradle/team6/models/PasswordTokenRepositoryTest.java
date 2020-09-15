package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.PasswordToken;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.PasswordTokenRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Set;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PasswordTokenRepositoryTest {

    @Autowired private ProfileRepository profileRepository;
    @Autowired private PasswordTokenRepository passwordTokenRepository;

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
    void creatingPasswordTokenGeneratesToken() throws Exception {
        Profile profile = createDummyProfile();
        PasswordToken passwordToken = new PasswordToken(profile);
        passwordToken = passwordTokenRepository.save(passwordToken);

        Assert.assertNotNull(passwordToken.getToken());
        Assert.assertEquals(16,passwordToken.getToken().length());
    }

    @Test
    void testPasswordTokenToProfileRelationship() throws Exception {
        Profile profile = createDummyProfile();
        PasswordToken passwordToken = new PasswordToken(profile);
        passwordToken = passwordTokenRepository.save(passwordToken);

        Assert.assertEquals(profile.getId(), passwordToken.getProfile().getId());
    }
}
