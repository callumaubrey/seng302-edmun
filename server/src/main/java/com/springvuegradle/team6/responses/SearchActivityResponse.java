package com.springvuegradle.team6.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.entities.ActivityType;
import com.springvuegradle.team6.models.entities.NamedLocation;
import com.springvuegradle.team6.models.entities.Tag;
import com.springvuegradle.team6.models.entities.VisibilityType;
import java.time.LocalDateTime;
import java.util.Set;

public class SearchActivityResponse {

  public SearchActivityResponse(
      String activityName,
      Integer activityId,
      Integer profileId,
      String description,
      Set<ActivityType> activityTypes,
      Set<Tag> hashtags,
      boolean continuous,
      LocalDateTime startTime,
      LocalDateTime endTime,
      NamedLocation location,
      VisibilityType visibilityType) {
    this.activityName = activityName;
    this.activityId = activityId;
    this.profileId = profileId;
    this.description = description;
    this.activityTypes = activityTypes;
    this.hashtags = hashtags;
    this.continuous = continuous;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.visibilityType = visibilityType;
  }

  @JsonProperty("activityName")
  public String activityName;

  @JsonProperty("id")
  public Integer activityId;

  @JsonProperty("profile_id")
  public Integer profileId;

  @JsonProperty("description")
  public String description;

  @JsonProperty("activityTypes")
  public Set<ActivityType> activityTypes;

  @JsonProperty("tags")
  public Set<Tag> hashtags;

  @JsonProperty("continuous")
  public boolean continuous;

  @JsonProperty("startTime")
  public LocalDateTime startTime;

  @JsonProperty("endTime")
  public LocalDateTime endTime;

  @JsonProperty("location")
  public NamedLocation location;

  @JsonProperty("visibilityType")
  public VisibilityType visibilityType;
}
