package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Oeuf;
import com.poulailler.intelligent.service.dto.OeufDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Oeuf} and its DTO {@link OeufDTO}.
 */
@Mapper(componentModel = "spring", uses = { VariableMapper.class })
public interface OeufMapper extends EntityMapper<OeufDTO, Oeuf> {
    @Mapping(target = "variable", source = "variable", qualifiedByName = "id")
    OeufDTO toDto(Oeuf s);
}
