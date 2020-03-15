package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.requests.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

import java.util.Optional;


@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@Controller
@RequestMapping("/account")
public class LoginController {
    private ProfileRepository profileRepository;
    private EmailRepository emailRepository;

    /**
     *
     * @param profileRepository
     * @param emailRepository
     */
    public LoginController(ProfileRepository profileRepository, EmailRepository emailRepository) {
        this.profileRepository = profileRepository;
        this.emailRepository = emailRepository;
    }



    /**
     * Logs user into a session
     * Takes JSON post data, checks the data and logs user into specific account if it exists
     *
     * @param loginDetail the request entity
     * @return ResponseEntity which can be success(2xx) or error(4xx)
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginDetail,
                                        HttpSession session) {
        // Check email exists
        Optional<Email> email = emailRepository.findById(loginDetail.getEmail());
        if (email.isPresent()) {
            // Check if email associates to users primary email
            Profile user = profileRepository.findByEmail(email.get());

            session.removeAttribute("id");
            if(user != null) {
                if (user.comparePassword(loginDetail.getPassword())) {
                    session.setAttribute("id", user.getId());
                    return ResponseEntity.ok("Password Correct");
                }
            }
        }
        return new ResponseEntity("No associated user with username and password", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Logs user out of session
     * @return ResponseEntity which can be success(2xx) if user exists or error(4xx) if not logged in
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<String> logout(HttpSession session){
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