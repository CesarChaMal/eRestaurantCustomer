package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.OnHoldDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.OnHold}.
 */
public interface OnHoldService {
    /**
     * Save a onHold.
     *
     * @param onHoldDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OnHoldDTO> save(OnHoldDTO onHoldDTO);

    /**
     * Updates a onHold.
     *
     * @param onHoldDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OnHoldDTO> update(OnHoldDTO onHoldDTO);

    /**
     * Partially updates a onHold.
     *
     * @param onHoldDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OnHoldDTO> partialUpdate(OnHoldDTO onHoldDTO);

    /**
     * Get all the onHolds.
     *
     * @return the list of entities.
     */
    Flux<OnHoldDTO> findAll();

    /**
     * Returns the number of onHolds available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" onHold.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OnHoldDTO> findOne(String id);

    /**
     * Delete the "id" onHold.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
