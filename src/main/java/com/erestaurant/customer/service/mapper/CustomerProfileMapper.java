package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.CustomerProfile;
import com.erestaurant.customer.service.dto.CustomerProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerProfile} and its DTO {@link CustomerProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerProfileMapper extends EntityMapper<CustomerProfileDTO, CustomerProfile> {}
