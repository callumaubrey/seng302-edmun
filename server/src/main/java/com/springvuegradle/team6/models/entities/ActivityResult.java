package com.springvuegradle.team6.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * ActivityResult superclass which records the ActivityQualificationMetrics and Profile associated.
 * The entity is also able to record special metrics such as DNF, technical failure or
 * disqualified @Inheritance groups subclasses attributes into a single table, and a discriminator
 * column will be created to differentiate the subclasses @DiscriminatorColumn changes the
 * discriminator column name, and each subclass is represented as integer
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "result_type", discriminatorType = DiscriminatorType.INTEGER)
public class ActivityResult {

  @Id @GeneratedValue private Integer id;

  @ManyToOne
  @JoinColumn(name = "metric_id", nullable = false)
  private ActivityQualificationMetric metricId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private Profile userId;

  @Column(name = "special_metric")
  private SpecialMetric specialMetric;

  public ActivityResult(ActivityQualificationMetric metricId, Profile userId) {
    this.metricId = metricId;
    this.userId = userId;
    this.specialMetric = null;
  }

  // For testing purposes only
  public ActivityResult() {}

  /**
   * Override activity result to record special metrics such as DNF, technical failure or
   * disqualified
   *
   * @param otherMetric one of the enums from OtherUnit
   */
  public void overrideResult(SpecialMetric otherMetric) {
    this.specialMetric = otherMetric;
  }

  public Integer getId() {
    return this.id;
  }

  @JsonProperty("metric_id")
  public Integer getMetricId() {
    return this.metricId.getId();
  }

  @JsonProperty("user_id")
  public Integer getUserId() {
    return this.userId.getId();
  }

  @JsonProperty("fullname")
  public String getFullName() {
    return this.userId.getFirstname() + " " + this.userId.getLastname();
  }

  @JsonProperty("special_metric")
  public SpecialMetric getSpecialMetric() {
    return this.specialMetric;
  }


  public String getType() {
    return this.metricId.getUnit().toString();
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
