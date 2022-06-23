package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.Payment;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.PaymentRepository;
import com.erestaurant.customer.service.dto.PaymentDTO;
import com.erestaurant.customer.service.mapper.PaymentMapper;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaymentResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment().description(DEFAULT_DESCRIPTION);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment().description(UPDATED_DESCRIPTION);
        return payment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Payment.class).block();
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
        payment = createEntity(em);
    }

    @Test
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId("existing_id");
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPaymentsAsStream() {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        List<Payment> paymentList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PaymentDTO.class)
            .getResponseBody()
            .map(paymentMapper::toEntity)
            .filter(payment::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paymentList).isNotNull();
        assertThat(paymentList).hasSize(1);
        Payment testPayment = paymentList.get(0);
        assertThat(testPayment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllPayments() {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        // Get all the paymentList
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
            .value(hasItem(payment.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getPayment() {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(payment.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingPayment() {
        // Get the payment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPayment() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).block();
        updatedPayment.description(UPDATED_DESCRIPTION);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().collectList().block().size();
        payment.setId(UUID.randomUUID().toString());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePayment() {
        // Initialize the database
        payment.setId(UUID.randomUUID().toString());
        paymentRepository.save(payment).block();

        int databaseSizeBeforeDelete = paymentRepository.findAll().collectList().block().size();

        // Delete the payment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, payment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll().collectList().block();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
