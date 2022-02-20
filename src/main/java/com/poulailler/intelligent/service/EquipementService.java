package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.repository.EquipementRepository;
import com.poulailler.intelligent.service.dto.EquipementDTO;
import com.poulailler.intelligent.service.mapper.EquipementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Equipement}.
 */
@Service
@Transactional
public class EquipementService {

    private final Logger log = LoggerFactory.getLogger(EquipementService.class);

    private final EquipementRepository equipementRepository;

    private final EquipementMapper equipementMapper;

    public EquipementService(EquipementRepository equipementRepository, EquipementMapper equipementMapper) {
        this.equipementRepository = equipementRepository;
        this.equipementMapper = equipementMapper;
    }

    /**
     * Save a equipement.
     *
     * @param equipementDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipementDTO save(EquipementDTO equipementDTO) {
        log.debug("Request to save Equipement : {}", equipementDTO);
        Equipement equipement = equipementMapper.toEntity(equipementDTO);
        equipement = equipementRepository.save(equipement);
        return equipementMapper.toDto(equipement);
    }

    /**
     * Partially update a equipement.
     *
     * @param equipementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EquipementDTO> partialUpdate(EquipementDTO equipementDTO) {
        log.debug("Request to partially update Equipement : {}", equipementDTO);

        return equipementRepository
            .findById(equipementDTO.getId())
            .map(existingEquipement -> {
                equipementMapper.partialUpdate(existingEquipement, equipementDTO);

                return existingEquipement;
            })
            .map(equipementRepository::save)
            .map(equipementMapper::toDto);
    }

    /**
     * Get all the equipements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EquipementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Equipements");
        return equipementRepository.findAll(pageable).map(equipementMapper::toDto);
    }

    /**
     * Get all the equipements with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EquipementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipementRepository.findAllWithEagerRelationships(pageable).map(equipementMapper::toDto);
    }

    /**
     * Get one equipement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EquipementDTO> findOne(Long id) {
        log.debug("Request to get Equipement : {}", id);
        return equipementRepository.findOneWithEagerRelationships(id).map(equipementMapper::toDto);
    }

    /**
     * Delete the equipement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Equipement : {}", id);
        equipementRepository.deleteById(id);
    }
}
