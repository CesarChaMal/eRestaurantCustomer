package com.erestaurant.customer.service.mapper;

import com.erestaurant.customer.domain.Close;
import com.erestaurant.customer.service.dto.CloseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Close} and its DTO {@link CloseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CloseMapper extends EntityMapper<CloseDTO, Close> {}
