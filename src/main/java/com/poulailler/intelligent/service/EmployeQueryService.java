package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Employe;
import com.poulailler.intelligent.repository.EmployeRepository;
import com.poulailler.intelligent.service.criteria.EmployeCriteria;
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
 * Service for executing complex queries for {@link Employe} entities in the database.
 * The main input is a {@link EmployeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Employe} or a {@link Page} of {@link Employe} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmployeQueryService extends QueryService<Employe> {

    private final Logger log = LoggerFactory.getLogger(EmployeQueryService.class);

    private final EmployeRepository employeRepository;

    public EmployeQueryService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    /**
     * Return a {@link List} of {@link Employe} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Employe> findByCriteria(EmployeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Employe> specification = createSpecification(criteria);
        return employeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Employe} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Employe> findByCriteria(EmployeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Employe> specification = createSpecification(criteria);
        return employeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmployeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Employe> specification = createSpecification(criteria);
        return employeRepository.count(specification);
    }

    /**
     * Function to convert {@link EmployeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Employe> createSpecification(EmployeCriteria criteria) {
        Specification<Employe> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Employe_.id));
            }
            if (criteria.getNumeroIdentite() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNumeroIdentite(), Employe_.numeroIdentite));
            }
            if (criteria.getAdresse() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdresse(), Employe_.adresse));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Employe_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
