package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.Country;
import com.springvuegradle.team6.models.CountryRepository;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.EditProfileRequest;
import com.springvuegradle.team6.requests.EditPasswordRequest;
import com.springvuegradle.team6.requests.EditEmailRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Controller @RequestMapping("/profile")
public class UserProfileController {

    private final ProfileRepository repository;
    private final CountryRepository countryRepository;

    UserProfileController(ProfileRepository rep, CountryRepository countryRepository) {
        this.repository = rep;
        this.countryRepository = countryRepository;
    }

    private ResponseEntity<String> checkAuthorised(Integer request_id, HttpSession session) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity("Muse be logged in", HttpStatus.UNAUTHORIZED);
        }

        if (id != request_id) {
            return new ResponseEntity("You can only edit you're own profile", HttpStatus.UNAUTHORIZED);
        }

        if (repository.existsById(request_id) == false) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getProfile(@PathVariable Integer id) {
        Profile p = repository.findById(id).get();
        return ResponseEntity.ok("Signed in users name" + p.getFirstname());
    }

    /**
     * Put request to: Update a user profile with specified Id given in request body,
     * this user must be logged in. New data is contained in request body, empty fields
     * are unchanged
     *
     * @param request EditProfileRequest form with Id of profile to edit and new info to update
     * @return returns response entity with details of update
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Integer id, @RequestBody EditProfileRequest request, HttpSession session) {
        Optional<Profile> p = repository.findById(id);
        if (p.isPresent()) {
            Profile edit = p.get();

            // Check if authorised
            ResponseEntity<String> authorisedResponse = this.checkAuthorised(id, session);
            if (authorisedResponse != null) {
                return authorisedResponse;
            }

            // Edit profile
            request.editProfileFromRequest(edit, countryRepository);
            repository.save(edit);
            return ResponseEntity.ok("User no." + edit.getId() + ": " + edit.getEmail() + " was updated.");
        } else {
            return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/")
    public ResponseEntity<String> getAll() {
        List<Profile> all = (List<Profile>) repository.findAll();
        StringBuilder response = new StringBuilder();
        for (Profile p : all) {
            response.append(p.toString());
        }
        return ResponseEntity.ok(response.toString());
    }
    /**
     * Get request to return which users name is logged into the session
     *
     * @return ResponseEntity which contains which user is logged in
     * @return ResponseEntity which can be success(2xx) with the users name
     * or error(4xx) user is not logged in
     */
    @GetMapping("/user")
    public ResponseEntity<String> getProfile2(HttpSession session){
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
        }
        else {
            int intId = (int) session.getAttribute("id");
            return ResponseEntity.ok("Signed in users name " + repository.findById(intId).getFirstname());
        }
    }

    /**
     * Creates a new user
     * Takes JSON post data, checks the data and adds it to DB
     *
     * @param request the request entity
     * @return ResponseEntity which can be success(2xx) or error(4xx)
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity createProfile(@Valid @RequestBody CreateProfileRequest request) {
        Profile profile = request.generateProfile();

        if (repository.existsByEmail(profile.getEmail())) {
            return new ResponseEntity("Email must be unique", HttpStatus.BAD_REQUEST);
        }

        repository.save(profile);
        return ResponseEntity.ok("User Created Successfully");
    }

    /**
     * Edit user password, user must be logged in
     * Takes JSON patch data, checks passwords match, then hashed and added to DB
     *
     * @param request the request entity
     * @return ResponseEntity which can be success(2xx) or error(4xx)
     */
    @PatchMapping("/editpassword")
    public ResponseEntity<String> editPassword(@Valid @RequestBody EditPasswordRequest request, HttpSession session) {
        ResponseEntity<String> authorised_response = this.checkAuthorised(request.id, session);
        if (authorised_response != null) {
            return authorised_response;
        }

        Profile profile = repository.findById(request.id).get();
        if (profile.comparePassword(request.oldpassword) == false) {
            return new ResponseEntity("Old password incorrect", HttpStatus.UNAUTHORIZED);
        }

        if (request.newpassword.equals(request.repeatedpassword) == false) {
            return new ResponseEntity("Passwords don't match", HttpStatus.BAD_REQUEST);
        }

        profile.setPassword(request.newpassword);
        repository.save(profile);
        return ResponseEntity.ok("Password Edited Successfully");
    }

    /**
     * Edit user emails, user must be logged in
     * Takes JSON patch data, makes sure all emails are valid emails
     * and that the same email dosen't exist in both primary and additional
     * then adds data to DB
     *
     * @param request the request entity
     * @return ResponseEntity which can be success(2xx) or error(4xx)
     */
    @PatchMapping("/editemail")
    public ResponseEntity<String> editEmail(@Valid @RequestBody EditEmailRequest request, HttpSession session){
        ResponseEntity<String> authorised_response = this.checkAuthorised(request.id, session);
        if (authorised_response != null) {
            return authorised_response;
        }

        Profile profile = repository.findById(request.id).get();

        List<String> validEmails = new ArrayList<String>();
        if (request.additionalemail != null && request.additionalemail.size() > 0) {
            for (String email: request.additionalemail) {
                if (email.equals(request.primaryemail)) {
                    return new ResponseEntity("Cannot have same email as primary and additional", HttpStatus.BAD_REQUEST);
                } else if (request.isValidEmail(email) == false) {
                    return new ResponseEntity("Email is not valid", HttpStatus.BAD_REQUEST);
                } else {
                    validEmails.add(email);
                }
            }

            if (validEmails.size() > 5) {
                return new ResponseEntity("You cannot have more than 5 valid additional emails", HttpStatus.BAD_REQUEST);
            }
        }

        if (validEmails.isEmpty() == false) {
            profile.setAdditionalemail(String.join(",", validEmails));
        }

        profile.setEmail(request.primaryemail);
        repository.save(profile);

        return ResponseEntity.ok("Email Successfully Edited");
    }
}
