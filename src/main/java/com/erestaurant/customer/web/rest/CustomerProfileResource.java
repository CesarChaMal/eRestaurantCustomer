package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.CustomerProfileRepository;
import com.erestaurant.customer.service.CustomerProfileService;
import com.erestaurant.customer.service.dto.CustomerProfileDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.CustomerProfile}.
 */
@RestController
@RequestMapping("/api")
public class CustomerProfileResource {

    private final Logger log = LoggerFactory.getLogger(CustomerProfileResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerCustomerProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerProfileService customerProfileService;

    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileResource(CustomerProfileService customerProfileService, CustomerProfileRepository customerProfileRepository) {
        this.customerProfileService = customerProfileService;
        this.customerProfileRepository = customerProfileRepository;
    }

    /**
     * {@code POST  /customer-profiles} : Create a new customerProfile.
     *
     * @param customerProfileDTO the customerProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerProfileDTO, or with status {@code 400 (Bad Request)} if the customerProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-profiles")
    public Mono<ResponseEntity<CustomerProfileDTO>> createCustomerProfile(@Valid @RequestBody CustomerProfileDTO customerProfileDTO)
        throws URISyntaxException {
        log.debug("REST request to save CustomerProfile : {}", customerProfileDTO);
        if (customerProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new customerProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return customerProfileService
            .save(customerProfileDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/customer-profiles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /customer-profiles/:id} : Updates an existing customerProfile.
     *
     * @param id the id of the customerProfileDTO to save.
     * @param customerProfileDTO the customerProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerProfileDTO,
     * or with status {@code 400 (Bad Request)} if the customerProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customer-profiles/{id}")
    public Mono<ResponseEntity<CustomerProfileDTO>> updateCustomerProfile(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CustomerProfileDTO customerProfileDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CustomerProfile : {}, {}", id, customerProfileDTO);
        if (customerProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return customerProfileService
                    .update(customerProfileDTO)
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
     * {@code PATCH  /customer-profiles/:id} : Partial updates given fields of an existing customerProfile, field will ignore if it is null
     *
     * @param id the id of the customerProfileDTO to save.
     * @param customerProfileDTO the customerProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerProfileDTO,
     * or with status {@code 400 (Bad Request)} if the customerProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customerProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customerProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/customer-profiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CustomerProfileDTO>> partialUpdateCustomerProfile(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CustomerProfileDTO customerProfileDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CustomerProfile partially : {}, {}", id, customerProfileDTO);
        if (customerProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customerProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return customerProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CustomerProfileDTO> result = customerProfileService.partialUpdate(customerProfileDTO);

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
     * {@code GET  /customer-profiles} : get all the customerProfiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerProfiles in body.
     */
    @GetMapping("/customer-profiles")
    public Mono<List<CustomerProfileDTO>> getAllCustomerProfiles() {
        log.debug("REST request to get all CustomerProfiles");
        return customerProfileService.findAll().collectList();
    }

    /**
     * {@code GET  /customer-profiles} : get all the customerProfiles as a stream.
     * @return the {@link Flux} of customerProfiles.
     */
    @GetMapping(value = "/customer-profiles", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CustomerProfileDTO> getAllCustomerProfilesAsStream() {
        log.debug("REST request to get all CustomerProfiles as a stream");
        return customerProfileService.findAll();
    }

    /**
     * {@code GET  /customer-profiles/:id} : get the "id" customerProfile.
     *
     * @param id the id of the customerProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customer-profiles/{id}")
    public Mono<ResponseEntity<CustomerProfileDTO>> getCustomerProfile(@PathVariable String id) {
        log.debug("REST request to get CustomerProfile : {}", id);
        Mono<CustomerProfileDTO> customerProfileDTO = customerProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerProfileDTO);
    }

    /**
     * {@code DELETE  /customer-profiles/:id} : delete the "id" customerProfile.
     *
     * @param id the id of the customerProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customer-profiles/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCustomerProfile(@PathVariable String id) {
        log.debug("REST request to delete CustomerProfile : {}", id);
        return customerProfileService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
