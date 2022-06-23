package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Customer;
import com.erestaurant.customer.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {}
