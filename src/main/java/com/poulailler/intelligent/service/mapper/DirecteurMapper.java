package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Directeur} and its DTO {@link DirecteurDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface DirecteurMapper extends EntityMapper<DirecteurDTO, Directeur> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    DirecteurDTO toDto(Directeur s);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", source = "user")
    Set<DirecteurDTO> toDtoIdSet(Set<Directeur> directeur);
}
