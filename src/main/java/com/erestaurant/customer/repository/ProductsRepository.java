package com.erestaurant.customer.repository;

import com.erestaurant.customer.domain.Products;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsRepository extends ReactiveCrudRepository<Products, String>, ProductsRepositoryInternal {
    @Override
    <S extends Products> Mono<S> save(S entity);

    @Override
    Flux<Products> findAll();

    @Override
    Mono<Products> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface ProductsRepositoryInternal {
    <S extends Products> Mono<S> save(S entity);

    Flux<Products> findAllBy(Pageable pageable);

    Flux<Products> findAll();

    Mono<Products> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Products> findAllBy(Pageable pageable, Criteria criteria);

}
