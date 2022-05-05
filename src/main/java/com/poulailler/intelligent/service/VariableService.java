package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.domain.Temperature;
import com.poulailler.intelligent.repository.TemperatureRepository;
import com.poulailler.intelligent.repository.VariableRepository;
import com.poulailler.intelligent.service.dto.EquipementDTO;
import com.poulailler.intelligent.service.dto.TemperatureDTO;
import com.poulailler.intelligent.service.mapper.TemperatureMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Temperature}.
 */
@Service
@Transactional
public class TemperatureService {

    private final Logger log = LoggerFactory.getLogger(TemperatureService.class);

    private final TemperatureRepository temperatureRepository;

    private final TemperatureMapper temperatureMapper;

    private final VariableRepository variableRepository;

    public TemperatureService(
        TemperatureRepository temperatureRepository,
        TemperatureMapper temperatureMapper,
        VariableRepository variableRepository
    ) {
        this.temperatureRepository = temperatureRepository;
        this.temperatureMapper = temperatureMapper;
        this.variableRepository = variableRepository;
    }

    /**
     * Save a temperature.
     *
     * @param temperatureDTO the entity to save.
     * @return the persisted entity.
     */
    public TemperatureDTO save(TemperatureDTO temperatureDTO) {
        log.debug("Request to save Temperature : {}", temperatureDTO);
        Temperature temperature = temperatureMapper.toEntity(temperatureDTO);
        Long variableId = temperatureDTO.getVariable().getId();
        variableRepository.findById(variableId).ifPresent(temperature::variable);
        temperature = temperatureRepository.save(temperature);
        return temperatureMapper.toDto(temperature);
    }

    /**
     * Partially update a temperature.
     *
     * @param temperatureDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TemperatureDTO> partialUpdate(TemperatureDTO temperatureDTO) {
        log.debug("Request to partially update Temperature : {}", temperatureDTO);

        return temperatureRepository
            .findById(temperatureDTO.getId())
            .map(existingTemperature -> {
                temperatureMapper.partialUpdate(existingTemperature, temperatureDTO);

                return existingTemperature;
            })
            .map(temperatureRepository::save)
            .map(temperatureMapper::toDto);
    }

    /**
     * Get all the temperatures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TemperatureDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Temperatures");
        return temperatureRepository.findAll(pageable).map(temperatureMapper::toDto);
    }

    /**
     * Get one temperature by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TemperatureDTO> findOne(Long id) {
        log.debug("Request to get Temperature : {}", id);
        return temperatureRepository.findById(id).map(temperatureMapper::toDto);
    }

    /**
     * Delete the temperature by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Temperature : {}", id);
        temperatureRepository.deleteById(id);
    }
}
