package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NewOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NewOrder.class);
        NewOrder newOrder1 = new NewOrder();
        newOrder1.setId("id1");
        NewOrder newOrder2 = new NewOrder();
        newOrder2.setId(newOrder1.getId());
        assertThat(newOrder1).isEqualTo(newOrder2);
        newOrder2.setId("id2");
        assertThat(newOrder1).isNotEqualTo(newOrder2);
        newOrder1.setId(null);
        assertThat(newOrder1).isNotEqualTo(newOrder2);
    }
}
