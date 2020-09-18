package com.springvuegradle.team6.models.entities;

import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.IntegerBridge;

@Entity
public class ActivityRole {

  public ActivityRole() {}

  @Id @GeneratedValue private int id;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  @OneToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Field(analyze = Analyze.YES, store = Store.NO)
  @FieldBridge(impl = IntegerBridge.class)
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

  public Profile getProfile() {
    return profile;
  }
  ;

  public ActivityRoleType getActivityRoleType() {
    return activityRoleType;
  }
  ;

  public String getRole() {
    if (activityRoleType == ActivityRoleType.Creator) {
      return "creator";
    } else if (activityRoleType == ActivityRoleType.Organiser) {
      return "organiser";
    } else if (activityRoleType == ActivityRoleType.Participant) {
      return "participant";
    } else if (activityRoleType == ActivityRoleType.Follower) {
      return "follower";
    } else {
      return "access";
    }
  }

  public void setActivityRoleType(ActivityRoleType activityRoleType) {
    this.activityRoleType = activityRoleType;
  }
}
