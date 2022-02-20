package com.poulailler.intelligent.service.mapper;

import com.poulailler.intelligent.domain.Ventilateur;
import com.poulailler.intelligent.service.dto.VentilateurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ventilateur} and its DTO {@link VentilateurDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface VentilateurMapper extends EntityMapper<VentilateurDTO, Ventilateur> {}
