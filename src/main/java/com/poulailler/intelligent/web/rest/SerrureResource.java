package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.SerrureRepository;
import com.poulailler.intelligent.service.SerrureQueryService;
import com.poulailler.intelligent.service.SerrureService;
import com.poulailler.intelligent.service.criteria.SerrureCriteria;
import com.poulailler.intelligent.service.dto.SerrureDTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Serrure}.
 */
@RestController
@RequestMapping("/api")
public class SerrureResource {

    private final Logger log = LoggerFactory.getLogger(SerrureResource.class);

    private static final String ENTITY_NAME = "serrure";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SerrureService serrureService;

    private final SerrureRepository serrureRepository;

    private final SerrureQueryService serrureQueryService;

    public SerrureResource(SerrureService serrureService, SerrureRepository serrureRepository, SerrureQueryService serrureQueryService) {
        this.serrureService = serrureService;
        this.serrureRepository = serrureRepository;
        this.serrureQueryService = serrureQueryService;
    }

    /**
     * {@code POST  /serrures} : Create a new serrure.
     *
     * @param serrureDTO the serrureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serrureDTO, or with status {@code 400 (Bad Request)} if the serrure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/serrures")
    public ResponseEntity<SerrureDTO> createSerrure(@RequestBody SerrureDTO serrureDTO) throws URISyntaxException {
        log.debug("REST request to save Serrure : {}", serrureDTO);
        if (serrureDTO.getId() != null) {
            throw new BadRequestAlertException("A new serrure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(serrureDTO.getEquipement())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        SerrureDTO result = serrureService.save(serrureDTO);
        return ResponseEntity
            .created(new URI("/api/serrures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /serrures/:id} : Updates an existing serrure.
     *
     * @param id the id of the serrureDTO to save.
     * @param serrureDTO the serrureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serrureDTO,
     * or with status {@code 400 (Bad Request)} if the serrureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serrureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/serrures/{id}")
    public ResponseEntity<SerrureDTO> updateSerrure(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SerrureDTO serrureDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Serrure : {}, {}", id, serrureDTO);
        if (serrureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serrureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serrureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SerrureDTO result = serrureService.save(serrureDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serrureDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /serrures/:id} : Partial updates given fields of an existing serrure, field will ignore if it is null
     *
     * @param id the id of the serrureDTO to save.
     * @param serrureDTO the serrureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serrureDTO,
     * or with status {@code 400 (Bad Request)} if the serrureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the serrureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the serrureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/serrures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SerrureDTO> partialUpdateSerrure(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SerrureDTO serrureDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Serrure partially : {}, {}", id, serrureDTO);
        if (serrureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serrureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serrureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SerrureDTO> result = serrureService.partialUpdate(serrureDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serrureDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /serrures} : get all the serrures.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serrures in body.
     */
    @GetMapping("/serrures")
    public ResponseEntity<List<SerrureDTO>> getAllSerrures(
        SerrureCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Serrures by criteria: {}", criteria);
        Page<SerrureDTO> page = serrureQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /serrures/count} : count all the serrures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/serrures/count")
    public ResponseEntity<Long> countSerrures(SerrureCriteria criteria) {
        log.debug("REST request to count Serrures by criteria: {}", criteria);
        return ResponseEntity.ok().body(serrureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /serrures/:id} : get the "id" serrure.
     *
     * @param id the id of the serrureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serrureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/serrures/{id}")
    public ResponseEntity<SerrureDTO> getSerrure(@PathVariable Long id) {
        log.debug("REST request to get Serrure : {}", id);
        Optional<SerrureDTO> serrureDTO = serrureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serrureDTO);
    }

    /**
     * {@code DELETE  /serrures/:id} : delete the "id" serrure.
     *
     * @param id the id of the serrureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/serrures/{id}")
    public ResponseEntity<Void> deleteSerrure(@PathVariable Long id) {
        log.debug("REST request to delete Serrure : {}", id);
        serrureService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
