package com.springvuegradle.team6.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.SpecialMetric;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import io.cucumber.java.an.E;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ActivityRecordOutcomeFeatureSteps {

  @Autowired
  private ActivityFollowingFeatureSteps activityFollowingFeatureSteps;

  @Autowired
  private ActivityHashtagFeatureSteps activityHashtagFeatureSteps;

  @Autowired
  private LoginSteps loginSteps;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ActivityRepository activityRepository;

  @Given("there is an activity that has a duration metric")
  public void there_is_an_activity_that_has_a_duration_metric(io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<JSONObject> metrics = new ArrayList<>();
    for (Map<String, String> metricMapping : dataTable.asMaps()) {
      JSONObject metric = new JSONObject();
      metric.put("title", metricMapping.get("Name"));
      metric.put("unit", metricMapping.get("Unit"));
      metrics.add(metric);
    }

    String jsonString =
        "{\n"
            + "  \"activity_name\": Going to Do stuff\""
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
  private JSONArray getResultsRequest(String activityId, String profileId) throws Exception {
    String response =
        mvc.perform(
            MockMvcRequestBuilders.get(
                "/profiles/{profileId}/activities/{activityId}/result",
                profileId,
                activityId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    return result;
  }

  @Then("the details of my participation is recorded as {int} seconds for the duration metric")
  public void the_details_of_my_participation_is_recorded_as_seconds_for_the_duration_metric(Integer expectedDuration)
      throws Exception{
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedDuration.toString(), resultDuration);
  }

  @Then("the details of my participation is recorded as {int} for the count metric")
  public void the_details_of_my_participation_is_recorded_as_for_the_count_metric(Integer expectedCount) throws  Exception{
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedCount.toString(), resultDuration);
  }

  @Then("the details of my participation is recorded as {int} km for the distance metric")
  public void the_details_of_my_participation_is_recorded_as_km_for_the_distance_metric(Integer expectedDuration) throws Exception {
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedDuration.toString(), resultDuration);
  }

  @Then("the details of my participation is recorded as")
  public void the_details_of_my_participation_is_recorded_as(io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<String> expectedValues = dataTable.asList();
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String startTime = result.getJSONObject(0).get("resultStart").toString();
    String endTime = result.getJSONObject(0).get("resultFinish").toString();
    Assert.assertEquals(expectedValues.get(0), startTime);
    Assert.assertEquals(expectedValues.get(1), endTime);
  }

  @Then("the details of my participation is recorded disqualified")
  public void the_details_of_my_participation_is_recorded_disqualified() throws  Exception{
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("specialMetric").toString();
    Assert.assertEquals(SpecialMetric.Disqualified.ordinal(), Integer.parseInt(special_metric));
  }

  @Then("the details of my participation is recorded technical failure")
  public void the_details_of_my_participation_is_recorded_technical_failure() throws Exception {
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("specialMetric").toString();
    Assert.assertEquals(SpecialMetric.TechnicalFailure.ordinal(), Integer.parseInt(special_metric));
  }
  @Then("the details of my participation is recorded as did not finish")
  public void the_details_of_my_participation_is_recorded_as_did_not_finish() throws Exception {
    Integer activityId =
        Integer.parseInt(
            activityHashtagFeatureSteps.mvcResponse.andReturn().getResponse().getContentAsString());
    JSONArray result = getResultsRequest(activityId.toString(), loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("specialMetric").toString();
    Assert.assertEquals(SpecialMetric.DidNotFinish.ordinal(), Integer.parseInt(special_metric));
  }
}
