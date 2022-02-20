package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Serrure;
import com.poulailler.intelligent.service.dto.SerrureDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Serrure} and its DTO {@link SerrureDTO}.
 */
@Mapper(componentModel = "spring", uses = { EquipementMapper.class })
public interface SerrureMapper extends EntityMapper<SerrureDTO, Serrure> {
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "id")
    SerrureDTO toDto(Serrure s);
}
