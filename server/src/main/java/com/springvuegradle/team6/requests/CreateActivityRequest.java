package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.Tag;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public class CreateActivityRequest {
  @NotNull(message = "activity_name cannot be null")
  @NotEmpty(message = "activity_name cannot be empty")
  @JsonProperty("activity_name")
  @Length(max = Activity.NAME_MAX_LENGTH)
  public String activityName;

  @JsonProperty("description")
  @Length(max = Activity.DESCRIPTION_MAX_LENGTH)
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

  @JsonProperty("visibility")
  public String visibility;

  @JsonProperty("metrics")
  public List<@Valid ActivityQualificationMetric> metrics;

  @JsonProperty("photo_filename")
  @Pattern(
      regexp = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif))$)",
      message = "Those image formats are not supported")
  public String photoFileName;

  public LocationUpdateRequest getLocation() {
    return location;
  }

  public void setLocation(LocationUpdateRequest location) {
    this.location = location;
  }
}
