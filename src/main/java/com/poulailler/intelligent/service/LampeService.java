package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Lampe;
import com.poulailler.intelligent.repository.EquipementRepository;
import com.poulailler.intelligent.repository.LampeRepository;
import com.poulailler.intelligent.service.dto.LampeDTO;
import com.poulailler.intelligent.service.mapper.LampeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Lampe}.
 */
@Service
@Transactional
public class LampeService {

    private final Logger log = LoggerFactory.getLogger(LampeService.class);

    private final LampeRepository lampeRepository;

    private final LampeMapper lampeMapper;

    private final EquipementRepository equipementRepository;

    public LampeService(LampeRepository lampeRepository, LampeMapper lampeMapper, EquipementRepository equipementRepository) {
        this.lampeRepository = lampeRepository;
        this.lampeMapper = lampeMapper;
        this.equipementRepository = equipementRepository;
    }

    /**
     * Save a lampe.
     *
     * @param lampeDTO the entity to save.
     * @return the persisted entity.
     */
    public LampeDTO save(LampeDTO lampeDTO) {
        log.debug("Request to save Lampe : {}", lampeDTO);
        Lampe lampe = lampeMapper.toEntity(lampeDTO);
        Long equipementId = lampeDTO.getEquipement().getId();
        equipementRepository.findById(equipementId).ifPresent(lampe::equipement);
        lampe = lampeRepository.save(lampe);
        return lampeMapper.toDto(lampe);
    }

    /**
     * Partially update a lampe.
     *
     * @param lampeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LampeDTO> partialUpdate(LampeDTO lampeDTO) {
        log.debug("Request to partially update Lampe : {}", lampeDTO);

        return lampeRepository
            .findById(lampeDTO.getId())
            .map(existingLampe -> {
                lampeMapper.partialUpdate(existingLampe, lampeDTO);

                return existingLampe;
            })
            .map(lampeRepository::save)
            .map(lampeMapper::toDto);
    }

    /**
     * Get all the lampes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LampeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Lampes");
        return lampeRepository.findAll(pageable).map(lampeMapper::toDto);
    }

    /**
     * Get one lampe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LampeDTO> findOne(Long id) {
        log.debug("Request to get Lampe : {}", id);
        return lampeRepository.findById(id).map(lampeMapper::toDto);
    }

    /**
     * Delete the lampe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Lampe : {}", id);
        lampeRepository.deleteById(id);
    }
}
