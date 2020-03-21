package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:9500", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH})
@Controller
@RequestMapping("/account")
public class LoginController {
    private ProfileRepository profileRepository;
    private EmailRepository emailRepository;

    /**
     * Constructor for class gets the database repo
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
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginDetail,
                                        HttpSession session) {
        try {
            Optional<Email> email = emailRepository.findByAddress(loginDetail.getEmail());
            Profile user = profileRepository.findByEmail(email.get());
            session.removeAttribute("id");
            if (user != null) {
                if (user.comparePassword(loginDetail.getPassword())) {
                    boolean isAdmin = false;
                    session.setAttribute("id", user.getId());
                    List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList();
                    for (Role role : user.getRoles()) {
                        if (role.getRoleName().equals("ROLE_ADMIN")) {
                            isAdmin = true;
                        }
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
                        updatedAuthorities.add(authority);
                    }

                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                                    SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                                    updatedAuthorities));
                    if (isAdmin) {
                        return ResponseEntity.ok("Password Correct, User is Admin");
                    }
                    return ResponseEntity.ok("Password Correct");
                }

                return new ResponseEntity("No associated user with username and password", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity("No associated user with username and password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logs user out of session
     * @return ResponseEntity which can be success(2xx) if user exists or error(4xx) if not logged in
     */
    @GetMapping("/logout")
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
