package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CancelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CancelDTO.class);
        CancelDTO cancelDTO1 = new CancelDTO();
        cancelDTO1.setId("id1");
        CancelDTO cancelDTO2 = new CancelDTO();
        assertThat(cancelDTO1).isNotEqualTo(cancelDTO2);
        cancelDTO2.setId(cancelDTO1.getId());
        assertThat(cancelDTO1).isEqualTo(cancelDTO2);
        cancelDTO2.setId("id2");
        assertThat(cancelDTO1).isNotEqualTo(cancelDTO2);
        cancelDTO1.setId(null);
        assertThat(cancelDTO1).isNotEqualTo(cancelDTO2);
    }
}
