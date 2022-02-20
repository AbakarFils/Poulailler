package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.OeufRepository;
import com.poulailler.intelligent.service.OeufQueryService;
import com.poulailler.intelligent.service.OeufService;
import com.poulailler.intelligent.service.criteria.OeufCriteria;
import com.poulailler.intelligent.service.dto.OeufDTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Oeuf}.
 */
@RestController
@RequestMapping("/api")
public class OeufResource {

    private final Logger log = LoggerFactory.getLogger(OeufResource.class);

    private static final String ENTITY_NAME = "oeuf";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OeufService oeufService;

    private final OeufRepository oeufRepository;

    private final OeufQueryService oeufQueryService;

    public OeufResource(OeufService oeufService, OeufRepository oeufRepository, OeufQueryService oeufQueryService) {
        this.oeufService = oeufService;
        this.oeufRepository = oeufRepository;
        this.oeufQueryService = oeufQueryService;
    }

    /**
     * {@code POST  /oeufs} : Create a new oeuf.
     *
     * @param oeufDTO the oeufDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oeufDTO, or with status {@code 400 (Bad Request)} if the oeuf has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/oeufs")
    public ResponseEntity<OeufDTO> createOeuf(@Valid @RequestBody OeufDTO oeufDTO) throws URISyntaxException {
        log.debug("REST request to save Oeuf : {}", oeufDTO);
        if (oeufDTO.getId() != null) {
            throw new BadRequestAlertException("A new oeuf cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(oeufDTO.getVariable())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        OeufDTO result = oeufService.save(oeufDTO);
        return ResponseEntity
            .created(new URI("/api/oeufs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /oeufs/:id} : Updates an existing oeuf.
     *
     * @param id the id of the oeufDTO to save.
     * @param oeufDTO the oeufDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oeufDTO,
     * or with status {@code 400 (Bad Request)} if the oeufDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oeufDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/oeufs/{id}")
    public ResponseEntity<OeufDTO> updateOeuf(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OeufDTO oeufDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Oeuf : {}, {}", id, oeufDTO);
        if (oeufDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oeufDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oeufRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OeufDTO result = oeufService.save(oeufDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oeufDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /oeufs/:id} : Partial updates given fields of an existing oeuf, field will ignore if it is null
     *
     * @param id the id of the oeufDTO to save.
     * @param oeufDTO the oeufDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oeufDTO,
     * or with status {@code 400 (Bad Request)} if the oeufDTO is not valid,
     * or with status {@code 404 (Not Found)} if the oeufDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the oeufDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/oeufs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OeufDTO> partialUpdateOeuf(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OeufDTO oeufDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Oeuf partially : {}, {}", id, oeufDTO);
        if (oeufDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oeufDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oeufRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OeufDTO> result = oeufService.partialUpdate(oeufDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oeufDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /oeufs} : get all the oeufs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oeufs in body.
     */
    @GetMapping("/oeufs")
    public ResponseEntity<List<OeufDTO>> getAllOeufs(
        OeufCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Oeufs by criteria: {}", criteria);
        Page<OeufDTO> page = oeufQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /oeufs/count} : count all the oeufs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/oeufs/count")
    public ResponseEntity<Long> countOeufs(OeufCriteria criteria) {
        log.debug("REST request to count Oeufs by criteria: {}", criteria);
        return ResponseEntity.ok().body(oeufQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /oeufs/:id} : get the "id" oeuf.
     *
     * @param id the id of the oeufDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oeufDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/oeufs/{id}")
    public ResponseEntity<OeufDTO> getOeuf(@PathVariable Long id) {
        log.debug("REST request to get Oeuf : {}", id);
        Optional<OeufDTO> oeufDTO = oeufService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oeufDTO);
    }

    /**
     * {@code DELETE  /oeufs/:id} : delete the "id" oeuf.
     *
     * @param id the id of the oeufDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/oeufs/{id}")
    public ResponseEntity<Void> deleteOeuf(@PathVariable Long id) {
        log.debug("REST request to delete Oeuf : {}", id);
        oeufService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
