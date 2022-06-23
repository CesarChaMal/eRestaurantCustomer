package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Payment;
import com.erestaurant.customer.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {}
