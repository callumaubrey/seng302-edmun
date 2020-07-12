package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import io.cucumber.java.en.Then;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivityFollowingFeatureSteps {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private MockMvc mvc;

  private ResultActions mvcResponse;

  private MockHttpSession session;

  @Then("User with email {string} home feed shows")
  public void user_with_email_home_feed_shows(
      String emailStr, io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<String> activityNames = new ArrayList<>();
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      String activityName = activityMapping.get("Activity Name");
      activityNames.add(activityName);
    }
    Email email = new Email(emailStr);
    Profile profile = profileRepository.findByEmailsContains(email);

    String url = "/feed/homefeed/" + profile.getId();
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));

    String response = mvcResponse.andReturn().getResponse().getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(activityNames.size(), arr.length());

    List<String> responseActivityNames = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      int activityId = arr.getJSONObject(i).getInt("activity_id");
      String name = activityRepository.findById(activityId).get().getActivityName();
      responseActivityNames.add(name);
    }
    org.junit.jupiter.api.Assertions.assertTrue(responseActivityNames.containsAll(activityNames));
  }
}
