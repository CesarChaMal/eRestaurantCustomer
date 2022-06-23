package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.Products;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.ProductsRepository;
import com.erestaurant.customer.service.dto.ProductsDTO;
import com.erestaurant.customer.service.mapper.ProductsMapper;
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
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Float DEFAULT_ESTIMATED_PREPARAING_TIME = 1F;
    private static final Float UPDATED_ESTIMATED_PREPARAING_TIME = 2F;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .estimatedPreparaingTime(DEFAULT_ESTIMATED_PREPARAING_TIME);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .estimatedPreparaingTime(UPDATED_ESTIMATED_PREPARAING_TIME);
        return products;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Products.class).block();
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
        products = createEntity(em);
    }

    @Test
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().collectList().block().size();
        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProducts.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testProducts.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testProducts.getEstimatedPreparaingTime()).isEqualTo(DEFAULT_ESTIMATED_PREPARAING_TIME);
    }

    @Test
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId("existing_id");
        ProductsDTO productsDTO = productsMapper.toDto(products);

        int databaseSizeBeforeCreate = productsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().collectList().block().size();
        // set the field null
        products.setName(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEstimatedPreparaingTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().collectList().block().size();
        // set the field null
        products.setEstimatedPreparaingTime(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductsAsStream() {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        List<Products> productsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ProductsDTO.class)
            .getResponseBody()
            .map(productsMapper::toEntity)
            .filter(products::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(productsList).isNotNull();
        assertThat(productsList).hasSize(1);
        Products testProducts = productsList.get(0);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProducts.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testProducts.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testProducts.getEstimatedPreparaingTime()).isEqualTo(DEFAULT_ESTIMATED_PREPARAING_TIME);
    }

    @Test
    void getAllProducts() {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        // Get all the productsList
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
            .value(hasItem(products.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].estimatedPreparaingTime")
            .value(hasItem(DEFAULT_ESTIMATED_PREPARAING_TIME.doubleValue()));
    }

    @Test
    void getProducts() {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        // Get the products
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, products.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(products.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.estimatedPreparaingTime")
            .value(is(DEFAULT_ESTIMATED_PREPARAING_TIME.doubleValue()));
    }

    @Test
    void getNonExistingProducts() {
        // Get the products
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProducts() throws Exception {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).block();
        updatedProducts
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .estimatedPreparaingTime(UPDATED_ESTIMATED_PREPARAING_TIME);
        ProductsDTO productsDTO = productsMapper.toDto(updatedProducts);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProducts.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testProducts.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testProducts.getEstimatedPreparaingTime()).isEqualTo(UPDATED_ESTIMATED_PREPARAING_TIME);
    }

    @Test
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProducts.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testProducts.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testProducts.getEstimatedPreparaingTime()).isEqualTo(DEFAULT_ESTIMATED_PREPARAING_TIME);
    }

    @Test
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .estimatedPreparaingTime(UPDATED_ESTIMATED_PREPARAING_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProducts.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testProducts.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testProducts.getEstimatedPreparaingTime()).isEqualTo(UPDATED_ESTIMATED_PREPARAING_TIME);
    }

    @Test
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(UUID.randomUUID().toString());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProducts() {
        // Initialize the database
        products.setId(UUID.randomUUID().toString());
        productsRepository.save(products).block();

        int databaseSizeBeforeDelete = productsRepository.findAll().collectList().block().size();

        // Delete the products
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, products.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
