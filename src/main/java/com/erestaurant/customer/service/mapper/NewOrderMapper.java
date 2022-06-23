package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.NewOrder;
import com.erestaurant.customer.service.dto.NewOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NewOrder} and its DTO {@link NewOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface NewOrderMapper extends EntityMapper<NewOrderDTO, NewOrder> {}
