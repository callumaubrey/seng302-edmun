package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.EditEmailsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "://", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@RestController
@RequestMapping("")
public class EmailsController {
    private final ProfileRepository repository;
    private final EmailRepository emailRepository;

    EmailsController(
            ProfileRepository rep,
            EmailRepository emailRepository
    ) {
        this.repository = rep;
        this.emailRepository = emailRepository;
    }

    private ResponseEntity<String> checkAuthorised(Integer requestId, HttpSession session) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        if (!(id.toString().equals(requestId.toString()))) {
            return new ResponseEntity<>("You can only edit you're own profile", HttpStatus.UNAUTHORIZED);
        }

        if (!repository.existsById(requestId)) {
            return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
        }

        return null;
    }

    /**
     * End point to update the primary email and the additional emails
     * @param profileId Determines the user that is being updated
     * @param request The request with fields indicating the change
     * @param session The session that the user currently runs on
     * @return the response with status code
     */
    @PutMapping("/profile/{profileId}/emails")
    public ResponseEntity<String> updateEmails(@PathVariable Integer profileId, @Valid  @RequestBody EditEmailsRequest request, HttpSession session) {
        Optional<Profile> p = repository.findById(profileId);
        if (p.isPresent()) {
            Profile profile = p.get();

            // Check if authorised
            ResponseEntity<String> authorisedResponse = this.checkAuthorised(profileId, session);
            if (authorisedResponse != null) {
                return authorisedResponse;
            }

            // Check if primary email is being used by another user
            if (emailRepository.findByAddress(request.primaryEmail).isPresent() && !(profile.getEmail().getAddress().equals(request.primaryEmail)) ) {
                return new ResponseEntity<>(request.primaryEmail + " is already being used", HttpStatus.BAD_REQUEST);
            }
            profile.getEmail().setAddress(request.primaryEmail);

            if (request.additionalEmail != null) {
                // Create a set containing all the emails requested
                Set<Email> newEmails = new HashSet<>();
                for (String email : request.additionalEmail) {
                    newEmails.add(new Email(email));
                }

                // Find out which emails the user is associated with already from the emails requested
                for (Iterator<Email> i = profile.getAdditionalemail().iterator(); i.hasNext(); ) {
                    Email email = i.next();
                    if (!(newEmails.contains(email))) {
                        i.remove();
                    }
                }

                // Add the ones that are requested but not associated with the user
                for (Email email : newEmails) {
                    if (!(profile.getAdditionalemail().contains(email))) {
                        // Check if the email is being used by another user
                        if (emailRepository.findByAddress(email.getAddress()).isPresent()) {
                            return new ResponseEntity<>(email.getAddress() + " is already being used", HttpStatus.BAD_REQUEST);
                        } else {
                            profile.getAdditionalemail().add(email);
                        }
                    }
                }
            } else {
                profile.getAdditionalemail().clear();
            }

            repository.save(profile);

            return ResponseEntity.ok("Profile updated successfully");
        } else {
            return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
        }

    }
}
