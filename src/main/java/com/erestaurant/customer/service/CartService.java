package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.CartDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.Cart}.
 */
public interface CartService {
    /**
     * Save a cart.
     *
     * @param cartDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CartDTO> save(CartDTO cartDTO);

    /**
     * Updates a cart.
     *
     * @param cartDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CartDTO> update(CartDTO cartDTO);

    /**
     * Partially updates a cart.
     *
     * @param cartDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CartDTO> partialUpdate(CartDTO cartDTO);

    /**
     * Get all the carts.
     *
     * @return the list of entities.
     */
    Flux<CartDTO> findAll();

    /**
     * Returns the number of carts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" cart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CartDTO> findOne(String id);

    /**
     * Delete the "id" cart.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
