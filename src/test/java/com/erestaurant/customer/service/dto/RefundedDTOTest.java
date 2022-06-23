package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundedDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RefundedDTO.class);
        RefundedDTO refundedDTO1 = new RefundedDTO();
        refundedDTO1.setId("id1");
        RefundedDTO refundedDTO2 = new RefundedDTO();
        assertThat(refundedDTO1).isNotEqualTo(refundedDTO2);
        refundedDTO2.setId(refundedDTO1.getId());
        assertThat(refundedDTO1).isEqualTo(refundedDTO2);
        refundedDTO2.setId("id2");
        assertThat(refundedDTO1).isNotEqualTo(refundedDTO2);
        refundedDTO1.setId(null);
        assertThat(refundedDTO1).isNotEqualTo(refundedDTO2);
    }
}
