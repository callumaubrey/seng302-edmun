package com.springvuegradle.team6.models.repositories;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.SortActivity;
import com.springvuegradle.team6.models.entities.VisibilityType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;
import org.hibernate.search.spatial.DistanceSortField;

/**
 * Implements CustomizedActivityRepository
 * @see com.springvuegradle.team6.models.repositories.CustomizedActivityRepository
 **/
public class CustomizedActivityRepositoryImpl implements CustomizedActivityRepository {
  @PersistenceContext private EntityManager em;
  private static final String CONTINUOUS = "continuous";
  private static final String START_TIME = "startTime";
  private static final String END_TIME = "endTime";
  private static final String LOCATION = "location";

  /**
   * Searches for a list of activities that match the given parameters and returns a list of
   * activities
   *
   * @param terms terms used to search the activity name
   * @param activityTypes array of activity types
   * @param hashtags array of hashtags
   * @param activityTypesMethod method of searching activity types (OR or AND)
   * @param hashTagsMethod method of searching hashtags (OR or AND)
   * @param time either continuous or duration
   * @param startDate earliest start date for an activity
   * @param endDate latest end date for an activity
   * @param limit the number of activities to return
   * @param offset the number of activities to skip
   * @param profileId the profileId of the current user logged in
   * @param isAdmin whether or not the user is an admin
   * @param longitude the longitude coordinate of the location of the activity
   * @param latitude the latitude coordinate of the location of the activity
   * @param radius the radius of the the circle centred at the location coordinate
   * @param sortActivity the sorting method for the activities returned
   * @return List of activities satisfying the parameters
   */
  @Override
  public List<Activity> searchActivity(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashTagsMethod,
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
      SortActivity sortActivity) {

    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        searchActivityQuery(
            terms,
            activityTypes,
            hashtags,
            activityTypesMethod,
            hashTagsMethod,
            time,
            startDate,
            endDate,
            limit,
            offset,
            profileId,
            isAdmin,
            longitude,
            latitude,
            radius,
            sortActivity);

    List<Activity> result = new ArrayList<>();
    if (jpaQuery != null) {
      result = jpaQuery.getResultList();
    }
    return result;
  }

  /**
   * * Searches for a list of activities that match the given parameters and returns the number of
   * activities
   *
   * @param terms terms used to search the activity name
   * @param activityTypes array of activity types
   * @param hashtags array of hashtags
   * @param activityTypesMethod method of searching activity types (OR or AND)
   * @param hashTagsMethod method of searching hashtags (OR or AND)
   * @param time either continuous or duration
   * @param startDate earliest start date for an activity
   * @param endDate latest end date for an activity
   * @param profileId the profileId of the current user logged in
   * @param isAdmin whether or not the user is an admin
   * @param longitude the longitude coordinate of the location of the activity
   * @param latitude the latitude coordinate of the location of the activity
   * @param radius the radius of the the circle centred at the location coordinate
   * @param sortActivity the sorting method for the activities returned
   * @return a count of the number of activities
   */
  @Override
  public Integer searchActivityCount(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashTagsMethod,
      String time,
      LocalDateTime startDate,
      LocalDateTime endDate,
      int profileId,
      boolean isAdmin,
      Double longitude,
      Double latitude,
      Integer radius,
      SortActivity sortActivity) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        searchActivityQuery(
            terms,
            activityTypes,
            hashtags,
            activityTypesMethod,
            hashTagsMethod,
            time,
            startDate,
            endDate,
            -1,
            -1,
            profileId,
            isAdmin,
            longitude,
            latitude,
            radius,
            sortActivity);

