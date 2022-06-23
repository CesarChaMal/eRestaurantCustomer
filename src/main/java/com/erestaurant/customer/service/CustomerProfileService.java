package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.CustomerProfileDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.CustomerProfile}.
 */
public interface CustomerProfileService {
    /**
     * Save a customerProfile.
     *
     * @param customerProfileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CustomerProfileDTO> save(CustomerProfileDTO customerProfileDTO);

    /**
     * Updates a customerProfile.
     *
     * @param customerProfileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CustomerProfileDTO> update(CustomerProfileDTO customerProfileDTO);

    /**
     * Partially updates a customerProfile.
     *
     * @param customerProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CustomerProfileDTO> partialUpdate(CustomerProfileDTO customerProfileDTO);

    /**
     * Get all the customerProfiles.
     *
     * @return the list of entities.
     */
    Flux<CustomerProfileDTO> findAll();

    /**
     * Returns the number of customerProfiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" customerProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CustomerProfileDTO> findOne(String id);

    /**
     * Delete the "id" customerProfile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
