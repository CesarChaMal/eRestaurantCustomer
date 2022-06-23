package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Complete;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Complete entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompleteRepository extends ReactiveCrudRepository<Complete, String>, CompleteRepositoryInternal {
    @Override
    <S extends Complete> Mono<S> save(S entity);

    @Override
    Flux<Complete> findAll();

    @Override
    Mono<Complete> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CompleteRepositoryInternal {
    <S extends Complete> Mono<S> save(S entity);

    Flux<Complete> findAllBy(Pageable pageable);

    Flux<Complete> findAll();

    Mono<Complete> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Complete> findAllBy(Pageable pageable, Criteria criteria);

}
