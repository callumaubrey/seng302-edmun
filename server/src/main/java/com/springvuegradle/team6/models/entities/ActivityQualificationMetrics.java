package com.springvuegradle.team6.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ActivityQualificationMetrics {

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
   * Boolean to rank metrics in different orders. Default value is true
   */
  @JsonProperty("rank_asc")
  private boolean rankByAsc;
  /**
   * The Unit Enmun associated with this instance
   */
  private Unit unit;

  public ActivityQualificationMetrics() {
  }

  // ==========SETTERS==============

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  // =========GETTERS================
  public Unit getUnit() {
    return this.unit;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean getRankByAsc() {
    return this.rankByAsc;
  }
}
