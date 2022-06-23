package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StateDTO.class);
        StateDTO stateDTO1 = new StateDTO();
        stateDTO1.setId("id1");
        StateDTO stateDTO2 = new StateDTO();
        assertThat(stateDTO1).isNotEqualTo(stateDTO2);
        stateDTO2.setId(stateDTO1.getId());
        assertThat(stateDTO1).isEqualTo(stateDTO2);
        stateDTO2.setId("id2");
        assertThat(stateDTO1).isNotEqualTo(stateDTO2);
        stateDTO1.setId(null);
        assertThat(stateDTO1).isNotEqualTo(stateDTO2);
    }
}
