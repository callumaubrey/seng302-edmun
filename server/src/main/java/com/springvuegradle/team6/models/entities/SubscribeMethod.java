package com.springvuegradle.team6.models.entities;

/**
 * Enum uses in SubscriptionHistory to identify the method of subscribing to the activity
 *
 * @see SubscriptionHistory
 */
public enum SubscribeMethod {
  /** The user subscribes to the activity. */
  SELF,
  /** The user was made a follower/subscriber by creator, organiser, or admin. */
  ADDED
}
