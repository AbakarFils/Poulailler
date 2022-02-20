package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.*; // for static metamodels
import com.poulailler.intelligent.domain.Serrure;
import com.poulailler.intelligent.repository.SerrureRepository;
import com.poulailler.intelligent.service.criteria.SerrureCriteria;
import com.poulailler.intelligent.service.dto.SerrureDTO;
import com.poulailler.intelligent.service.mapper.SerrureMapper;
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
 * Service for executing complex queries for {@link Serrure} entities in the database.
 * The main input is a {@link SerrureCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SerrureDTO} or a {@link Page} of {@link SerrureDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SerrureQueryService extends QueryService<Serrure> {

    private final Logger log = LoggerFactory.getLogger(SerrureQueryService.class);

    private final SerrureRepository serrureRepository;

    private final SerrureMapper serrureMapper;

    public SerrureQueryService(SerrureRepository serrureRepository, SerrureMapper serrureMapper) {
        this.serrureRepository = serrureRepository;
        this.serrureMapper = serrureMapper;
    }

    /**
     * Return a {@link List} of {@link SerrureDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SerrureDTO> findByCriteria(SerrureCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Serrure> specification = createSpecification(criteria);
        return serrureMapper.toDto(serrureRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SerrureDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SerrureDTO> findByCriteria(SerrureCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Serrure> specification = createSpecification(criteria);
        return serrureRepository.findAll(specification, page).map(serrureMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SerrureCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Serrure> specification = createSpecification(criteria);
        return serrureRepository.count(specification);
    }

    /**
     * Function to convert {@link SerrureCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Serrure> createSpecification(SerrureCriteria criteria) {
        Specification<Serrure> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Serrure_.id));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Serrure_.libelle));
            }
            if (criteria.getDimension() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDimension(), Serrure_.dimension));
            }
            if (criteria.getEquipementId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEquipementId(),
                            root -> root.join(Serrure_.equipement, JoinType.LEFT).get(Equipement_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
