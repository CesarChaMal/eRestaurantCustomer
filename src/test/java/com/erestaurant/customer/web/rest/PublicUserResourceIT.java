package com.erestaurant.customer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.customer.IntegrationTest;
import com.erestaurant.customer.config.Constants;
import com.erestaurant.customer.config.TestSecurityConfiguration;
import com.erestaurant.customer.domain.User;
import com.erestaurant.customer.repository.EntityManager;
import com.erestaurant.customer.repository.UserRepository;
import com.erestaurant.customer.repository.search.UserSearchRepository;
import com.erestaurant.customer.security.AuthoritiesConstants;
import com.erestaurant.customer.service.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
class PublicUserResourceIT {

    private static final String DEFAULT_LOGIN = "johndoe";

    @Autowired
    private UserRepository userRepository;

    /**
     * This repository is mocked in the com.erestaurant.customer.repository.search test package.
     *
     * @see com.erestaurant.customer.repository.search.UserSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserSearchRepository mockUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private User user;

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        user = UserResourceIT.initTestUser(userRepository, em);
    }

    @Test
    void getAllPublicUsers() {
        // Initialize the database
        userRepository.create(user).block();

        // Get all the users
        UserDTO foundUser = webTestClient
            .get()
            .uri("/api/users?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .returnResult(UserDTO.class)
            .getResponseBody()
            .blockFirst();

        assertThat(foundUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
    }
}
