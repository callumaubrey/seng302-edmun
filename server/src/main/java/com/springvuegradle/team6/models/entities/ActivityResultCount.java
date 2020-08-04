package com.springvuegradle.team6.models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * One of the subclasses of ActivityResult. It records count as result
 */
@Entity
@DiscriminatorValue("0")
public class ActivityResultCount extends ActivityResult {

  @Column(name = "count_result")
  private Integer result;

  public ActivityResultCount(
      ActivityQualificationMetric metricId, Profile userId, Integer result) {
    super(metricId, userId);
    this.result = result;
  }

  // For testing purposes
  public ActivityResultCount() {
  }
}
