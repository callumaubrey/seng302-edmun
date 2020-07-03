package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class ActivityRole {

  public ActivityRole() {}

  @Id
  @GeneratedValue
  private int id;

  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  @OneToMany
  private List<Profile> profile;

  @Enumerated(EnumType.ORDINAL)
  private ActivityRoleType activityRoleTypes;
}
