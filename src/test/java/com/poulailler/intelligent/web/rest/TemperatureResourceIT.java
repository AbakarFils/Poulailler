package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Temperature;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.TemperatureRepository;
import com.poulailler.intelligent.service.criteria.TemperatureCriteria;
import com.poulailler.intelligent.service.dto.TemperatureDTO;
import com.poulailler.intelligent.service.mapper.TemperatureMapper;
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
 * Integration tests for the {@link TemperatureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TemperatureResourceIT {

    private static final Long DEFAULT_DREGREE = 1L;
    private static final Long UPDATED_DREGREE = 2L;
    private static final Long SMALLER_DREGREE = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/temperatures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTemperatureMockMvc;

    private Temperature temperature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Temperature createEntity(EntityManager em) {
        Temperature temperature = new Temperature().dregree(DEFAULT_DREGREE);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        temperature.setVariable(variable);
        return temperature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Temperature createUpdatedEntity(EntityManager em) {
        Temperature temperature = new Temperature().dregree(UPDATED_DREGREE);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createUpdatedEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        temperature.setVariable(variable);
        return temperature;
    }

    @BeforeEach
    public void initTest() {
        temperature = createEntity(em);
    }

    @Test
    @Transactional
    void createTemperature() throws Exception {
        int databaseSizeBeforeCreate = temperatureRepository.findAll().size();
        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);
        restTemperatureMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeCreate + 1);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getDregree()).isEqualTo(DEFAULT_DREGREE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testTemperature.getId()).isEqualTo(testTemperature.getVariable().getId());
    }

    @Test
    @Transactional
    void createTemperatureWithExistingId() throws Exception {
        // Create the Temperature with an existing ID
        temperature.setId(1L);
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        int databaseSizeBeforeCreate = temperatureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTemperatureMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateTemperatureMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);
        int databaseSizeBeforeCreate = temperatureRepository.findAll().size();

        // Add a new parent entity
        Variable variable = VariableResourceIT.createUpdatedEntity(em);
        em.persist(variable);
        em.flush();

        // Load the temperature
        Temperature updatedTemperature = temperatureRepository.findById(temperature.getId()).get();
        assertThat(updatedTemperature).isNotNull();
        // Disconnect from session so that the updates on updatedTemperature are not directly saved in db
        em.detach(updatedTemperature);

        // Update the Variable with new association value
        updatedTemperature.setVariable(variable);
        TemperatureDTO updatedTemperatureDTO = temperatureMapper.toDto(updatedTemperature);
        assertThat(updatedTemperatureDTO).isNotNull();

        // Update the entity
        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTemperatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTemperatureDTO))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeCreate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testTemperature.getId()).isEqualTo(testTemperature.getVariable().getId());
    }

    @Test
    @Transactional
    void getAllTemperatures() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(temperature.getId().intValue())))
            .andExpect(jsonPath("$.[*].dregree").value(hasItem(DEFAULT_DREGREE.intValue())));
    }

    @Test
    @Transactional
    void getTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get the temperature
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL_ID, temperature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(temperature.getId().intValue()))
            .andExpect(jsonPath("$.dregree").value(DEFAULT_DREGREE.intValue()));
    }

    @Test
    @Transactional
    void getTemperaturesByIdFiltering() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        Long id = temperature.getId();

        defaultTemperatureShouldBeFound("id.equals=" + id);
        defaultTemperatureShouldNotBeFound("id.notEquals=" + id);

        defaultTemperatureShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTemperatureShouldNotBeFound("id.greaterThan=" + id);

        defaultTemperatureShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTemperatureShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree equals to DEFAULT_DREGREE
        defaultTemperatureShouldBeFound("dregree.equals=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree equals to UPDATED_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.equals=" + UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree not equals to DEFAULT_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.notEquals=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree not equals to UPDATED_DREGREE
        defaultTemperatureShouldBeFound("dregree.notEquals=" + UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsInShouldWork() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree in DEFAULT_DREGREE or UPDATED_DREGREE
        defaultTemperatureShouldBeFound("dregree.in=" + DEFAULT_DREGREE + "," + UPDATED_DREGREE);

        // Get all the temperatureList where dregree equals to UPDATED_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.in=" + UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree is not null
        defaultTemperatureShouldBeFound("dregree.specified=true");

        // Get all the temperatureList where dregree is null
        defaultTemperatureShouldNotBeFound("dregree.specified=false");
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree is greater than or equal to DEFAULT_DREGREE
        defaultTemperatureShouldBeFound("dregree.greaterThanOrEqual=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree is greater than or equal to UPDATED_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.greaterThanOrEqual=" + UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree is less than or equal to DEFAULT_DREGREE
        defaultTemperatureShouldBeFound("dregree.lessThanOrEqual=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree is less than or equal to SMALLER_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.lessThanOrEqual=" + SMALLER_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsLessThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree is less than DEFAULT_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.lessThan=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree is less than UPDATED_DREGREE
        defaultTemperatureShouldBeFound("dregree.lessThan=" + UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByDregreeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        // Get all the temperatureList where dregree is greater than DEFAULT_DREGREE
        defaultTemperatureShouldNotBeFound("dregree.greaterThan=" + DEFAULT_DREGREE);

        // Get all the temperatureList where dregree is greater than SMALLER_DREGREE
        defaultTemperatureShouldBeFound("dregree.greaterThan=" + SMALLER_DREGREE);
    }

    @Test
    @Transactional
    void getAllTemperaturesByVariableIsEqualToSomething() throws Exception {
        // Get already existing entity
        Variable variable = temperature.getVariable();
        temperatureRepository.saveAndFlush(temperature);
        Long variableId = variable.getId();

        // Get all the temperatureList where variable equals to variableId
        defaultTemperatureShouldBeFound("variableId.equals=" + variableId);

        // Get all the temperatureList where variable equals to (variableId + 1)
        defaultTemperatureShouldNotBeFound("variableId.equals=" + (variableId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTemperatureShouldBeFound(String filter) throws Exception {
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(temperature.getId().intValue())))
            .andExpect(jsonPath("$.[*].dregree").value(hasItem(DEFAULT_DREGREE.intValue())));

        // Check, that the count call also returns 1
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTemperatureShouldNotBeFound(String filter) throws Exception {
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTemperatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTemperature() throws Exception {
        // Get the temperature
        restTemperatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature
        Temperature updatedTemperature = temperatureRepository.findById(temperature.getId()).get();
        // Disconnect from session so that the updates on updatedTemperature are not directly saved in db
        em.detach(updatedTemperature);
        updatedTemperature.dregree(UPDATED_DREGREE);
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(updatedTemperature);

        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, temperatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getDregree()).isEqualTo(UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void putNonExistingTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, temperatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(temperatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTemperatureWithPatch() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature using partial update
        Temperature partialUpdatedTemperature = new Temperature();
        partialUpdatedTemperature.setId(temperature.getId());

        partialUpdatedTemperature.dregree(UPDATED_DREGREE);

        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemperature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemperature))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getDregree()).isEqualTo(UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void fullUpdateTemperatureWithPatch() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();

        // Update the temperature using partial update
        Temperature partialUpdatedTemperature = new Temperature();
        partialUpdatedTemperature.setId(temperature.getId());

        partialUpdatedTemperature.dregree(UPDATED_DREGREE);

        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTemperature.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTemperature))
            )
            .andExpect(status().isOk());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
        Temperature testTemperature = temperatureList.get(temperatureList.size() - 1);
        assertThat(testTemperature.getDregree()).isEqualTo(UPDATED_DREGREE);
    }

    @Test
    @Transactional
    void patchNonExistingTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, temperatureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTemperature() throws Exception {
        int databaseSizeBeforeUpdate = temperatureRepository.findAll().size();
        temperature.setId(count.incrementAndGet());

        // Create the Temperature
        TemperatureDTO temperatureDTO = temperatureMapper.toDto(temperature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTemperatureMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(temperatureDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Temperature in the database
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTemperature() throws Exception {
        // Initialize the database
        temperatureRepository.saveAndFlush(temperature);

        int databaseSizeBeforeDelete = temperatureRepository.findAll().size();

        // Delete the temperature
        restTemperatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, temperature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Temperature> temperatureList = temperatureRepository.findAll();
        assertThat(temperatureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
