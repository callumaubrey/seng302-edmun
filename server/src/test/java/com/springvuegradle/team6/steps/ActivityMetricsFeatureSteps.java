package com.springvuegradle.team6.steps;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityResultDuration;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.entities.SpecialMetric;
import com.springvuegradle.team6.models.entities.Unit;
import com.springvuegradle.team6.models.repositories.ActivityQualificationMetricRepository;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.requests.CreateActivityResultRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class ActivityMetricsFeatureSteps {

  @Autowired private ObjectMapper mapper;

  @Autowired private LoginSteps loginSteps;

  @Autowired private MockMvc mvc;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityQualificationMetricRepository activityQualificationMetricRepository;
  public ResultActions mvcResponse;

  private String activityId;

  @Given("I am a participant of the activity")
  public void i_am_a_participant_of_the_activity() throws Exception {
    System.out.println(loginSteps.profileId);
    System.out.println(activityId);
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/subscriptions/activities/{activityId}/participate",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation in the activity with duration of {string}")
  public void i_add_the_details_of_my_participation_in_the_activity_with_duration_of_seconds(
      String duration) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setValue(duration);
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation in the activity with {int} count")
  public void i_add_the_details_of_my_participation_in_the_activity_with_count(Integer count)
      throws Exception {
    // Write code here that turns the phrase above into concrete actions
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setValue(count.toString());
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation in the activity with {int} km as a distance")
  public void i_add_the_details_of_my_participation_in_the_activity_with_km_as_a_distance(
      Integer distance) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setValue(distance.toString());
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation in the activity")
  public void i_add_the_details_of_my_participation_in_the_activity(
      io.cucumber.datatable.DataTable dataTable) throws Exception {
    Map<String, String> data = dataTable.asMap(String.class, String.class);
    LocalDateTime startTime =
        LocalDateTime.parse(
            data.get("Start Time"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
    LocalDateTime endTime =
        LocalDateTime.parse(
            data.get("End Time"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setStart(startTime);
    createActivityResultRequest.setEnd(endTime);

    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation as disqualified")
  public void i_add_the_details_of_my_participation_as_disqualified() throws Exception {
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setSpecialMetric(SpecialMetric.Disqualified);
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation as technical failure")
  public void i_add_the_details_of_my_participation_as_technical_failure() throws Exception {
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setSpecialMetric(SpecialMetric.TechnicalFailure);
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

  @When("I add the details of my participation as did not finish")
  public void i_add_the_details_of_my_participation_as_did_not_finish() throws Exception {
    List<ActivityQualificationMetric> activityQualificationMetrics =
        activityQualificationMetricRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityQualificationMetric activityQualificationMetric = activityQualificationMetrics.get(0);
    CreateActivityResultRequest createActivityResultRequest = new CreateActivityResultRequest();
    createActivityResultRequest.setMetricId(activityQualificationMetric.getId());
    createActivityResultRequest.setSpecialMetric(SpecialMetric.DidNotFinish);
    mvcResponse =
        mvc.perform(
                MockMvcRequestBuilders.post(
                        "/profiles/{profileId}/activities/{activityId}/result",
                        loginSteps.profileId,
                        activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(createActivityResultRequest))
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }

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

    mvcResponse =
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

    mvcResponse =
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
        Integer.parseInt(mvcResponse.andReturn().getResponse().getContentAsString());
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

    String activityId = mvcResponse.andReturn().getResponse().getContentAsString();
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

  @Then("there will be an activity {string} with metrics")
  public void there_will_be_an_activity_with_metrics(
      String activityName, io.cucumber.datatable.DataTable dataTable) throws Exception {
    String activityId = mvcResponse.andReturn().getResponse().getContentAsString();
    String activityResponse =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles/{profileId}/activities/{activityId}/metrics",
                        loginSteps.profileId,
                        activityId)
                    .session(loginSteps.session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    System.out.println(activityResponse);
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
        Integer.parseInt(mvcResponse.andReturn().getResponse().getContentAsString());
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

  @Given("there is an activity that has a duration metric")
  public void there_is_an_activity_that_has_a_duration_metric() {
    Profile profile = new Profile();
    profileRepository.save(profile);
    Activity activity = new Activity();
    List<ActivityQualificationMetric> activityQualificationMetrics = new ArrayList<>();
    ActivityQualificationMetric activityQualificationMetric = new ActivityQualificationMetric();
    activityQualificationMetric.setActivity(activity);
    activityQualificationMetric.setTitle("Longest Time");
    activityQualificationMetric.setUnit(Unit.TimeDuration);
    activityQualificationMetrics.add(activityQualificationMetric);
    activity.setMetrics(activityQualificationMetrics);
    activity.setProfile(profile);
    activityId = activityRepository.save(activity).getId().toString();
    activityQualificationMetricRepository.save(activityQualificationMetric);
  }

  @Given("there is an activity that has a count metric")
  public void there_is_an_activity_that_has_a_count_metric() {
    Profile profile = new Profile();
    profileRepository.save(profile);
    Activity activity = new Activity();
    List<ActivityQualificationMetric> activityQualificationMetrics = new ArrayList<>();
    ActivityQualificationMetric activityQualificationMetric = new ActivityQualificationMetric();
    activityQualificationMetric.setActivity(activity);
    activityQualificationMetric.setTitle("Easter eggs found");
    activityQualificationMetric.setUnit(Unit.Count);
    activityQualificationMetrics.add(activityQualificationMetric);
    activity.setMetrics(activityQualificationMetrics);
    activity.setProfile(profile);
    activityId = activityRepository.save(activity).getId().toString();
    activityQualificationMetricRepository.save(activityQualificationMetric);
  }

  @Given("there is an activity that has a distance metric")
  public void there_is_an_activity_that_has_a_distance_metric() {
    Profile profile = new Profile();
    profileRepository.save(profile);
    Activity activity = new Activity();
    List<ActivityQualificationMetric> activityQualificationMetrics = new ArrayList<>();
    ActivityQualificationMetric activityQualificationMetric = new ActivityQualificationMetric();
    activityQualificationMetric.setActivity(activity);
    activityQualificationMetric.setTitle("Distance Travelled in KM");
    activityQualificationMetric.setUnit(Unit.Distance);
    activityQualificationMetrics.add(activityQualificationMetric);
    activity.setMetrics(activityQualificationMetrics);
    activity.setProfile(profile);
    activityId = activityRepository.save(activity).getId().toString();
    activityQualificationMetricRepository.save(activityQualificationMetric);
  }

  @Given("there is an activity that has a finish metric")
  public void there_is_an_activity_that_has_a_finish_metric() {
    Profile profile = new Profile();
    profileRepository.save(profile);
    Activity activity = new Activity();
    List<ActivityQualificationMetric> activityQualificationMetrics = new ArrayList<>();
    ActivityQualificationMetric activityQualificationMetric = new ActivityQualificationMetric();
    activityQualificationMetric.setActivity(activity);
    activityQualificationMetric.setTitle("Distance Travelled in KM");
    activityQualificationMetric.setUnit(Unit.TimeStartFinish);
    activityQualificationMetrics.add(activityQualificationMetric);
    activity.setMetrics(activityQualificationMetrics);
    activity.setProfile(profile);
    activityId = activityRepository.save(activity).getId().toString();
    activityQualificationMetricRepository.save(activityQualificationMetric);
  }

  private JSONArray getResultsRequest(String activityId, String profileId) throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/activities/{activityId}/result", activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(loginSteps.session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    return result;
  }

  @Then("the details of my participation is recorded as {string} for the duration metric")
  public void the_details_of_my_participation_is_recorded_as_seconds_for_the_duration_metric(
      String expectedDuration) throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedDuration, resultDuration);
  }

  @Then("the details of my participation is recorded as {int} for the count metric")
  public void the_details_of_my_participation_is_recorded_as_for_the_count_metric(
      Integer expectedCount) throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedCount.toString(), resultDuration);
  }

  @Then("the details of my participation is recorded as {double} km for the distance metric")
  public void the_details_of_my_participation_is_recorded_as_km_for_the_distance_metric(
      double expectedDuration) throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String resultDuration = result.getJSONObject(0).get("result").toString();
    Assert.assertEquals(expectedDuration, Double.parseDouble(resultDuration), 0.1);
  }

  @Then("the details of my participation is recorded as")
  public void the_details_of_my_participation_is_recorded_as(
      io.cucumber.datatable.DataTable dataTable) throws Exception {
    Map<String, String> data = dataTable.asMap(String.class, String.class);
    String expectedStartTime = data.get("Start Time");
    String expectedEndTime = data.get("End Time");
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String startTime = result.getJSONObject(0).get("result_start").toString();
    String endTime = result.getJSONObject(0).get("result_finish").toString();
    Assert.assertEquals(expectedStartTime, startTime);
    Assert.assertEquals(expectedEndTime, endTime);
  }

  @Then("the details of my participation is recorded disqualified")
  public void the_details_of_my_participation_is_recorded_disqualified() throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("special_metric").toString();
    Assert.assertEquals(SpecialMetric.Disqualified.toString(), special_metric);
  }

  @Then("the details of my participation is recorded technical failure")
  public void the_details_of_my_participation_is_recorded_technical_failure() throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("special_metric").toString();
    Assert.assertEquals(SpecialMetric.TechnicalFailure.toString(), special_metric);
  }

  @Then("the details of my participation is recorded as did not finish")
  public void the_details_of_my_participation_is_recorded_as_did_not_finish() throws Exception {
    JSONArray result = getResultsRequest(activityId, loginSteps.profileId);
    String special_metric = result.getJSONObject(0).get("special_metric").toString();
    Assert.assertEquals(SpecialMetric.DidNotFinish.toString(), special_metric);
  }
}
