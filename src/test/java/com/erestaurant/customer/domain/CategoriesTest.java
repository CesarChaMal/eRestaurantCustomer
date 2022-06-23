package com.erestaurant.customer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoriesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Categories.class);
        Categories categories1 = new Categories();
        categories1.setId("id1");
        Categories categories2 = new Categories();
        categories2.setId(categories1.getId());
        assertThat(categories1).isEqualTo(categories2);
        categories2.setId("id2");
        assertThat(categories1).isNotEqualTo(categories2);
        categories1.setId(null);
        assertThat(categories1).isNotEqualTo(categories2);
    }
}
