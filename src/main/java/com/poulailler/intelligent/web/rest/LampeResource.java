package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.LampeRepository;
import com.poulailler.intelligent.service.LampeQueryService;
import com.poulailler.intelligent.service.LampeService;
import com.poulailler.intelligent.service.criteria.LampeCriteria;
import com.poulailler.intelligent.service.dto.LampeDTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Lampe}.
 */
@RestController
@RequestMapping("/api")
public class LampeResource {

    private final Logger log = LoggerFactory.getLogger(LampeResource.class);

    private static final String ENTITY_NAME = "lampe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LampeService lampeService;

    private final LampeRepository lampeRepository;

    private final LampeQueryService lampeQueryService;

    public LampeResource(LampeService lampeService, LampeRepository lampeRepository, LampeQueryService lampeQueryService) {
        this.lampeService = lampeService;
        this.lampeRepository = lampeRepository;
        this.lampeQueryService = lampeQueryService;
    }

    /**
     * {@code POST  /lampes} : Create a new lampe.
     *
     * @param lampeDTO the lampeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lampeDTO, or with status {@code 400 (Bad Request)} if the lampe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lampes")
    public ResponseEntity<LampeDTO> createLampe(@RequestBody LampeDTO lampeDTO) throws URISyntaxException {
        log.debug("REST request to save Lampe : {}", lampeDTO);
        if (lampeDTO.getId() != null) {
            throw new BadRequestAlertException("A new lampe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(lampeDTO.getEquipement())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        LampeDTO result = lampeService.save(lampeDTO);
        return ResponseEntity
            .created(new URI("/api/lampes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lampes/:id} : Updates an existing lampe.
     *
     * @param id the id of the lampeDTO to save.
     * @param lampeDTO the lampeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lampeDTO,
     * or with status {@code 400 (Bad Request)} if the lampeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lampeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lampes/{id}")
    public ResponseEntity<LampeDTO> updateLampe(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LampeDTO lampeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Lampe : {}, {}", id, lampeDTO);
        if (lampeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lampeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lampeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LampeDTO result = lampeService.save(lampeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lampeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lampes/:id} : Partial updates given fields of an existing lampe, field will ignore if it is null
     *
     * @param id the id of the lampeDTO to save.
     * @param lampeDTO the lampeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lampeDTO,
     * or with status {@code 400 (Bad Request)} if the lampeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lampeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lampeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lampes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LampeDTO> partialUpdateLampe(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LampeDTO lampeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lampe partially : {}, {}", id, lampeDTO);
        if (lampeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lampeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lampeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LampeDTO> result = lampeService.partialUpdate(lampeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lampeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lampes} : get all the lampes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lampes in body.
     */
    @GetMapping("/lampes")
    public ResponseEntity<List<LampeDTO>> getAllLampes(
        LampeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Lampes by criteria: {}", criteria);
        Page<LampeDTO> page = lampeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lampes/count} : count all the lampes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/lampes/count")
    public ResponseEntity<Long> countLampes(LampeCriteria criteria) {
        log.debug("REST request to count Lampes by criteria: {}", criteria);
        return ResponseEntity.ok().body(lampeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lampes/:id} : get the "id" lampe.
     *
     * @param id the id of the lampeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lampeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lampes/{id}")
    public ResponseEntity<LampeDTO> getLampe(@PathVariable Long id) {
        log.debug("REST request to get Lampe : {}", id);
        Optional<LampeDTO> lampeDTO = lampeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lampeDTO);
    }

    /**
     * {@code DELETE  /lampes/:id} : delete the "id" lampe.
     *
     * @param id the id of the lampeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lampes/{id}")
    public ResponseEntity<Void> deleteLampe(@PathVariable Long id) {
        log.debug("REST request to delete Lampe : {}", id);
        lampeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
