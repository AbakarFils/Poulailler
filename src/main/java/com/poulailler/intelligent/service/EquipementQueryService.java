package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.repository.EquipementRepository;
import com.poulailler.intelligent.service.criteria.EquipementCriteria;
import com.poulailler.intelligent.service.dto.EquipementDTO;
import com.poulailler.intelligent.service.mapper.EquipementMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Equipement} entities in the database.
 * The main input is a {@link EquipementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EquipementDTO} or a {@link Page} of {@link EquipementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EquipementQueryService extends QueryService<Equipement> {

    private final Logger log = LoggerFactory.getLogger(EquipementQueryService.class);

    private final EquipementRepository equipementRepository;

    private final EquipementMapper equipementMapper;

    public EquipementQueryService(EquipementRepository equipementRepository, EquipementMapper equipementMapper) {
        this.equipementRepository = equipementRepository;
        this.equipementMapper = equipementMapper;
    }

    /**
     * Return a {@link List} of {@link EquipementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EquipementDTO> findByCriteria(EquipementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Equipement> specification = createSpecification(criteria);
        return equipementMapper.toDto(equipementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EquipementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EquipementDTO> findByCriteria(EquipementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Equipement> specification = createSpecification(criteria);
        return equipementRepository.findAll(specification, page).map(equipementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EquipementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Equipement> specification = createSpecification(criteria);
        return equipementRepository.count(specification);
    }

    /**
     * Function to convert {@link EquipementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Equipement> createSpecification(EquipementCriteria criteria) {
        Specification<Equipement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Equipement_.id));
            }
            if (criteria.getStatut() != null) {
                specification = specification.and(buildSpecification(criteria.getStatut(), Equipement_.statut));
            }
            if (criteria.getRefArduino() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRefArduino(), Equipement_.refArduino));
            }
            if (criteria.getDateCrea() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCrea(), Equipement_.dateCrea));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Equipement_.libelle));
            }
            if (criteria.getMarque() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMarque(), Equipement_.marque));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Equipement_.type));
            }
            if (criteria.getEmployeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEmployeId(), root -> root.join(Equipement_.employes, JoinType.LEFT).get(Employe_.id))
                    );
            }
            if (criteria.getGestionnaireId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getGestionnaireId(),
                            root -> root.join(Equipement_.gestionnaires, JoinType.LEFT).get(Directeur_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
