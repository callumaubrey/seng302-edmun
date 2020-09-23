package com.springvuegradle.team6.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class ActivityMetricsFeatureSteps {

  @Autowired private ActivityFollowingFeatureSteps activityFollowingFeatureSteps;

  @Autowired private ActivityHashtagFeatureSteps activityHashtagFeatureSteps;

  @Autowired private LoginSteps loginSteps;

  @Autowired private MockMvc mvc;

  @Autowired private ActivityRepository activityRepository;

  @When("I create an activity {string} with metrics")
  public void i_create_an_activity_with_metrics(
      String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<JSONObject> metrics = new ArrayList<>();
    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      JSONObject metric = new JSONObject();
      metric.put("title", metricMapping.get("Name"));
      metric.put("unit", metricMapping.get("Unit"));
      metrics.add(metric);
    }

    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + activityName
            + "\",\n"
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

    activityHashtagFeatureSteps.mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", loginSteps.profileId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session));
  }

  @When("I create an activity {string} without metrics")
  public void i_create_an_activity_without_metrics(String activityName) throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + activityName
            + "\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}\n";

    activityHashtagFeatureSteps.mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", loginSteps.profileId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session));
  }

  @Transactional
  // ensures all method calls in this test case EAGERLY loads object, a way to fix
  // LazyInitializationException
  @Then("there will be an activity {string} without metrics")
  public void there_will_be_an_activity_without_metrics(String string)
      throws UnsupportedEncodingException {
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    Activity activity = activityRepository.findById(activityId).get();
    Assert.assertTrue(activity.getMetrics().isEmpty());
  }

  @When("I add metrics to activity {string}")
  public void i_add_metrics_to_activity(
      String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<JSONObject> metrics = new ArrayList<>();
    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      JSONObject metric = new JSONObject();
      metric.put("title", metricMapping.get("Name"));
      metric.put("unit", metricMapping.get("Unit"));
      metrics.add(metric);
    }

    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + activityName
            + "\",\n"
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
  public void there_will_be_an_activity_with_metrics(
      String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String activityResponse =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/activities/{activityId}", activityFollowingFeatureSteps.activityId)
                    .session(loginSteps.session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      Assert.assertTrue(activityResponse.contains(metricMapping.get("Name")));
      Assert.assertTrue(activityResponse.contains(metricMapping.get("Unit")));
    }
  }

  @Transactional
  @When("I delete metrics from activity {string}")
  public void i_delete_metrics_from_activity(
      String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    Activity activity = activityRepository.findById(activityId).get();

    // Filter our metrics to be deleted
    List<ActivityQualificationMetric> metricsIterator = activity.getMetrics();
    List<ActivityQualificationMetric> metricsAfter = activity.getMetrics();
    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      for (ActivityQualificationMetric metric : metricsIterator) {
        if (metricMapping.get("Name").equals(metric.getTitle())
            && metricMapping.get("Unit").equals(metric.getUnit().toString())) {
          metricsAfter.remove(metric);
        }
      }
    }

    List<JSONObject> metrics = new ArrayList<>();
    for (ActivityQualificationMetric metric : metricsAfter) {
      JSONObject metricJSON = new JSONObject();
      metricJSON.put("title", metric.getTitle());
      metricJSON.put("unit", metric.getUnit());
      metrics.add(metricJSON);
    }

    String jsonString =
        "{\n"
            + "  \"activity_name\": \""
            + activityName
            + "\",\n"
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
                    activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session))
        .andExpect(status().isOk());
  }
}
