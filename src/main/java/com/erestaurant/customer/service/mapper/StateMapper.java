package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.State;
import com.erestaurant.customer.service.dto.StateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link State} and its DTO {@link StateDTO}.
 */
@Mapper(componentModel = "spring")
public interface StateMapper extends EntityMapper<StateDTO, State> {}
