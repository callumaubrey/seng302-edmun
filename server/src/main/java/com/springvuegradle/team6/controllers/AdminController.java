package com.springvuegradle.team6.controllers;


import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.AddRoleRequest;
import com.springvuegradle.team6.requests.DeleteProfileRequest;
import com.springvuegradle.team6.requests.DeleteRoleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.Optional;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final ProfileRepository profileRepository;
    private final EmailRepository emailRepository;
    private final RoleRepository roleRepository;

    AdminController(ProfileRepository profileRep, EmailRepository emailRep, RoleRepository roleRep) {
        this.profileRepository = profileRep;
        this.emailRepository = emailRep;
        this.roleRepository = roleRep;
    }

    @Transactional
    @DeleteMapping("/profile")
    public ResponseEntity<String> removeProfile(@RequestBody DeleteProfileRequest request) {
        Optional<Email> email = emailRepository.findByAddress(request.getEmail());
        if (email.isPresent()) {
            profileRepository.removeByEmail(email.get());
            emailRepository.delete(email.get());
            return ResponseEntity.ok("User account with email: " + request.getEmail() + " is terminated");
        } else {
            return new ResponseEntity("No user associated with " + request.getEmail(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile/role")
    public ResponseEntity<String> addRole(@RequestBody AddRoleRequest request) {
        Optional<Email> email = emailRepository.findByAddress(request.getEmail());
        if (email.isPresent()) {
            Profile profile = profileRepository.findByEmail(email.get());
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

    @DeleteMapping("/profile/role")
    public ResponseEntity<String> deleteRole(@RequestBody DeleteRoleRequest request) {
        Optional<Email> email = emailRepository.findByAddress(request.getEmail());
        if (email.isPresent()) {
            Profile profile = profileRepository.findByEmail(email.get());
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


}
