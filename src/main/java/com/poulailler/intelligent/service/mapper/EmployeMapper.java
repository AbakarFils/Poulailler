package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.domain.Employe;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.service.dto.EmployeDTO;
import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface EmployeMapper extends EntityMapper<EmployeDTO, Employe> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    EmployeDTO toDto(Employe e);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", source = "user")
    Set<EmployeDTO> toDtoIdSet(Set<Employe> employe);
}
