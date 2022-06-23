package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Categories;
import com.erestaurant.customer.service.dto.CategoriesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Categories} and its DTO {@link CategoriesDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoriesMapper extends EntityMapper<CategoriesDTO, Categories> {}
