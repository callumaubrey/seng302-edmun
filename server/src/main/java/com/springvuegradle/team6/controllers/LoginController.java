package com.springvuegradle.team6.controllers;

import javax.servlet.http.HttpSession;

import com.springvuegradle.team6.exceptions.NotLoggedInException;
import com.springvuegradle.team6.requests.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;

@CrossOrigin
@Controller
@RequestMapping("/account")
public class LoginController {
    private ProfileRepository repository;

    /**
     * Constructor for class gets the database repo
     * @param repo
     */
    LoginController(ProfileRepository repo) {
        this.repository = repo;
    }

    /**
     * Logs user into a session
     * Takes JSON post data, checks the data and logs user into specific account if it exists
     *
     * @param loginDetail the request entity
     * @return ResponseEntity which can be success(2xx) or error(4xx)
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody LoginRequest loginDetail,
                                        HttpSession session) {
        Profile user = repository.findByEmail(loginDetail.getEmail());
        session.removeAttribute("id");
        if(user != null) {
            if (user.comparePassword(loginDetail.getPassword())) {
                session.setAttribute("id", user.getId());
                return ResponseEntity.ok("Password Correct");
            }

            return new ResponseEntity("No associated user with username and password", HttpStatus.UNAUTHORIZED);
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
        Profile profile = repository.findById(intId);
        session.removeAttribute("id");
        return ResponseEntity.ok("Successfully logged out from the user: " + profile.getFirstname());
    }
}
