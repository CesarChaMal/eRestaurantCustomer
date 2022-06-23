package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Cancel;
import com.erestaurant.customer.service.dto.CancelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cancel} and its DTO {@link CancelDTO}.
 */
@Mapper(componentModel = "spring")
public interface CancelMapper extends EntityMapper<CancelDTO, Cancel> {}
