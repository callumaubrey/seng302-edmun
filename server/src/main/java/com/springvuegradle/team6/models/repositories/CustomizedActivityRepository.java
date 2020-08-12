package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Profile;
import java.util.List;

public interface CustomizedActivityRepository {
  List<Activity> searchActivity(
      String terms,
      String[] activityTypes,
      String[] hashTags,
      String activityTypesMethod,
      String hashTagsMethod,
      String durationContinuousAll,
      String startDate,
      String endDate,
      int limit,
      int offset);

  Integer searchActivityCount(
      String terms,
      String[] activityTypes,
      String[] hashTags,
      String activityTypesMethod,
      String hashTagsMethod,
      String durationContinuousAll,
      String startDate,
      String endDate,
      int limit,
      int offset);
}
