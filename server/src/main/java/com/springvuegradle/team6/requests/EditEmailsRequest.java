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
import javax.validation.constraints.Pattern;
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
                && !(profile.getEmail().getAddress().equals(primaryEmail))
                && !(profile.getAdditionalemail().contains(new Email(primaryEmail)))) {
            return new ResponseEntity<>(primaryEmail + " is already being used", HttpStatus.BAD_REQUEST);
        }
        Email newPrimary = null;
        if (additionalEmail != null) {
            if (profile.getAdditionalemail().contains(new Email(primaryEmail))) {
                for (Email email : profile.getAdditionalemail()) {
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
                    if (emailRepository.findByAddress(email.getAddress()).isPresent()
                            && !(profile.getEmail().equals(email))) {
                        return new ResponseEntity<>(email.getAddress() + " is already being used", HttpStatus.BAD_REQUEST);
                    } else {
                        if (profile.getEmail().equals(email)) {
                            profile.getAdditionalemail().add(profile.getEmail());
                            flag = true;
                        } else {
                            profile.getAdditionalemail().add(email);
                        }
                    }
                }
            }
        } else {
            profile.getAdditionalemail().clear();
        }
        if (flag) {
            profile.setEmail(newPrimary);
        } else {
            profile.getEmail().setAddress(primaryEmail);
        }
        return null;
    }

}
