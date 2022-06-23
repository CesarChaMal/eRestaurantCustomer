package com.erestaurant.customer.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.customer.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoriesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoriesDTO.class);
        CategoriesDTO categoriesDTO1 = new CategoriesDTO();
        categoriesDTO1.setId("id1");
        CategoriesDTO categoriesDTO2 = new CategoriesDTO();
        assertThat(categoriesDTO1).isNotEqualTo(categoriesDTO2);
        categoriesDTO2.setId(categoriesDTO1.getId());
        assertThat(categoriesDTO1).isEqualTo(categoriesDTO2);
        categoriesDTO2.setId("id2");
        assertThat(categoriesDTO1).isNotEqualTo(categoriesDTO2);
        categoriesDTO1.setId(null);
        assertThat(categoriesDTO1).isNotEqualTo(categoriesDTO2);
    }
}
