package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.models.entities.LoginAttempt;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.Role;
import com.springvuegradle.team6.models.repositories.EmailRepository;
import com.springvuegradle.team6.models.repositories.LoginAttemptRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.requests.LoginRequest;
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(
    origins = {
      "http://localhost:9000",
      "http://localhost:9500",
      "https://csse-s302g7.canterbury.ac.nz/test",
      "https://csse-s302g7.canterbury.ac.nz/prod"
    },
    allowCredentials = "true",
    allowedHeaders = "*",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RestController
@RequestMapping("")
public class LoginController {
  private ProfileRepository profileRepository;
  private EmailRepository emailRepository;
  private LoginAttemptRepository loginAttemptRepository;

  /**
   * Constructor for class gets the database repo
   *
   * @param profileRepository
   * @param emailRepository
   */
  public LoginController(
      ProfileRepository profileRepository,
      EmailRepository emailRepository,
      LoginAttemptRepository loginAttemptRepository) {
    this.profileRepository = profileRepository;
    this.emailRepository = emailRepository;
    this.loginAttemptRepository = loginAttemptRepository;
  }

  /**
   * Logs user into a session Takes JSON post data, checks the data and logs user into specific
   * account if it exists.
   * After three failed attempts within 10 minutes, the account is considered locked.
   * The global admin never gets locked no matter the number of attempts.
   * Emails that don't exist do not get locked.
   *
   * @param loginDetail the request entity
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginDetail, HttpSession session) {
    try {
      String emailStr = loginDetail.getEmail();
      Optional<Email> email = emailRepository.findByAddress(emailStr);
      if (!(email.isPresent())) {
        return new ResponseEntity(
            "No associated user with email and password", HttpStatus.UNAUTHORIZED);
      }
      Profile user = profileRepository.findByEmailsContains(email.get());
      session.removeAttribute("id");
      if (user != null) {
        if (user.isLocked()) {
          return new ResponseEntity("This account is locked", HttpStatus.UNAUTHORIZED);
        }

        boolean isGlobalAdmin = false;
        session.setAttribute("id", user.getId());
        for (Role role : user.getRoles()) {
          if (role.getRoleName().equals("ROLE_ADMIN")) {
            isGlobalAdmin = true;
          }
        }

        if (user.comparePassword(loginDetail.getPassword())) {
          boolean isAdmin = false;
          session.setAttribute("id", user.getId());
          List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList();
          for (Role role : user.getRoles()) {
            if (role.getRoleName().equals("ROLE_ADMIN")
                || role.getRoleName().equals("ROLE_USER_ADMIN")) {
              isAdmin = true;
            }
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
            updatedAuthorities.add(authority);
          }

          SecurityContextHolder.getContext()
              .setAuthentication(
                  new UsernamePasswordAuthenticationToken(
                      SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                      SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                      updatedAuthorities));
          ObjectMapper mapper = new ObjectMapper();
          String postJson = mapper.writeValueAsString(user.getId());
          return ResponseEntity.ok(postJson);
        } else if (isGlobalAdmin) {
          return new ResponseEntity(
              "No associated user with email and password", HttpStatus.UNAUTHORIZED);

        } else {
          LoginAttempt loginAttempt = new LoginAttempt(user);
          loginAttemptRepository.save(loginAttempt);
          LocalDateTime time = LocalDateTime.now().minusMinutes(10);
          List<LoginAttempt> attempts =
              loginAttemptRepository.findByProfileIdGreaterThanTime(user.getId(), time);
          if (attempts.size() > 3) {
            user.setLockStatus(true);
            profileRepository.save(user);
            return new ResponseEntity("This account is locked", HttpStatus.UNAUTHORIZED);
          } else {
            return new ResponseEntity(
                "No associated user with email and password", HttpStatus.UNAUTHORIZED);
          }
        }
      }
      return new ResponseEntity(
          "No associated user with email and password", HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Logs user out of session
   *
   * @return ResponseEntity which can be success(2xx) if user exists or error(4xx) if not logged in
   */
  @GetMapping("/logout/")
  public ResponseEntity<String> logout(HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    }
    int intId = (int) session.getAttribute("id");
    Profile profile = profileRepository.findById(intId);
    session.removeAttribute("id");
    return ResponseEntity.ok("Successfully logged out from the user: " + profile.getFirstname());
  }
}
