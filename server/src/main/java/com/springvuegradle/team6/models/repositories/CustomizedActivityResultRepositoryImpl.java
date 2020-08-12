package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.ActivityResult;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CustomizedActivityResultRepositoryImpl implements CustomizedActivityResultRepository {
  @PersistenceContext
  private EntityManager entityManager;

  public List<Object[]> getSortedResultsByMetricId(int metricId) {
    Query query1 =
        this.entityManager.createNativeQuery(
            "SELECT @SortingDirection \"\\\\:=\" q.rank_by_asc, @SortingUnit \"\\\\:=\" q.unit FROM activity_qualification_metric q WHERE q.id = :metricId");
    query1.setParameter("metricId", metricId);
    Query query2 = this.entityManager.createNativeQuery(          "SELECT a.result_type, a.id, a.special_metric, a.count_result, a.distance_result, a.duration_result, a.result_finish, a.result_start, a.metric_id, a.user_id FROM activity_result a "
        + "WHERE a.metric_id = :metricId "
        + "ORDER BY "
        + "CASE WHEN @SortingUnit=0 AND @SortingDirection=1 THEN duration_result ELSE 0 END ASC, "
        + "CASE WHEN @SortingUnit=0 AND @SortingDirection=0 THEN duration_result ELSE 0 END DESC");
    query2.setParameter("metricId", metricId);
    List<Object[]> rows = query2.getResultList();
    return rows;
  }
}
