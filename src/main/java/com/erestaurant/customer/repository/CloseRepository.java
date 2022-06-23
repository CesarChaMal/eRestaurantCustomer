package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Close;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Close entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CloseRepository extends ReactiveCrudRepository<Close, String>, CloseRepositoryInternal {
    @Override
    <S extends Close> Mono<S> save(S entity);

    @Override
    Flux<Close> findAll();

    @Override
    Mono<Close> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CloseRepositoryInternal {
    <S extends Close> Mono<S> save(S entity);

    Flux<Close> findAllBy(Pageable pageable);

    Flux<Close> findAll();

    Mono<Close> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Close> findAllBy(Pageable pageable, Criteria criteria);

}
