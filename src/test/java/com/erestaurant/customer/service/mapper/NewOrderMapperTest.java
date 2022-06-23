package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NewOrderMapperTest {

    private NewOrderMapper newOrderMapper;

    @BeforeEach
    public void setUp() {
        newOrderMapper = new NewOrderMapperImpl();
    }
}
