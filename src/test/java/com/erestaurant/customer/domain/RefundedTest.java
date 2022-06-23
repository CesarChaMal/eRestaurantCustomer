package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Refunded.class);
        Refunded refunded1 = new Refunded();
        refunded1.setId("id1");
        Refunded refunded2 = new Refunded();
        refunded2.setId(refunded1.getId());
        assertThat(refunded1).isEqualTo(refunded2);
        refunded2.setId("id2");
        assertThat(refunded1).isNotEqualTo(refunded2);
        refunded1.setId(null);
        assertThat(refunded1).isNotEqualTo(refunded2);
    }
}
