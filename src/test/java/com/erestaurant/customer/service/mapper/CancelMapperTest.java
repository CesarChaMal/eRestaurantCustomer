package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CancelMapperTest {

    private CancelMapper cancelMapper;

    @BeforeEach
    public void setUp() {
        cancelMapper = new CancelMapperImpl();
    }
}
