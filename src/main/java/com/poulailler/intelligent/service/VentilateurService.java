package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Ventilateur;
import com.poulailler.intelligent.repository.VentilateurRepository;
import com.poulailler.intelligent.service.dto.VentilateurDTO;
import com.poulailler.intelligent.service.mapper.VentilateurMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ventilateur}.
 */
@Service
@Transactional
public class VentilateurService {

    private final Logger log = LoggerFactory.getLogger(VentilateurService.class);

    private final VentilateurRepository ventilateurRepository;

    private final VentilateurMapper ventilateurMapper;

    public VentilateurService(VentilateurRepository ventilateurRepository, VentilateurMapper ventilateurMapper) {
        this.ventilateurRepository = ventilateurRepository;
        this.ventilateurMapper = ventilateurMapper;
    }

    /**
     * Save a ventilateur.
     *
     * @param ventilateurDTO the entity to save.
     * @return the persisted entity.
     */
    public VentilateurDTO save(VentilateurDTO ventilateurDTO) {
        log.debug("Request to save Ventilateur : {}", ventilateurDTO);
        Ventilateur ventilateur = ventilateurMapper.toEntity(ventilateurDTO);
        ventilateur = ventilateurRepository.save(ventilateur);
        return ventilateurMapper.toDto(ventilateur);
    }

    /**
     * Partially update a ventilateur.
     *
     * @param ventilateurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VentilateurDTO> partialUpdate(VentilateurDTO ventilateurDTO) {
        log.debug("Request to partially update Ventilateur : {}", ventilateurDTO);

        return ventilateurRepository
            .findById(ventilateurDTO.getId())
            .map(existingVentilateur -> {
                ventilateurMapper.partialUpdate(existingVentilateur, ventilateurDTO);

                return existingVentilateur;
            })
            .map(ventilateurRepository::save)
            .map(ventilateurMapper::toDto);
    }

    /**
     * Get all the ventilateurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VentilateurDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ventilateurs");
        return ventilateurRepository.findAll(pageable).map(ventilateurMapper::toDto);
    }

    /**
     * Get one ventilateur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VentilateurDTO> findOne(Long id) {
        log.debug("Request to get Ventilateur : {}", id);
        return ventilateurRepository.findById(id).map(ventilateurMapper::toDto);
    }

    /**
     * Delete the ventilateur by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Ventilateur : {}", id);
        ventilateurRepository.deleteById(id);
    }
}
