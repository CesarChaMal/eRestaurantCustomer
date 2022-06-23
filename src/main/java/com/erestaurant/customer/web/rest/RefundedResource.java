package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.RefundedRepository;
import com.erestaurant.customer.service.RefundedService;
import com.erestaurant.customer.service.dto.RefundedDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.Refunded}.
 */
@RestController
@RequestMapping("/api")
public class RefundedResource {

    private final Logger log = LoggerFactory.getLogger(RefundedResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerRefunded";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RefundedService refundedService;

    private final RefundedRepository refundedRepository;

    public RefundedResource(RefundedService refundedService, RefundedRepository refundedRepository) {
        this.refundedService = refundedService;
        this.refundedRepository = refundedRepository;
    }

    /**
     * {@code POST  /refundeds} : Create a new refunded.
     *
     * @param refundedDTO the refundedDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new refundedDTO, or with status {@code 400 (Bad Request)} if the refunded has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/refundeds")
    public Mono<ResponseEntity<RefundedDTO>> createRefunded(@Valid @RequestBody RefundedDTO refundedDTO) throws URISyntaxException {
        log.debug("REST request to save Refunded : {}", refundedDTO);
        if (refundedDTO.getId() != null) {
            throw new BadRequestAlertException("A new refunded cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return refundedService
            .save(refundedDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/refundeds/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /refundeds/:id} : Updates an existing refunded.
     *
     * @param id the id of the refundedDTO to save.
     * @param refundedDTO the refundedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundedDTO,
     * or with status {@code 400 (Bad Request)} if the refundedDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the refundedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/refundeds/{id}")
    public Mono<ResponseEntity<RefundedDTO>> updateRefunded(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody RefundedDTO refundedDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Refunded : {}, {}", id, refundedDTO);
        if (refundedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return refundedRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return refundedService
                    .update(refundedDTO)
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
     * {@code PATCH  /refundeds/:id} : Partial updates given fields of an existing refunded, field will ignore if it is null
     *
     * @param id the id of the refundedDTO to save.
     * @param refundedDTO the refundedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundedDTO,
     * or with status {@code 400 (Bad Request)} if the refundedDTO is not valid,
     * or with status {@code 404 (Not Found)} if the refundedDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the refundedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/refundeds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RefundedDTO>> partialUpdateRefunded(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody RefundedDTO refundedDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Refunded partially : {}, {}", id, refundedDTO);
        if (refundedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return refundedRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RefundedDTO> result = refundedService.partialUpdate(refundedDTO);

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
     * {@code GET  /refundeds} : get all the refundeds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of refundeds in body.
     */
    @GetMapping("/refundeds")
    public Mono<List<RefundedDTO>> getAllRefundeds() {
        log.debug("REST request to get all Refundeds");
        return refundedService.findAll().collectList();
    }

    /**
     * {@code GET  /refundeds} : get all the refundeds as a stream.
     * @return the {@link Flux} of refundeds.
     */
    @GetMapping(value = "/refundeds", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RefundedDTO> getAllRefundedsAsStream() {
        log.debug("REST request to get all Refundeds as a stream");
        return refundedService.findAll();
    }

    /**
     * {@code GET  /refundeds/:id} : get the "id" refunded.
     *
     * @param id the id of the refundedDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the refundedDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/refundeds/{id}")
    public Mono<ResponseEntity<RefundedDTO>> getRefunded(@PathVariable String id) {
        log.debug("REST request to get Refunded : {}", id);
        Mono<RefundedDTO> refundedDTO = refundedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(refundedDTO);
    }

    /**
     * {@code DELETE  /refundeds/:id} : delete the "id" refunded.
     *
     * @param id the id of the refundedDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/refundeds/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRefunded(@PathVariable String id) {
        log.debug("REST request to delete Refunded : {}", id);
        return refundedService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
