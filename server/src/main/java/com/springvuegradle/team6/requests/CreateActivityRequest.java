package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityType;
import com.springvuegradle.team6.models.Tag;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class CreateActivityRequest {
  @NotNull(message = "activity_name cannot be null")
  @NotEmpty(message = "activity_name cannot be empty")
  @JsonProperty("activity_name")
  @Length(max=Activity.NAME_MAX_LENGTH)
  public String activityName;

  @JsonProperty("description")
  @Length(max=Activity.DESCRIPTION_MAX_LENGTH)
  public String description;

  @JsonProperty("activity_type")
  @Size(min = 1)
  @NotNull(message = "activity_type cannot be null")
  public Set<ActivityType> activityTypes;

  @JsonProperty("continuous")
  @NotNull(message = "continuous cannot be null")
  public boolean continuous;

  @JsonProperty("start_time")
  public String startTime;

  @JsonProperty("end_time")
  public String endTime;

  @JsonProperty("hashtags")
  @Size(max = 30)
  public Set<Tag> hashTags;

  @JsonProperty("location")
  @Valid
  public LocationUpdateRequest location;

  public LocationUpdateRequest getLocation() {
    return location;
  }

  public void setLocation(LocationUpdateRequest location) {
    this.location = location;
  }
}
