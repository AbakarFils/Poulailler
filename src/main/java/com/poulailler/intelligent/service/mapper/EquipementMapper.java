package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.service.dto.EquipementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipement} and its DTO {@link EquipementDTO}.
 */
@Mapper(componentModel = "spring", uses = { EmployeMapper.class, DirecteurMapper.class })
public interface EquipementMapper extends EntityMapper<EquipementDTO, Equipement> {
    @Mapping(target = "employes", source = "employes", qualifiedByName = "idSet")
    @Mapping(target = "gestionnaires", source = "gestionnaires", qualifiedByName = "idSet")
    EquipementDTO toDto(Equipement s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipementDTO toDtoId(Equipement equipement);

    @Mapping(target = "removeEmploye", ignore = true)
    @Mapping(target = "removeGestionnaire", ignore = true)
    Equipement toEntity(EquipementDTO equipementDTO);
}
