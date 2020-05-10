package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.validators.EmailCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EditEmailsRequest {
    /**
     * The primary email of the user, used for them to log in.
     */
    @NotNull
    @NotEmpty
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email
    public String primaryEmail;

    @EmailCollection
    @Size(max=5)
    @JsonProperty("additional_email")
    public List<String> additionalEmail;

    /**
     * Checks the database for emails and see if the client request doesn't invalidate any database constraints on email
     * and if there are no invalids, then update the repository with the requested emails.
     * @param profile The profile taken from the database that needs to be updated
     * @param emailRepository The repository containing all the emails
     * @param additionalEmail The additional emails from the HTTP request
     * @param primaryEmail The primary email from the HTTP request
     * @return ResponseEntity holding error messages if one of the conditions failed, otherwise return null to indicate success
     */
    public static ResponseEntity<String> editEmails(Profile profile, EmailRepository emailRepository, List<String> additionalEmail, String primaryEmail) {
        // Check that the request additional email is not in the request primary email and vice versa
        if (additionalEmail != null) {
            if (additionalEmail.contains(primaryEmail)) {
                return new ResponseEntity<>("The primary email cannot be an additional email", HttpStatus.BAD_REQUEST);
            }
        }

        // Check if primary email is being used by another user
        if (emailRepository.findByAddress(primaryEmail).isPresent()
                && !(profile.getPrimaryEmail().getAddress().equals(primaryEmail))
                && !(profile.getEmails().contains(new Email(primaryEmail)))) {
            return new ResponseEntity<>(primaryEmail + " is already being used", HttpStatus.BAD_REQUEST);
        }
        Email newPrimary = null;
        if (additionalEmail != null) {
            if (profile.getEmails().contains(new Email(primaryEmail))) {
                for (Email email : profile.getEmails()) {
                    if (email.equals(new Email(primaryEmail))) {
                        newPrimary = email;
                        break;
                    }
                }
            }
        }

        boolean flag = false;

        if (additionalEmail != null) {
            // Create a set containing all the emails requested
            Set<Email> newEmails = new HashSet<>();
            for (String email : additionalEmail) {
                newEmails.add(new Email(email));
            }

            // Find out which emails the user is associated with already from the emails requested
            for (Iterator<Email> i = profile.getEmails().iterator(); i.hasNext(); ) {
                Email email = i.next();
                if (!(newEmails.contains(email))) {
                    i.remove();
                }
            }

            // Add the ones that are requested but not associated with the user
            for (Email email : newEmails) {
                if (!(profile.getEmails().contains(email))) {
                    // Check if the email is being used by another user
                    if (emailRepository.findByAddress(email.getAddress()).isPresent()
                            && !(profile.getPrimaryEmail().equals(email))) {
                        return new ResponseEntity<>(email.getAddress() + " is already being used", HttpStatus.BAD_REQUEST);
                    } else {
                        if (profile.getPrimaryEmail().equals(email)) {
                            profile.getEmails().add(profile.getPrimaryEmail());
                            flag = true;
                        } else {
                            profile.getEmails().add(email);
                        }
                    }
                }
            }
        } else {
            profile.getEmails().clear();
        }
        if (flag) {
            profile.setPrimaryEmail(newPrimary);
        } else {
            profile.getPrimaryEmail().setAddress(primaryEmail);
        }
        return null;
    }

}
