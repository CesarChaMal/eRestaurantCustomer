package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OnHoldTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OnHold.class);
        OnHold onHold1 = new OnHold();
        onHold1.setId("id1");
        OnHold onHold2 = new OnHold();
        onHold2.setId(onHold1.getId());
        assertThat(onHold1).isEqualTo(onHold2);
        onHold2.setId("id2");
        assertThat(onHold1).isNotEqualTo(onHold2);
        onHold1.setId(null);
        assertThat(onHold1).isNotEqualTo(onHold2);
    }
}
