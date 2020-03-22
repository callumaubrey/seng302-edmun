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
    @PutMapping("/profiles/{profileId}/emails")
    public ResponseEntity<String> updateEmails(@PathVariable Integer profileId, @Valid  @RequestBody EditEmailsRequest request, HttpSession session) {
        Optional<Profile> p = repository.findById(profileId);
        if (p.isPresent()) {
            Profile profile = p.get();

            // Check if authorised
            ResponseEntity<String> authorisedResponse = this.checkAuthorised(profileId, session);
            if (authorisedResponse != null) {
                return authorisedResponse;
            }

            ResponseEntity<String> editEmailsResponse = EditEmailsRequest.editEmails(profile, emailRepository, request.additionalEmail, request.primaryEmail);
            if (editEmailsResponse != null) {
                return editEmailsResponse;
            }
            repository.save(profile);

            return ResponseEntity.ok("Profile updated successfully");
        } else {
            return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
        }

    }
}
