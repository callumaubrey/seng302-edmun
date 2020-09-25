package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIServiceMocking;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class LocationUpdateTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private GoogleAPIServiceMocking googleAPIService;

    @Test
    void testEditProfileLocationUpdate() throws Exception {
        googleAPIService.mockReverseGeocode("controllers/46BalgaySt_OK.json");

        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ],\n" +
                "  \"location\": {\n" +
                "    \"latitude\": \"-43.525650\",\n" +
                "    \"longitude\": \"172.639847\"\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ],\n" +
                "  \"location\": {\n" +
                "    \"latitude\": \"-36.848461\",\n" +
                "    \"longitude\": \"174.763336\"\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void updateLocation() throws Exception {
        googleAPIService.mockReverseGeocode("controllers/46BalgaySt_OK.json");

        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

       String  jsonString ="{\n" +
               "    \"latitude\": \"-36.848461\",\n" +
               "    \"longitude\": \"174.763336\"\n" +
               "  }\n";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk()).andDo(print());

        jsonString ="{\n" +
                "    \"latitude\": \"-43.525650\",\n" +
                "    \"longitude\": \"172.639847\"\n" +
                "  }\n";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void deleteLocation() throws Exception {
        googleAPIService.mockReverseGeocode("controllers/46BalgaySt_OK.json");

        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String  jsonString ="{\n" +
                "    \"latitude\": \"-43.525650\",\n" +
                "    \"longitude\": \"172.639847\"\n" +
                "  }\n";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        // Test delete
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{profileId}/location", id)
                .session(session)
        ).andExpect(status().isOk());
    }
}
