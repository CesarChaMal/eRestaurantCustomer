package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefundedMapperTest {

    private RefundedMapper refundedMapper;

    @BeforeEach
    public void setUp() {
        refundedMapper = new RefundedMapperImpl();
    }
}