    int count = 0;
    if (jpaQuery != null) {
      count = jpaQuery.getResultSize();
    }
    return count;
  }

  /**
   * The general query used by searchActivity and searchActivityCount to search the database based
   * on the given parameters and returns the result as a FullTextQuery.
   *
   * @param terms terms used to search the activity name
   * @param activityTypes array of activity types
   * @param hashtags array of hashtags
   * @param activityTypesMethod method of searching activity types (OR or AND)
   * @param hashTagsMethod method of searching hashtags (OR or AND)
   * @param time either continuous or duration
   * @param startDate earliest start date for an activity
   * @param endDate latest end date for an activity
   * @param limit the number of activities to return
   * @param offset the number of activities to skip
   * @param profileId the profileId of the current user logged in
   * @param isAdmin whether or not the user is an admin
   * @param longitude the longitude coordinate of the location of the activity
   * @param latitude the latitude coordinate of the location of the activity
   * @param radius the radius of the the circle centred at the location coordinate
   * @param sortActivity the sorting method for the activities returned
   * @return FullTextQuery containing results
   */
  private org.hibernate.search.jpa.FullTextQuery searchActivityQuery(
      String terms,
      String[] activityTypes,
      String[] hashtags,
      String activityTypesMethod,
      String hashTagsMethod,
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
      SortActivity sortActivity) {
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
      finalQuery = queryBuilder.keyword().onField("activity_name").matching(terms).createQuery();
    }

    if (activityTypes != null && activityTypes.length > 0) {
      BooleanJunction<?> activityQuery =
          addActivityTypeQuery(queryBuilder, activityTypes, activityTypesMethod);
      if (finalQuery == null) {
        finalQuery = activityQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(activityQuery.createQuery()).createQuery();
      }
    }

    if (hashtags != null && hashtags.length > 0) {
      BooleanJunction<?> hashtagQuery = addHashtagQuery(queryBuilder, hashtags, hashTagsMethod);
      if (finalQuery == null) {
        finalQuery = hashtagQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(hashtagQuery.createQuery()).createQuery();
      }
    }

    if (time != null) {
      BooleanJunction<?> timeQuery = addTimeQuery(queryBuilder, time, startDate, endDate);
      if (finalQuery == null) {
        finalQuery = timeQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(timeQuery.createQuery()).createQuery();
      }
    }

    if (finalQuery != null) {
      BooleanJunction<?> visibilityQuery = addVisibilityQuery(queryBuilder, profileId, isAdmin);
      if (visibilityQuery != null) {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(visibilityQuery.createQuery()).createQuery();
      }
    }

    if (longitude != null && latitude != null && radius != null) {
      BooleanJunction<?> locationQuery =
          addLocationQuery(queryBuilder, longitude, latitude, radius);
      if (finalQuery == null) {
        finalQuery = locationQuery.createQuery();
      } else {
        finalQuery =
            queryBuilder.bool().must(finalQuery).must(locationQuery.createQuery()).createQuery();
      }
    }

    if (finalQuery != null) {
      org.apache.lucene.search.Sort sort = sortingMethod(sortActivity, latitude, longitude);

      org.hibernate.search.jpa.FullTextQuery jpaQuery =
          fullTextEntityManager.createFullTextQuery(finalQuery, Activity.class);

      jpaQuery.setSort(sort);

      if (limit != -1) {
        jpaQuery.setMaxResults(limit);
      }
      if (offset != -1) {
        jpaQuery.setFirstResult(offset);
      }

      return jpaQuery;
    }
    return null;
  }

  /**
   * Creates the BooleanJunction that can be turned into a query used to search the activity by
   * hashtags
   *
   * @param queryBuilder used for building the query
   * @param hashtagsArray the array of hashtags
   * @param method determines how to search the hashtags (OR or AND)
   * @return BooleanJunction that can be turned into a query used to search the activity
   */
  private BooleanJunction<?> addHashtagQuery(
      QueryBuilder queryBuilder, String[] hashtagsArray, String method) {
    BooleanJunction<?> query = queryBuilder.bool();
    for (String hashtag : hashtagsArray) {
      org.apache.lucene.search.Query hashtagQuery =
          queryBuilder.simpleQueryString().onField("tags").matching(hashtag).createQuery();
      if (method == null) {
        query.must(hashtagQuery);
      } else {
        if (method.equals("or")) {
          query.should(hashtagQuery);
        } else {
          query.must(hashtagQuery);
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
   * @return BooleanJunction that can be turned into a query used to search the activity
   */
  private BooleanJunction<?> addActivityTypeQuery(
      QueryBuilder queryBuilder, String[] activityTypesArray, String method) {
    BooleanJunction<?> query = queryBuilder.bool();
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
        if (method.equals("or")) {
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
   * either duration or continuous. If duration is selected also search by the startDate and endDate
   * if they are specified.
   *
   * @param queryBuilder used for building the query
   * @param time either continuous or duration
   * @param startDate earlier start date for activity
   * @param endDate latest start date for activity
   * @return BooleanJunction that can be turned into a query used to search the activity
   */
  private BooleanJunction<?> addTimeQuery(
      QueryBuilder queryBuilder, String time, LocalDateTime startDate, LocalDateTime endDate) {
    BooleanJunction<?> booleanJunction = queryBuilder.bool();

    Query query;
    if (time.equals(CONTINUOUS)) {
      query = queryBuilder.keyword().onField(CONTINUOUS).matching(true).createQuery();
    } else {
      query = queryBuilder.keyword().onField(CONTINUOUS).matching(false).createQuery();
      if (startDate != null) {
        Query startDateQuery =
            queryBuilder.range().onField(START_TIME).above(startDate).createQuery();
        booleanJunction.must(startDateQuery);
      }
      if (endDate != null) {
        Query endDateQuery = queryBuilder.range().onField(END_TIME).below(endDate).createQuery();
        booleanJunction.must(endDateQuery);
      }
    }
    booleanJunction.must(query);

    return booleanJunction;
  }

  /**
   * Creates the BooleanJunction that can be turned into a query used to search the activity by
   * permissions. The user only has permission to view certain activities such as public ones, ones
   * they own and ones they are apart of.
   *
   * @param queryBuilder used for building the query
   * @param profileId the profileId of the logged in user
   * @return BooleanJunction that can be turned into a query used to search the activity
   */
  private BooleanJunction<?> addVisibilityQuery(
      QueryBuilder queryBuilder, Integer profileId, boolean isAdmin) {
    if (isAdmin) {
      return null;
    } else {
      BooleanJunction<?> booleanJunction = queryBuilder.bool();

      Query publicActivityQuery =
          queryBuilder
              .keyword()
              .onField("visibility")
              .matching(VisibilityType.Public)
              .createQuery();
      Query ownActivityQuery =
          queryBuilder.keyword().onField("profile").matching(profileId.toString()).createQuery();
      Query hasAccessToActivityQuery =
          queryBuilder
              .keyword()
              .onField("activityRole.profile")
              .matching(profileId.toString())
              .createQuery();
      booleanJunction.should(hasAccessToActivityQuery);
      booleanJunction.should(publicActivityQuery);
      booleanJunction.should(ownActivityQuery);
      return booleanJunction;
    }
  }

  /**
   * Creates the BooleanJunction that can be turned into a query used to search the activity by
   * location. Location consists of longitude and latitude, and the search searches around an area
   * radius around the location.
   *
   * @param queryBuilder used for building the query
   * @param longitude the longitude coordinate
   * @param latitude the latitude coordinate
   * @param radius the distance the circle area will extend from the centre of the coordinate
   * @return BooleanJunction that can be turned into a query used to search the activity
   */
  private BooleanJunction<?> addLocationQuery(
      QueryBuilder queryBuilder, Double longitude, Double latitude, Integer radius) {
    BooleanJunction<?> booleanJunction = queryBuilder.bool();
    org.apache.lucene.search.Query luceneQuery =
        queryBuilder
            .spatial()
            .onField(LOCATION)
            .within(radius, Unit.KM)
            .ofLatitude(latitude)
            .andLongitude(longitude)
            .createQuery();
    booleanJunction.must(luceneQuery);
    return booleanJunction;
  }

  /**
   * Given parameters, figure out the sorting method to use for sorting the final query.
   *
   * @param sortActivity the sorting method desired
   * @param latitude the latitude coordinate of the search location
   * @param longitude the longitude coordinate of the search location
   * @return the sort
   */
  private org.apache.lucene.search.Sort sortingMethod(
      SortActivity sortActivity, Double latitude, Double longitude) {
    org.apache.lucene.search.Sort sort;
    if (sortActivity != null) {
      switch (sortActivity) {
        case EARLIEST_START_DATE:
          sort =
              new Sort(
                  new SortField(START_TIME, SortField.Type.STRING),
                  new SortField("id", SortField.Type.STRING, true));
          break;
        case LATEST_START_DATE:
          sort =
              new Sort(
                  new SortField(START_TIME, SortField.Type.STRING, true),
                  new SortField("id", SortField.Type.STRING, true));
          break;
        case EARLIEST_END_DATE:
          sort =
              new Sort(
                  new SortField(END_TIME, SortField.Type.STRING),
                  new SortField("id", SortField.Type.STRING, true));
          break;
        case LATEST_END_DATE:
          sort =
              new Sort(
                  new SortField(END_TIME, SortField.Type.STRING, true),
                  new SortField("id", SortField.Type.STRING, true));
          break;
        case CLOSEST_LOCATION:
          sort =
              new Sort(
                  new DistanceSortField(latitude, longitude, LOCATION),
                  SortField.FIELD_SCORE,
                  new SortField("id", SortField.Type.STRING, true));
          break;
        case FURTHEST_LOCATION:
          sort =
              new Sort(
                  new DistanceSortField(latitude, longitude, LOCATION, true),
                  SortField.FIELD_SCORE,
                  new SortField("id", SortField.Type.STRING, true));
          break;
        default:
          sort = new Sort(SortField.FIELD_SCORE, new SortField("id", SortField.Type.STRING, true));
      }
    } else if (longitude != null && latitude != null) {
      sort =
          new Sort(
              new DistanceSortField(latitude, longitude, LOCATION),
              SortField.FIELD_SCORE,
              new SortField("id", SortField.Type.STRING, true));
    } else {
      sort = new Sort(SortField.FIELD_SCORE, new SortField("id", SortField.Type.STRING, true));
    }
    return sort;
  }
}
