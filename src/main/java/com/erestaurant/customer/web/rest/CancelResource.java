package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.CancelRepository;
import com.erestaurant.customer.service.CancelService;
import com.erestaurant.customer.service.dto.CancelDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.Cancel}.
 */
@RestController
@RequestMapping("/api")
public class CancelResource {

    private final Logger log = LoggerFactory.getLogger(CancelResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerCancel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CancelService cancelService;

    private final CancelRepository cancelRepository;

    public CancelResource(CancelService cancelService, CancelRepository cancelRepository) {
        this.cancelService = cancelService;
        this.cancelRepository = cancelRepository;
    }

    /**
     * {@code POST  /cancels} : Create a new cancel.
     *
     * @param cancelDTO the cancelDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cancelDTO, or with status {@code 400 (Bad Request)} if the cancel has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cancels")
    public Mono<ResponseEntity<CancelDTO>> createCancel(@Valid @RequestBody CancelDTO cancelDTO) throws URISyntaxException {
        log.debug("REST request to save Cancel : {}", cancelDTO);
        if (cancelDTO.getId() != null) {
            throw new BadRequestAlertException("A new cancel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cancelService
            .save(cancelDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/cancels/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /cancels/:id} : Updates an existing cancel.
     *
     * @param id the id of the cancelDTO to save.
     * @param cancelDTO the cancelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cancelDTO,
     * or with status {@code 400 (Bad Request)} if the cancelDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cancelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cancels/{id}")
    public Mono<ResponseEntity<CancelDTO>> updateCancel(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CancelDTO cancelDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Cancel : {}, {}", id, cancelDTO);
        if (cancelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cancelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cancelRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return cancelService
                    .update(cancelDTO)
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
     * {@code PATCH  /cancels/:id} : Partial updates given fields of an existing cancel, field will ignore if it is null
     *
     * @param id the id of the cancelDTO to save.
     * @param cancelDTO the cancelDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cancelDTO,
     * or with status {@code 400 (Bad Request)} if the cancelDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cancelDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cancelDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cancels/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CancelDTO>> partialUpdateCancel(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CancelDTO cancelDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cancel partially : {}, {}", id, cancelDTO);
        if (cancelDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cancelDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cancelRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CancelDTO> result = cancelService.partialUpdate(cancelDTO);

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
     * {@code GET  /cancels} : get all the cancels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cancels in body.
     */
    @GetMapping("/cancels")
    public Mono<List<CancelDTO>> getAllCancels() {
        log.debug("REST request to get all Cancels");
        return cancelService.findAll().collectList();
    }

    /**
     * {@code GET  /cancels} : get all the cancels as a stream.
     * @return the {@link Flux} of cancels.
     */
    @GetMapping(value = "/cancels", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CancelDTO> getAllCancelsAsStream() {
        log.debug("REST request to get all Cancels as a stream");
        return cancelService.findAll();
    }

    /**
     * {@code GET  /cancels/:id} : get the "id" cancel.
     *
     * @param id the id of the cancelDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cancelDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cancels/{id}")
    public Mono<ResponseEntity<CancelDTO>> getCancel(@PathVariable String id) {
        log.debug("REST request to get Cancel : {}", id);
        Mono<CancelDTO> cancelDTO = cancelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cancelDTO);
    }

    /**
     * {@code DELETE  /cancels/:id} : delete the "id" cancel.
     *
     * @param id the id of the cancelDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cancels/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCancel(@PathVariable String id) {
        log.debug("REST request to delete Cancel : {}", id);
        return cancelService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
