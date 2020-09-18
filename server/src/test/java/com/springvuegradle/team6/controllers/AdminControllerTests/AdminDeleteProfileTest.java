package com.springvuegradle.team6.controllers.AdminControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.*;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityResultRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:tearDown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class AdminDeleteProfileTest {

    @Autowired private MockMvc mvc;

    @Autowired private ProfileRepository profileRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ActivityQualificationMetricRepository metricRepository;
    @Autowired private ActivityResultRepository resultRepository;


    @Autowired private ObjectMapper mapper;

    private MockHttpSession session;

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void testDeleteProfileDeletesAllAssociatedEntities() throws Exception {
        Profile profile = new Profile();
        profile.setFirstname("Johnny");
        profile.setLastname("Dong");
        Set<Email> email1 = new HashSet<Email>();
        email1.add(new Email("example1@email.com"));
        profile.setEmails(email1);
        profile.setPassword("Password1");
        profile = profileRepository.save(profile);
        int profileId = profile.getId();

        Activity activity = new Activity();
        activity.setProfile(profile);
        activity.setActivityName("My running activity");
        activity.setContinuous(true);
        activity = activityRepository.save(activity);
        int activityId = activity.getId();

        ActivityQualificationMetric metric = new ActivityQualificationMetric();
        metric.setTitle("title");
        metric.setActivity(activity);
        metric.setUnit(Unit.Count);
        metric = metricRepository.save(metric);
        int metricId = metric.getId();

        ActivityResultCount result = new ActivityResultCount(metric,profile, 5);
        result = resultRepository.save(result);
        int resultId = result.getId();

        String deleteProfileUrl = "/admin/profiles";

        //Delete existing primary email
        String jsonString = "{\n" +
                "\t\"primary_email\": \"example1@email.com\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteProfileUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Assert.assertNull(profileRepository.findByEmails_address("example1@email.com"));
        Assert.assertFalse(activityRepository.findById(activityId).isPresent());
        Assert.assertFalse(metricRepository.findById(metricId).isPresent());
        Assert.assertFalse(resultRepository.findById(resultId).isPresent());
    }
}
