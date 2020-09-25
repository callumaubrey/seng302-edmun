package com.springvuegradle.team6.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  @NotNull(message = "Metric title cannot be null")
  private String title;

  /**
   * The description of the qualification metric
   */
  private String description;

  /**
   * The activity this instance is associated with
   */
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  /**
   * Boolean to rank metrics in different orders. Default value is false
   */
  @JsonProperty("rank_asc")
  private boolean rankByAsc;

  /**
   * The Unit Enmun associated with this instance
   */
  @NotNull(message = "Metric unit cannot be null")
  private Unit unit;

  /**
   * This boolean is used to check if a metric can be edited or not
   * Default as true and when a result is added it becomes false
   */
  private boolean editable = true;

  public ActivityQualificationMetric() {
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public void setRankByAsc(boolean rank) {
    this.rankByAsc = rank;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTitle(String title) {this.title = title; }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public int getId() {
    return this.id;
  }

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

  public boolean getEditable() {
    return this.editable;
  }
}
