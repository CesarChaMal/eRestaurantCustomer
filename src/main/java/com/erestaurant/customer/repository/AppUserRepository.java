package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the AppUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppUserRepository extends ReactiveCrudRepository<AppUser, String>, AppUserRepositoryInternal {
    Flux<AppUser> findAllBy(Pageable pageable);

    @Override
    Mono<AppUser> findOneWithEagerRelationships(String id);

    @Override
    Flux<AppUser> findAllWithEagerRelationships();

    @Override
    Flux<AppUser> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM app_user entity WHERE entity.internal_user_id = :id")
    Flux<AppUser> findByInternalUser(String id);

    @Query("SELECT * FROM app_user entity WHERE entity.internal_user_id IS NULL")
    Flux<AppUser> findAllWhereInternalUserIsNull();

    @Override
    <S extends AppUser> Mono<S> save(S entity);

    @Override
    Flux<AppUser> findAll();

    @Override
    Mono<AppUser> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface AppUserRepositoryInternal {
    <S extends AppUser> Mono<S> save(S entity);

    Flux<AppUser> findAllBy(Pageable pageable);

    Flux<AppUser> findAll();

    Mono<AppUser> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppUser> findAllBy(Pageable pageable, Criteria criteria);

    Mono<AppUser> findOneWithEagerRelationships(String id);

    Flux<AppUser> findAllWithEagerRelationships();

    Flux<AppUser> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(String id);
}
