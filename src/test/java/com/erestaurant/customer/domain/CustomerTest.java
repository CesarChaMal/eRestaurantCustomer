package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = new Customer();
        customer1.setId("id1");
        Customer customer2 = new Customer();
        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);
        customer2.setId("id2");
        assertThat(customer1).isNotEqualTo(customer2);
        customer1.setId(null);
        assertThat(customer1).isNotEqualTo(customer2);
    }
}
