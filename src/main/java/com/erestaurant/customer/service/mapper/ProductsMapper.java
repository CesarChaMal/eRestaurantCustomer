package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Products;
import com.erestaurant.customer.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {}
