package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductsDTO.class);
        ProductsDTO productsDTO1 = new ProductsDTO();
        productsDTO1.setId("id1");
        ProductsDTO productsDTO2 = new ProductsDTO();
        assertThat(productsDTO1).isNotEqualTo(productsDTO2);
        productsDTO2.setId(productsDTO1.getId());
        assertThat(productsDTO1).isEqualTo(productsDTO2);
        productsDTO2.setId("id2");
        assertThat(productsDTO1).isNotEqualTo(productsDTO2);
        productsDTO1.setId(null);
        assertThat(productsDTO1).isNotEqualTo(productsDTO2);
    }
}
