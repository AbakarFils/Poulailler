package com.poulailler.intelligent.web.rest;

import static com.poulailler.intelligent.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.VariableRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VariableResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VariableResourceIT {

    private static final Long DEFAULT_PLAGE_MAX = 1L;
    private static final Long UPDATED_PLAGE_MAX = 2L;

    private static final ZonedDateTime DEFAULT_DATE_CREATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_CREATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_LUE = false;
    private static final Boolean UPDATED_LUE = true;

    private static final String ENTITY_API_URL = "/api/variables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVariableMockMvc;

    private Variable variable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variable createEntity(EntityManager em) {
        Variable variable = new Variable().plageMax(DEFAULT_PLAGE_MAX).dateCreation(DEFAULT_DATE_CREATION).lue(DEFAULT_LUE);
        return variable;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variable createUpdatedEntity(EntityManager em) {
        Variable variable = new Variable().plageMax(UPDATED_PLAGE_MAX).dateCreation(UPDATED_DATE_CREATION).lue(UPDATED_LUE);
        return variable;
    }

    @BeforeEach
    public void initTest() {
        variable = createEntity(em);
    }

    @Test
    @Transactional
    void getAllVariables() throws Exception {
        // Initialize the database
        variableRepository.saveAndFlush(variable);

        // Get all the variableList
        restVariableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variable.getId().intValue())))
            .andExpect(jsonPath("$.[*].plageMax").value(hasItem(DEFAULT_PLAGE_MAX.intValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(sameInstant(DEFAULT_DATE_CREATION))))
            .andExpect(jsonPath("$.[*].lue").value(hasItem(DEFAULT_LUE.booleanValue())));
    }

    @Test
    @Transactional
    void getVariable() throws Exception {
        // Initialize the database
        variableRepository.saveAndFlush(variable);

        // Get the variable
        restVariableMockMvc
            .perform(get(ENTITY_API_URL_ID, variable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(variable.getId().intValue()))
            .andExpect(jsonPath("$.plageMax").value(DEFAULT_PLAGE_MAX.intValue()))
            .andExpect(jsonPath("$.dateCreation").value(sameInstant(DEFAULT_DATE_CREATION)))
            .andExpect(jsonPath("$.lue").value(DEFAULT_LUE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingVariable() throws Exception {
        // Get the variable
        restVariableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
