package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.OnHoldRepository;
import com.erestaurant.customer.service.OnHoldService;
import com.erestaurant.customer.service.dto.OnHoldDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.OnHold}.
 */
@RestController
@RequestMapping("/api")
public class OnHoldResource {

    private final Logger log = LoggerFactory.getLogger(OnHoldResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerOnHold";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OnHoldService onHoldService;

    private final OnHoldRepository onHoldRepository;

    public OnHoldResource(OnHoldService onHoldService, OnHoldRepository onHoldRepository) {
        this.onHoldService = onHoldService;
        this.onHoldRepository = onHoldRepository;
    }

    /**
     * {@code POST  /on-holds} : Create a new onHold.
     *
     * @param onHoldDTO the onHoldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new onHoldDTO, or with status {@code 400 (Bad Request)} if the onHold has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/on-holds")
    public Mono<ResponseEntity<OnHoldDTO>> createOnHold(@Valid @RequestBody OnHoldDTO onHoldDTO) throws URISyntaxException {
        log.debug("REST request to save OnHold : {}", onHoldDTO);
        if (onHoldDTO.getId() != null) {
            throw new BadRequestAlertException("A new onHold cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return onHoldService
            .save(onHoldDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/on-holds/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /on-holds/:id} : Updates an existing onHold.
     *
     * @param id the id of the onHoldDTO to save.
     * @param onHoldDTO the onHoldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated onHoldDTO,
     * or with status {@code 400 (Bad Request)} if the onHoldDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the onHoldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/on-holds/{id}")
    public Mono<ResponseEntity<OnHoldDTO>> updateOnHold(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody OnHoldDTO onHoldDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OnHold : {}, {}", id, onHoldDTO);
        if (onHoldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, onHoldDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return onHoldRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return onHoldService
                    .update(onHoldDTO)
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
     * {@code PATCH  /on-holds/:id} : Partial updates given fields of an existing onHold, field will ignore if it is null
     *
     * @param id the id of the onHoldDTO to save.
     * @param onHoldDTO the onHoldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated onHoldDTO,
     * or with status {@code 400 (Bad Request)} if the onHoldDTO is not valid,
     * or with status {@code 404 (Not Found)} if the onHoldDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the onHoldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/on-holds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OnHoldDTO>> partialUpdateOnHold(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody OnHoldDTO onHoldDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OnHold partially : {}, {}", id, onHoldDTO);
        if (onHoldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, onHoldDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return onHoldRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OnHoldDTO> result = onHoldService.partialUpdate(onHoldDTO);

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
     * {@code GET  /on-holds} : get all the onHolds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of onHolds in body.
     */
    @GetMapping("/on-holds")
    public Mono<List<OnHoldDTO>> getAllOnHolds() {
        log.debug("REST request to get all OnHolds");
        return onHoldService.findAll().collectList();
    }

    /**
     * {@code GET  /on-holds} : get all the onHolds as a stream.
     * @return the {@link Flux} of onHolds.
     */
    @GetMapping(value = "/on-holds", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OnHoldDTO> getAllOnHoldsAsStream() {
        log.debug("REST request to get all OnHolds as a stream");
        return onHoldService.findAll();
    }

    /**
     * {@code GET  /on-holds/:id} : get the "id" onHold.
     *
     * @param id the id of the onHoldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the onHoldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/on-holds/{id}")
    public Mono<ResponseEntity<OnHoldDTO>> getOnHold(@PathVariable String id) {
        log.debug("REST request to get OnHold : {}", id);
        Mono<OnHoldDTO> onHoldDTO = onHoldService.findOne(id);
        return ResponseUtil.wrapOrNotFound(onHoldDTO);
    }

    /**
     * {@code DELETE  /on-holds/:id} : delete the "id" onHold.
     *
     * @param id the id of the onHoldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/on-holds/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOnHold(@PathVariable String id) {
        log.debug("REST request to delete OnHold : {}", id);
        return onHoldService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
