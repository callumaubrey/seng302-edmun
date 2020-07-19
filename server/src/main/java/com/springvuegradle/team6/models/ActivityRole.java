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

  @OneToOne
  private Profile profile;

  @Enumerated(EnumType.ORDINAL)
  private ActivityRoleType activityRoleType;

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }
  public ActivityRoleType getActivityRoleType() {
    return activityRoleType;
  }

  public Profile getProfile() {return profile;};

  public void setActivityRoleType(ActivityRoleType activityRoleType) {
    this.activityRoleType = activityRoleType;
  }
}
