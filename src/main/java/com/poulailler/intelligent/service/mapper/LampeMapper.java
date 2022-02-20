package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Lampe;
import com.poulailler.intelligent.service.dto.LampeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lampe} and its DTO {@link LampeDTO}.
 */
@Mapper(componentModel = "spring", uses = { EquipementMapper.class })
public interface LampeMapper extends EntityMapper<LampeDTO, Lampe> {
    @Mapping(target = "equipement", source = "equipement", qualifiedByName = "id")
    LampeDTO toDto(Lampe s);
}
