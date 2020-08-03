package com.springvuegradle.team6.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ActivityMetricsFeatureSteps {

  @Autowired
  private ActivityFollowingFeatureSteps activityFollowingFeatureSteps;

  @Autowired
  private LoginSteps loginSteps;

  @Autowired
  private MockMvc mvc;


  @When("I add metrics to activity {string}")
  public void i_add_metrics_to_activity(String activityName,
      io.cucumber.datatable.DataTable dataTable)
      throws Exception {
    List<JSONObject> metrics = new ArrayList<>();
    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      JSONObject metric = new JSONObject();
      metric.put("title", metricMapping.get("Name"));
      metric.put("unit", metricMapping.get("Unit"));
      metrics.add(metric);
    }

    String jsonString =
        "{\n"
            + "  \"activity_name\": \"" + activityName + "\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"metrics\": "
            + metrics
            + "}\n";

    mvc.perform(
        MockMvcRequestBuilders.put(
            "/profiles/{profileId}/activities/{activityId}",
            loginSteps.profileId,
            activityFollowingFeatureSteps.activityId)
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(loginSteps.session))
        .andExpect(status().isOk());
  }

  @Then("there will be an activity {string} with metrics")
  public void there_will_be_an_activity_with_metrics(String activityName,
      io.cucumber.datatable.DataTable dataTable)
      throws Exception {
    String activityResponse = mvc.perform(
        MockMvcRequestBuilders.get(
            "/activities/{activityId}",
            activityFollowingFeatureSteps.activityId)
            .session(loginSteps.session))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      Assert.assertTrue(activityResponse.contains(metricMapping.get("Name")));
      Assert.assertTrue(activityResponse.contains(metricMapping.get("Unit")));
    }
  }


}
