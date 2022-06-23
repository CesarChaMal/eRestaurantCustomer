package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.Cancel;
import com.erestaurant.customer.repository.CancelRepository;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.service.dto.CancelDTO;
import com.erestaurant.customer.service.mapper.CancelMapper;
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
 * Integration tests for the {@link CancelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CancelResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/cancels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CancelRepository cancelRepository;

    @Autowired
    private CancelMapper cancelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cancel cancel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cancel createEntity(EntityManager em) {
        Cancel cancel = new Cancel().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return cancel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cancel createUpdatedEntity(EntityManager em) {
        Cancel cancel = new Cancel().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return cancel;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cancel.class).block();
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
        cancel = createEntity(em);
    }

    @Test
    void createCancel() throws Exception {
        int databaseSizeBeforeCreate = cancelRepository.findAll().collectList().block().size();
        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeCreate + 1);
        Cancel testCancel = cancelList.get(cancelList.size() - 1);
        assertThat(testCancel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCancel.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void createCancelWithExistingId() throws Exception {
        // Create the Cancel with an existing ID
        cancel.setId("existing_id");
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        int databaseSizeBeforeCreate = cancelRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCancelsAsStream() {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        List<Cancel> cancelList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CancelDTO.class)
            .getResponseBody()
            .map(cancelMapper::toEntity)
            .filter(cancel::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cancelList).isNotNull();
        assertThat(cancelList).hasSize(1);
        Cancel testCancel = cancelList.get(0);
        assertThat(testCancel.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCancel.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllCancels() {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        // Get all the cancelList
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
            .value(hasItem(cancel.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getCancel() {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        // Get the cancel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cancel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cancel.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingCancel() {
        // Get the cancel
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCancel() throws Exception {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();

        // Update the cancel
        Cancel updatedCancel = cancelRepository.findById(cancel.getId()).block();
        updatedCancel.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        CancelDTO cancelDTO = cancelMapper.toDto(updatedCancel);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cancelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
        Cancel testCancel = cancelList.get(cancelList.size() - 1);
        assertThat(testCancel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCancel.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void putNonExistingCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cancelDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCancelWithPatch() throws Exception {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();

        // Update the cancel using partial update
        Cancel partialUpdatedCancel = new Cancel();
        partialUpdatedCancel.setId(cancel.getId());

        partialUpdatedCancel.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCancel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCancel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
        Cancel testCancel = cancelList.get(cancelList.size() - 1);
        assertThat(testCancel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCancel.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void fullUpdateCancelWithPatch() throws Exception {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();

        // Update the cancel using partial update
        Cancel partialUpdatedCancel = new Cancel();
        partialUpdatedCancel.setId(cancel.getId());

        partialUpdatedCancel.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCancel.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCancel))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
        Cancel testCancel = cancelList.get(cancelList.size() - 1);
        assertThat(testCancel.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCancel.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cancelDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCancel() throws Exception {
        int databaseSizeBeforeUpdate = cancelRepository.findAll().collectList().block().size();
        cancel.setId(UUID.randomUUID().toString());

        // Create the Cancel
        CancelDTO cancelDTO = cancelMapper.toDto(cancel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cancelDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cancel in the database
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCancel() {
        // Initialize the database
        cancel.setId(UUID.randomUUID().toString());
        cancelRepository.save(cancel).block();

        int databaseSizeBeforeDelete = cancelRepository.findAll().collectList().block().size();

        // Delete the cancel
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cancel.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cancel> cancelList = cancelRepository.findAll().collectList().block();
        assertThat(cancelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
