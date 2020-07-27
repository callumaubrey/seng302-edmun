package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagRepositoryTest {

  @Autowired private TagRepository tagRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ProfileRepository profileRepository;

  @BeforeAll
  void setup() {
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

    com.springvuegradle.team6.models.entities.Tag cool = new com.springvuegradle.team6.models.entities.Tag();
    cool.setName("cool");
    cool = tagRepository.save(cool);

    com.springvuegradle.team6.models.entities.Tag cold = new com.springvuegradle.team6.models.entities.Tag();
    cold.setName("cold");
    cold = tagRepository.save(cold);

    com.springvuegradle.team6.models.entities.Tag colour = new com.springvuegradle.team6.models.entities.Tag();
    colour.setName("colour");
    colour = tagRepository.save(colour);

    com.springvuegradle.team6.models.entities.Tag awesome = new com.springvuegradle.team6.models.entities.Tag();
    awesome.setName("awesome");
    awesome = tagRepository.save(awesome);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<com.springvuegradle.team6.models.entities.Tag> tags = new HashSet<>();
    tags.add(cold);
    tags.add(cool);
    activity.setTags(tags);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    Set<com.springvuegradle.team6.models.entities.Tag> tags1 = new HashSet<>();
    tags1.add(cold);
    tags1.add(colour);
    activity1.setTags(tags1);
    activity1 = activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setActivityName("Burnside Park Rugby");
    activity2.setContinuous(true);
    Set<Tag> tags2 = new HashSet<>();
    tags2.add(cold);
    tags2.add(awesome);
    tags2.add(cool);
    activity2.setTags(tags2);
    activity2 = activityRepository.save(activity2);
  }

  @AfterAll
  void tearDown(@Autowired DataSource dataSource) throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("tearDown.sql"));
    }
  }

  @Test
  void testFindTagsMatchingSearchSingleResult() {
    List<String> result = tagRepository.findTagsMatchingSearch("awes", PageRequest.of(0, 10));
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("awesome");
    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testFindTagsMatchingSearchMultipleResult() {
    List<String> result = tagRepository.findTagsMatchingSearch("co", PageRequest.of(0, 10));
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("cold");
    expectedResult.add("cool");
    expectedResult.add("colour");
    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, result);
  }
}
