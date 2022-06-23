package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CancelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cancel.class);
        Cancel cancel1 = new Cancel();
        cancel1.setId("id1");
        Cancel cancel2 = new Cancel();
        cancel2.setId(cancel1.getId());
        assertThat(cancel1).isEqualTo(cancel2);
        cancel2.setId("id2");
        assertThat(cancel1).isNotEqualTo(cancel2);
        cancel1.setId(null);
        assertThat(cancel1).isNotEqualTo(cancel2);
    }
}
