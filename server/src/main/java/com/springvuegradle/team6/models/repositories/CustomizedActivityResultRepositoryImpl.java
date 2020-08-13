package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityQualificationMetric;
import com.springvuegradle.team6.models.entities.ActivityResult;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CustomizedActivityResultRepositoryImpl implements CustomizedActivityResultRepository {
  @PersistenceContext
  private EntityManager entityManager;

  public List<ActivityResult> getSortedResultsByMetricId(int metricId) {
    Query metricQuery =
        this.entityManager.createNativeQuery("SELECT * FROM activity_qualification_metric q WHERE q.id = :metricId LIMIT 1",
            ActivityQualificationMetric.class);
    metricQuery.setParameter("metricId", metricId);
    List<ActivityQualificationMetric> metricRows = metricQuery.getResultList();
    ActivityQualificationMetric metric = metricRows.get(0);

    Query query2 = this.entityManager.createNativeQuery(
        "SELECT a.result_type, a.id, a.special_metric, a.count_result, a.distance_result, "
        + "a.duration_result, a.result_finish, a.result_start, a.metric_id, a.user_id FROM activity_result a "
        + "WHERE a.metric_id = :metricId "
        + "ORDER BY "
        + "CASE WHEN :sortingUnit=0 AND :sortingDirection=1 THEN duration_result ELSE 0 END ASC, "
        + "CASE WHEN :sortingUnit=0 AND :sortingDirection=0 THEN duration_result ELSE 0 END DESC", ActivityResult.class);
    query2.setParameter("sortingUnit", metric.getUnit());
    query2.setParameter("sortingDirection", metric.getRankByAsc());
    query2.setParameter("metricId", metricId);
    List<ActivityResult> rows = query2.getResultList();
    return rows;
  }

  public List<ActivityResult> getSortedResultsByMetricIdAndProfile(int metricId, int profileId) {
    Query metricQuery =
        this.entityManager.createNativeQuery("SELECT * FROM activity_qualification_metric q WHERE q.id = :metricId LIMIT 1",
            ActivityQualificationMetric.class);
    metricQuery.setParameter("metricId", metricId);
    List<ActivityQualificationMetric> metricRows = metricQuery.getResultList();
    ActivityQualificationMetric metric = metricRows.get(0);

    Query query2 = this.entityManager.createNativeQuery(
        "SELECT a.result_type, a.id, a.special_metric, a.count_result, a.distance_result, "
            + "a.duration_result, a.result_finish, a.result_start, a.metric_id, a.user_id FROM activity_result a "
            + "LEFT JOIN profile p ON a.user_id = p.id "
            + "WHERE a.metric_id = :metricId AND p.id = :profileId "
            + "ORDER BY "
            + "CASE WHEN :sortingUnit=0 AND :sortingDirection=1 THEN duration_result ELSE 0 END ASC, "
            + "CASE WHEN :sortingUnit=0 AND :sortingDirection=0 THEN duration_result ELSE 0 END DESC", ActivityResult.class);
    query2.setParameter("sortingUnit", metric.getUnit());
    query2.setParameter("sortingDirection", metric.getRankByAsc());
    query2.setParameter("metricId", metricId);
    query2.setParameter("profileId", profileId);
    List<ActivityResult> rows = query2.getResultList();
    return rows;
  }
}
