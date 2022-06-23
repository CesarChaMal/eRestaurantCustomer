package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.CloseRepository;
import com.erestaurant.customer.service.CloseService;
import com.erestaurant.customer.service.dto.CloseDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.Close}.
 */
@RestController
@RequestMapping("/api")
public class CloseResource {

    private final Logger log = LoggerFactory.getLogger(CloseResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerClose";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CloseService closeService;

    private final CloseRepository closeRepository;

    public CloseResource(CloseService closeService, CloseRepository closeRepository) {
        this.closeService = closeService;
        this.closeRepository = closeRepository;
    }

    /**
     * {@code POST  /closes} : Create a new close.
     *
     * @param closeDTO the closeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new closeDTO, or with status {@code 400 (Bad Request)} if the close has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/closes")
    public Mono<ResponseEntity<CloseDTO>> createClose(@Valid @RequestBody CloseDTO closeDTO) throws URISyntaxException {
        log.debug("REST request to save Close : {}", closeDTO);
        if (closeDTO.getId() != null) {
            throw new BadRequestAlertException("A new close cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return closeService
            .save(closeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/closes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /closes/:id} : Updates an existing close.
     *
     * @param id the id of the closeDTO to save.
     * @param closeDTO the closeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated closeDTO,
     * or with status {@code 400 (Bad Request)} if the closeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the closeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/closes/{id}")
    public Mono<ResponseEntity<CloseDTO>> updateClose(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CloseDTO closeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Close : {}, {}", id, closeDTO);
        if (closeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, closeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return closeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return closeService
                    .update(closeDTO)
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
     * {@code PATCH  /closes/:id} : Partial updates given fields of an existing close, field will ignore if it is null
     *
     * @param id the id of the closeDTO to save.
     * @param closeDTO the closeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated closeDTO,
     * or with status {@code 400 (Bad Request)} if the closeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the closeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the closeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/closes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CloseDTO>> partialUpdateClose(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CloseDTO closeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Close partially : {}, {}", id, closeDTO);
        if (closeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, closeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return closeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CloseDTO> result = closeService.partialUpdate(closeDTO);

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
     * {@code GET  /closes} : get all the closes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of closes in body.
     */
    @GetMapping("/closes")
    public Mono<List<CloseDTO>> getAllCloses() {
        log.debug("REST request to get all Closes");
        return closeService.findAll().collectList();
    }

    /**
     * {@code GET  /closes} : get all the closes as a stream.
     * @return the {@link Flux} of closes.
     */
    @GetMapping(value = "/closes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CloseDTO> getAllClosesAsStream() {
        log.debug("REST request to get all Closes as a stream");
        return closeService.findAll();
    }

    /**
     * {@code GET  /closes/:id} : get the "id" close.
     *
     * @param id the id of the closeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the closeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/closes/{id}")
    public Mono<ResponseEntity<CloseDTO>> getClose(@PathVariable String id) {
        log.debug("REST request to get Close : {}", id);
        Mono<CloseDTO> closeDTO = closeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(closeDTO);
    }

    /**
     * {@code DELETE  /closes/:id} : delete the "id" close.
     *
     * @param id the id of the closeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/closes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteClose(@PathVariable String id) {
        log.debug("REST request to delete Close : {}", id);
        return closeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
