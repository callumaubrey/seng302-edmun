package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.Profile;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.lucene.search.Query;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

public class CustomizedActivityRepositoryImpl implements CustomizedActivityRepository {
  @PersistenceContext private EntityManager em;

  /**
   * Searches for a list of activities that match the given parameters and returns a list of
   * activities
   *
   * @param terms terms used to search the activity name
   * @param activityTypes array of activity types
   * @param hashtags array of hashtags
   * @param activityTypesMethod method of searching activity types (OR or AND)
   * @param hashTagsMethod method of searching hashtags (OR or AND)
   * @param durationContinuousAll
   * @param startDate
   * @param endDate
   * @param limit
   * @param offset
   * @return List of activities satisfying the parameters
   */
  @Override
  public List<Activity> searchActivity(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashTagsMethod,
      String durationContinuousAll,
      String startDate,
      String endDate,
      int limit,
      int offset) {

    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    try {
      fullTextEntityManager.createIndexer().startAndWait();
    } catch (Exception e) {
      System.out.println(e);
    }

    QueryBuilder queryBuilder =
        fullTextEntityManager
            .getSearchFactory()
            .buildQueryBuilder()
            .forEntity(Activity.class)
            .get();

    org.apache.lucene.search.Query finalQuery = null;

    if (terms != null) {
      org.apache.lucene.search.Query activityNameQuery =
          queryBuilder.keyword().onField("activity_name").matching(terms).createQuery();
      finalQuery = activityNameQuery;
    }

    if (activityTypes != null && activityTypes.length > 0) {
      BooleanJunction activityQuery =
          addActivityTypeQuery(queryBuilder, activityTypes, activityTypesMethod);
      if (finalQuery == null) {
        finalQuery = activityQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(activityQuery.createQuery()).createQuery();
      }
    }

    if (hashtags != null && hashtags.length > 0) {
      BooleanJunction hashtagQuery = addHashtagQuery(queryBuilder, hashtags, hashTagsMethod);
      if (finalQuery == null) {
        finalQuery = hashtagQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(hashtagQuery.createQuery()).createQuery();
      }
    }
    List result = new ArrayList();
    if (finalQuery != null) {
      org.hibernate.search.jpa.FullTextQuery jpaQuery =
          fullTextEntityManager.createFullTextQuery(finalQuery, Activity.class);

      result = jpaQuery.getResultList();
    }

    return result;
  }

  @Override
  public Integer searchActivityCount(
      String terms,
      String[] activityTypes,
      String[] hashTags,
      String activityTypesMethod,
      String hashTagsMethod,
      String durationContinuousAll,
      String startDate,
      String endDate,
      int limit,
      int offset) {
    return null;
  }

  /**
   * Creates the BooleanJunction that can be turned into a query used to search the activity by
   * hashtags
   *
   * @param queryBuilder used for building the query
   * @param hashtagsArray the array of hashtags
   * @param method determines how to search the hashtags (OR or AND)
   * @return BooleanJunction that can be turned into a query used to search the activity by hashtags
   */
  private BooleanJunction addHashtagQuery(
      QueryBuilder queryBuilder, String[] hashtagsArray, String method) {
    BooleanJunction query = queryBuilder.bool();
    for (String hashtag : hashtagsArray) {
      org.apache.lucene.search.Query activityQuery =
          queryBuilder.simpleQueryString().onField("tags").matching(hashtag).createQuery();
      if (method == null) {
        query.must(activityQuery);
      } else {
        if (method.equals("OR")) {
          query.should(activityQuery);
        } else {
          query.must(activityQuery);
        }
      }
    }
    return query;
  }

  /**
   * Creates the BooleanJunction that can be turned into a query used to search the activity by
   * activity types
   *
   * @param queryBuilder used for building the query
   * @param activityTypesArray array of activity types
   * @param method determines how to search the activity types (OR or AND)
   * @return BooleanJunction that can be turned into a query used to search the activity by activity
   *     types
   */
  private BooleanJunction addActivityTypeQuery(
      QueryBuilder queryBuilder, String[] activityTypesArray, String method) {
    BooleanJunction query = queryBuilder.bool();
    for (String activity : activityTypesArray) {
      org.apache.lucene.search.Query activityQuery =
          queryBuilder
              .simpleQueryString()
              .onField("activityTypes")
              .matching(activity)
              .createQuery();
      if (method == null) {
        query.must(activityQuery);
      } else {
        if (method.equals("OR")) {
          query.should(activityQuery);
        } else {
          query.must(activityQuery);
        }
      }
    }
    return query;
  }
}
