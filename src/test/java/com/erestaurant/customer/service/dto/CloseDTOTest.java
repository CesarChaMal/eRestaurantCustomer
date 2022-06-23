package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CloseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CloseDTO.class);
        CloseDTO closeDTO1 = new CloseDTO();
        closeDTO1.setId("id1");
        CloseDTO closeDTO2 = new CloseDTO();
        assertThat(closeDTO1).isNotEqualTo(closeDTO2);
        closeDTO2.setId(closeDTO1.getId());
        assertThat(closeDTO1).isEqualTo(closeDTO2);
        closeDTO2.setId("id2");
        assertThat(closeDTO1).isNotEqualTo(closeDTO2);
        closeDTO1.setId(null);
        assertThat(closeDTO1).isNotEqualTo(closeDTO2);
    }
}
