package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.domain.AppUser;
import com.erestaurant.customer.repository.AppUserRepository;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.service.AppUserService;
import com.erestaurant.customer.service.dto.AppUserDTO;
import com.erestaurant.customer.service.mapper.AppUserMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    @Autowired
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserService appUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .email(DEFAULT_EMAIL);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL);
        return appUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppUser.class).block();
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
        appUser = createEntity(em);
    }

    @Test
    void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppUser.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAppUser.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testAppUser.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testAppUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId("existing_id");
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        int databaseSizeBeforeCreate = appUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().collectList().block().size();
        // set the field null
        appUser.setName(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppUsers() {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        // Get all the appUserList
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
            .value(hasItem(appUser.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsEnabled() {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsNotEnabled() {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAppUser() {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appUser.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingAppUser() {
        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAppUser() throws Exception {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).block();
        updatedAppUser
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppUser.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testAppUser.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testAppUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void putNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppUser.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testAppUser.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testAppUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppUser.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testAppUser.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testAppUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void patchNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().collectList().block().size();
        appUser.setId(UUID.randomUUID().toString());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppUser() {
        // Initialize the database
        appUser.setId(UUID.randomUUID().toString());
        appUserRepository.save(appUser).block();

        int databaseSizeBeforeDelete = appUserRepository.findAll().collectList().block().size();

        // Delete the appUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll().collectList().block();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
