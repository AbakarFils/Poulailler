package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Ventilateur;
import com.poulailler.intelligent.repository.VentilateurRepository;
import com.poulailler.intelligent.service.criteria.VentilateurCriteria;
import com.poulailler.intelligent.service.dto.VentilateurDTO;
import com.poulailler.intelligent.service.mapper.VentilateurMapper;
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
 * Service for executing complex queries for {@link Ventilateur} entities in the database.
 * The main input is a {@link VentilateurCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VentilateurDTO} or a {@link Page} of {@link VentilateurDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VentilateurQueryService extends QueryService<Ventilateur> {

    private final Logger log = LoggerFactory.getLogger(VentilateurQueryService.class);

    private final VentilateurRepository ventilateurRepository;

    private final VentilateurMapper ventilateurMapper;

    public VentilateurQueryService(VentilateurRepository ventilateurRepository, VentilateurMapper ventilateurMapper) {
        this.ventilateurRepository = ventilateurRepository;
        this.ventilateurMapper = ventilateurMapper;
    }

    /**
     * Return a {@link List} of {@link VentilateurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VentilateurDTO> findByCriteria(VentilateurCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ventilateur> specification = createSpecification(criteria);
        return ventilateurMapper.toDto(ventilateurRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VentilateurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VentilateurDTO> findByCriteria(VentilateurCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ventilateur> specification = createSpecification(criteria);
        return ventilateurRepository.findAll(specification, page).map(ventilateurMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VentilateurCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ventilateur> specification = createSpecification(criteria);
        return ventilateurRepository.count(specification);
    }

    /**
     * Function to convert {@link VentilateurCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ventilateur> createSpecification(VentilateurCriteria criteria) {
        Specification<Ventilateur> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ventilateur_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Ventilateur_.libelle));
            }
            if (criteria.getVitesse() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVitesse(), Ventilateur_.vitesse));
            }
        }
        return specification;
    }
}
