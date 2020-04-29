package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ActivityControllerTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MockMvc mvc;

    private int id;

    private MockHttpSession session;

    @BeforeEach
    void setup() throws Exception {
        session = new MockHttpSession();
        String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated()).andDo(print());

        String body = mvc.perform(
                get("/profiles/id").session(session)
        ).andReturn().getResponse().getContentAsString();
        id = Integer.parseInt(body);
    }

    @Test
    void createActivityWithTimeReturnStatusIsCreated() throws Exception {
        String jsonString="{\n" +
                "  \"activity_name\": \"Kaikoura Coast Track race\",\n" +
                "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Walk\"\n" +
                "  ],\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
            .post("/profiles/{profileId}/activities", id)
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session)
        ).andExpect(status().isCreated());
    }

    @Test
    void createActivityWithDateOnlyReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2030-08-28T24:00:00+1300\"\n"
            + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());
    }

    @Test
    void createActivityWithStartDateBeforeNowReturnStatusBadRequest() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": false,\n"
                        + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
                        + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
                        + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createActivityWithBothDateBeforeNowReturnStatusBadRequest() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": false,\n"
                        + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
                        + "  \"end_time\": \"2010-08-28T24:00:00+1300\"\n"
                        + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createActivityWithStartDateAfterEndDateNowReturnStatusBadRequest() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": false,\n"
                        + "  \"start_time\": \"2060-04-28T24:00:00+1300\", \n"
                        + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
                        + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createActivityWithStartDateEqualEndDateReturnStatusIsCreated() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": false,\n"
                        + "  \"start_time\": \"2050-08-28T24:00:00+1300\", \n"
                        + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
                        + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());
    }

    @Test
    void createActivityWithOnlyMandatoryValuesReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());
    }

    @Test
    void createActivityWithNoActivityNameReturnStatusBadRequest() throws Exception {
        String jsonString =
                "{\n" +
                        "  \"activity_type\":[ \n" +
                        "    \"Walk\"\n" +
                        "  ],\n" +
                        "  \"continuous\": true\n" +
                        "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createActivityWithNoActivityTypeReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"continuous\": true\n"
            + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createActivityWithNoContinuousReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ]\n"
            + "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }


}
