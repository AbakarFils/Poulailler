package com.poulailler.intelligent.web.rest;

import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.VariableRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.poulailler.intelligent.domain.Variable}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VariableResource {

    private final Logger log = LoggerFactory.getLogger(VariableResource.class);

    private final VariableRepository variableRepository;

    public VariableResource(VariableRepository variableRepository) {
        this.variableRepository = variableRepository;
    }

    /**
     * {@code GET  /variables} : get all the variables.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of variables in body.
     */
    @GetMapping("/variables")
    public ResponseEntity<List<Variable>> getAllVariables(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Variables");
        Page<Variable> page = variableRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /variables/:id} : get the "id" variable.
     *
     * @param id the id of the variable to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the variable, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/variables/{id}")
    public ResponseEntity<Variable> getVariable(@PathVariable Long id) {
        log.debug("REST request to get Variable : {}", id);
        Optional<Variable> variable = variableRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(variable);
    }
}
