package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OnHoldDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OnHoldDTO.class);
        OnHoldDTO onHoldDTO1 = new OnHoldDTO();
        onHoldDTO1.setId("id1");
        OnHoldDTO onHoldDTO2 = new OnHoldDTO();
        assertThat(onHoldDTO1).isNotEqualTo(onHoldDTO2);
        onHoldDTO2.setId(onHoldDTO1.getId());
        assertThat(onHoldDTO1).isEqualTo(onHoldDTO2);
        onHoldDTO2.setId("id2");
        assertThat(onHoldDTO1).isNotEqualTo(onHoldDTO2);
        onHoldDTO1.setId(null);
        assertThat(onHoldDTO1).isNotEqualTo(onHoldDTO2);
    }
}
