package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityType;
import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.models.location.NamedLocationRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class EditActivityRequest {

  @NotNull
  @NotEmpty
  @JsonProperty("activity_name")
  public String activityName;

  @NotNull
  @JsonProperty("description")
  public String description;

  @JsonProperty("activity_type")
  @Size(min = 1)
  @NotNull
  public Set<ActivityType> activityTypes;

  @JsonProperty("continuous")
  @NotNull
  public boolean continuous;

  @JsonProperty("start_time")
  public String startTime;

  @JsonProperty("end_time")
  public String endTime;

  @JsonProperty("location")
  @Valid
  public LocationUpdateRequest location;

  public void editActivityFromRequest(
      Activity activity, NamedLocationRepository locationRepository) {
    activity.setActivityName(this.activityName);
    activity.setDescription(this.description);
    activity.setActivityTypes(this.activityTypes);
    activity.setContinuous(this.continuous);
    if (activity.isContinuous()) {
      activity.setStartTime(null);
      activity.setEndTime(null);
    } else {
      activity.setStartTime(this.startTime);
      activity.setEndTime(this.endTime);
    }
    if (this.location != null) {
      NamedLocation location =
          new NamedLocation(this.location.country, this.location.state, this.location.city);
      activity.setLocation(location);
    }
  }
}
