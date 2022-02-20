package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Serrure;
import com.poulailler.intelligent.repository.EquipementRepository;
import com.poulailler.intelligent.repository.SerrureRepository;
import com.poulailler.intelligent.service.dto.SerrureDTO;
import com.poulailler.intelligent.service.mapper.SerrureMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Serrure}.
 */
@Service
@Transactional
public class SerrureService {

    private final Logger log = LoggerFactory.getLogger(SerrureService.class);

    private final SerrureRepository serrureRepository;

    private final SerrureMapper serrureMapper;

    private final EquipementRepository equipementRepository;

    public SerrureService(SerrureRepository serrureRepository, SerrureMapper serrureMapper, EquipementRepository equipementRepository) {
        this.serrureRepository = serrureRepository;
        this.serrureMapper = serrureMapper;
        this.equipementRepository = equipementRepository;
    }

    /**
     * Save a serrure.
     *
     * @param serrureDTO the entity to save.
     * @return the persisted entity.
     */
    public SerrureDTO save(SerrureDTO serrureDTO) {
        log.debug("Request to save Serrure : {}", serrureDTO);
        Serrure serrure = serrureMapper.toEntity(serrureDTO);
        Long equipementId = serrureDTO.getEquipement().getId();
        equipementRepository.findById(equipementId).ifPresent(serrure::equipement);
        serrure = serrureRepository.save(serrure);
        return serrureMapper.toDto(serrure);
    }

    /**
     * Partially update a serrure.
     *
     * @param serrureDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SerrureDTO> partialUpdate(SerrureDTO serrureDTO) {
        log.debug("Request to partially update Serrure : {}", serrureDTO);

        return serrureRepository
            .findById(serrureDTO.getId())
            .map(existingSerrure -> {
                serrureMapper.partialUpdate(existingSerrure, serrureDTO);

                return existingSerrure;
            })
            .map(serrureRepository::save)
            .map(serrureMapper::toDto);
    }

    /**
     * Get all the serrures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SerrureDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Serrures");
        return serrureRepository.findAll(pageable).map(serrureMapper::toDto);
    }

    /**
     * Get one serrure by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SerrureDTO> findOne(Long id) {
        log.debug("Request to get Serrure : {}", id);
        return serrureRepository.findById(id).map(serrureMapper::toDto);
    }

    /**
     * Delete the serrure by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Serrure : {}", id);
        serrureRepository.deleteById(id);
    }
}
