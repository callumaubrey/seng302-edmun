package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Entity to keep track of each time a subscription is change. Used to display this change
 * on home feed. Each time subscription state changes a new row is created with the current timestamp
 * and the current state of the subscription.
 */
@Entity
public class SubscriptionHistory {

    // For testing purposes only
    public SubscriptionHistory() {
        this.profile = null;
        this.activity = null;
        this.timeDate = null;
        this.subscribe = null;
    }

    /**
     * Each history item has its own id
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Link to the profile this sub is associated with
     */
    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    /**
     * Link to the activity this sub is associated with
     */
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /**
     * The time this instance of sub hostory was created
     */
    @Column(name = "time_date")
    private Date timeDate;

    /**
     * Whether the user is subscribed or not to the activity at the point of this history item
     */
    private Boolean subscribe;

    //==========GETTERS==========

    public Integer getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public Activity getActivity() {
        return activity;
    }

    public Date getTimeDate() {
        return timeDate;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    //==========SETTERS===========

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setTimeDate(Date timeDate) {
        this.timeDate = timeDate;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }
}
