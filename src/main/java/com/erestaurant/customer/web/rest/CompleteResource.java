package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.CompleteRepository;
import com.erestaurant.customer.service.CompleteService;
import com.erestaurant.customer.service.dto.CompleteDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.Complete}.
 */
@RestController
@RequestMapping("/api")
public class CompleteResource {

    private final Logger log = LoggerFactory.getLogger(CompleteResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerComplete";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompleteService completeService;

    private final CompleteRepository completeRepository;

    public CompleteResource(CompleteService completeService, CompleteRepository completeRepository) {
        this.completeService = completeService;
        this.completeRepository = completeRepository;
    }

    /**
     * {@code POST  /completes} : Create a new complete.
     *
     * @param completeDTO the completeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new completeDTO, or with status {@code 400 (Bad Request)} if the complete has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/completes")
    public Mono<ResponseEntity<CompleteDTO>> createComplete(@Valid @RequestBody CompleteDTO completeDTO) throws URISyntaxException {
        log.debug("REST request to save Complete : {}", completeDTO);
        if (completeDTO.getId() != null) {
            throw new BadRequestAlertException("A new complete cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return completeService
            .save(completeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/completes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /completes/:id} : Updates an existing complete.
     *
     * @param id the id of the completeDTO to save.
     * @param completeDTO the completeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated completeDTO,
     * or with status {@code 400 (Bad Request)} if the completeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the completeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/completes/{id}")
    public Mono<ResponseEntity<CompleteDTO>> updateComplete(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CompleteDTO completeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Complete : {}, {}", id, completeDTO);
        if (completeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, completeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return completeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return completeService
                    .update(completeDTO)
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
     * {@code PATCH  /completes/:id} : Partial updates given fields of an existing complete, field will ignore if it is null
     *
     * @param id the id of the completeDTO to save.
     * @param completeDTO the completeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated completeDTO,
     * or with status {@code 400 (Bad Request)} if the completeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the completeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the completeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/completes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CompleteDTO>> partialUpdateComplete(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CompleteDTO completeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Complete partially : {}, {}", id, completeDTO);
        if (completeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, completeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return completeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CompleteDTO> result = completeService.partialUpdate(completeDTO);

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
     * {@code GET  /completes} : get all the completes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of completes in body.
     */
    @GetMapping("/completes")
    public Mono<List<CompleteDTO>> getAllCompletes() {
        log.debug("REST request to get all Completes");
        return completeService.findAll().collectList();
    }

    /**
     * {@code GET  /completes} : get all the completes as a stream.
     * @return the {@link Flux} of completes.
     */
    @GetMapping(value = "/completes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CompleteDTO> getAllCompletesAsStream() {
        log.debug("REST request to get all Completes as a stream");
        return completeService.findAll();
    }

    /**
     * {@code GET  /completes/:id} : get the "id" complete.
     *
     * @param id the id of the completeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the completeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/completes/{id}")
    public Mono<ResponseEntity<CompleteDTO>> getComplete(@PathVariable String id) {
        log.debug("REST request to get Complete : {}", id);
        Mono<CompleteDTO> completeDTO = completeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(completeDTO);
    }

    /**
     * {@code DELETE  /completes/:id} : delete the "id" complete.
     *
     * @param id the id of the completeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/completes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteComplete(@PathVariable String id) {
        log.debug("REST request to delete Complete : {}", id);
        return completeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
