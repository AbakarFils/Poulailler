package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Lampe;
import com.poulailler.intelligent.repository.LampeRepository;
import com.poulailler.intelligent.service.criteria.LampeCriteria;
import com.poulailler.intelligent.service.dto.LampeDTO;
import com.poulailler.intelligent.service.mapper.LampeMapper;
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
 * Service for executing complex queries for {@link Lampe} entities in the database.
 * The main input is a {@link LampeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LampeDTO} or a {@link Page} of {@link LampeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LampeQueryService extends QueryService<Lampe> {

    private final Logger log = LoggerFactory.getLogger(LampeQueryService.class);

    private final LampeRepository lampeRepository;

    private final LampeMapper lampeMapper;

    public LampeQueryService(LampeRepository lampeRepository, LampeMapper lampeMapper) {
        this.lampeRepository = lampeRepository;
        this.lampeMapper = lampeMapper;
    }

    /**
     * Return a {@link List} of {@link LampeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LampeDTO> findByCriteria(LampeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Lampe> specification = createSpecification(criteria);
        return lampeMapper.toDto(lampeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LampeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LampeDTO> findByCriteria(LampeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Lampe> specification = createSpecification(criteria);
        return lampeRepository.findAll(specification, page).map(lampeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LampeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Lampe> specification = createSpecification(criteria);
        return lampeRepository.count(specification);
    }

    /**
     * Function to convert {@link LampeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Lampe> createSpecification(LampeCriteria criteria) {
        Specification<Lampe> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Lampe_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Lampe_.libelle));
            }
            if (criteria.getEquipementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEquipementId(),
                            root -> root.join(Lampe_.equipement, JoinType.LEFT).get(Equipement_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
