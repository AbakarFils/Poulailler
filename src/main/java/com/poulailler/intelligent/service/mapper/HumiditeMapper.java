package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Humidite;
import com.poulailler.intelligent.service.dto.HumiditeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Humidite} and its DTO {@link HumiditeDTO}.
 */
@Mapper(componentModel = "spring", uses = { VariableMapper.class })
public interface HumiditeMapper extends EntityMapper<HumiditeDTO, Humidite> {
    @Mapping(target = "variable", source = "variable", qualifiedByName = "id")
    HumiditeDTO toDto(Humidite s);
}
