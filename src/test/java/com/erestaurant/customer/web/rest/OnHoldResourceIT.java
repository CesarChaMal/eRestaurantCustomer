package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.OnHold;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.OnHoldRepository;
import com.erestaurant.customer.service.dto.OnHoldDTO;
import com.erestaurant.customer.service.mapper.OnHoldMapper;
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
 * Integration tests for the {@link OnHoldResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OnHoldResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/on-holds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private OnHoldRepository onHoldRepository;

    @Autowired
    private OnHoldMapper onHoldMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OnHold onHold;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OnHold createEntity(EntityManager em) {
        OnHold onHold = new OnHold().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return onHold;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OnHold createUpdatedEntity(EntityManager em) {
        OnHold onHold = new OnHold().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return onHold;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OnHold.class).block();
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
        onHold = createEntity(em);
    }

    @Test
    void createOnHold() throws Exception {
        int databaseSizeBeforeCreate = onHoldRepository.findAll().collectList().block().size();
        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeCreate + 1);
        OnHold testOnHold = onHoldList.get(onHoldList.size() - 1);
        assertThat(testOnHold.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOnHold.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void createOnHoldWithExistingId() throws Exception {
        // Create the OnHold with an existing ID
        onHold.setId("existing_id");
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        int databaseSizeBeforeCreate = onHoldRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllOnHoldsAsStream() {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        List<OnHold> onHoldList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OnHoldDTO.class)
            .getResponseBody()
            .map(onHoldMapper::toEntity)
            .filter(onHold::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(onHoldList).isNotNull();
        assertThat(onHoldList).hasSize(1);
        OnHold testOnHold = onHoldList.get(0);
        assertThat(testOnHold.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOnHold.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllOnHolds() {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        // Get all the onHoldList
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
            .value(hasItem(onHold.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getOnHold() {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        // Get the onHold
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, onHold.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(onHold.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingOnHold() {
        // Get the onHold
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOnHold() throws Exception {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();

        // Update the onHold
        OnHold updatedOnHold = onHoldRepository.findById(onHold.getId()).block();
        updatedOnHold.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(updatedOnHold);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, onHoldDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
        OnHold testOnHold = onHoldList.get(onHoldList.size() - 1);
        assertThat(testOnHold.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOnHold.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void putNonExistingOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, onHoldDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOnHoldWithPatch() throws Exception {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();

        // Update the onHold using partial update
        OnHold partialUpdatedOnHold = new OnHold();
        partialUpdatedOnHold.setId(onHold.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOnHold.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOnHold))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
        OnHold testOnHold = onHoldList.get(onHoldList.size() - 1);
        assertThat(testOnHold.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOnHold.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void fullUpdateOnHoldWithPatch() throws Exception {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();

        // Update the onHold using partial update
        OnHold partialUpdatedOnHold = new OnHold();
        partialUpdatedOnHold.setId(onHold.getId());

        partialUpdatedOnHold.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOnHold.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOnHold))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
        OnHold testOnHold = onHoldList.get(onHoldList.size() - 1);
        assertThat(testOnHold.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOnHold.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, onHoldDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOnHold() throws Exception {
        int databaseSizeBeforeUpdate = onHoldRepository.findAll().collectList().block().size();
        onHold.setId(UUID.randomUUID().toString());

        // Create the OnHold
        OnHoldDTO onHoldDTO = onHoldMapper.toDto(onHold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(onHoldDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OnHold in the database
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOnHold() {
        // Initialize the database
        onHold.setId(UUID.randomUUID().toString());
        onHoldRepository.save(onHold).block();

        int databaseSizeBeforeDelete = onHoldRepository.findAll().collectList().block().size();

        // Delete the onHold
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, onHold.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OnHold> onHoldList = onHoldRepository.findAll().collectList().block();
        assertThat(onHoldList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
