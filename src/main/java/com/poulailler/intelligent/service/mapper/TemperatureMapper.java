package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Temperature;
import com.poulailler.intelligent.service.dto.TemperatureDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Temperature} and its DTO {@link TemperatureDTO}.
 */
@Mapper(componentModel = "spring", uses = { VariableMapper.class })
public interface TemperatureMapper extends EntityMapper<TemperatureDTO, Temperature> {
    @Mapping(target = "variable", source = "variable", qualifiedByName = "id")
    TemperatureDTO toDto(Temperature s);
}
