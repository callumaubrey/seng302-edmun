package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.SortActivity;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Determines the queries used for ActivityRepository and implemented by CustomizedActivityRepositoryImpl
 * @see ActivityRepository
 * @see CustomizedActivityRepositoryImpl
 **/
public interface CustomizedActivityRepository {
  List<Activity> searchActivity(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashtagsMethod,
      String time,
      LocalDateTime startDate,
      LocalDateTime endDate,
      int limit,
      int offset,
      int profileId,
      boolean isAdmin,
      Double longitude,
      Double latitude,
      Integer radius,
      SortActivity sortActivity);

  Integer searchActivityCount(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashtagsMethod,
      String time,
      LocalDateTime startDate,
      LocalDateTime endDate,
      int profileId,
      boolean isAdmin,
      Double longitude,
      Double latitude,
      Integer radius,
      SortActivity sortActivity);
}
