package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomerProfileDTO.class);
        CustomerProfileDTO customerProfileDTO1 = new CustomerProfileDTO();
        customerProfileDTO1.setId("id1");
        CustomerProfileDTO customerProfileDTO2 = new CustomerProfileDTO();
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
        customerProfileDTO2.setId(customerProfileDTO1.getId());
        assertThat(customerProfileDTO1).isEqualTo(customerProfileDTO2);
        customerProfileDTO2.setId("id2");
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
        customerProfileDTO1.setId(null);
        assertThat(customerProfileDTO1).isNotEqualTo(customerProfileDTO2);
    }
}
