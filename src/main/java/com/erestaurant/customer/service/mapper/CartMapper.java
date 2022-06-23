package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Cart;
import com.erestaurant.customer.service.dto.CartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartMapper extends EntityMapper<CartDTO, Cart> {}
