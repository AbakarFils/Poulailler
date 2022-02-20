package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.NH3Repository;
import com.poulailler.intelligent.service.NH3QueryService;
import com.poulailler.intelligent.service.NH3Service;
import com.poulailler.intelligent.service.criteria.NH3Criteria;
import com.poulailler.intelligent.service.dto.NH3DTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.NH3}.
 */
@RestController
@RequestMapping("/api")
public class NH3Resource {

    private final Logger log = LoggerFactory.getLogger(NH3Resource.class);

    private static final String ENTITY_NAME = "nH3";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NH3Service nH3Service;

    private final NH3Repository nH3Repository;

    private final NH3QueryService nH3QueryService;

    public NH3Resource(NH3Service nH3Service, NH3Repository nH3Repository, NH3QueryService nH3QueryService) {
        this.nH3Service = nH3Service;
        this.nH3Repository = nH3Repository;
        this.nH3QueryService = nH3QueryService;
    }

    /**
     * {@code POST  /nh-3-s} : Create a new nH3.
     *
     * @param nH3DTO the nH3DTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nH3DTO, or with status {@code 400 (Bad Request)} if the nH3 has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/nh-3-s")
    public ResponseEntity<NH3DTO> createNH3(@Valid @RequestBody NH3DTO nH3DTO) throws URISyntaxException {
        log.debug("REST request to save NH3 : {}", nH3DTO);
        if (nH3DTO.getId() != null) {
            throw new BadRequestAlertException("A new nH3 cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(nH3DTO.getVariable())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        NH3DTO result = nH3Service.save(nH3DTO);
        return ResponseEntity
            .created(new URI("/api/nh-3-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /nh-3-s/:id} : Updates an existing nH3.
     *
     * @param id the id of the nH3DTO to save.
     * @param nH3DTO the nH3DTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nH3DTO,
     * or with status {@code 400 (Bad Request)} if the nH3DTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the nH3DTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/nh-3-s/{id}")
    public ResponseEntity<NH3DTO> updateNH3(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody NH3DTO nH3DTO)
        throws URISyntaxException {
        log.debug("REST request to update NH3 : {}, {}", id, nH3DTO);
        if (nH3DTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nH3DTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nH3Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NH3DTO result = nH3Service.save(nH3DTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nH3DTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /nh-3-s/:id} : Partial updates given fields of an existing nH3, field will ignore if it is null
     *
     * @param id the id of the nH3DTO to save.
     * @param nH3DTO the nH3DTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nH3DTO,
     * or with status {@code 400 (Bad Request)} if the nH3DTO is not valid,
     * or with status {@code 404 (Not Found)} if the nH3DTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the nH3DTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/nh-3-s/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NH3DTO> partialUpdateNH3(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NH3DTO nH3DTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NH3 partially : {}, {}", id, nH3DTO);
        if (nH3DTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nH3DTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nH3Repository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NH3DTO> result = nH3Service.partialUpdate(nH3DTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nH3DTO.getId().toString())
        );
    }

    /**
     * {@code GET  /nh-3-s} : get all the nH3s.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nH3s in body.
     */
    @GetMapping("/nh-3-s")
    public ResponseEntity<List<NH3DTO>> getAllNH3s(NH3Criteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get NH3s by criteria: {}", criteria);
        Page<NH3DTO> page = nH3QueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /nh-3-s/count} : count all the nH3s.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/nh-3-s/count")
    public ResponseEntity<Long> countNH3s(NH3Criteria criteria) {
        log.debug("REST request to count NH3s by criteria: {}", criteria);
        return ResponseEntity.ok().body(nH3QueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /nh-3-s/:id} : get the "id" nH3.
     *
     * @param id the id of the nH3DTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the nH3DTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/nh-3-s/{id}")
    public ResponseEntity<NH3DTO> getNH3(@PathVariable Long id) {
        log.debug("REST request to get NH3 : {}", id);
        Optional<NH3DTO> nH3DTO = nH3Service.findOne(id);
        return ResponseUtil.wrapOrNotFound(nH3DTO);
    }

    /**
     * {@code DELETE  /nh-3-s/:id} : delete the "id" nH3.
     *
     * @param id the id of the nH3DTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/nh-3-s/{id}")
    public ResponseEntity<Void> deleteNH3(@PathVariable Long id) {
        log.debug("REST request to delete NH3 : {}", id);
        nH3Service.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
