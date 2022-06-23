package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerProfileMapperTest {

    private CustomerProfileMapper customerProfileMapper;

    @BeforeEach
    public void setUp() {
        customerProfileMapper = new CustomerProfileMapperImpl();
    }
}
