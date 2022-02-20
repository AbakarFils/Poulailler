package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Oeuf;
import com.poulailler.intelligent.repository.OeufRepository;
import com.poulailler.intelligent.service.criteria.OeufCriteria;
import com.poulailler.intelligent.service.dto.OeufDTO;
import com.poulailler.intelligent.service.mapper.OeufMapper;
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
 * Service for executing complex queries for {@link Oeuf} entities in the database.
 * The main input is a {@link OeufCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OeufDTO} or a {@link Page} of {@link OeufDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OeufQueryService extends QueryService<Oeuf> {

    private final Logger log = LoggerFactory.getLogger(OeufQueryService.class);

    private final OeufRepository oeufRepository;

    private final OeufMapper oeufMapper;

    public OeufQueryService(OeufRepository oeufRepository, OeufMapper oeufMapper) {
        this.oeufRepository = oeufRepository;
        this.oeufMapper = oeufMapper;
    }

    /**
     * Return a {@link List} of {@link OeufDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OeufDTO> findByCriteria(OeufCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Oeuf> specification = createSpecification(criteria);
        return oeufMapper.toDto(oeufRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OeufDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OeufDTO> findByCriteria(OeufCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Oeuf> specification = createSpecification(criteria);
        return oeufRepository.findAll(specification, page).map(oeufMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OeufCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Oeuf> specification = createSpecification(criteria);
        return oeufRepository.count(specification);
    }

    /**
     * Function to convert {@link OeufCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Oeuf> createSpecification(OeufCriteria criteria) {
        Specification<Oeuf> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Oeuf_.id));
            }
            if (criteria.getNombreJournalier() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNombreJournalier(), Oeuf_.nombreJournalier));
            }
            if (criteria.getVariableId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVariableId(), root -> root.join(Oeuf_.variable, JoinType.LEFT).get(Variable_.id))
                    );
            }
        }
        return specification;
    }
}
