package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Refunded;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Refunded entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RefundedRepository extends ReactiveCrudRepository<Refunded, String>, RefundedRepositoryInternal {
    @Override
    <S extends Refunded> Mono<S> save(S entity);

    @Override
    Flux<Refunded> findAll();

    @Override
    Mono<Refunded> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface RefundedRepositoryInternal {
    <S extends Refunded> Mono<S> save(S entity);

    Flux<Refunded> findAllBy(Pageable pageable);

    Flux<Refunded> findAll();

    Mono<Refunded> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Refunded> findAllBy(Pageable pageable, Criteria criteria);

}
