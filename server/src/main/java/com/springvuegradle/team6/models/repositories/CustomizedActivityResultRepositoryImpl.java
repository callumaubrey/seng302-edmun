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
   * @param unit Unit for this sort case
   * @param sortExpression sort expression for the sort unit
   * @return SQL query sort cases
   */
  private String getResultSortCases(Unit unit, String sortExpression, boolean isLast) {
    if (isLast) {
      return    "CASE WHEN :sortingUnit="+unit.ordinal()+" AND :sortingDirection=1 THEN "+sortExpression+" ELSE 0 END ASC, "
          + "CASE WHEN :sortingUnit="+unit.ordinal()+" AND :sortingDirection=0 THEN "+sortExpression+" ELSE 0 END DESC";
    }
    return    "CASE WHEN :sortingUnit="+unit.ordinal()+" AND :sortingDirection=1 THEN "+sortExpression+" ELSE 0 END ASC, "
            + "CASE WHEN :sortingUnit="+unit.ordinal()+" AND :sortingDirection=0 THEN "+sortExpression+" ELSE 0 END DESC, ";
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
    queryStr.append("SELECT a.result_type, a.id, a.special_metric, a.count_result, a.distance_result, "
            + "a.duration_result, a.result_finish, a.result_start, a.metric_id, a.user_id FROM activity_result a ");

    // Add Conditions
    queryStr.append("WHERE a.metric_id = :metricId ");
    if (profileID != null) {
      queryStr.append("AND a.user_id = :profileId ");
    }

    // Add Sorting
    queryStr.append("ORDER BY ");
    queryStr.append(getResultSortCases(Unit.Count, ActivityResultCount.SQL_SORT_EXPRESSION, false));
    queryStr.append(getResultSortCases(Unit.Distance, ActivityResultDistance.SQL_SORT_EXPRESSION, false));
    queryStr.append(getResultSortCases(Unit.TimeDuration, ActivityResultDuration.SQL_SORT_EXPRESSION, false));
    queryStr.append(getResultSortCases(Unit.TimeStartFinish, ActivityResultStartFinish.SQL_SORT_EXPRESSION, true));

    Query query = this.entityManager.createNativeQuery(queryStr.toString(), ActivityResult.class);
    query.setParameter("metricId", metric.getId());
    query.setParameter("sortingUnit", metric.getUnit());
    query.setParameter("sortingDirection", metric.getRankByAsc());

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
