package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.NH3;
import com.poulailler.intelligent.repository.NH3Repository;
import com.poulailler.intelligent.repository.VariableRepository;
import com.poulailler.intelligent.service.dto.NH3DTO;
import com.poulailler.intelligent.service.mapper.NH3Mapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link NH3}.
 */
@Service
@Transactional
public class NH3Service {

    private final Logger log = LoggerFactory.getLogger(NH3Service.class);

    private final NH3Repository nH3Repository;

    private final NH3Mapper nH3Mapper;

    private final VariableRepository variableRepository;

    public NH3Service(NH3Repository nH3Repository, NH3Mapper nH3Mapper, VariableRepository variableRepository) {
        this.nH3Repository = nH3Repository;
        this.nH3Mapper = nH3Mapper;
        this.variableRepository = variableRepository;
    }

    /**
     * Save a nH3.
     *
     * @param nH3DTO the entity to save.
     * @return the persisted entity.
     */
    public NH3DTO save(NH3DTO nH3DTO) {
        log.debug("Request to save NH3 : {}", nH3DTO);
        NH3 nH3 = nH3Mapper.toEntity(nH3DTO);
        Long variableId = nH3DTO.getVariable().getId();
        variableRepository.findById(variableId).ifPresent(nH3::variable);
        nH3 = nH3Repository.save(nH3);
        return nH3Mapper.toDto(nH3);
    }

    /**
     * Partially update a nH3.
     *
     * @param nH3DTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NH3DTO> partialUpdate(NH3DTO nH3DTO) {
        log.debug("Request to partially update NH3 : {}", nH3DTO);

        return nH3Repository
            .findById(nH3DTO.getId())
            .map(existingNH3 -> {
                nH3Mapper.partialUpdate(existingNH3, nH3DTO);

                return existingNH3;
            })
            .map(nH3Repository::save)
            .map(nH3Mapper::toDto);
    }

    /**
     * Get all the nH3s.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NH3DTO> findAll(Pageable pageable) {
        log.debug("Request to get all NH3s");
        return nH3Repository.findAll(pageable).map(nH3Mapper::toDto);
    }

    /**
     * Get one nH3 by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NH3DTO> findOne(Long id) {
        log.debug("Request to get NH3 : {}", id);
        return nH3Repository.findById(id).map(nH3Mapper::toDto);
    }

    /**
     * Delete the nH3 by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete NH3 : {}", id);
        nH3Repository.deleteById(id);
    }
}
