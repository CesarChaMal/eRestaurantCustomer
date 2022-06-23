package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloseMapperTest {

    private CloseMapper closeMapper;

    @BeforeEach
    public void setUp() {
        closeMapper = new CloseMapperImpl();
    }
}
