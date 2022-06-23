package com.erestaurant.customer.service;

import com.erestaurant.customer.service.dto.CategoriesDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.customer.domain.Categories}.
 */
public interface CategoriesService {
    /**
     * Save a categories.
     *
     * @param categoriesDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CategoriesDTO> save(CategoriesDTO categoriesDTO);

    /**
     * Updates a categories.
     *
     * @param categoriesDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CategoriesDTO> update(CategoriesDTO categoriesDTO);

    /**
     * Partially updates a categories.
     *
     * @param categoriesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CategoriesDTO> partialUpdate(CategoriesDTO categoriesDTO);

    /**
     * Get all the categories.
     *
     * @return the list of entities.
     */
    Flux<CategoriesDTO> findAll();

    /**
     * Returns the number of categories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" categories.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CategoriesDTO> findOne(String id);

    /**
     * Delete the "id" categories.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
