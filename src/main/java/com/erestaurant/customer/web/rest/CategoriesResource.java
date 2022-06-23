package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.CategoriesRepository;
import com.erestaurant.customer.service.CategoriesService;
import com.erestaurant.customer.service.dto.CategoriesDTO;
import com.erestaurant.customer.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.erestaurant.customer.domain.Categories}.
 */
@RestController
@RequestMapping("/api")
public class CategoriesResource {

    private final Logger log = LoggerFactory.getLogger(CategoriesResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerCategories";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoriesService categoriesService;

    private final CategoriesRepository categoriesRepository;

    public CategoriesResource(CategoriesService categoriesService, CategoriesRepository categoriesRepository) {
        this.categoriesService = categoriesService;
        this.categoriesRepository = categoriesRepository;
    }

    /**
     * {@code POST  /categories} : Create a new categories.
     *
     * @param categoriesDTO the categoriesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoriesDTO, or with status {@code 400 (Bad Request)} if the categories has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/categories")
    public Mono<ResponseEntity<CategoriesDTO>> createCategories(@Valid @RequestBody CategoriesDTO categoriesDTO) throws URISyntaxException {
        log.debug("REST request to save Categories : {}", categoriesDTO);
        if (categoriesDTO.getId() != null) {
            throw new BadRequestAlertException("A new categories cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return categoriesService
            .save(categoriesDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing categories.
     *
     * @param id the id of the categoriesDTO to save.
     * @param categoriesDTO the categoriesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoriesDTO,
     * or with status {@code 400 (Bad Request)} if the categoriesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoriesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/categories/{id}")
    public Mono<ResponseEntity<CategoriesDTO>> updateCategories(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CategoriesDTO categoriesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Categories : {}, {}", id, categoriesDTO);
        if (categoriesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoriesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categoriesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return categoriesService
                    .update(categoriesDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing categories, field will ignore if it is null
     *
     * @param id the id of the categoriesDTO to save.
     * @param categoriesDTO the categoriesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoriesDTO,
     * or with status {@code 400 (Bad Request)} if the categoriesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the categoriesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoriesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CategoriesDTO>> partialUpdateCategories(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CategoriesDTO categoriesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Categories partially : {}, {}", id, categoriesDTO);
        if (categoriesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoriesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return categoriesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CategoriesDTO> result = categoriesService.partialUpdate(categoriesDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /categories} : get all the categories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping("/categories")
    public Mono<List<CategoriesDTO>> getAllCategories() {
        log.debug("REST request to get all Categories");
        return categoriesService.findAll().collectList();
    }

    /**
     * {@code GET  /categories} : get all the categories as a stream.
     * @return the {@link Flux} of categories.
     */
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CategoriesDTO> getAllCategoriesAsStream() {
        log.debug("REST request to get all Categories as a stream");
        return categoriesService.findAll();
    }

    /**
     * {@code GET  /categories/:id} : get the "id" categories.
     *
     * @param id the id of the categoriesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoriesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/categories/{id}")
    public Mono<ResponseEntity<CategoriesDTO>> getCategories(@PathVariable String id) {
        log.debug("REST request to get Categories : {}", id);
        Mono<CategoriesDTO> categoriesDTO = categoriesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categoriesDTO);
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" categories.
     *
     * @param id the id of the categoriesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/categories/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCategories(@PathVariable String id) {
        log.debug("REST request to delete Categories : {}", id);
        return categoriesService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
