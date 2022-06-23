package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.NewOrder;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.NewOrderRepository;
import com.erestaurant.customer.service.dto.NewOrderDTO;
import com.erestaurant.customer.service.mapper.NewOrderMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link NewOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NewOrderResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/new-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private NewOrderRepository newOrderRepository;

    @Autowired
    private NewOrderMapper newOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private NewOrder newOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NewOrder createEntity(EntityManager em) {
        NewOrder newOrder = new NewOrder().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return newOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NewOrder createUpdatedEntity(EntityManager em) {
        NewOrder newOrder = new NewOrder().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return newOrder;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(NewOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        newOrder = createEntity(em);
    }

    @Test
    void createNewOrder() throws Exception {
        int databaseSizeBeforeCreate = newOrderRepository.findAll().collectList().block().size();
        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeCreate + 1);
        NewOrder testNewOrder = newOrderList.get(newOrderList.size() - 1);
        assertThat(testNewOrder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNewOrder.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void createNewOrderWithExistingId() throws Exception {
        // Create the NewOrder with an existing ID
        newOrder.setId("existing_id");
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        int databaseSizeBeforeCreate = newOrderRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllNewOrdersAsStream() {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        List<NewOrder> newOrderList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(NewOrderDTO.class)
            .getResponseBody()
            .map(newOrderMapper::toEntity)
            .filter(newOrder::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(newOrderList).isNotNull();
        assertThat(newOrderList).hasSize(1);
        NewOrder testNewOrder = newOrderList.get(0);
        assertThat(testNewOrder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNewOrder.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllNewOrders() {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        // Get all the newOrderList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(newOrder.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNewOrder() {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        // Get the newOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, newOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(newOrder.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingNewOrder() {
        // Get the newOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNewOrder() throws Exception {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();

        // Update the newOrder
        NewOrder updatedNewOrder = newOrderRepository.findById(newOrder.getId()).block();
        updatedNewOrder.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(updatedNewOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, newOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
        NewOrder testNewOrder = newOrderList.get(newOrderList.size() - 1);
        assertThat(testNewOrder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNewOrder.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void putNonExistingNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, newOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNewOrderWithPatch() throws Exception {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();

        // Update the newOrder using partial update
        NewOrder partialUpdatedNewOrder = new NewOrder();
        partialUpdatedNewOrder.setId(newOrder.getId());

        partialUpdatedNewOrder.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNewOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNewOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
        NewOrder testNewOrder = newOrderList.get(newOrderList.size() - 1);
        assertThat(testNewOrder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNewOrder.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void fullUpdateNewOrderWithPatch() throws Exception {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();

        // Update the newOrder using partial update
        NewOrder partialUpdatedNewOrder = new NewOrder();
        partialUpdatedNewOrder.setId(newOrder.getId());

        partialUpdatedNewOrder.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNewOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNewOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
        NewOrder testNewOrder = newOrderList.get(newOrderList.size() - 1);
        assertThat(testNewOrder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNewOrder.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, newOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNewOrder() throws Exception {
        int databaseSizeBeforeUpdate = newOrderRepository.findAll().collectList().block().size();
        newOrder.setId(UUID.randomUUID().toString());

        // Create the NewOrder
        NewOrderDTO newOrderDTO = newOrderMapper.toDto(newOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(newOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NewOrder in the database
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNewOrder() {
        // Initialize the database
        newOrder.setId(UUID.randomUUID().toString());
        newOrderRepository.save(newOrder).block();

        int databaseSizeBeforeDelete = newOrderRepository.findAll().collectList().block().size();

        // Delete the newOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, newOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<NewOrder> newOrderList = newOrderRepository.findAll().collectList().block();
        assertThat(newOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
