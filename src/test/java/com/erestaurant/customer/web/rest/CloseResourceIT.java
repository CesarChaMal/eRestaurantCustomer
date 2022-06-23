package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.Close;
import com.erestaurant.customer.repository.CloseRepository;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.service.dto.CloseDTO;
import com.erestaurant.customer.service.mapper.CloseMapper;
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
 * Integration tests for the {@link CloseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CloseResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/closes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CloseRepository closeRepository;

    @Autowired
    private CloseMapper closeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Close close;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Close createEntity(EntityManager em) {
        Close close = new Close().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return close;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Close createUpdatedEntity(EntityManager em) {
        Close close = new Close().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return close;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Close.class).block();
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
        close = createEntity(em);
    }

    @Test
    void createClose() throws Exception {
        int databaseSizeBeforeCreate = closeRepository.findAll().collectList().block().size();
        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeCreate + 1);
        Close testClose = closeList.get(closeList.size() - 1);
        assertThat(testClose.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClose.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void createCloseWithExistingId() throws Exception {
        // Create the Close with an existing ID
        close.setId("existing_id");
        CloseDTO closeDTO = closeMapper.toDto(close);

        int databaseSizeBeforeCreate = closeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllClosesAsStream() {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        List<Close> closeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CloseDTO.class)
            .getResponseBody()
            .map(closeMapper::toEntity)
            .filter(close::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(closeList).isNotNull();
        assertThat(closeList).hasSize(1);
        Close testClose = closeList.get(0);
        assertThat(testClose.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClose.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllCloses() {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        // Get all the closeList
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
            .value(hasItem(close.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getClose() {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        // Get the close
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, close.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(close.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingClose() {
        // Get the close
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewClose() throws Exception {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();

        // Update the close
        Close updatedClose = closeRepository.findById(close.getId()).block();
        updatedClose.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        CloseDTO closeDTO = closeMapper.toDto(updatedClose);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, closeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
        Close testClose = closeList.get(closeList.size() - 1);
        assertThat(testClose.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testClose.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void putNonExistingClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, closeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCloseWithPatch() throws Exception {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();

        // Update the close using partial update
        Close partialUpdatedClose = new Close();
        partialUpdatedClose.setId(close.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClose.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClose))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
        Close testClose = closeList.get(closeList.size() - 1);
        assertThat(testClose.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClose.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void fullUpdateCloseWithPatch() throws Exception {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();

        // Update the close using partial update
        Close partialUpdatedClose = new Close();
        partialUpdatedClose.setId(close.getId());

        partialUpdatedClose.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClose.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedClose))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
        Close testClose = closeList.get(closeList.size() - 1);
        assertThat(testClose.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testClose.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, closeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClose() throws Exception {
        int databaseSizeBeforeUpdate = closeRepository.findAll().collectList().block().size();
        close.setId(UUID.randomUUID().toString());

        // Create the Close
        CloseDTO closeDTO = closeMapper.toDto(close);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(closeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Close in the database
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClose() {
        // Initialize the database
        close.setId(UUID.randomUUID().toString());
        closeRepository.save(close).block();

        int databaseSizeBeforeDelete = closeRepository.findAll().collectList().block().size();

        // Delete the close
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, close.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Close> closeList = closeRepository.findAll().collectList().block();
        assertThat(closeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
