package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Complete;
import com.erestaurant.customer.service.dto.CompleteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Complete} and its DTO {@link CompleteDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompleteMapper extends EntityMapper<CompleteDTO, Complete> {}
