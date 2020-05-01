package com.springvuegradle.team6.models;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedProfileRepositoryImpl implements CustomizedProfileRepository {
  @PersistenceContext private EntityManager em;

  /**
   * Searches the database using hibernate search to find a profile for a full name that matches
   *
   * @param terms The given query parameters
   * @param limit The number of results to return
   * @param offset The number of results to skip
   * @return A list of profiles that matches the full name to some degree
   */
  @Override
  public List<Profile> searchFullname(String terms, int limit, int offset) {
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

      luceneQuery =
          queryBuilder
              .bool()
              .must(firstnameQuery)
              .must(lastnameQuery)
              .must(fullnameQuery)
              .createQuery();
    } else {
      luceneQuery = queryBuilder.bool().must(firstnameQuery).must(fullnameQuery).createQuery();
    }

    javax.persistence.Query jpaQuery =
        fullTextEntityManager.createFullTextQuery(luceneQuery, Profile.class);

    // In the case where we want to implement server side pagination
    if (limit != -1) {
      jpaQuery.setMaxResults(limit);
    }
    if (offset != -1) {
      jpaQuery.setFirstResult(offset);
    }

    // execute search
    return jpaQuery.getResultList();
  }
}
