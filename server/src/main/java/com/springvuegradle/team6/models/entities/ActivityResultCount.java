package com.springvuegradle.team6.models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** One of the subclasses of ActivityResult. It records count as result */
@Entity
@DiscriminatorValue("0")
public class ActivityResultCount extends ActivityResult {

  public static final String SQL_SORT_EXPRESSION = "count_result";

  @Column(name = "count_result")
  private Integer result;

  public ActivityResultCount(ActivityQualificationMetric metricId, Profile userId, Integer result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultCount() {}

  public void setResult(Integer result) {
    this.result = result;
  }

  public int getValue() {
    return this.result;
  }

  public Integer getResult() {
    return this.result;
  }

}
