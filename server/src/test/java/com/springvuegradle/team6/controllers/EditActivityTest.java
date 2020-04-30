package com.springvuegradle.team6.controllers;

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
public class EditActivityTest {

    @Autowired
    private MockMvc mvc;

    private int id;

    private int activityId;

    private MockHttpSession session;

    @BeforeEach
    void setup() throws Exception {
        session = new MockHttpSession();
        String profileJson = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        // Logs in and get profile Id
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated()).andDo(print());

        String body = mvc.perform(
                get("/profiles/id").session(session)
        ).andReturn().getResponse().getContentAsString();
        id = Integer.parseInt(body);

        String activityJson="{\n" +
                "  \"activity_name\": \"Kaikoura Coast Track race\",\n" +
                "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Walk\"\n" +
                "  ],\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n" +
                "}";
        // Creates an activity
        String activityBody = mvc.perform(MockMvcRequestBuilders
                .post("/profiles/{profileId}/activities", id)
                .content(activityJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andReturn().getResponse().getContentAsString();
        activityId = Integer.parseInt(activityBody);
    }

    @Test
    void editAllActivitywithTimeReturnStatusIsOk() throws Exception {
        String jsonString="{\n" +
                "  \"activity_name\": \"Changed activity name\",\n" +
                "  \"description\": \"A new description\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Run\"\n" +
                "  ],\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editAllActivitywithoutTimeReturnStatusIsOk() throws Exception {
        String jsonString="{\n" +
                "  \"activity_name\": \"Changed activity name\",\n" +
                "  \"description\": \"A new description\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Run\"\n" +
                "  ],\n" +
                "  \"continuous\": true\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editAllActivityWithoutAuthorizationReturnIsUnauthorized() throws Exception {
        String jsonString="{\n" +
                "  \"activity_name\": \"Changed activity name\",\n" +
                "  \"description\": \"A new description\",\n" +
                "  \"activity_type\":[ \n" +
                "    \"Run\"\n" +
                "  ],\n" +
                "  \"continuous\": true\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, (activityId + 1))
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void editActivitywithStartDateBeforeNowReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void editActivitywithBothDatesBeforeNowReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2000-08-28T15:50:41+1300\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void editActivitywithStartDateAfterEndDateReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2040-10-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void editActivitywithStartDateEqualEndDateReturnStatusIsOk() throws Exception {
        String jsonString="{\n" +
                "  \"continuous\": false,\n" +
                "  \"start_time\": \"2000-08-28T15:50:41+1300\", \n" +
                "  \"end_time\": \"2000-08-28T15:50:41+1300\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }


}
