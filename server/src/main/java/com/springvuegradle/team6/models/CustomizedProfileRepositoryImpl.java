package com.springvuegradle.team6.models;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedProfileRepositoryImpl implements CustomizedProfileRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Profile> searchFullname(String terms, int limit, int offset) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Profile.class)
                .get();
        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .keyword()
                .onFields("firstname", "middlename", "lastname")
                .matching(terms)
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
