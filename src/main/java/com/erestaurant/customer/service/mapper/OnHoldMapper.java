package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.OnHold;
import com.erestaurant.customer.service.dto.OnHoldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OnHold} and its DTO {@link OnHoldDTO}.
 */
@Mapper(componentModel = "spring")
public interface OnHoldMapper extends EntityMapper<OnHoldDTO, OnHold> {}
