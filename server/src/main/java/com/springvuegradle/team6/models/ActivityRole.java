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
  private ActivityRoleType activityRoleType;

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setProfile(List<Profile> profiles) {
    this.profile = profiles;
  }

  public void setActivityRoleType(ActivityRoleType activityRoleType) {
    this.activityRoleType = activityRoleType;
  }
}
