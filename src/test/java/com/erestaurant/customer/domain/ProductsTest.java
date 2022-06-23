package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Products.class);
        Products products1 = new Products();
        products1.setId("id1");
        Products products2 = new Products();
        products2.setId(products1.getId());
        assertThat(products1).isEqualTo(products2);
        products2.setId("id2");
        assertThat(products1).isNotEqualTo(products2);
        products1.setId(null);
        assertThat(products1).isNotEqualTo(products2);
    }
}
