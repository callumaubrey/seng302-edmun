package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.PasswordToken;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.CountryRepository;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.PasswordTokenRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.RoleRepository;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.EditEmailsRequest;
import com.springvuegradle.team6.requests.EditPasswordRequest;
import com.springvuegradle.team6.requests.EditProfileRequest;
import com.springvuegradle.team6.requests.ChangePasswordWithoutOldPasswordRequest;
import com.springvuegradle.team6.requests.LocationUpdateRequest;
import com.springvuegradle.team6.requests.ResetPasswordRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import com.springvuegradle.team6.services.EmailService;
import com.springvuegradle.team6.services.FileService;
import com.springvuegradle.team6.services.LocationService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(
    origins = {
      "http://localhost:9000",
      "http://localhost:9500",
      "https://csse-s302g7.canterbury.ac.nz/test",
      "https://csse-s302g7.canterbury.ac.nz/prod"
    },
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RequestMapping("/profiles")
public class UserProfileController {

  @Autowired
  public EmailService emailService;

  @Value("#{environment.ADMIN_EMAIL}")
  private String adminEmail;

  private final ProfileRepository repository;
  private final CountryRepository countryRepository;
  private final RoleRepository roleRepository;
  private final EmailRepository emailRepository;
  private final LocationRepository locationRepository;
  private final LocationService locationService;
  private final PasswordTokenRepository passwordTokenRepository;
  private final FileService fileService;

  UserProfileController(
          ProfileRepository rep,
          CountryRepository countryRepository,
          EmailRepository emailRepository,
          RoleRepository roleRep,
          LocationRepository locationRepository,
          LocationService locationService,
          PasswordTokenRepository passwordTokenRepository,
          FileService fileService) {
    this.repository = rep;
    this.countryRepository = countryRepository;
    this.roleRepository = roleRep;
    this.emailRepository = emailRepository;
    this.locationRepository = locationRepository;
    this.locationService = locationService;
    this.passwordTokenRepository = passwordTokenRepository;
    this.fileService = fileService;
  }

  /**
   * Gets the user where the provided id. Otherwise return unauthorized if id doesnt exist or not
   * logged in.
   *
   * @param id The requested id
   * @param session The current Http Session
   * @return The response code and message/Json
   * @throws JsonProcessingException
   */
  @GetMapping("/{id}")
  public ResponseEntity getProfile(@PathVariable Integer id, HttpSession session)
      throws JsonProcessingException {

    // Check if user can view profile
    ResponseEntity<String> canViewResponse =
        UserSecurityService.checkViewingPermission(id, session, repository);
    if (canViewResponse != null) {
      return canViewResponse;
    }

    // Get Profile
    Optional<Profile> p = repository.findById(id);
    if (p.isEmpty()) {
      return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
    }
    Profile profile = p.get();

    // Check if user is authorised to view private location
    int sessionId = Integer.parseInt(session.getAttribute("id").toString());
    boolean authorised = UserSecurityService.checkIsAdminOrCreator(sessionId, id);

    // Return Json
    return ResponseEntity.ok(profile.getJSON(authorised));
  }

  /**
   * Put request to: Update a user profile with specified Id given in request body, this user must
   * be logged in. New data is contained in request body, empty fields are unchanged
   *
   * @param request EditProfileRequest form with Id of profile to edit and new info to update
   * @return returns response entity with details of update or 404 if not found or bad request if
   *     incorrect request body
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateProfile(
      @PathVariable Integer id,
      @Valid @RequestBody EditProfileRequest request,
      HttpSession session) {

    // Update Location
    if (request.location != null) {
      updateLocation(id, request.location, session);
    }

    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile edit = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }
      // Tries to edit profile using request body as an EditProfileRequest if fails returns 400
      // error
      try {
        request.editProfileFromRequest(
            edit, countryRepository, emailRepository, locationRepository);
      } catch (Exception e) {
        return new ResponseEntity<>("Error in request body", HttpStatus.BAD_REQUEST);
      }
      ResponseEntity<String> editEmailsResponse =
          EditEmailsRequest.editEmails(
              edit, emailRepository, request.additionalemail, request.primaryemail);
      if (editEmailsResponse != null) {
        return editEmailsResponse;
      }
      repository.save(edit);

      return ResponseEntity.ok("User " + edit.getFirstname() + "'s profile was updated.");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get all profile's information in a string
   *
   * @return response entity with user profile information
   */
  @GetMapping("/")
  public ResponseEntity<String> getAll() {
    List<Profile> all = repository.findAll();
    StringBuilder response = new StringBuilder();
    for (Profile p : all) {
      response.append(p.toString());
    }
    return ResponseEntity.ok(response.toString());
  }
  /**
   * Get request to return the current user that is logged in the system
   *
   * @return ResponseEntity which contains which user is logged in
   * @return ResponseEntity which can be success(2xx) with the users name or error(4xx) user is not
   *     logged in
   */
  @GetMapping("/user")
  public ResponseEntity getProfile2(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = (int) session.getAttribute("id");
      return ResponseEntity.ok(repository.findById(intId));
    }
  }

  /**
   * Get request to return the current user's firstname that is logged in the system
   *
   * @return ResponseEntity which contains which user is logged in
   * @return ResponseEntity which can be success(2xx) with the users name or error(4xx) user is not
   *     logged in
   */
  @GetMapping("/firstname")
  public ResponseEntity getProfileFirstName(HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = (int) session.getAttribute("id");
      return ResponseEntity.ok(repository.findById(intId).getFirstname());
    }
  }

  /**
   * Return all emails based on the profileId Can be used by any logged in user
   *
   * @param session The logged in session
   * @param profileId The profileId of emails that you want
   * @return
   */
  @GetMapping("/{profileId}/emails")
  public ResponseEntity getProfileEmails(HttpSession session, @PathVariable Integer profileId) {
    Object id = session.getAttribute("id");
    ResponseEntity permissionResponse =
        UserSecurityService.checkViewingPermission(profileId, session, repository);
    if (permissionResponse != null) {
      return permissionResponse;
    }
    List<Email> emails = emailRepository.getEmailsByProfileId(profileId);
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("emails", emails);
    return ResponseEntity.ok(resultsObject);
  }

  /**
   * Get request to return the id of the current user logged into the session
   *
   * @param session the current Http session
   * @return response entity with the current logged in user id
   */
  @GetMapping("/id")
  public ResponseEntity<String> getUserId(HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      return ResponseEntity.ok().body(id.toString());
    }
  }

  /**
   * Get request to return logged in user's role
   *
   * @param session the current Http session
   * @return response entity with the logged in user's role
   * @throws JsonProcessingException
   */
  @GetMapping("/role")
  public ResponseEntity getRole(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = Integer.parseInt(session.getAttribute("id").toString());
      return ResponseEntity.ok(repository.findById(intId).getRoles());
    }
  }

  /**
   * Creates a new user Takes JSON post data, checks the data and adds it to DB
   *
   * @param request the request entity
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PostMapping("")
  @ResponseBody
  public ResponseEntity createProfile(
      @Valid @RequestBody CreateProfileRequest request, HttpSession session) {
    Profile profile =
        request.generateProfile(emailRepository, countryRepository, locationRepository, locationService);
    profile.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

    // Check if primary email is being used by another user
    String emailAddress = profile.getPrimaryEmail().getAddress();

    if (emailRepository.findByAddress(emailAddress).isPresent()) {
      return new ResponseEntity<>(emailAddress + " is already being used", HttpStatus.BAD_REQUEST);
    }

    if (profile.getEmails() != null) {
      // Check that none of the additional emails are in the repository already
      for (Email email : profile.getEmails()) {
        if (emailRepository.findByAddress(email.getAddress()).isPresent()) {
          return new ResponseEntity<>(
              email.getAddress() + " is already being used", HttpStatus.BAD_REQUEST);
        }
      }
    }

    if (request.isValidDate(profile.getDob()) == false) {
      return new ResponseEntity("Date must be less than current date", HttpStatus.BAD_REQUEST);
    }

    repository.save(profile);

    session.setAttribute("id", profile.getId());
    return new ResponseEntity(profile.getId(), HttpStatus.CREATED);
  }

  /**
   * Edit user password, user must be logged in Takes JSON patch data, checks passwords match, then
   * hashed and added to DB
   *
   * @param request the request entity
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PutMapping("/{profileId}/password")
  public ResponseEntity<String> editPassword(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditPasswordRequest request,
      HttpSession session) {
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, repository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    Profile profile = repository.findById(profileId).get();
    if (!profile.comparePassword(request.oldpassword)) {
      return new ResponseEntity<>("Old password incorrect", HttpStatus.UNAUTHORIZED);
    }

    if (!request.newpassword.equals(request.repeatedpassword)) {
      return new ResponseEntity<>("Passwords don't match", HttpStatus.BAD_REQUEST);
    }

    profile.setPassword(request.newpassword);
    repository.save(profile);
    return ResponseEntity.ok("Password Edited Successfully");
  }

  /**
   * Put request to update user's location.
   * Generates public and private profile locations with names
   *
   * @param id user id under query
   * @param location location of type Location
   * @param session Http session
   * @return ResponseEntity will return 200 success if user is authorised to update location, else
   *     return 404 response if user is not found
   */
  @PutMapping("/{id}/location")
  public ResponseEntity<String> updateLocation(
      @PathVariable Integer id,
      @Valid @RequestBody LocationUpdateRequest location,
      HttpSession session) {

    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      // Remove previous location
      Location privateLocation = profile.getPrivateLocation();
      Location publicLocation = profile.getPublicLocation();
      profile.setPrivateLocation(null);
      profile.setPublicLocation(null);
      repository.save(profile);
      if(privateLocation != null) locationRepository.delete(privateLocation);
      if(publicLocation != null) locationRepository.delete(publicLocation);

      // Add new location
      Location newLocation = new Location(location.latitude, location.longitude);
      locationService.updateProfileLocation(profile, newLocation, locationRepository);

      repository.save(profile);

      return ResponseEntity.ok("OK");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Delete request to delete user's location
   *
   * @param id user id under query
   * @param session http session
   * @return Response entity will return 200 success if user is authorised to delete location, else
   *     return 404 if user profile is not found
   */
  @DeleteMapping("/{id}/location")
  public ResponseEntity<String> deleteLocation(@PathVariable Integer id, HttpSession session) {
    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      // Update location
      profile.setPrivateLocation(null);
      profile.setPublicLocation(null);
      repository.save(profile);

      return ResponseEntity.ok("OK");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value="/{id}/location", method=RequestMethod.GET)
  public ResponseEntity getLocation(@PathVariable Integer id, HttpSession session) {
    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
              UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }
      return ResponseEntity.ok(profile.getPrivateLocation());
    } else {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    }
  }

  /**
   * PUT request for user to change their password without knowing their old password.
   *
   * @param token of String type that was sent to the user's email
   * @param request request containing new and repeat password
   * @return Response Entity of 200 status code if user is found and passwords matched, otherwise
   *     return 404 if user not found or 400 if passwords mismatched
   */
  @RequestMapping(value = "/forgotpassword/{token}", method = RequestMethod.PUT)
  public ResponseEntity editPasswordWithoutOldPassword(
      @PathVariable String token,
      @Valid @RequestBody ChangePasswordWithoutOldPasswordRequest request) {

    // Check if user and token exists
    if (passwordTokenRepository.findByToken(token) == null) {
      return new ResponseEntity<>("No such user exists", HttpStatus.NOT_FOUND);
    }
    Profile profile = passwordTokenRepository.findByToken(token).getProfile();

    // Check if passwords are matched
    if (!request.newPassword.equals(request.repeatPassword)) {
      return new ResponseEntity<>("Passwords dont match", HttpStatus.BAD_REQUEST);
    }
    profile.setPassword(request.newPassword);
    repository.save(profile);
    return ResponseEntity.ok("Password Edited Successfully");
  }

  /**
   * POST request for when a user wants to reset their password
   * Takes users email in the request and checks if exists then runs email service
   *
   * @param request request containing the users email
   * @return ResponseEntity 200 or 4xx
   */
  @PostMapping("/resetpassword")
  public ResponseEntity resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {
    if (request.getEmail().toLowerCase().equals(this.adminEmail.toLowerCase())) {
      return new ResponseEntity("Admin account cannot request password reset", HttpStatus.BAD_REQUEST);
    }

    Profile profile = repository.findByEmails_address(request.getEmail());
    if (profile == null) {
      return new ResponseEntity("Email is not associated to an account", HttpStatus.BAD_REQUEST);
    }

    PasswordToken token = new PasswordToken(profile);
    passwordTokenRepository.save(token);

    boolean sent = emailService.sendPasswordTokenEmail(
        request.getEmail(), "Reset Password", token.getToken(), profile.getFirstname());
    if (!sent) {
      return new ResponseEntity("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity("Password reset link sent", HttpStatus.OK);
  }

  /**
   * Updates and saves the users profile image and the link to it in the db
   * @param id the users id
   * @param file the image file
   * @param session the current http session
   * @return response entity ok for success or 4xx for unsuccessful
   */
  @PutMapping("/{id}/image")
  public ResponseEntity updatePhoto(
          @PathVariable Integer id,
          @RequestParam MultipartFile file,
          HttpSession session) {

    Optional<Profile> p = repository.findById(id);
    if (p.isEmpty()) {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
    Profile profile = p.get();

    // Check if authorised
    ResponseEntity<String> authorisedResponse =
            UserSecurityService.checkAuthorised(id, session, repository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    if (file == null) {
      profile.setPhotoFilename(null);
      profile = repository.save(profile);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    String fileName = fileService.uploadProfileImage(file, id);

    if (fileName == null) {
      return new ResponseEntity<>("Image or image type not valid", HttpStatus.BAD_REQUEST);
    }

    profile.setPhotoFilename(fileName);
    profile = repository.save(profile);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}
