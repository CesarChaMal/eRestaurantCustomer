package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CloseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Close.class);
        Close close1 = new Close();
        close1.setId("id1");
        Close close2 = new Close();
        close2.setId(close1.getId());
        assertThat(close1).isEqualTo(close2);
        close2.setId("id2");
        assertThat(close1).isNotEqualTo(close2);
        close1.setId(null);
        assertThat(close1).isNotEqualTo(close2);
    }
}
