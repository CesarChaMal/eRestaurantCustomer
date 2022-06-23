package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.OrderDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.Order}.
 */
public interface OrderService {
    /**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrderDTO> save(OrderDTO orderDTO);

    /**
     * Updates a order.
     *
     * @param orderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrderDTO> update(OrderDTO orderDTO);

    /**
     * Partially updates a order.
     *
     * @param orderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrderDTO> partialUpdate(OrderDTO orderDTO);

    /**
     * Get all the orders.
     *
     * @return the list of entities.
     */
    Flux<OrderDTO> findAll();

    /**
     * Returns the number of orders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" order.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrderDTO> findOne(String id);

    /**
     * Delete the "id" order.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
