package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Humidite;
import com.poulailler.intelligent.repository.HumiditeRepository;
import com.poulailler.intelligent.repository.VariableRepository;
import com.poulailler.intelligent.service.dto.HumiditeDTO;
import com.poulailler.intelligent.service.mapper.HumiditeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Humidite}.
 */
@Service
@Transactional
public class HumiditeService {

    private final Logger log = LoggerFactory.getLogger(HumiditeService.class);

    private final HumiditeRepository humiditeRepository;

    private final HumiditeMapper humiditeMapper;

    private final VariableRepository variableRepository;

    public HumiditeService(HumiditeRepository humiditeRepository, HumiditeMapper humiditeMapper, VariableRepository variableRepository) {
        this.humiditeRepository = humiditeRepository;
        this.humiditeMapper = humiditeMapper;
        this.variableRepository = variableRepository;
    }

    /**
     * Save a humidite.
     *
     * @param humiditeDTO the entity to save.
     * @return the persisted entity.
     */
    public HumiditeDTO save(HumiditeDTO humiditeDTO) {
        log.debug("Request to save Humidite : {}", humiditeDTO);
        Humidite humidite = humiditeMapper.toEntity(humiditeDTO);
        Long variableId = humiditeDTO.getVariable().getId();
        variableRepository.findById(variableId).ifPresent(humidite::variable);
        humidite = humiditeRepository.save(humidite);
        return humiditeMapper.toDto(humidite);
    }

    /**
     * Partially update a humidite.
     *
     * @param humiditeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HumiditeDTO> partialUpdate(HumiditeDTO humiditeDTO) {
        log.debug("Request to partially update Humidite : {}", humiditeDTO);

        return humiditeRepository
            .findById(humiditeDTO.getId())
            .map(existingHumidite -> {
                humiditeMapper.partialUpdate(existingHumidite, humiditeDTO);

                return existingHumidite;
            })
            .map(humiditeRepository::save)
            .map(humiditeMapper::toDto);
    }

    /**
     * Get all the humidites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HumiditeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Humidites");
        return humiditeRepository.findAll(pageable).map(humiditeMapper::toDto);
    }

    /**
     * Get one humidite by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HumiditeDTO> findOne(Long id) {
        log.debug("Request to get Humidite : {}", id);
        return humiditeRepository.findById(id).map(humiditeMapper::toDto);
    }

    /**
     * Delete the humidite by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Humidite : {}", id);
        humiditeRepository.deleteById(id);
    }
}
