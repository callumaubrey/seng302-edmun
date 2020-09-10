package com.springvuegradle.team6.models.entities;

/**
 * Used for determining the sort type for activities returned from the getActivities endpoint in
 * SearchActivityController
 *
 * @see com.springvuegradle.team6.controllers.SearchActivityController
 */
public enum SortActivity {
  CLOSEST_LOCATION,
  FURTHEST_LOCATION,
  EARLIEST_START_DATE,
  LATEST_START_DATE,
  EARLIEST_END_DATE,
  LATEST_END_DATE;

  /** @return whether or not the SortActivity is a location sort */
  public boolean isLocationSort() {
    return this.equals(CLOSEST_LOCATION) || this.equals(FURTHEST_LOCATION);
  }
}
