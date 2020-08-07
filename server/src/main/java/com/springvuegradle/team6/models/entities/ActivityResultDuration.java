package com.springvuegradle.team6.models.entities;

import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * One of the subclasses of ActivityResult. It records duration as result
 */
@Entity
@DiscriminatorValue("2")
public class ActivityResultDuration extends ActivityResult {

  // duration in terms of seconds
  @Column(name = "duration_result")
  private Duration result;

  public ActivityResultDuration(
      ActivityQualificationMetric metricId, Profile userId, Duration result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultDuration() {
  }

  public Duration getResult() {
    return this.result;
  }

  public String getType() {
    return "Duration";
  }
}
