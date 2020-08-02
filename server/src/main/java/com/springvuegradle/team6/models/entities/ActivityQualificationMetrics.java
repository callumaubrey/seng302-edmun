package com.springvuegradle.team6.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ActivityQualificationMetrics {

  @Id
  @GeneratedValue
  private int id;

  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  private boolean rankByAsc;

  private Unit unit;
}

