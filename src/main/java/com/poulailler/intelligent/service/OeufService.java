package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Oeuf;
import com.poulailler.intelligent.repository.OeufRepository;
import com.poulailler.intelligent.repository.VariableRepository;
import com.poulailler.intelligent.service.dto.OeufDTO;
import com.poulailler.intelligent.service.mapper.OeufMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Oeuf}.
 */
@Service
@Transactional
public class OeufService {

    private final Logger log = LoggerFactory.getLogger(OeufService.class);

    private final OeufRepository oeufRepository;

    private final OeufMapper oeufMapper;

    private final VariableRepository variableRepository;

    public OeufService(OeufRepository oeufRepository, OeufMapper oeufMapper, VariableRepository variableRepository) {
        this.oeufRepository = oeufRepository;
        this.oeufMapper = oeufMapper;
        this.variableRepository = variableRepository;
    }

    /**
     * Save a oeuf.
     *
     * @param oeufDTO the entity to save.
     * @return the persisted entity.
     */
    public OeufDTO save(OeufDTO oeufDTO) {
        log.debug("Request to save Oeuf : {}", oeufDTO);
        Oeuf oeuf = oeufMapper.toEntity(oeufDTO);
        Long variableId = oeufDTO.getVariable().getId();
        variableRepository.findById(variableId).ifPresent(oeuf::variable);
        oeuf = oeufRepository.save(oeuf);
        return oeufMapper.toDto(oeuf);
    }

    /**
     * Partially update a oeuf.
     *
     * @param oeufDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OeufDTO> partialUpdate(OeufDTO oeufDTO) {
        log.debug("Request to partially update Oeuf : {}", oeufDTO);

        return oeufRepository
            .findById(oeufDTO.getId())
            .map(existingOeuf -> {
                oeufMapper.partialUpdate(existingOeuf, oeufDTO);

                return existingOeuf;
            })
            .map(oeufRepository::save)
            .map(oeufMapper::toDto);
    }

    /**
     * Get all the oeufs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OeufDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Oeufs");
        return oeufRepository.findAll(pageable).map(oeufMapper::toDto);
    }

    /**
     * Get one oeuf by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OeufDTO> findOne(Long id) {
        log.debug("Request to get Oeuf : {}", id);
        return oeufRepository.findById(id).map(oeufMapper::toDto);
    }

    /**
     * Delete the oeuf by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Oeuf : {}", id);
        oeufRepository.deleteById(id);
    }
}
