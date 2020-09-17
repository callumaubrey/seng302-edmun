package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = {
                "http://localhost:9000",
                "http://localhost:9500",
                "https://csse-s302g7.canterbury.ac.nz/test",
                "https://csse-s302g7.canterbury.ac.nz/prod"
        },
        allowCredentials = "true",
        allowedHeaders = "://",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.DELETE,
                RequestMethod.PUT,
                RequestMethod.PATCH
        })
@RestController
@RequestMapping("")
public class MailController {

    MailController(){};

    @Autowired
    public EmailService emailService;

    @GetMapping("/forgotpassword")
    ResponseEntity<String> sendForgotPasswordEmail(){
        emailService.sendSimpleMessage("martylopez0599@gmail.com", "YEET", "it works");
        return ResponseEntity.ok("worked");
    }

}
