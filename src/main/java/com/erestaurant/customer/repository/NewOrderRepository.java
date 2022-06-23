package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.NewOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the NewOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NewOrderRepository extends ReactiveCrudRepository<NewOrder, String>, NewOrderRepositoryInternal {
    @Override
    <S extends NewOrder> Mono<S> save(S entity);

    @Override
    Flux<NewOrder> findAll();

    @Override
    Mono<NewOrder> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface NewOrderRepositoryInternal {
    <S extends NewOrder> Mono<S> save(S entity);

    Flux<NewOrder> findAllBy(Pageable pageable);

    Flux<NewOrder> findAll();

    Mono<NewOrder> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<NewOrder> findAllBy(Pageable pageable, Criteria criteria);

}
