package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@CrossOrigin(
        origins = "http://localhost:9500",
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
@RequestMapping("hashtag")
public class TagController {

  private final TagRepository tagRepository;

  TagController(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @GetMapping("/{hashTag}")
  public ResponseEntity getActivities(@PathVariable String hashTag, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    return new ResponseEntity(tagRepository.findActivitiesByName(hashTag), HttpStatus.OK);
  }
}
