package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Path;
import com.springvuegradle.team6.models.entities.PathType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.models.repositories.PathRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

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
    @Autowired private LocationRepository locationRepository;

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

        jsonString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": true\n"
                        + "}";

        String activityBody =
                mvc.perform(
                        MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session))
                        .andReturn().getResponse().getContentAsString();
        activityId = Integer.parseInt(activityBody);
    }

    @Test
    void editPathWithValidCoordinates() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"coordinates\": [ \n"
                        + "     {\n"
                        + "          \"latitude\": -43.525650,\n"
                        + "          \"longitude\": 172.639847\n"
                        + "     },\n"
                        + "     {\n"
                        + "          \"latitude\": -43.825650,\n"
                        + "          \"longitude\": 172.839847\n"
                        + "     }\n"
                        + "  ],\n"
                        + "  \"pathType\": \"STRAIGHT\"\n"
                        + "}";

        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}/path", id, activityId)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());

        Path path = pathRepository.findByActivity_Id(activityId);
        Location location1 = locationRepository.findByLatitudeAndLongitude(-43.525650, 172.639847).get();
        Location location2 = locationRepository.findByLatitudeAndLongitude(-43.825650, 172.839847).get();

        Assert.assertNotNull(location1);
        Assert.assertNotNull(location2);
        Assert.assertTrue(location1.getId() < location2.getId());
        Assert.assertEquals(PathType.STRAIGHT, path.getType());
    }

    @Test
    void editPathWithValidCoordinatesDeletesOldPathLocations() throws Exception {
        Activity activity = activityRepository.findById(activityId).get();
        Location location1 = new Location(-20.5, 20.5);
        Location location2 = new Location(-30.5, 30.5);
        locationRepository.save(location1);
        locationRepository.save(location2);
        List<Location> oldLocations = new ArrayList<Location>();
        oldLocations.add(location1);
        oldLocations.add(location2);
        Path oldPath = new Path(activity, oldLocations, PathType.STRAIGHT);
        oldPath = pathRepository.save(oldPath);
        activity.setPath(oldPath);
        activity = activityRepository.save(activity);

        String jsonString =
                "{\n"
                        + "  \"coordinates\": [ \n"
                        + "     {\n"
                        + "          \"latitude\": -43.525650,\n"
                        + "          \"longitude\": 172.639847\n"
                        + "     },\n"
                        + "     {\n"
                        + "          \"latitude\": -43.825650,\n"
                        + "          \"longitude\": 172.839847\n"
                        + "     }\n"
                        + "  ],\n"
                        + "  \"pathType\": \"STRAIGHT\"\n"
                        + "}";

        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}/path", id, activityId)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk());

        Path path = pathRepository.findByActivity_Id(activityId);
        Assert.assertTrue(locationRepository.findByLatitudeAndLongitude(-43.525650, 172.639847).isPresent());
        Assert.assertFalse(locationRepository.findByLatitudeAndLongitude(-20.5, 20.5).isPresent());
    }

    @Test
    void editPathWithInvalidPathType() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"coordinates\": [ \n"
                        + "     {\n"
                        + "          \"latitude\": -43.525650,\n"
                        + "          \"longitude\": 172.639847\n"
                        + "     },\n"
                        + "     {\n"
                        + "          \"latitude\": -43.825650,\n"
                        + "          \"longitude\": 172.839847\n"
                        + "     }\n"
                        + "  ],\n"
                        + "  \"pathType\": \"random\"\n"
                        + "}";

        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}/path", id, activityId)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editPathWithInvalidCoordinates() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"coordinates\": [ \n"
                        + "     {\n"
                        + "          \"latitude\": 200.525650,\n"
                        + "          \"longitude\": 192.639847\n"
                        + "     },\n"
                        + "     {\n"
                        + "          \"latitude\": -43.825650,\n"
                        + "          \"longitude\": 172.839847\n"
                        + "     }\n"
                        + "  ],\n"
                        + "  \"pathType\": \"straight\"\n"
                        + "}";

        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}/path", id, activityId)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editPathWithoutAuthorization() throws Exception {
        String jsonString =
                "{\r\n  \"lastname\": \"Random\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"random@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

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
         int profileId = Integer.parseInt(body);

        String pathString =
                "{\n"
                        + "  \"coordinates\": [ \n"
                        + "     {\n"
                        + "          \"latitude\": -43.525650,\n"
                        + "          \"longitude\": 172.639847\n"
                        + "     },\n"
                        + "     {\n"
                        + "          \"latitude\": -43.825650,\n"
                        + "          \"longitude\": 172.839847\n"
                        + "     }\n"
                        + "  ],\n"
                        + "  \"pathType\": \"STRAIGHT\"\n"
                        + "}";

        mvc.perform(
                MockMvcRequestBuilders.put("/profiles/{profileId}/activities/{activityId}/path", profileId, activityId)
                        .content(pathString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isUnauthorized());
    }

}
