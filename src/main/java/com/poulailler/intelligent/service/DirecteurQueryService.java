package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.repository.DirecteurRepository;
import com.poulailler.intelligent.service.criteria.DirecteurCriteria;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.service.mapper.DirecteurMapper;
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
 * Service for executing complex queries for {@link Directeur} entities in the database.
 * The main input is a {@link DirecteurCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DirecteurDTO} or a {@link Page} of {@link DirecteurDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DirecteurQueryService extends QueryService<Directeur> {

    private final Logger log = LoggerFactory.getLogger(DirecteurQueryService.class);

    private final DirecteurRepository directeurRepository;

    private final DirecteurMapper directeurMapper;

    public DirecteurQueryService(DirecteurRepository directeurRepository, DirecteurMapper directeurMapper) {
        this.directeurRepository = directeurRepository;
        this.directeurMapper = directeurMapper;
    }

    /**
     * Return a {@link List} of {@link DirecteurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DirecteurDTO> findByCriteria(DirecteurCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Directeur> specification = createSpecification(criteria);
        return directeurMapper.toDto(directeurRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DirecteurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DirecteurDTO> findByCriteria(DirecteurCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Directeur> specification = createSpecification(criteria);
        return directeurRepository.findAll(specification, page).map(directeurMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DirecteurCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Directeur> specification = createSpecification(criteria);
        return directeurRepository.count(specification);
    }

    /**
     * Function to convert {@link DirecteurCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Directeur> createSpecification(DirecteurCriteria criteria) {
        Specification<Directeur> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Directeur_.id));
            }
            if (criteria.getAdresse() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdresse(), Directeur_.adresse));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Directeur_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
