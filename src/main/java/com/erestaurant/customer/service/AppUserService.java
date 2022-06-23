package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.AppUserDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.AppUser}.
 */
public interface AppUserService {
    /**
     * Save a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> save(AppUserDTO appUserDTO);

    /**
     * Updates a appUser.
     *
     * @param appUserDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> update(AppUserDTO appUserDTO);

    /**
     * Partially updates a appUser.
     *
     * @param appUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO);

    /**
     * Get all the appUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AppUserDTO> findAll(Pageable pageable);

    /**
     * Get all the appUsers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AppUserDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of appUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" appUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AppUserDTO> findOne(String id);

    /**
     * Delete the "id" appUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
