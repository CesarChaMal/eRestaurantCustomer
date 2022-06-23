package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.Refunded;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.RefundedRepository;
import com.erestaurant.customer.service.dto.RefundedDTO;
import com.erestaurant.customer.service.mapper.RefundedMapper;
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
 * Integration tests for the {@link RefundedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RefundedResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/refundeds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private RefundedRepository refundedRepository;

    @Autowired
    private RefundedMapper refundedMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Refunded refunded;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refunded createEntity(EntityManager em) {
        Refunded refunded = new Refunded().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return refunded;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refunded createUpdatedEntity(EntityManager em) {
        Refunded refunded = new Refunded().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return refunded;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Refunded.class).block();
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
        refunded = createEntity(em);
    }

    @Test
    void createRefunded() throws Exception {
        int databaseSizeBeforeCreate = refundedRepository.findAll().collectList().block().size();
        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeCreate + 1);
        Refunded testRefunded = refundedList.get(refundedList.size() - 1);
        assertThat(testRefunded.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRefunded.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void createRefundedWithExistingId() throws Exception {
        // Create the Refunded with an existing ID
        refunded.setId("existing_id");
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        int databaseSizeBeforeCreate = refundedRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRefundedsAsStream() {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        List<Refunded> refundedList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RefundedDTO.class)
            .getResponseBody()
            .map(refundedMapper::toEntity)
            .filter(refunded::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(refundedList).isNotNull();
        assertThat(refundedList).hasSize(1);
        Refunded testRefunded = refundedList.get(0);
        assertThat(testRefunded.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRefunded.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllRefundeds() {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        // Get all the refundedList
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
            .value(hasItem(refunded.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getRefunded() {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        // Get the refunded
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, refunded.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(refunded.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingRefunded() {
        // Get the refunded
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRefunded() throws Exception {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();

        // Update the refunded
        Refunded updatedRefunded = refundedRepository.findById(refunded.getId()).block();
        updatedRefunded.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        RefundedDTO refundedDTO = refundedMapper.toDto(updatedRefunded);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, refundedDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
        Refunded testRefunded = refundedList.get(refundedList.size() - 1);
        assertThat(testRefunded.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRefunded.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void putNonExistingRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, refundedDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRefundedWithPatch() throws Exception {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();

        // Update the refunded using partial update
        Refunded partialUpdatedRefunded = new Refunded();
        partialUpdatedRefunded.setId(refunded.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRefunded.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRefunded))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
        Refunded testRefunded = refundedList.get(refundedList.size() - 1);
        assertThat(testRefunded.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRefunded.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void fullUpdateRefundedWithPatch() throws Exception {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();

        // Update the refunded using partial update
        Refunded partialUpdatedRefunded = new Refunded();
        partialUpdatedRefunded.setId(refunded.getId());

        partialUpdatedRefunded.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRefunded.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRefunded))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
        Refunded testRefunded = refundedList.get(refundedList.size() - 1);
        assertThat(testRefunded.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRefunded.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, refundedDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRefunded() throws Exception {
        int databaseSizeBeforeUpdate = refundedRepository.findAll().collectList().block().size();
        refunded.setId(UUID.randomUUID().toString());

        // Create the Refunded
        RefundedDTO refundedDTO = refundedMapper.toDto(refunded);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(refundedDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Refunded in the database
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRefunded() {
        // Initialize the database
        refunded.setId(UUID.randomUUID().toString());
        refundedRepository.save(refunded).block();

        int databaseSizeBeforeDelete = refundedRepository.findAll().collectList().block().size();

        // Delete the refunded
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, refunded.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Refunded> refundedList = refundedRepository.findAll().collectList().block();
        assertThat(refundedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
