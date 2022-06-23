package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.StateDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.State}.
 */
public interface StateService {
    /**
     * Save a state.
     *
     * @param stateDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StateDTO> save(StateDTO stateDTO);

    /**
     * Updates a state.
     *
     * @param stateDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StateDTO> update(StateDTO stateDTO);

    /**
     * Partially updates a state.
     *
     * @param stateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StateDTO> partialUpdate(StateDTO stateDTO);

    /**
     * Get all the states.
     *
     * @return the list of entities.
     */
    Flux<StateDTO> findAll();

    /**
     * Returns the number of states available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" state.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StateDTO> findOne(String id);

    /**
     * Delete the "id" state.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
