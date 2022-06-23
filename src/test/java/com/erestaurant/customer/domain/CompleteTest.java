package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompleteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Complete.class);
        Complete complete1 = new Complete();
        complete1.setId("id1");
        Complete complete2 = new Complete();
        complete2.setId(complete1.getId());
        assertThat(complete1).isEqualTo(complete2);
        complete2.setId("id2");
        assertThat(complete1).isNotEqualTo(complete2);
        complete1.setId(null);
        assertThat(complete1).isNotEqualTo(complete2);
    }
}
