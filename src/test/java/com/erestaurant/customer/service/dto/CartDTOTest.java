package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartDTO.class);
        CartDTO cartDTO1 = new CartDTO();
        cartDTO1.setId("id1");
        CartDTO cartDTO2 = new CartDTO();
        assertThat(cartDTO1).isNotEqualTo(cartDTO2);
        cartDTO2.setId(cartDTO1.getId());
        assertThat(cartDTO1).isEqualTo(cartDTO2);
        cartDTO2.setId("id2");
        assertThat(cartDTO1).isNotEqualTo(cartDTO2);
        cartDTO1.setId(null);
        assertThat(cartDTO1).isNotEqualTo(cartDTO2);
    }
}
