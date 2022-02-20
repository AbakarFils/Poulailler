package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.DirecteurRepository;
import com.poulailler.intelligent.service.DirecteurQueryService;
import com.poulailler.intelligent.service.DirecteurService;
import com.poulailler.intelligent.service.criteria.DirecteurCriteria;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.poulailler.intelligent.domain.Directeur}.
 */
@RestController
@RequestMapping("/api")
public class DirecteurResource {

    private final Logger log = LoggerFactory.getLogger(DirecteurResource.class);

    private static final String ENTITY_NAME = "directeur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DirecteurService directeurService;

    private final DirecteurRepository directeurRepository;

    private final DirecteurQueryService directeurQueryService;

    public DirecteurResource(
        DirecteurService directeurService,
        DirecteurRepository directeurRepository,
        DirecteurQueryService directeurQueryService
    ) {
        this.directeurService = directeurService;
        this.directeurRepository = directeurRepository;
        this.directeurQueryService = directeurQueryService;
    }

    /**
     * {@code POST  /directeurs} : Create a new directeur.
     *
     * @param directeurDTO the directeurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new directeurDTO, or with status {@code 400 (Bad Request)} if the directeur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/directeurs")
    public ResponseEntity<DirecteurDTO> createDirecteur(@Valid @RequestBody DirecteurDTO directeurDTO) throws URISyntaxException {
        log.debug("REST request to save Directeur : {}", directeurDTO);
        if (directeurDTO.getId() != null) {
            throw new BadRequestAlertException("A new directeur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(directeurDTO.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        DirecteurDTO result = directeurService.save(directeurDTO);
        return ResponseEntity
            .created(new URI("/api/directeurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /directeurs/:id} : Updates an existing directeur.
     *
     * @param id the id of the directeurDTO to save.
     * @param directeurDTO the directeurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directeurDTO,
     * or with status {@code 400 (Bad Request)} if the directeurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the directeurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/directeurs/{id}")
    public ResponseEntity<DirecteurDTO> updateDirecteur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DirecteurDTO directeurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Directeur : {}, {}", id, directeurDTO);
        if (directeurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directeurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!directeurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DirecteurDTO result = directeurService.save(directeurDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, directeurDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /directeurs/:id} : Partial updates given fields of an existing directeur, field will ignore if it is null
     *
     * @param id the id of the directeurDTO to save.
     * @param directeurDTO the directeurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directeurDTO,
     * or with status {@code 400 (Bad Request)} if the directeurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the directeurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the directeurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/directeurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DirecteurDTO> partialUpdateDirecteur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DirecteurDTO directeurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Directeur partially : {}, {}", id, directeurDTO);
        if (directeurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directeurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!directeurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DirecteurDTO> result = directeurService.partialUpdate(directeurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, directeurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /directeurs} : get all the directeurs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of directeurs in body.
     */
    @GetMapping("/directeurs")
    public ResponseEntity<List<DirecteurDTO>> getAllDirecteurs(
        DirecteurCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Directeurs by criteria: {}", criteria);
        Page<DirecteurDTO> page = directeurQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /directeurs/count} : count all the directeurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/directeurs/count")
    public ResponseEntity<Long> countDirecteurs(DirecteurCriteria criteria) {
        log.debug("REST request to count Directeurs by criteria: {}", criteria);
        return ResponseEntity.ok().body(directeurQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /directeurs/:id} : get the "id" directeur.
     *
     * @param id the id of the directeurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the directeurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/directeurs/{id}")
    public ResponseEntity<DirecteurDTO> getDirecteur(@PathVariable Long id) {
        log.debug("REST request to get Directeur : {}", id);
        Optional<DirecteurDTO> directeurDTO = directeurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(directeurDTO);
    }

    /**
     * {@code DELETE  /directeurs/:id} : delete the "id" directeur.
     *
     * @param id the id of the directeurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/directeurs/{id}")
    public ResponseEntity<Void> deleteDirecteur(@PathVariable Long id) {
        log.debug("REST request to delete Directeur : {}", id);
        directeurService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
