package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Order;
import com.erestaurant.customer.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {}
