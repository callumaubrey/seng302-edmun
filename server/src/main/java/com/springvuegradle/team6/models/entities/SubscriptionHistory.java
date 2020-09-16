package com.springvuegradle.team6.models.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to keep track of each time a subscription is change. Used to display this change on home
 * feed. Each SubscriptionHistory has a start date time, end date time, subscribe method and
 * unsubscribe method. When a subscription is made, the start date time is set to now and the
 * subscribe method is mentioned. When a subscription is removed, the end date time of the
 * subscription is set to now and unsubscribe method is mentioned.
 *
 * @see SubscribeMethod
 * @see UnsubscribeMethod
 */
@Entity
public class SubscriptionHistory {

  // For testing purposes only
  public SubscriptionHistory() {
    this.profile = null;
    this.activity = null;
    this.startDateTime = null;
    this.endDateTime = null;
  }

  /**
   * Constructor taking the profile and activity involved in the subscription also sets start date
   * to now
   *
   * @param profile the profile that is subscribing to an activity
   * @param activity the activity being subscribed to
   */
  public SubscriptionHistory(Profile profile, Activity activity, SubscribeMethod subscribeMethod) {
    this.profile = profile;
    this.activity = activity;
    this.startDateTime = LocalDateTime.now();
    this.subscribeMethod = subscribeMethod;
    this.endDateTime = null;
  }

  /** Each history item has its own id */
  @Id @GeneratedValue private Integer id;

  /** Link to the profile this sub is associated with */
  @ManyToOne(cascade = CascadeType.REMOVE)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "profile_id", nullable = false)
  private Profile profile;

  /** Link to the activity this sub is associated with */
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "activity_id")
  private Activity activity;

  /** The time this instance of sub history was created */
  @Column(name = "start_date_time", columnDefinition = "datetime default NOW()")
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time")
  private LocalDateTime endDateTime;

  /** The method of subscribing to the activity */
  @Column(name = "subscribe_method", nullable = false)
  private SubscribeMethod subscribeMethod;

  /** The method of unsubscribing to the activity */
  @Column(name = "unsubscribe_method")
  private UnsubscribeMethod unsubscribeMethod;

  // ==========GETTERS==========

  public Integer getId() {
    return id;
  }

  public Profile getProfile() {
    return profile;
  }

  public Activity getActivity() {
    return activity;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  // ==========SETTERS===========

  public void setId(Integer id) {
    this.id = id;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  public SubscribeMethod getSubscribeMethod() {
    return subscribeMethod;
  }

  public void setSubscribeMethod(SubscribeMethod subscribeMethod) {
    this.subscribeMethod = subscribeMethod;
  }

  public UnsubscribeMethod getUnsubscribeMethod() {
    return unsubscribeMethod;
  }

  public void setUnsubscribeMethod(UnsubscribeMethod unsubscribeMethod) {
    this.unsubscribeMethod = unsubscribeMethod;
  }
}
