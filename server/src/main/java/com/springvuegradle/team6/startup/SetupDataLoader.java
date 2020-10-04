package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.ActivityRoleType;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.PathType;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Role;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.PathRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import com.springvuegradle.team6.services.LocationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

  @Autowired
  private PathRepository pathRepository;

  @Autowired
  private TagRepository tagRepository;

  @Value("#{environment.ADMIN_EMAIL}")
  private String adminEmail;

  @Value("#{environment.ADMIN_PASSWORD}")
  private String adminPassword;

  @Value("#{environment.generate_sample_data}")
  private Boolean generateSampleData;

  private Random random = new Random();

  @Autowired
  private LocationService locationService;

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

  private String[] gender = {"male", "female"};


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
//    if (isAlreadySetup()) {
//      return;
//    }
    createRoleIfNotFound("ROLE_ADMIN");
    createRoleIfNotFound("ROLE_USER");
    createRoleIfNotFound("ROLE_USER_ADMIN");
//    Role adminRole = roleRepository.findByName("ROLE_ADMIN");
//    Profile user = new Profile();
//    profileRepository.save(user);
//    user.setPassword(adminPassword);
//    Email email = new Email((adminEmail));
//    emailRepository.save(email);
//    user.setPrimaryEmail(email);
//    Collection<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
//    user.setRoles(roles);

    if (generateSampleData) {
      System.out.println("----- Generating Sample Data -----");
      createSampleUsers(50);
//      createSampleActivities(0);
    }
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
      int  randomGender = random.nextInt(2);
      Integer randomYear = random.nextInt((2010 - 1940) + 1) + 1940;
      Integer randomMonth = random.nextInt((12 - 1) + 1) + 1;
      Integer randomDay = random.nextInt((28 - 1) + 1) + 1;
      List<ActivityType> activityTypes = new ArrayList<>();
      activityTypes.add(ActivityType.Bike);
      activityTypes.add(ActivityType.Hike);
      activityTypes.add(ActivityType.Run);
      activityTypes.add(ActivityType.Swim);
      activityTypes.add(ActivityType.Walk);
      Random rand = new Random();
      ActivityType activityType = activityTypes.get(rand.nextInt(activityTypes.size()));
      Role userRole = roleRepository.findByName("ROLE_USER");
      Profile user = new Profile();
      profileRepository.save(user);

      Set<ActivityType> activityTypesSet = new HashSet<>();
      activityTypesSet.add(activityType);
      user.setActivityTypes(activityTypesSet);
      user.setFirstname(userFirstNames[randomNumFirstName % userFirstNames.length]);
      user.setLastname(userFirstNames[randomNumLastName % userFirstNames.length]);
      user.setPassword("Password1");
      user.setGender(gender[randomGender]);
      String randomMonthStr;
      String randomDayStr;
      if (randomMonth < 10) {
        randomMonthStr = "0" + randomMonth;
      } else {
        randomMonthStr = randomMonth.toString();
      }
      if (randomDay < 10) {
        randomDayStr = "0" + randomDay;
      } else {
        randomDayStr = randomDay.toString();
      }

      String dob = randomYear + "-" + randomMonthStr + "-" + randomDayStr;
      user.setDob(dob);


      Email email = new Email(
          userFirstNames[randomNumFirstName % userFirstNames.length].toLowerCase() + userFirstNames[
              randomNumLastName % userFirstNames.length].toLowerCase() + index + "@testemail.com");
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
        {-36.892801, 174.768907}, {-39.496623, 176.873982}};

    //SetUp Paths
    //    Christchurch
    List<Location> christchurchPathLocations = Arrays.asList(new Location(-43.521170, 172.626230),
        new Location(-43.539963, 172.612831), new Location(-43.517062, 172.572292));
    for (Location location : christchurchPathLocations) {
      locationRepository.save(location);
    }
    Path christchurchDefined = new Path(christchurchPathLocations,
        PathType.DEFINED);//Christchurch Path
    pathRepository.save(christchurchDefined);

    //    Napier
    List<Location> napierPathLocations = Arrays.asList(new Location(-39.533703, 176.850110),
        new Location(-39.497020, 176.864883), new Location(-39.479102, 176.917514),
        new Location(-39.489635, 176.900073));
    for (Location location : napierPathLocations) {
      locationRepository.save(location);
    }
    Path napierDefined = new Path(napierPathLocations, PathType.DEFINED);//Napier Path
    pathRepository.save(napierDefined);

    ArrayList<Tag> locationTags = new ArrayList<>();
    double randomLocationPositionLng;
    double randomLocationPositionLat;
    int randomType;
    int randomLocation;
    int continuous;

    //tags
    for (String name : locationNames) {
      Tag tag = new Tag(name);
      tagRepository.save(tag);
      locationTags.add(tag);
    }

    for (int index = 1; index < numberOfActivities + 1; index++) {
      //Create activities iteratively based off of numberOfActivities
      randomLocationPositionLat = (double) (random.nextInt(100) - 50) / 200;
      randomLocationPositionLng = (double) (random.nextInt(100) - 50) / 200;
      randomType = random.nextInt(1000);
      randomLocation = random.nextInt(4);
      Profile creator = profileRepository.findAll()
          .get(random.nextInt((int) profileRepository.count()));
      continuous = random.nextInt(4);

      Activity activity = new Activity();
      activity.setProfile(creator);
      Set<ActivityType> activityTypes = new HashSet<>();

      //Set hashtags
      Set<Tag> tags = new HashSet<>();
      tags.add(locationTags.get(randomLocation));

      //Activity location. This is based off of city locations with some randomness
      if (locationNames[randomLocation].equals("Christchurch")) {
        activity.setPath(christchurchDefined);
      }
      if (locationNames[randomLocation].equals("Napier")) {
        activity.setPath(napierDefined);
      }

      Location location = new Location(locations[randomLocation][0] + randomLocationPositionLat,
          locations[randomLocation][1] + randomLocationPositionLng);
      String locationName = locationService
          .getLocationAddressFromLatLng(locations[randomLocation][0] + randomLocationPositionLat,
              locations[randomLocation][1] + randomLocationPositionLng, false);
      if (locationName != null) {
        location.setName(locationName);
      }
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
      if (continuous >= 2) {
        activity.setContinuous(true);
      } else {
        activity.setStartTimeByString("2000-04-28T15:50:41+1300");
        activity.setEndTimeByString("2030-08-28T15:50:41+1300");
        activity.setContinuous(false);
      }
      activity.setActivityTypes(activityTypes);

      activity.setTags(tags);
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
