package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.OnHold;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the OnHold entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OnHoldRepository extends ReactiveCrudRepository<OnHold, String>, OnHoldRepositoryInternal {
    @Override
    <S extends OnHold> Mono<S> save(S entity);

    @Override
    Flux<OnHold> findAll();

    @Override
    Mono<OnHold> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface OnHoldRepositoryInternal {
    <S extends OnHold> Mono<S> save(S entity);

    Flux<OnHold> findAllBy(Pageable pageable);

    Flux<OnHold> findAll();

    Mono<OnHold> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OnHold> findAllBy(Pageable pageable, Criteria criteria);

}
