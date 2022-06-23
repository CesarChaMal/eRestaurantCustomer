package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, String>, CustomerRepositoryInternal {
    @Override
    <S extends Customer> Mono<S> save(S entity);

    @Override
    Flux<Customer> findAll();

    @Override
    Mono<Customer> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CustomerRepositoryInternal {
    <S extends Customer> Mono<S> save(S entity);

    Flux<Customer> findAllBy(Pageable pageable);

    Flux<Customer> findAll();

    Mono<Customer> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Customer> findAllBy(Pageable pageable, Criteria criteria);

}
