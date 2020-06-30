package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class TagControllerTest {

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ProfileRepository profileRepository;

  private MockHttpSession session;

  @Autowired
  private MockMvc mvc;

  private int id;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    activityRepository.deleteAll();

    String jsonString =
            "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andDo(print());

    String body =
            mvc.perform(get("/profiles/id").session(session))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    id = Integer.parseInt(body);
  }

  @Test
  void getActivitiesByHashTagOneResult() throws Exception {
    // Set up data
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<Tag>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);

    tagRepository.save(tag);
    activityRepository.save(activity);

    // Actual MVC response
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/hashtag/myrun")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void getActivitiesByHashtagNoResult() throws Exception {
    Tag tag = new Tag();
    tag.setName("myrun");
    tagRepository.save(tag);

    // Actual MVC response
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/hashtag/myrun")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(0, result.length());
  }
}
