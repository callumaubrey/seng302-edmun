package com.springvuegradle.team6.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ActivityQualificationMetric {

  /**
   * Each activity qualification metric instance has its own unique id
   */
  @Id
  @GeneratedValue
  private int id;
  /**
   * The title of the qualification metric
   */
  private String title;
  /**
   * The description of the qualification metric
   */
  private String description;
  /**
   * The activity this instance is associated with
   */
  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;
  /**
   * Boolean to rank metrics in different orders.
   */
  private boolean rankByAsc;
  /**
   * The Unit Enmun associated with this instance
   */
  private Unit unit;

  public ActivityQualificationMetric() {
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  // =========GETTERS================
  public Unit getUnit() {
    return this.unit;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}
