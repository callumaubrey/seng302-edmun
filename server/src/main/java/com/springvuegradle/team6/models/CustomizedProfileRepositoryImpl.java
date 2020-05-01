package com.springvuegradle.team6.models;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedProfileRepositoryImpl implements CustomizedProfileRepository {
  @PersistenceContext private EntityManager em;

  @Override
  public List<Profile> searchFullname(String terms, int limit, int offset) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

    QueryBuilder queryBuilder =
        fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Profile.class).get();

    String[] splited = terms.split(" ");

    org.apache.lucene.search.Query firstnameQuery =
        queryBuilder.keyword().onField("firstname").boostedTo(5).matching(splited[0]).createQuery();
    org.apache.lucene.search.Query lastnameQuery =
        queryBuilder
            .keyword()
            .onField("lastname")
            .boostedTo(5)
            .matching(splited[splited.length - 1])
            .createQuery();
    org.apache.lucene.search.Query fullnameQuery =
        queryBuilder.keyword().onFields("fullname").matching(terms).createQuery();

    org.apache.lucene.search.Query luceneQuery =
        queryBuilder
            .bool()
            .must(firstnameQuery)
            .must(lastnameQuery)
            .should(fullnameQuery)
            .createQuery();

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
