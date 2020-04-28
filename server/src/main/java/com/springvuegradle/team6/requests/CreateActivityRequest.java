package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.ActivityType;
import com.springvuegradle.team6.models.location.OSMElementID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class CreateActivityRequest {
  @NotNull
  @NotEmpty
  @JsonProperty("activity_name")
  public String activityName;

  @JsonProperty("description")
  public String description;

  @JsonProperty("activity_type")
  @Size(min = 1)
  public Set<ActivityType> activityTypes;

  @JsonProperty("continuous")
  @NotNull
  public boolean continuous;

  @JsonProperty("start_time")
  public String start_time;

  @JsonProperty("end_time")
  public String end_time;

  //    @JsonProperty("location")
  //    public OSMElementID location;

}
