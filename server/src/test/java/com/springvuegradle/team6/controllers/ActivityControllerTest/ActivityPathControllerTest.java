package com.springvuegradle.team6.controllers.ActivityControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityPathControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityHistoryRepository activityHistoryRepository;

  @Autowired private PathRepository pathRepository;

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  private int id;

  private int activityId;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\","
            + "\r\n  \"firstname\": \"Poly\","
            + "\r\n  \"middlename\": \"Michelle\","
            + "\r\n  \"nickname\": \"Pino\","
            + "\r\n  \"primary_email\": \"poly@pocket.com\","
            + "\r\n  \"password\": \"Password1\","
            + "\r\n  \"bio\": \"Poly Pocket is so tiny.\","
            + "\r\n  \"date_of_birth\": \"2000-11-11\","
            + "\r\n  \"gender\": \"female\"\r\n}";

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

    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);
    activityId = activity.getId();
  }

  @Test
  void createActivityPath() throws Exception {
    String jsonString =
        "{\n"
            + "    \"type\": \""
            + PathType.STRAIGHT
            + "\",\n"
            + "    \"locations\": [\n"
            + "        {\n"
            + "            \"latitude\": 10.5678,\n"
            + "            \"longitude\": 10.5672\n"
            + "        },\n"
            + "        {\n"
            + "            \"latitude\": 10.5670,\n"
            + "            \"longitude\": 10.5670\n"
            + "        }\n"
            + "    ]\n"
            + "\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.post(
                    "/profiles/{profileId}/activities/{activityId}/path", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());

    Path path = pathRepository.findByActivity_Id(activityId);

    Assert.assertEquals(PathType.STRAIGHT, path.getType());
    Assert.assertEquals(2, path.getLocations().size());
  }
}
