package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.repository.TemperatureRepository;
import com.poulailler.intelligent.service.TemperatureQueryService;
import com.poulailler.intelligent.service.TemperatureService;
import com.poulailler.intelligent.service.criteria.TemperatureCriteria;
import com.poulailler.intelligent.service.dto.TemperatureDTO;
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
 * REST controller for managing {@link com.poulailler.intelligent.domain.Temperature}.
 */
@RestController
@RequestMapping("/api")
public class TemperatureResource {

    private final Logger log = LoggerFactory.getLogger(TemperatureResource.class);

    private static final String ENTITY_NAME = "temperature";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TemperatureService temperatureService;

    private final TemperatureRepository temperatureRepository;

    private final TemperatureQueryService temperatureQueryService;

    public TemperatureResource(
        TemperatureService temperatureService,
        TemperatureRepository temperatureRepository,
        TemperatureQueryService temperatureQueryService
    ) {
        this.temperatureService = temperatureService;
        this.temperatureRepository = temperatureRepository;
        this.temperatureQueryService = temperatureQueryService;
    }

    /**
     * {@code POST  /temperatures} : Create a new temperature.
     *
     * @param temperatureDTO the temperatureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new temperatureDTO, or with status {@code 400 (Bad Request)} if the temperature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/temperatures")
    public ResponseEntity<TemperatureDTO> createTemperature(@Valid @RequestBody TemperatureDTO temperatureDTO) throws URISyntaxException {
        log.debug("REST request to save Temperature : {}", temperatureDTO);
        if (temperatureDTO.getId() != null) {
            throw new BadRequestAlertException("A new temperature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(temperatureDTO.getVariable())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        TemperatureDTO result = temperatureService.save(temperatureDTO);
        return ResponseEntity
            .created(new URI("/api/temperatures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /temperatures/:id} : Updates an existing temperature.
     *
     * @param id the id of the temperatureDTO to save.
     * @param temperatureDTO the temperatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temperatureDTO,
     * or with status {@code 400 (Bad Request)} if the temperatureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the temperatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/temperatures/{id}")
    public ResponseEntity<TemperatureDTO> updateTemperature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TemperatureDTO temperatureDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Temperature : {}, {}", id, temperatureDTO);
        if (temperatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, temperatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!temperatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TemperatureDTO result = temperatureService.save(temperatureDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, temperatureDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /temperatures/:id} : Partial updates given fields of an existing temperature, field will ignore if it is null
     *
     * @param id the id of the temperatureDTO to save.
     * @param temperatureDTO the temperatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated temperatureDTO,
     * or with status {@code 400 (Bad Request)} if the temperatureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the temperatureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the temperatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/temperatures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TemperatureDTO> partialUpdateTemperature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TemperatureDTO temperatureDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Temperature partially : {}, {}", id, temperatureDTO);
        if (temperatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, temperatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!temperatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TemperatureDTO> result = temperatureService.partialUpdate(temperatureDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, temperatureDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /temperatures} : get all the temperatures.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of temperatures in body.
     */
    @GetMapping("/temperatures")
    public ResponseEntity<List<TemperatureDTO>> getAllTemperatures(
        TemperatureCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Temperatures by criteria: {}", criteria);
        Page<TemperatureDTO> page = temperatureQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /temperatures/count} : count all the temperatures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/temperatures/count")
    public ResponseEntity<Long> countTemperatures(TemperatureCriteria criteria) {
        log.debug("REST request to count Temperatures by criteria: {}", criteria);
        return ResponseEntity.ok().body(temperatureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /temperatures/:id} : get the "id" temperature.
     *
     * @param id the id of the temperatureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the temperatureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/temperatures/{id}")
    public ResponseEntity<TemperatureDTO> getTemperature(@PathVariable Long id) {
        log.debug("REST request to get Temperature : {}", id);
        Optional<TemperatureDTO> temperatureDTO = temperatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(temperatureDTO);
    }

    /**
     * {@code DELETE  /temperatures/:id} : delete the "id" temperature.
     *
     * @param id the id of the temperatureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/temperatures/{id}")
    public ResponseEntity<Void> deleteTemperature(@PathVariable Long id) {
        log.debug("REST request to delete Temperature : {}", id);
        temperatureService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
