package com.springvuegradle.team6.models.entities;

/**
 * Enum uses in SubscriptionHistory to identify the method of unsubscribing to the activity
 *
 * @see SubscriptionHistory
 */
public enum UnsubscribeMethod {
  /** The user unsubscribes to the activity */
  SELF,
  /**
   * The user is removed from the subscription of the activity. This happens when the owner changes
   * the visibility of the activity and chooses to remove the user, or the admin wants to remove the
   * user.
   */
  REMOVED
}
