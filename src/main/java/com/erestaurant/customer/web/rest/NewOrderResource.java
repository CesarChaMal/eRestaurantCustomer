package com.erestaurant.customer.web.rest;

import com.erestaurant.customer.repository.NewOrderRepository;
import com.erestaurant.customer.service.NewOrderService;
import com.erestaurant.customer.service.dto.NewOrderDTO;
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
 * REST controller for managing {@link com.erestaurant.customer.domain.NewOrder}.
 */
@RestController
@RequestMapping("/api")
public class NewOrderResource {

    private final Logger log = LoggerFactory.getLogger(NewOrderResource.class);

    private static final String ENTITY_NAME = "eRestaurantCustomerNewOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NewOrderService newOrderService;

    private final NewOrderRepository newOrderRepository;

    public NewOrderResource(NewOrderService newOrderService, NewOrderRepository newOrderRepository) {
        this.newOrderService = newOrderService;
        this.newOrderRepository = newOrderRepository;
    }

    /**
     * {@code POST  /new-orders} : Create a new newOrder.
     *
     * @param newOrderDTO the newOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new newOrderDTO, or with status {@code 400 (Bad Request)} if the newOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/new-orders")
    public Mono<ResponseEntity<NewOrderDTO>> createNewOrder(@Valid @RequestBody NewOrderDTO newOrderDTO) throws URISyntaxException {
        log.debug("REST request to save NewOrder : {}", newOrderDTO);
        if (newOrderDTO.getId() != null) {
            throw new BadRequestAlertException("A new newOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return newOrderService
            .save(newOrderDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/new-orders/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /new-orders/:id} : Updates an existing newOrder.
     *
     * @param id the id of the newOrderDTO to save.
     * @param newOrderDTO the newOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated newOrderDTO,
     * or with status {@code 400 (Bad Request)} if the newOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the newOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/new-orders/{id}")
    public Mono<ResponseEntity<NewOrderDTO>> updateNewOrder(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody NewOrderDTO newOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NewOrder : {}, {}", id, newOrderDTO);
        if (newOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, newOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return newOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return newOrderService
                    .update(newOrderDTO)
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
     * {@code PATCH  /new-orders/:id} : Partial updates given fields of an existing newOrder, field will ignore if it is null
     *
     * @param id the id of the newOrderDTO to save.
     * @param newOrderDTO the newOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated newOrderDTO,
     * or with status {@code 400 (Bad Request)} if the newOrderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the newOrderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the newOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/new-orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<NewOrderDTO>> partialUpdateNewOrder(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody NewOrderDTO newOrderDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NewOrder partially : {}, {}", id, newOrderDTO);
        if (newOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, newOrderDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return newOrderRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<NewOrderDTO> result = newOrderService.partialUpdate(newOrderDTO);

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
     * {@code GET  /new-orders} : get all the newOrders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of newOrders in body.
     */
    @GetMapping("/new-orders")
    public Mono<List<NewOrderDTO>> getAllNewOrders() {
        log.debug("REST request to get all NewOrders");
        return newOrderService.findAll().collectList();
    }

    /**
     * {@code GET  /new-orders} : get all the newOrders as a stream.
     * @return the {@link Flux} of newOrders.
     */
    @GetMapping(value = "/new-orders", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<NewOrderDTO> getAllNewOrdersAsStream() {
        log.debug("REST request to get all NewOrders as a stream");
        return newOrderService.findAll();
    }

    /**
     * {@code GET  /new-orders/:id} : get the "id" newOrder.
     *
     * @param id the id of the newOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the newOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/new-orders/{id}")
    public Mono<ResponseEntity<NewOrderDTO>> getNewOrder(@PathVariable String id) {
        log.debug("REST request to get NewOrder : {}", id);
        Mono<NewOrderDTO> newOrderDTO = newOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(newOrderDTO);
    }

    /**
     * {@code DELETE  /new-orders/:id} : delete the "id" newOrder.
     *
     * @param id the id of the newOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/new-orders/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNewOrder(@PathVariable String id) {
        log.debug("REST request to delete NewOrder : {}", id);
        return newOrderService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
