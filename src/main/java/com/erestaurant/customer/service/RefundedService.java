package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.RefundedDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.Refunded}.
 */
public interface RefundedService {
    /**
     * Save a refunded.
     *
     * @param refundedDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> save(RefundedDTO refundedDTO);

    /**
     * Updates a refunded.
     *
     * @param refundedDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> update(RefundedDTO refundedDTO);

    /**
     * Partially updates a refunded.
     *
     * @param refundedDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> partialUpdate(RefundedDTO refundedDTO);

    /**
     * Get all the refundeds.
     *
     * @return the list of entities.
     */
    Flux<RefundedDTO> findAll();

    /**
     * Returns the number of refundeds available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" refunded.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RefundedDTO> findOne(String id);

    /**
     * Delete the "id" refunded.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
