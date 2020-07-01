package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.TagRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivityHashtagFeatureSteps {
  @Autowired private ActivityRepository activityRepository;

  @Autowired private TagRepository tagRepository;

  private MockHttpSession session;

  @Autowired private MockMvc mvc;

  private String profileId;

  private List<String> autocompleteResult;

  @When("I search for hashtag {string}")
  public void i_search_for_hashtag(String string) throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=number", profileId)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < arr.length(); i++) {
      list.add(arr.getString(i));
    }
    autocompleteResult = list;
  }

  @Then("I get hashtag search results containing")
  public void i_get_hashtag_search_results_containing(io.cucumber.datatable.DataTable dataTable) {
    Assert.assertTrue(autocompleteResult.containsAll(dataTable.asList()));
  }

  @Then("I get hashtag search results in order")
  public void i_get_hashtag_search_results_in_order(io.cucumber.datatable.DataTable dataTable) {
    Assert.assertTrue(autocompleteResult.equals(dataTable.asList()));
  }
}
