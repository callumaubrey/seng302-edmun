package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.location.OSMLocation;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Activity {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Integer id;

  private String activityName;

  private String description;

  @ElementCollection(targetClass=ActivityType.class)
  @Enumerated(EnumType.ORDINAL)
  private Set<ActivityType> activityTypes;

  private boolean continuous;

  private String startTime;

  private String endTime;

  @OneToOne
  private OSMLocation location;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<ActivityType> getActivityTypes() {
    return activityTypes;
  }

  public void setActivityTypes(Set<ActivityType> activityTypes) {
    this.activityTypes = activityTypes;
  }

  public boolean isContinuous() {
    return continuous;
  }

  public void setContinuous(boolean continuous) {
    this.continuous = continuous;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public OSMLocation getLocation() {
    return location;
  }

  public void setLocation(OSMLocation location) {
    this.location = location;
  }
}
