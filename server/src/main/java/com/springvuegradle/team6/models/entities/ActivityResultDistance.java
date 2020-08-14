package com.springvuegradle.team6.models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** One of the subclasses of ActivityResult. It records distance as result */
@Entity
@DiscriminatorValue("1")
public class ActivityResultDistance extends ActivityResult {

  @Column(name = "distance_result")
  private Float result;

  public ActivityResultDistance(
      ActivityQualificationMetric metricId, Profile userId, Float result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultDistance() {}

  public void setResult(Float result) {
    this.result = result;
  }

  public Float getValue() {
    return this.result;
  }

  public Float getResult() {
    return this.result;
  }


}
