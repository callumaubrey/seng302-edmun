package com.springvuegradle.team6.models;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import com.springvuegradle.team6.models.ActivityType;
import org.springframework.security.core.parameters.P;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedProfileRepositoryImpl implements CustomizedProfileRepository {
  @PersistenceContext private EntityManager em;

  /**
   * Uses Hibernate search and lucene queries to search for profiles that match the fullname
   *
   * @param terms The given fullname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return the query results
   */
  public org.hibernate.search.jpa.FullTextQuery searchFullnameQuery(
      String terms, String activityTypes, int limit, int offset) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    try {
      fullTextEntityManager.createIndexer().startAndWait();
    } catch (Exception e) {
      System.out.println(e);
    }

    QueryBuilder queryBuilder =
        fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Profile.class).get();

    String[] splited = terms.split(" ");

    org.apache.lucene.search.Query firstnameQuery =
        queryBuilder.keyword().onField("firstname").boostedTo(5).matching(splited[0]).createQuery();

    org.apache.lucene.search.Query fullnameQuery =
        queryBuilder.simpleQueryString().onField("fullname").matching(terms).createQuery();

    org.apache.lucene.search.Query luceneQuery;
    if (splited.length > 1) {
      org.apache.lucene.search.Query lastnameQuery =
          queryBuilder
              .keyword()
              .onField("lastname")
              .boostedTo(5)
              .matching(splited[splited.length - 1])
              .createQuery();

      if (activityTypes == null) {
        luceneQuery =
                queryBuilder
                        .bool()
                        .should(firstnameQuery)
                        .should(lastnameQuery)
                        .should(fullnameQuery)
                        .createQuery();
      } else {
        org.apache.lucene.search.Query activityQuery =
                queryBuilder.simpleQueryString().onField("activityTypes").matching(activityTypes).createQuery();

        luceneQuery =
                queryBuilder
                        .bool()
                        .should(firstnameQuery)
                        .should(lastnameQuery)
                        .should(fullnameQuery)
                        .must(activityQuery)
                        .createQuery();
      }
    } else {
      if (activityTypes == null) {
        luceneQuery = queryBuilder.bool().should(firstnameQuery).should(fullnameQuery).createQuery();
      } else {
        org.apache.lucene.search.Query activityQuery =
                queryBuilder.simpleQueryString().onField("activityTypes").matching(activityTypes).createQuery();
        luceneQuery = queryBuilder.bool().should(firstnameQuery).should(fullnameQuery).must(activityQuery).createQuery();
      }
    }

    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        fullTextEntityManager.createFullTextQuery(luceneQuery, Profile.class);

    // In the case where we want to implement server side pagination
    if (limit != -1) {
      jpaQuery.setMaxResults(limit);
    }
    if (offset != -1) {
      jpaQuery.setFirstResult(offset);
    }
    return jpaQuery;
  }

  /**
   * Searches the database using hibernate search to find a profile for a full name that matches
   *
   * @param terms The given query parameters
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return A list of profiles that matches the full name to some degree
   */
  @Override
  public List<Profile> searchFullname(String terms, String activityType, int limit, int offset) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchFullnameQuery(terms, activityType, limit, offset);

    return jpaQuery.getResultList();
  }

  /**
   * Find the total number of profiles that matches the fullname
   *
   * @param terms The given nickname to search
   * @return A number of profiles that matches the nickname
   */
  @Override
  public Integer searchFullnameCount(String terms, String activityType) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchFullnameQuery(terms, activityType, -1, -1);

    return jpaQuery.getResultSize();
  }

  /**
   * Uses Hibernate search and lucene queries to search for profiles that match the nickname
   *
   * @param terms The given nickname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return the query results
   */
  public org.hibernate.search.jpa.FullTextQuery searchNicknameQuery(
      String terms, int limit, int offset) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    try {
      fullTextEntityManager.createIndexer().startAndWait();
    } catch (Exception e) {
      System.out.println(e);
    }

    QueryBuilder queryBuilder =
        fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Profile.class).get();

    org.apache.lucene.search.Query luceneQuery =
        queryBuilder.keyword().onField("nickname").matching(terms).createQuery();

    org.hibernate.search.jpa.FullTextQuery jpaQuery =
        fullTextEntityManager.createFullTextQuery(luceneQuery, Profile.class);

    // In the case where we want to implement server side pagination
    if (limit != -1) {
      jpaQuery.setMaxResults(limit);
    }
    if (offset != -1) {
      jpaQuery.setFirstResult(offset);
    }
    return jpaQuery;
  }

  /**
   * Searches the database using hibernate search to find a profile for a nickname that matches
   *
   * @param terms The given nickname to search
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return A list of profiles that matches the nickname
   */
  @Override
  public List<Profile> searchNickname(String terms, int limit, int offset) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchNicknameQuery(terms, limit, offset);

    return jpaQuery.getResultList();
  }

  /**
   * Find the total number of profiles that matches the nickname
   *
   * @param terms The given nickname to search
   * @return A number of profiles that matches the nickname
   */
  @Override
  public Integer searchNicknameCount(String terms) {
    org.hibernate.search.jpa.FullTextQuery jpaQuery = searchNicknameQuery(terms, -1, -1);

    return jpaQuery.getResultSize();
  }
}
