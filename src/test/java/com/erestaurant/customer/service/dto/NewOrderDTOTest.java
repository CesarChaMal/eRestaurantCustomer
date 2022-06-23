package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NewOrderDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NewOrderDTO.class);
        NewOrderDTO newOrderDTO1 = new NewOrderDTO();
        newOrderDTO1.setId("id1");
        NewOrderDTO newOrderDTO2 = new NewOrderDTO();
        assertThat(newOrderDTO1).isNotEqualTo(newOrderDTO2);
        newOrderDTO2.setId(newOrderDTO1.getId());
        assertThat(newOrderDTO1).isEqualTo(newOrderDTO2);
        newOrderDTO2.setId("id2");
        assertThat(newOrderDTO1).isNotEqualTo(newOrderDTO2);
        newOrderDTO1.setId(null);
        assertThat(newOrderDTO1).isNotEqualTo(newOrderDTO2);
    }
}
