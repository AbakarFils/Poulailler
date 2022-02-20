package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.HumiditeRepository;
import com.poulailler.intelligent.service.HumiditeQueryService;
import com.poulailler.intelligent.service.HumiditeService;
import com.poulailler.intelligent.service.criteria.HumiditeCriteria;
import com.poulailler.intelligent.service.dto.HumiditeDTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Humidite}.
 */
@RestController
@RequestMapping("/api")
public class HumiditeResource {

    private final Logger log = LoggerFactory.getLogger(HumiditeResource.class);

    private static final String ENTITY_NAME = "humidite";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HumiditeService humiditeService;

    private final HumiditeRepository humiditeRepository;

    private final HumiditeQueryService humiditeQueryService;

    public HumiditeResource(
        HumiditeService humiditeService,
        HumiditeRepository humiditeRepository,
        HumiditeQueryService humiditeQueryService
    ) {
        this.humiditeService = humiditeService;
        this.humiditeRepository = humiditeRepository;
        this.humiditeQueryService = humiditeQueryService;
    }

    /**
     * {@code POST  /humidites} : Create a new humidite.
     *
     * @param humiditeDTO the humiditeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new humiditeDTO, or with status {@code 400 (Bad Request)} if the humidite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/humidites")
    public ResponseEntity<HumiditeDTO> createHumidite(@Valid @RequestBody HumiditeDTO humiditeDTO) throws URISyntaxException {
        log.debug("REST request to save Humidite : {}", humiditeDTO);
        if (humiditeDTO.getId() != null) {
            throw new BadRequestAlertException("A new humidite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(humiditeDTO.getVariable())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        HumiditeDTO result = humiditeService.save(humiditeDTO);
        return ResponseEntity
            .created(new URI("/api/humidites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /humidites/:id} : Updates an existing humidite.
     *
     * @param id the id of the humiditeDTO to save.
     * @param humiditeDTO the humiditeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humiditeDTO,
     * or with status {@code 400 (Bad Request)} if the humiditeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the humiditeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/humidites/{id}")
    public ResponseEntity<HumiditeDTO> updateHumidite(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HumiditeDTO humiditeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Humidite : {}, {}", id, humiditeDTO);
        if (humiditeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humiditeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humiditeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HumiditeDTO result = humiditeService.save(humiditeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humiditeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /humidites/:id} : Partial updates given fields of an existing humidite, field will ignore if it is null
     *
     * @param id the id of the humiditeDTO to save.
     * @param humiditeDTO the humiditeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated humiditeDTO,
     * or with status {@code 400 (Bad Request)} if the humiditeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the humiditeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the humiditeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/humidites/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HumiditeDTO> partialUpdateHumidite(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HumiditeDTO humiditeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Humidite partially : {}, {}", id, humiditeDTO);
        if (humiditeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, humiditeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!humiditeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HumiditeDTO> result = humiditeService.partialUpdate(humiditeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, humiditeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /humidites} : get all the humidites.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of humidites in body.
     */
    @GetMapping("/humidites")
    public ResponseEntity<List<HumiditeDTO>> getAllHumidites(
        HumiditeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Humidites by criteria: {}", criteria);
        Page<HumiditeDTO> page = humiditeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /humidites/count} : count all the humidites.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/humidites/count")
    public ResponseEntity<Long> countHumidites(HumiditeCriteria criteria) {
        log.debug("REST request to count Humidites by criteria: {}", criteria);
        return ResponseEntity.ok().body(humiditeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /humidites/:id} : get the "id" humidite.
     *
     * @param id the id of the humiditeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the humiditeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/humidites/{id}")
    public ResponseEntity<HumiditeDTO> getHumidite(@PathVariable Long id) {
        log.debug("REST request to get Humidite : {}", id);
        Optional<HumiditeDTO> humiditeDTO = humiditeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(humiditeDTO);
    }

    /**
     * {@code DELETE  /humidites/:id} : delete the "id" humidite.
     *
     * @param id the id of the humiditeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/humidites/{id}")
    public ResponseEntity<Void> deleteHumidite(@PathVariable Long id) {
        log.debug("REST request to delete Humidite : {}", id);
        humiditeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
