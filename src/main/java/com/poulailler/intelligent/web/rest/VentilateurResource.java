package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.VentilateurRepository;
import com.poulailler.intelligent.service.VentilateurQueryService;
import com.poulailler.intelligent.service.VentilateurService;
import com.poulailler.intelligent.service.criteria.VentilateurCriteria;
import com.poulailler.intelligent.service.dto.VentilateurDTO;
import com.poulailler.intelligent.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Ventilateur}.
 */
@RestController
@RequestMapping("/api")
public class VentilateurResource {

    private final Logger log = LoggerFactory.getLogger(VentilateurResource.class);

    private static final String ENTITY_NAME = "ventilateur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VentilateurService ventilateurService;

    private final VentilateurRepository ventilateurRepository;

    private final VentilateurQueryService ventilateurQueryService;

    public VentilateurResource(
        VentilateurService ventilateurService,
        VentilateurRepository ventilateurRepository,
        VentilateurQueryService ventilateurQueryService
    ) {
        this.ventilateurService = ventilateurService;
        this.ventilateurRepository = ventilateurRepository;
        this.ventilateurQueryService = ventilateurQueryService;
    }

    /**
     * {@code POST  /ventilateurs} : Create a new ventilateur.
     *
     * @param ventilateurDTO the ventilateurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ventilateurDTO, or with status {@code 400 (Bad Request)} if the ventilateur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ventilateurs")
    public ResponseEntity<VentilateurDTO> createVentilateur(@RequestBody VentilateurDTO ventilateurDTO) throws URISyntaxException {
        log.debug("REST request to save Ventilateur : {}", ventilateurDTO);
        if (ventilateurDTO.getId() != null) {
            throw new BadRequestAlertException("A new ventilateur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VentilateurDTO result = ventilateurService.save(ventilateurDTO);
        return ResponseEntity
            .created(new URI("/api/ventilateurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ventilateurs/:id} : Updates an existing ventilateur.
     *
     * @param id the id of the ventilateurDTO to save.
     * @param ventilateurDTO the ventilateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventilateurDTO,
     * or with status {@code 400 (Bad Request)} if the ventilateurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ventilateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ventilateurs/{id}")
    public ResponseEntity<VentilateurDTO> updateVentilateur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VentilateurDTO ventilateurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Ventilateur : {}, {}", id, ventilateurDTO);
        if (ventilateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventilateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ventilateurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VentilateurDTO result = ventilateurService.save(ventilateurDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ventilateurDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ventilateurs/:id} : Partial updates given fields of an existing ventilateur, field will ignore if it is null
     *
     * @param id the id of the ventilateurDTO to save.
     * @param ventilateurDTO the ventilateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ventilateurDTO,
     * or with status {@code 400 (Bad Request)} if the ventilateurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ventilateurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ventilateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ventilateurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VentilateurDTO> partialUpdateVentilateur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VentilateurDTO ventilateurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ventilateur partially : {}, {}", id, ventilateurDTO);
        if (ventilateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ventilateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ventilateurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VentilateurDTO> result = ventilateurService.partialUpdate(ventilateurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ventilateurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ventilateurs} : get all the ventilateurs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ventilateurs in body.
     */
    @GetMapping("/ventilateurs")
    public ResponseEntity<List<VentilateurDTO>> getAllVentilateurs(
        VentilateurCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Ventilateurs by criteria: {}", criteria);
        Page<VentilateurDTO> page = ventilateurQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ventilateurs/count} : count all the ventilateurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ventilateurs/count")
    public ResponseEntity<Long> countVentilateurs(VentilateurCriteria criteria) {
        log.debug("REST request to count Ventilateurs by criteria: {}", criteria);
        return ResponseEntity.ok().body(ventilateurQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ventilateurs/:id} : get the "id" ventilateur.
     *
     * @param id the id of the ventilateurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ventilateurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ventilateurs/{id}")
    public ResponseEntity<VentilateurDTO> getVentilateur(@PathVariable Long id) {
        log.debug("REST request to get Ventilateur : {}", id);
        Optional<VentilateurDTO> ventilateurDTO = ventilateurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ventilateurDTO);
    }

    /**
     * {@code DELETE  /ventilateurs/:id} : delete the "id" ventilateur.
     *
     * @param id the id of the ventilateurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ventilateurs/{id}")
    public ResponseEntity<Void> deleteVentilateur(@PathVariable Long id) {
        log.debug("REST request to delete Ventilateur : {}", id);
        ventilateurService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
