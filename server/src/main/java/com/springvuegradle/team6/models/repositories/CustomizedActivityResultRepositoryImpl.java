package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Repository for metric results by sorting them by ranking using unit and rankByAsc.
 * This implementation can be improved in future to show profile global rankings by using
 * an SQL VIEW table.
 */
public class CustomizedActivityResultRepositoryImpl implements CustomizedActivityResultRepository {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Returns a sorted list of metric results based on unit and rankByAsc.
   * Returned in order of global ranking.
   * @param metricId metric id
   * @return Sorted list of metric results
   */
  public List<ActivityResult> getSortedResultsByMetricId(int metricId) {
    ActivityQualificationMetric metric = getMetricById(metricId);
    return (List<ActivityResult>) getResultSortedQuery(metric, null).getResultList();
  }

  /**
   * Returns a sorted list of metric results based on unit and rankByAsc. Returns only results of
   * profileId
   * Returned in order of personal best ranking.
   * @param metricId metric id
   * @param profileId profile to filter results by
   * @return Sorted list of metric results of a profile
   */
  public List<ActivityResult> getSortedResultsByMetricIdAndProfile(int metricId, int profileId) {
    ActivityQualificationMetric metric = getMetricById(metricId);
    return (List<ActivityResult>) getResultSortedQuery(metric, profileId).getResultList();
  }

  /**
   * Returns an SQL string of a unit including the sorting expression
   * @param metric The metric to build the sort string query from
   * @return SQL query sort cases
   */
  private String getResultSortString(ActivityQualificationMetric metric) {
    StringBuilder sortBy = new StringBuilder();
    sortBy.append("ORDER BY ");

    switch (metric.getUnit()) {
      case TimeDuration:
        sortBy.append(ActivityResultDuration.SQL_SORT_EXPRESSION);
        break;
      case TimeStartFinish:
        sortBy.append(ActivityResultStartFinish.SQL_SORT_EXPRESSION);
        break;
      case Count:
        sortBy.append(ActivityResultCount.SQL_SORT_EXPRESSION);
        break;
      case Distance:
        sortBy.append(ActivityResultDistance.SQL_SORT_EXPRESSION);
        break;
    }
    sortBy.append(metric.getRankByAsc() ? " ASC " : " DESC ");

    return sortBy.toString();
  }

  /**
   * Returns a query that sorts the list of metric results based on the sorting unit and direction
   * @param metric activity metric to list
   * @param profileID optional profile id to limit search to.
   * @return The built query
   */
  private Query getResultSortedQuery(ActivityQualificationMetric metric, Integer profileID) {
    StringBuilder queryStr = new StringBuilder();

    // Add SELECT Statement
    queryStr.append("SELECT * FROM activity_result ");

    // Add Conditions
    queryStr.append("WHERE metric_id = :metricId ");
    if (profileID != null) {
      queryStr.append("AND user_id = :profileId ");
    }

    // Add Sorting
    queryStr.append(getResultSortString(metric));

    Query query = this.entityManager.createNativeQuery(queryStr.toString(), ActivityResult.class);
    query.setParameter("metricId", metric.getId());

    if(profileID != null) {
      query.setParameter("profileId", profileID);
    }

    return query;
  }

  /**
   * Returns an activity metric based on metric id
   * @param metricID metric id to find
   * @return Activity metric that matches metricID
   */
  private ActivityQualificationMetric getMetricById(int metricID) {
    Query metricQuery = this.entityManager.createNativeQuery("SELECT * FROM activity_qualification_metric q " +
            "WHERE q.id = :metricId LIMIT 1", ActivityQualificationMetric.class);
    metricQuery.setParameter("metricId", metricID);
    List<ActivityQualificationMetric> metricRows = metricQuery.getResultList();
    return metricRows.get(0);
  }
}
