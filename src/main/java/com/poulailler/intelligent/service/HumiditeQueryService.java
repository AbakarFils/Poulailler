package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Humidite;
import com.poulailler.intelligent.repository.HumiditeRepository;
import com.poulailler.intelligent.service.criteria.HumiditeCriteria;
import com.poulailler.intelligent.service.dto.HumiditeDTO;
import com.poulailler.intelligent.service.mapper.HumiditeMapper;
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
 * Service for executing complex queries for {@link Humidite} entities in the database.
 * The main input is a {@link HumiditeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link HumiditeDTO} or a {@link Page} of {@link HumiditeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HumiditeQueryService extends QueryService<Humidite> {

    private final Logger log = LoggerFactory.getLogger(HumiditeQueryService.class);

    private final HumiditeRepository humiditeRepository;

    private final HumiditeMapper humiditeMapper;

    public HumiditeQueryService(HumiditeRepository humiditeRepository, HumiditeMapper humiditeMapper) {
        this.humiditeRepository = humiditeRepository;
        this.humiditeMapper = humiditeMapper;
    }

    /**
     * Return a {@link List} of {@link HumiditeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<HumiditeDTO> findByCriteria(HumiditeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Humidite> specification = createSpecification(criteria);
        return humiditeMapper.toDto(humiditeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link HumiditeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HumiditeDTO> findByCriteria(HumiditeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Humidite> specification = createSpecification(criteria);
        return humiditeRepository.findAll(specification, page).map(humiditeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HumiditeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Humidite> specification = createSpecification(criteria);
        return humiditeRepository.count(specification);
    }

    /**
     * Function to convert {@link HumiditeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Humidite> createSpecification(HumiditeCriteria criteria) {
        Specification<Humidite> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Humidite_.id));
            }
            if (criteria.getNiveau() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNiveau(), Humidite_.niveau));
            }
            if (criteria.getVariableId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVariableId(), root -> root.join(Humidite_.variable, JoinType.LEFT).get(Variable_.id))
                    );
            }
        }
        return specification;
    }
}
