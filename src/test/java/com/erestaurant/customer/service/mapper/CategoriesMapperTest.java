package com.erestaurant.customer.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriesMapperTest {

    private CategoriesMapper categoriesMapper;

    @BeforeEach
    public void setUp() {
        categoriesMapper = new CategoriesMapperImpl();
    }
}
