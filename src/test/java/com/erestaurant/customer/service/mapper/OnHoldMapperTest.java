package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OnHoldMapperTest {

    private OnHoldMapper onHoldMapper;

    @BeforeEach
    public void setUp() {
        onHoldMapper = new OnHoldMapperImpl();
    }
}
