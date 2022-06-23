package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.CustomerProfile;
import com.erestaurant.customer.repository.CustomerProfileRepository;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.service.dto.CustomerProfileDTO;
import com.erestaurant.customer.service.mapper.CustomerProfileMapper;
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
 * Integration tests for the {@link CustomerProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerProfileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION_RANGE = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION_RANGE = "BBBBBBBBBB";

    private static final String DEFAULT_REFERALS = "AAAAAAAAAA";
    private static final String UPDATED_REFERALS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/customer-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private CustomerProfileMapper customerProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CustomerProfile customerProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerProfile createEntity(EntityManager em) {
        CustomerProfile customerProfile = new CustomerProfile()
            .name(DEFAULT_NAME)
            .location(DEFAULT_LOCATION)
            .locationRange(DEFAULT_LOCATION_RANGE)
            .referals(DEFAULT_REFERALS);
        return customerProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerProfile createUpdatedEntity(EntityManager em) {
        CustomerProfile customerProfile = new CustomerProfile()
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION)
            .locationRange(UPDATED_LOCATION_RANGE)
            .referals(UPDATED_REFERALS);
        return customerProfile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CustomerProfile.class).block();
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
        customerProfile = createEntity(em);
    }

    @Test
    void createCustomerProfile() throws Exception {
        int databaseSizeBeforeCreate = customerProfileRepository.findAll().collectList().block().size();
        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeCreate + 1);
        CustomerProfile testCustomerProfile = customerProfileList.get(customerProfileList.size() - 1);
        assertThat(testCustomerProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCustomerProfile.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testCustomerProfile.getLocationRange()).isEqualTo(DEFAULT_LOCATION_RANGE);
        assertThat(testCustomerProfile.getReferals()).isEqualTo(DEFAULT_REFERALS);
    }

    @Test
    void createCustomerProfileWithExistingId() throws Exception {
        // Create the CustomerProfile with an existing ID
        customerProfile.setId("existing_id");
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        int databaseSizeBeforeCreate = customerProfileRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerProfileRepository.findAll().collectList().block().size();
        // set the field null
        customerProfile.setName(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLocationIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerProfileRepository.findAll().collectList().block().size();
        // set the field null
        customerProfile.setLocation(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLocationRangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerProfileRepository.findAll().collectList().block().size();
        // set the field null
        customerProfile.setLocationRange(null);

        // Create the CustomerProfile, which fails.
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCustomerProfilesAsStream() {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        List<CustomerProfile> customerProfileList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CustomerProfileDTO.class)
            .getResponseBody()
            .map(customerProfileMapper::toEntity)
            .filter(customerProfile::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(customerProfileList).isNotNull();
        assertThat(customerProfileList).hasSize(1);
        CustomerProfile testCustomerProfile = customerProfileList.get(0);
        assertThat(testCustomerProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCustomerProfile.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testCustomerProfile.getLocationRange()).isEqualTo(DEFAULT_LOCATION_RANGE);
        assertThat(testCustomerProfile.getReferals()).isEqualTo(DEFAULT_REFERALS);
    }

    @Test
    void getAllCustomerProfiles() {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        // Get all the customerProfileList
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
            .value(hasItem(customerProfile.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION))
            .jsonPath("$.[*].locationRange")
            .value(hasItem(DEFAULT_LOCATION_RANGE))
            .jsonPath("$.[*].referals")
            .value(hasItem(DEFAULT_REFERALS.toString()));
    }

    @Test
    void getCustomerProfile() {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        // Get the customerProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customerProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customerProfile.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION))
            .jsonPath("$.locationRange")
            .value(is(DEFAULT_LOCATION_RANGE))
            .jsonPath("$.referals")
            .value(is(DEFAULT_REFERALS.toString()));
    }

    @Test
    void getNonExistingCustomerProfile() {
        // Get the customerProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCustomerProfile() throws Exception {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();

        // Update the customerProfile
        CustomerProfile updatedCustomerProfile = customerProfileRepository.findById(customerProfile.getId()).block();
        updatedCustomerProfile
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION)
            .locationRange(UPDATED_LOCATION_RANGE)
            .referals(UPDATED_REFERALS);
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(updatedCustomerProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
        CustomerProfile testCustomerProfile = customerProfileList.get(customerProfileList.size() - 1);
        assertThat(testCustomerProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCustomerProfile.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testCustomerProfile.getLocationRange()).isEqualTo(UPDATED_LOCATION_RANGE);
        assertThat(testCustomerProfile.getReferals()).isEqualTo(UPDATED_REFERALS);
    }

    @Test
    void putNonExistingCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerProfileWithPatch() throws Exception {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();

        // Update the customerProfile using partial update
        CustomerProfile partialUpdatedCustomerProfile = new CustomerProfile();
        partialUpdatedCustomerProfile.setId(customerProfile.getId());

        partialUpdatedCustomerProfile.location(UPDATED_LOCATION).locationRange(UPDATED_LOCATION_RANGE).referals(UPDATED_REFERALS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
        CustomerProfile testCustomerProfile = customerProfileList.get(customerProfileList.size() - 1);
        assertThat(testCustomerProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCustomerProfile.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testCustomerProfile.getLocationRange()).isEqualTo(UPDATED_LOCATION_RANGE);
        assertThat(testCustomerProfile.getReferals()).isEqualTo(UPDATED_REFERALS);
    }

    @Test
    void fullUpdateCustomerProfileWithPatch() throws Exception {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();

        // Update the customerProfile using partial update
        CustomerProfile partialUpdatedCustomerProfile = new CustomerProfile();
        partialUpdatedCustomerProfile.setId(customerProfile.getId());

        partialUpdatedCustomerProfile
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION)
            .locationRange(UPDATED_LOCATION_RANGE)
            .referals(UPDATED_REFERALS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomerProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomerProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
        CustomerProfile testCustomerProfile = customerProfileList.get(customerProfileList.size() - 1);
        assertThat(testCustomerProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCustomerProfile.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testCustomerProfile.getLocationRange()).isEqualTo(UPDATED_LOCATION_RANGE);
        assertThat(testCustomerProfile.getReferals()).isEqualTo(UPDATED_REFERALS);
    }

    @Test
    void patchNonExistingCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customerProfileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomerProfile() throws Exception {
        int databaseSizeBeforeUpdate = customerProfileRepository.findAll().collectList().block().size();
        customerProfile.setId(UUID.randomUUID().toString());

        // Create the CustomerProfile
        CustomerProfileDTO customerProfileDTO = customerProfileMapper.toDto(customerProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(customerProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CustomerProfile in the database
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomerProfile() {
        // Initialize the database
        customerProfile.setId(UUID.randomUUID().toString());
        customerProfileRepository.save(customerProfile).block();

        int databaseSizeBeforeDelete = customerProfileRepository.findAll().collectList().block().size();

        // Delete the customerProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customerProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CustomerProfile> customerProfileList = customerProfileRepository.findAll().collectList().block();
        assertThat(customerProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
