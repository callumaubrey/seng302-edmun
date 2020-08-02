package com.springvuegradle.team6.models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * ActivityResult superclass which records the ActivityQualificationMetrics and Profile associated.
 * The entity is also able to record special outcomes such as DNF, technical failure or
 * disqualified
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "result_type", discriminatorType = DiscriminatorType.INTEGER)
public class ActivityResult {

  @Id
  @GeneratedValue
  private Integer id;
  @ManyToOne
  @JoinColumn(name = "metric_id", nullable = false)
  private ActivityQualificationMetrics metricId;
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private Profile userId;
  @Column(name = "other_metric")
  private OtherUnit otherMetric;

  public ActivityResult(ActivityQualificationMetrics metricId, Profile userId) {
    this.metricId = metricId;
    this.userId = userId;
    this.otherMetric = null;
  }

  // For testing purposes only
  public ActivityResult() {
  }

  /**
   * Override activity result to record special metrics such as DNF, technical failure or
   * disqualified
   *
   * @param otherMetric one of the enums from OtherUnit
   */
  public void overrideResult(OtherUnit otherMetric) {
    this.otherMetric = otherMetric;
  }
}
