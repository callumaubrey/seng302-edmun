package com.springvuegradle.team6.models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** One of the subclasses of ActivityResult. It records distance as result */
@Entity
@DiscriminatorValue("1")
public class ActivityResultDistance extends ActivityResult {

  public static final String SQL_SORT_EXPRESSION = "distance_result";

  @Column(name = "distance_result")
  private Double result;

  public ActivityResultDistance(
      ActivityQualificationMetric metricId, Profile userId, Double result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultDistance() {}

  public void setResult(Double result) {
    this.result = result;
  }

  public Double getValue() {
    return this.result;
  }

  public Double getResult() {
    return this.result;
  }


}
