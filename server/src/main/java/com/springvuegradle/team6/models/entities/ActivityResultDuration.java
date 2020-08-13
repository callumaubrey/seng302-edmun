package com.springvuegradle.team6.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** One of the subclasses of ActivityResult. It records duration as result */
@Entity
@DiscriminatorValue("2")
public class ActivityResultDuration extends ActivityResult {

  public static final String SQL_SORT_EXPRESSION = "duration_result";

  // duration in terms of seconds
  @Column(name = "duration_result")
  private Duration result;

  public ActivityResultDuration(
      ActivityQualificationMetric metricId, Profile userId, Duration result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultDuration() {}

  public void setResult(Duration result) {
    this.result = result;
  }

  public Duration getValue() {
    return this.result;
  }

  public Duration getResult() {
    return this.result;
  }

  @JsonProperty("pretty_result")
  public String getPrettyResult() {
    long seconds = this.result.getSeconds();
    long absSeconds = Math.abs(seconds);
    String positive = String.format(
      "%d:%02d:%02d",
      absSeconds / 3600,
      (absSeconds % 3600) / 60,
      absSeconds % 60);
    return seconds < 0 ? "-" + positive : positive;
  }

  public String getType() {
    return "Duration";
  }
}
