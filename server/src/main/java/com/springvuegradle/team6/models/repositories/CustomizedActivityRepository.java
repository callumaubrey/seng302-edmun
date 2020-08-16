package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Profile;
import java.time.LocalDateTime;
import java.util.List;

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
      boolean isAdmin);

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
      boolean isAdmin);
}
