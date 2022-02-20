package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.service.dto.VariableDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface VariableMapper extends EntityMapper<VariableDTO, Variable> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    DirecteurDTO toDto(Directeur s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VariableDTO toDtoId(Variable variable);
}
