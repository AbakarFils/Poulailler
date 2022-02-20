package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.NH3;
import com.poulailler.intelligent.service.dto.NH3DTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NH3} and its DTO {@link NH3DTO}.
 */
@Mapper(componentModel = "spring", uses = { VariableMapper.class })
public interface NH3Mapper extends EntityMapper<NH3DTO, NH3> {
    @Mapping(target = "variable", source = "variable", qualifiedByName = "id")
    NH3DTO toDto(NH3 s);
}
