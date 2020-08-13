package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityResult;
import java.util.List;

public interface CustomizedActivityResultRepository {

  List<ActivityResult> getSortedResultsByMetricId(int metricId);
}
