package com.poulailler.intelligent.service;

import com.poulailler.intelligent.service.dto.DirecteurDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.poulailler.intelligent.domain.Directeur}.
 */
public interface DirecteurService {
    /**
     * Save a directeur.
     *
     * @param directeurDTO the entity to save.
     * @return the persisted entity.
     */
    DirecteurDTO save(DirecteurDTO directeurDTO);

    /**
     * Partially updates a directeur.
     *
     * @param directeurDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DirecteurDTO> partialUpdate(DirecteurDTO directeurDTO);

    /**
     * Get all the directeurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DirecteurDTO> findAll(Pageable pageable);

    /**
     * Get the "id" directeur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DirecteurDTO> findOne(Long id);

    /**
     * Delete the "id" directeur.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
