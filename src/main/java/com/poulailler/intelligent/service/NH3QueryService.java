package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.NH3;
import com.poulailler.intelligent.repository.NH3Repository;
import com.poulailler.intelligent.service.criteria.NH3Criteria;
import com.poulailler.intelligent.service.dto.NH3DTO;
import com.poulailler.intelligent.service.mapper.NH3Mapper;
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
 * Service for executing complex queries for {@link NH3} entities in the database.
 * The main input is a {@link NH3Criteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NH3DTO} or a {@link Page} of {@link NH3DTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NH3QueryService extends QueryService<NH3> {

    private final Logger log = LoggerFactory.getLogger(NH3QueryService.class);

    private final NH3Repository nH3Repository;

    private final NH3Mapper nH3Mapper;

    public NH3QueryService(NH3Repository nH3Repository, NH3Mapper nH3Mapper) {
        this.nH3Repository = nH3Repository;
        this.nH3Mapper = nH3Mapper;
    }

    /**
     * Return a {@link List} of {@link NH3DTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NH3DTO> findByCriteria(NH3Criteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<NH3> specification = createSpecification(criteria);
        return nH3Mapper.toDto(nH3Repository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link NH3DTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NH3DTO> findByCriteria(NH3Criteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NH3> specification = createSpecification(criteria);
        return nH3Repository.findAll(specification, page).map(nH3Mapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NH3Criteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<NH3> specification = createSpecification(criteria);
        return nH3Repository.count(specification);
    }

    /**
     * Function to convert {@link NH3Criteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NH3> createSpecification(NH3Criteria criteria) {
        Specification<NH3> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), NH3_.id));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), NH3_.volume));
            }
            if (criteria.getVariableId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVariableId(), root -> root.join(NH3_.variable, JoinType.LEFT).get(Variable_.id))
                    );
            }
        }
        return specification;
    }
}
