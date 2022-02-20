package com.poulailler.intelligent.service;

import com.poulailler.intelligent.domain.Employe;
import com.poulailler.intelligent.repository.EmployeRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Employe}.
 */
@Service
@Transactional
public class EmployeService {

    private final Logger log = LoggerFactory.getLogger(EmployeService.class);

    private final EmployeRepository employeRepository;

    public EmployeService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    /**
     * Save a employe.
     *
     * @param employe the entity to save.
     * @return the persisted entity.
     */
    public Employe save(Employe employe) {
        log.debug("Request to save Employe : {}", employe);
        return employeRepository.save(employe);
    }

    /**
     * Partially update a employe.
     *
     * @param employe the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Employe> partialUpdate(Employe employe) {
        log.debug("Request to partially update Employe : {}", employe);

        return employeRepository
            .findById(employe.getId())
            .map(existingEmploye -> {
                if (employe.getNumeroIdentite() != null) {
                    existingEmploye.setNumeroIdentite(employe.getNumeroIdentite());
                }
                if (employe.getAdresse() != null) {
                    existingEmploye.setAdresse(employe.getAdresse());
                }

                return existingEmploye;
            })
            .map(employeRepository::save);
    }

    /**
     * Get all the employes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Employe> findAll(Pageable pageable) {
        log.debug("Request to get all Employes");
        return employeRepository.findAll(pageable);
    }

    /**
     * Get one employe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Employe> findOne(Long id) {
        log.debug("Request to get Employe : {}", id);
        return employeRepository.findById(id);
    }

    /**
     * Delete the employe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Employe : {}", id);
        employeRepository.deleteById(id);
    }
}
