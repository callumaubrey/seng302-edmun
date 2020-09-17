package com.springvuegradle.team6.models.Path;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.PathType;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.PathRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class PathRepositoryTest {

  @Autowired
  private PathRepository pathRepository;
  @Autowired
  private ActivityRepository activityRepository;
  @Autowired
  private ProfileRepository profileRepository;
  @Autowired
  private LocationRepository locationRepository;

  @Test
  void findPathByActivityIdReturnsPath() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("secondemail@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("Gon");
    profile.setLastname("Freecss");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);


    Location location1 = new Location(12.34, 56.78);
    Location location2 = new Location(3.45, 6.78);
    locationRepository.save(location1);
    locationRepository.save(location2);
    List<Location> locations = new ArrayList<>();
    locations.add(location1);
    locations.add(location2);

    Path path = new Path(locations, PathType.STRAIGHT);
    path = pathRepository.save(path);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity.setPath(path);
    activity = activityRepository.save(activity);

    Path pathReturned = activityRepository.findById(activity.getId()).get().getPath();
    org.junit.jupiter.api.Assertions.assertNotNull(pathReturned);
    org.junit.jupiter.api.Assertions.assertEquals(2, path.getLocations().size());
    org.junit.jupiter.api.Assertions.assertEquals(PathType.STRAIGHT, path.getType());
  }

  @Test
  void findPathByActivityIdReturnsNullWhenActivityHasNoPath() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("secondemail@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("Gon");
    profile.setLastname("Freecss");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    Path pathReturned = activityRepository.findById(activity.getId()).get().getPath();
    org.junit.jupiter.api.Assertions.assertNull(pathReturned);
  }
}
