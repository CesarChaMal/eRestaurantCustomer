package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.StateRepository;
import com.erestaurant.customer.service.StateService;
import com.erestaurant.customer.service.dto.StateDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.State}.
 */
@RestController
@RequestMapping("/api")
public class StateResource {

    private final Logger log = LoggerFactory.getLogger(StateResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerState";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StateService stateService;

    private final StateRepository stateRepository;

    public StateResource(StateService stateService, StateRepository stateRepository) {
        this.stateService = stateService;
        this.stateRepository = stateRepository;
    }

    /**
     * {@code POST  /states} : Create a new state.
     *
     * @param stateDTO the stateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stateDTO, or with status {@code 400 (Bad Request)} if the state has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/states")
    public Mono<ResponseEntity<StateDTO>> createState(@Valid @RequestBody StateDTO stateDTO) throws URISyntaxException {
        log.debug("REST request to save State : {}", stateDTO);
        if (stateDTO.getId() != null) {
            throw new BadRequestAlertException("A new state cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stateService
            .save(stateDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/states/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /states/:id} : Updates an existing state.
     *
     * @param id the id of the stateDTO to save.
     * @param stateDTO the stateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stateDTO,
     * or with status {@code 400 (Bad Request)} if the stateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/states/{id}")
    public Mono<ResponseEntity<StateDTO>> updateState(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody StateDTO stateDTO
    ) throws URISyntaxException {
        log.debug("REST request to update State : {}, {}", id, stateDTO);
        if (stateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stateRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return stateService
                    .update(stateDTO)
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
     * {@code PATCH  /states/:id} : Partial updates given fields of an existing state, field will ignore if it is null
     *
     * @param id the id of the stateDTO to save.
     * @param stateDTO the stateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stateDTO,
     * or with status {@code 400 (Bad Request)} if the stateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/states/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StateDTO>> partialUpdateState(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody StateDTO stateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update State partially : {}, {}", id, stateDTO);
        if (stateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stateRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StateDTO> result = stateService.partialUpdate(stateDTO);

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
     * {@code GET  /states} : get all the states.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of states in body.
     */
    @GetMapping("/states")
    public Mono<List<StateDTO>> getAllStates() {
        log.debug("REST request to get all States");
        return stateService.findAll().collectList();
    }

    /**
     * {@code GET  /states} : get all the states as a stream.
     * @return the {@link Flux} of states.
     */
    @GetMapping(value = "/states", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StateDTO> getAllStatesAsStream() {
        log.debug("REST request to get all States as a stream");
        return stateService.findAll();
    }

    /**
     * {@code GET  /states/:id} : get the "id" state.
     *
     * @param id the id of the stateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/states/{id}")
    public Mono<ResponseEntity<StateDTO>> getState(@PathVariable String id) {
        log.debug("REST request to get State : {}", id);
        Mono<StateDTO> stateDTO = stateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stateDTO);
    }

    /**
     * {@code DELETE  /states/:id} : delete the "id" state.
     *
     * @param id the id of the stateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/states/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteState(@PathVariable String id) {
        log.debug("REST request to delete State : {}", id);
        return stateService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
