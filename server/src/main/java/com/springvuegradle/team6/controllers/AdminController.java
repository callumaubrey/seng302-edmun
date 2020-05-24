package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.*;
import com.springvuegradle.team6.startup.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controller for "/admin" endpoints
 */

@PreAuthorize("hasRole('ADMIN') or hasRole('USER_ADMIN')")
@RequestMapping("/admin")
@RestController
public class AdminController {

  private final ProfileRepository profileRepository;
  private final EmailRepository emailRepository;
  private final RoleRepository roleRepository;

  /**
   * Constructor for AdminController class which gets the profile, email and role repository
   *
   * @param profileRep the profile repository
   * @param emailRep the email repository
   * @param roleRep the role repository
   */
  AdminController(ProfileRepository profileRep, EmailRepository emailRep, RoleRepository roleRep) {
    this.profileRepository = profileRep;
    this.emailRepository = emailRep;
    this.roleRepository = roleRep;
  }

  /**
   * Delete user profile based on user's primary email
   *
   * @param request DeleteProfileRequest
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @Transactional
  @DeleteMapping("/profiles")
  public ResponseEntity<String> removeProfile(@RequestBody DeleteProfileRequest request) {
    Optional<Email> email = emailRepository.findByAddress(request.getEmail());
    if (email.isPresent()) {
      Profile profile = profileRepository.findByEmailsContains(email.get());
      profileRepository.delete(profile);

      return ResponseEntity.ok("User account with email: " + request.getEmail() + " is terminated");
    } else {
      return new ResponseEntity<String>(
          "No user associated with " + request.getEmail(), HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Add role to user's list of roles, identify user based on their primary email
   *
   * @param request AddRoleRequest
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PostMapping("/profiles/role")
  public ResponseEntity<String> addRole(@RequestBody AddRoleRequest request) {
    Optional<Email> email = emailRepository.findByAddress(request.getEmail());
    if (email.isPresent()) {
      Profile profile = profileRepository.findByEmailsContains(email.get());
      if (request.getRole().equals("ROLE_ADMIN") || request.getRole().equals("ROLE_USER")) {
        Role role = roleRepository.findByName(request.getRole());
        try {
          profile.addRole(role);
          profileRepository.save(profile);
        } catch (DuplicateRoleException e) {
          return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(request.getRole() + " is added to user");
      } else {
        return new ResponseEntity("Invalid role requested", HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity("User does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Remove role from user's list of role, identify user by primary email
   *
   * @param request DeleteRoleRequest
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @DeleteMapping("/profiles/role")
  public ResponseEntity<String> deleteRole(@RequestBody DeleteRoleRequest request) {
    Optional<Email> email = emailRepository.findByAddress(request.getEmail());
    if (email.isPresent()) {
      Profile profile = profileRepository.findByEmailsContains(email.get());
      if (request.getRole().equals("ROLE_ADMIN") || request.getRole().equals("ROLE_USER")) {
        try {
          profile.removeRole(request.getRole());
          profileRepository.save(profile);
        } catch (RoleNotFoundException e) {
          return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(request.getRole() + " is deleted from user profile");
      } else {
        return new ResponseEntity("Invalid role requested", HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity("User does not exist", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * The admin may edit another user's password without providing the user's current password
   *
   * @param profileId  id of the user to be edited
   * @param request the edit password request containing the new password
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PutMapping("/profiles/{profileId}/password")
  public ResponseEntity<String> adminEditPassword(
      @PathVariable Integer profileId, @Valid @RequestBody AdminEditPasswordRequest request) {

    if (profileRepository.findById(profileId).isPresent()) {
      Profile profile = profileRepository.findById(profileId).get();
      if (!request.newPassword.equals(request.repeatPassword)) {
        return new ResponseEntity<>("Passwords don't match", HttpStatus.BAD_REQUEST);
      }
      profile.setPassword(request.newPassword);
      profileRepository.save(profile);
      return ResponseEntity.ok("Password Edited Successfully");
    } else {
      return new ResponseEntity<>("No such user exists", HttpStatus.NOT_FOUND);
    }
  }
}
