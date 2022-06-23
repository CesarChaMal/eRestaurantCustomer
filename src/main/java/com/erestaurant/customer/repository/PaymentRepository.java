package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends ReactiveCrudRepository<Payment, String>, PaymentRepositoryInternal {
    @Override
    <S extends Payment> Mono<S> save(S entity);

    @Override
    Flux<Payment> findAll();

    @Override
    Mono<Payment> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface PaymentRepositoryInternal {
    <S extends Payment> Mono<S> save(S entity);

    Flux<Payment> findAllBy(Pageable pageable);

    Flux<Payment> findAll();

    Mono<Payment> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Payment> findAllBy(Pageable pageable, Criteria criteria);

}
