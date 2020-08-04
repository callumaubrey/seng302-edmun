package com.springvuegradle.team6.models.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * One of the subclasses of ActivityResult. It records start DateTime and end DateTime as result
 */
@Entity
@DiscriminatorValue("3")
public class ActivityResultStartFinish extends ActivityResult {

  @Column(name = "result_start")
  private LocalDateTime resultStart;
  @Column(name = "result_finish")
  private LocalDateTime resultFinish;

  public ActivityResultStartFinish(
      ActivityQualificationMetric metricId,
      Profile userId,
      LocalDateTime resultStart,
      LocalDateTime resultFinish) {
    super(metricId, userId);
    this.resultStart = resultStart;
    this.resultFinish = resultFinish;
  }

  // For testing purposes
  public ActivityResultStartFinish() {
  }
}
