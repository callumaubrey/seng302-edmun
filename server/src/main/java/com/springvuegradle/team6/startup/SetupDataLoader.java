package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Role;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * SetupDataLoader class to check for default admin account. If there isn't one, a default admin
 * account will be created and sample data will populate the database.
 */

@Component
public class SetupDataLoader implements
    ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private EmailRepository emailRepository;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private ActivityRoleRepository activityRoleRepository;

  @Value("#{environment.ADMIN_EMAIL}")
  private String adminEmail;

  @Value("#{environment.ADMIN_PASSWORD}")
  private String adminPassword;

  private Random random = new Random();

  //The potential first names for sample users
  private String[] userFirstNames = {"Oliver", "Noah", "Leo", "Jack", "Lucas", "George", "William",
      "James", "Thomas", "Charlie", "Arlo", "Hunter", "Liam",
      "Mason", "Theodore", "Oscar", "Ethan", "Elijah", "Lachlan", "Henry", "Max", "Hudson",
      "Cooper", "Alexander", "Luca", "Jackson", "Jacob", "Carter",
      "Benjamin", "Levi", "Archie", "Finn", "Harrison", "Joshua", "Arthur", "Isaac", "Samuel",
      "Harry", "Beau", "Hugo", "Lincoln", "Ryan", "Caleb", "Theo",
      "Eli", "Asher", "Felix", "Daniel", "Luke", "Amelia", "Charlotte", "Isla", "Olivia", "Ruby",
      "Sophie", "Harper", "Mila", "Willow", "Ava", "Grace",
      "Ella", "Emily", "Isabella", "Mia", "Zoe", "Chloe", "Hazel", "Lily", "Lucy", "Sophia",
      "Evelyn", "Aria", "Ivy", "Georgia", "Matilda", "Emma", "Evie",
      "Scarlett", "Abigail", "Thea", "Freya", "Riley", "Layla", "Sienna", "Maia", "Poppy",
      "Mackenzie", "Millie", "Zara", "Ellie", "Indie", "Maddison", "Olive",
      "Eva", "Frankie", "Ayla", "Piper", "Alice", "Violet", "Luna", "Bella", "Hannah", "Isabelle",
      "Florence", "Sadie", "Emilia", "Elizabeth"};

  //The potential last names for sample users
  private String[] userLastNames = {"Edmun"};


  /**
   * Create a default admin account if it is not found in the database Populates the database with
   * sample data if there is no default admin therefore this will only run if the database is
   * empty.
   *
   * @param event ContextRefreshedEvent
   */
  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (isAlreadySetup()) {
      return;
    }
    createRoleIfNotFound("ROLE_ADMIN");
    createRoleIfNotFound("ROLE_USER");
    createRoleIfNotFound("ROLE_USER_ADMIN");

    Role adminRole = roleRepository.findByName("ROLE_ADMIN");
    Profile user = new Profile();
    profileRepository.save(user);
    user.setPassword(adminPassword);
    Email email = new Email((adminEmail));
    emailRepository.save(email);
    user.setPrimaryEmail(email);
    Collection<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
    user.setRoles(roles);

    createSampleUsers(50);
    createSampleActivities(50);
  }

  /**
   * Check if default admin account is already setup
   *
   * @return True if found or else False
   */
  private boolean isAlreadySetup() {
    Optional<Email> email = emailRepository.findByAddress(adminEmail);
    return email.isPresent();
  }


  /**
   * Create an object of Role class and save to role repository if not found
   *
   * @param name role name
   */
  @Transactional
  public void createRoleIfNotFound(String name) {
    Role role = roleRepository.findByName(name);
    if (role == null) {
      role = new Role(name);
      roleRepository.save(role);
    }
  }

  /**
   * Creates semi random users, used for populating the database
   * <p>
   * First Name: chooses a random name from the first name list Last Name: always Edmun to identify
   * sample users Primary Email: FirstName LastName Index @testemail.com (this is to ensure there
   * are no primary keys being the same) Role: Assigns the profile to be a user
   *
   * @param numberOfUsers The number of users you want to create
   */
  private void createSampleUsers(int numberOfUsers) {
    for (int index = 1; index < numberOfUsers + 1; index++) {
      int randomNumFirstName = random.nextInt(1000);
      int randomNumLastName = random.nextInt(1000);

      Role userRole = roleRepository.findByName("ROLE_USER");
      Profile user = new Profile();
      profileRepository.save(user);

      user.setFirstname(userFirstNames[randomNumFirstName % userFirstNames.length]);
      user.setLastname(userLastNames[randomNumLastName % userLastNames.length]);
      user.setBio("This is a test user");
      user.setPassword("Password1");

      Email email = new Email(
          userFirstNames[randomNumFirstName % userFirstNames.length].toLowerCase() + userLastNames[
              randomNumLastName % userLastNames.length].toLowerCase() + index + "@testemail.com");
      emailRepository.save(email);
      user.setPrimaryEmail(email);
      Collection<Role> roles = new ArrayList<>(Arrays.asList(userRole));
      user.setRoles(roles);
    }
  }

  /**
   * Creates semi random activities used for populating the database
   * <p>
   * Location: Chooses a location from the location options (array of doubles with lat lng) for
   * where to position the activity Description: Mostly static example descriptions but some with
   * dynamic location name Activity Name: Based off of the location example names Activity Type:
   * Randomly assigns a type to the activity Activity Members: Assigns a random number of followers,
   * participants and organisers (cant be the creator of the activity) Activity Duration/Continuous:
   * Randomly assigns activity to duration or continuous but favours continuous
   *
   * @param numberOfActivities The number of activities you want to create
   */
  private void createSampleActivities(int numberOfActivities) {
    //City locations and names
    String[] locationNames = {"Napier", "Christchurch", "Auckland", "Wellington"};
    double[][] locations = {{-39.548530, 176.796837}, {-43.522663, 172.554899},
        {-36.892801, 174.768907}, {-41.112469, 175.060221}};
    double randomLocationPositionLng;
    double randomLocationPositionLat;
    int randomType;
    int randomLocation;
    int continous;

    //Create activities iteratively based off of numberOfActivities
    for (int index = 1; index < numberOfActivities + 1; index++) {
      randomLocationPositionLat = (double) (random.nextInt(100) - 50) / 200;
      randomLocationPositionLng = (double) (random.nextInt(100) - 50) / 200;
      randomType = random.nextInt(1000);
      randomLocation = random.nextInt(4);
      Profile randomUser = profileRepository.findAll()
          .get(random.nextInt((int) profileRepository.count()));
      Profile creator = profileRepository.findAll()
          .get(random.nextInt((int) profileRepository.count()));
      continous = random.nextInt(4);

      Activity activity = new Activity();
      activity.setProfile(creator);
      Set<ActivityType> activityTypes = new HashSet<>();

      //Activity location. This is based off of city locations with some randomness
      Location location = new Location(locations[randomLocation][0] + randomLocationPositionLat,
          locations[randomLocation][1] + randomLocationPositionLng);
      locationRepository.save(location);
      activity.setLocation(location);

      //Description, activity name and type
      if (randomType % 4 == 0) {
        activityTypes.add(ActivityType.Walk);
        activity.setActivityName(locationNames[randomLocation] + " Walk");
        activity.setDescription("This event takes place in " + locationNames[randomLocation]
            + ". We will explore the city streets as we walk to different attractions around the great city of "
            + locationNames[randomLocation]);
      } else if (randomType % 4 == 1) {
        activityTypes.add(ActivityType.Run);
        activity.setActivityName(locationNames[randomLocation] + " Pub Crawl");
        activity.setDescription(
            "In this event we are going to run from the start location to the local "
                + locationNames[randomLocation] + "pub wherever that might be");
      } else if (randomType % 4 == 2) {
        activityTypes.add(ActivityType.Hike);
        activity.setActivityName("Hike in " + locationNames[randomLocation]);
        activity.setDescription(
            "This hike coves a large distance in " + locationNames[randomLocation]
                + ". We will explore the nearby tracks and attractions");
      } else if (randomType % 4 == 3) {
        activityTypes.add(ActivityType.Swim);
        activity.setActivityName("100m Swim trials " + locationNames[randomLocation]);
        activity.setDescription(
            "In this event we will find out who can swim the fastest! Bring your A game because since this is a test activity the location of it could be on land! How good.");
      } else {
        activityTypes.add(ActivityType.Bike);
        activity.setActivityName(locationNames[randomLocation] + " Bike Ride");
        activity.setDescription("This is a bike ride activity.");
      }

      //Activity Type
      if (continous >= 2) {
        activity.setContinuous(true);
      } else {
        activity.setStartTimeByString("2000-04-28T15:50:41+1300");
        activity.setEndTimeByString("2030-08-28T15:50:41+1300");
        activity.setContinuous(false);
      }
      activity.setActivityTypes(activityTypes);
      activityRepository.save(activity);

      //participants
      for (int i = 0; i < random.nextInt(10); i++) {
        ActivityRole role = new ActivityRole();
        Profile participant = profileRepository.findAll()
            .get(random.nextInt((int) profileRepository.count()));
        if (participant != creator) {
          role.setProfile(participant);
          role.setActivityRoleType(ActivityRoleType.Participant);
          role.setActivity(activity);
          activityRoleRepository.save(role);
        }
      }
      //Followers
      for (int i = 0; i < random.nextInt(40); i++) {
        ActivityRole role = new ActivityRole();
        Profile follower = profileRepository.findAll()
            .get(random.nextInt((int) profileRepository.count()));
        if (follower != creator) {
          role.setProfile(follower);
          role.setActivityRoleType(ActivityRoleType.Follower);
          role.setActivity(activity);
          activityRoleRepository.save(role);
        }
      }
      //Organisers
      for (int i = 0; i < random.nextInt(4); i++) {
        ActivityRole role = new ActivityRole();
        Profile organiser = profileRepository.findAll()
            .get(random.nextInt((int) profileRepository.count()));
        if (organiser != creator) {
          role.setProfile(organiser);
          role.setActivityRoleType(ActivityRoleType.Organiser);
          role.setActivity(activity);
          activityRoleRepository.save(role);
        }
      }
    }
  }
}
