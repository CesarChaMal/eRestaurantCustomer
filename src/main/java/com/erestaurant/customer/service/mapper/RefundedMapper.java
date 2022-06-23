package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Refunded;
import com.erestaurant.customer.service.dto.RefundedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refunded} and its DTO {@link RefundedDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundedMapper extends EntityMapper<RefundedDTO, Refunded> {}
