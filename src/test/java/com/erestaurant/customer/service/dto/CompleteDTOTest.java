package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompleteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompleteDTO.class);
        CompleteDTO completeDTO1 = new CompleteDTO();
        completeDTO1.setId("id1");
        CompleteDTO completeDTO2 = new CompleteDTO();
        assertThat(completeDTO1).isNotEqualTo(completeDTO2);
        completeDTO2.setId(completeDTO1.getId());
        assertThat(completeDTO1).isEqualTo(completeDTO2);
        completeDTO2.setId("id2");
        assertThat(completeDTO1).isNotEqualTo(completeDTO2);
        completeDTO1.setId(null);
        assertThat(completeDTO1).isNotEqualTo(completeDTO2);
    }
}
