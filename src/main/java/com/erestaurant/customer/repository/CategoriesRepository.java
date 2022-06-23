package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Categories;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Categories entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriesRepository extends ReactiveCrudRepository<Categories, String>, CategoriesRepositoryInternal {
    @Override
    <S extends Categories> Mono<S> save(S entity);

    @Override
    Flux<Categories> findAll();

    @Override
    Mono<Categories> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CategoriesRepositoryInternal {
    <S extends Categories> Mono<S> save(S entity);

    Flux<Categories> findAllBy(Pageable pageable);

    Flux<Categories> findAll();

    Mono<Categories> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Categories> findAllBy(Pageable pageable, Criteria criteria);

}
