package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Humidite;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.HumiditeRepository;
import com.poulailler.intelligent.service.criteria.HumiditeCriteria;
import com.poulailler.intelligent.service.dto.HumiditeDTO;
import com.poulailler.intelligent.service.mapper.HumiditeMapper;
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
 * Integration tests for the {@link HumiditeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HumiditeResourceIT {

    private static final Long DEFAULT_NIVEAU = 1L;
    private static final Long UPDATED_NIVEAU = 2L;
    private static final Long SMALLER_NIVEAU = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/humidites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HumiditeRepository humiditeRepository;

    @Autowired
    private HumiditeMapper humiditeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHumiditeMockMvc;

    private Humidite humidite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Humidite createEntity(EntityManager em) {
        Humidite humidite = new Humidite().niveau(DEFAULT_NIVEAU);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        humidite.setVariable(variable);
        return humidite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Humidite createUpdatedEntity(EntityManager em) {
        Humidite humidite = new Humidite().niveau(UPDATED_NIVEAU);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createUpdatedEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        humidite.setVariable(variable);
        return humidite;
    }

    @BeforeEach
    public void initTest() {
        humidite = createEntity(em);
    }

    @Test
    @Transactional
    void createHumidite() throws Exception {
        int databaseSizeBeforeCreate = humiditeRepository.findAll().size();
        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);
        restHumiditeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humiditeDTO)))
            .andExpect(status().isCreated());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeCreate + 1);
        Humidite testHumidite = humiditeList.get(humiditeList.size() - 1);
        assertThat(testHumidite.getNiveau()).isEqualTo(DEFAULT_NIVEAU);

        // Validate the id for MapsId, the ids must be same
        assertThat(testHumidite.getId()).isEqualTo(testHumidite.getVariable().getId());
    }

    @Test
    @Transactional
    void createHumiditeWithExistingId() throws Exception {
        // Create the Humidite with an existing ID
        humidite.setId(1L);
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        int databaseSizeBeforeCreate = humiditeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHumiditeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humiditeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateHumiditeMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);
        int databaseSizeBeforeCreate = humiditeRepository.findAll().size();

        // Add a new parent entity
        Variable variable = VariableResourceIT.createUpdatedEntity(em);
        em.persist(variable);
        em.flush();

        // Load the humidite
        Humidite updatedHumidite = humiditeRepository.findById(humidite.getId()).get();
        assertThat(updatedHumidite).isNotNull();
        // Disconnect from session so that the updates on updatedHumidite are not directly saved in db
        em.detach(updatedHumidite);

        // Update the Variable with new association value
        updatedHumidite.setVariable(variable);
        HumiditeDTO updatedHumiditeDTO = humiditeMapper.toDto(updatedHumidite);
        assertThat(updatedHumiditeDTO).isNotNull();

        // Update the entity
        restHumiditeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHumiditeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHumiditeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeCreate);
        Humidite testHumidite = humiditeList.get(humiditeList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testHumidite.getId()).isEqualTo(testHumidite.getVariable().getId());
    }

    @Test
    @Transactional
    void getAllHumidites() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humidite.getId().intValue())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.intValue())));
    }

    @Test
    @Transactional
    void getHumidite() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get the humidite
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL_ID, humidite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(humidite.getId().intValue()))
            .andExpect(jsonPath("$.niveau").value(DEFAULT_NIVEAU.intValue()));
    }

    @Test
    @Transactional
    void getHumiditesByIdFiltering() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        Long id = humidite.getId();

        defaultHumiditeShouldBeFound("id.equals=" + id);
        defaultHumiditeShouldNotBeFound("id.notEquals=" + id);

        defaultHumiditeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultHumiditeShouldNotBeFound("id.greaterThan=" + id);

        defaultHumiditeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultHumiditeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsEqualToSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau equals to DEFAULT_NIVEAU
        defaultHumiditeShouldBeFound("niveau.equals=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau equals to UPDATED_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.equals=" + UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsNotEqualToSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau not equals to DEFAULT_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.notEquals=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau not equals to UPDATED_NIVEAU
        defaultHumiditeShouldBeFound("niveau.notEquals=" + UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsInShouldWork() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau in DEFAULT_NIVEAU or UPDATED_NIVEAU
        defaultHumiditeShouldBeFound("niveau.in=" + DEFAULT_NIVEAU + "," + UPDATED_NIVEAU);

        // Get all the humiditeList where niveau equals to UPDATED_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.in=" + UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsNullOrNotNull() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau is not null
        defaultHumiditeShouldBeFound("niveau.specified=true");

        // Get all the humiditeList where niveau is null
        defaultHumiditeShouldNotBeFound("niveau.specified=false");
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau is greater than or equal to DEFAULT_NIVEAU
        defaultHumiditeShouldBeFound("niveau.greaterThanOrEqual=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau is greater than or equal to UPDATED_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.greaterThanOrEqual=" + UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau is less than or equal to DEFAULT_NIVEAU
        defaultHumiditeShouldBeFound("niveau.lessThanOrEqual=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau is less than or equal to SMALLER_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.lessThanOrEqual=" + SMALLER_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsLessThanSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau is less than DEFAULT_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.lessThan=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau is less than UPDATED_NIVEAU
        defaultHumiditeShouldBeFound("niveau.lessThan=" + UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByNiveauIsGreaterThanSomething() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        // Get all the humiditeList where niveau is greater than DEFAULT_NIVEAU
        defaultHumiditeShouldNotBeFound("niveau.greaterThan=" + DEFAULT_NIVEAU);

        // Get all the humiditeList where niveau is greater than SMALLER_NIVEAU
        defaultHumiditeShouldBeFound("niveau.greaterThan=" + SMALLER_NIVEAU);
    }

    @Test
    @Transactional
    void getAllHumiditesByVariableIsEqualToSomething() throws Exception {
        // Get already existing entity
        Variable variable = humidite.getVariable();
        humiditeRepository.saveAndFlush(humidite);
        Long variableId = variable.getId();

        // Get all the humiditeList where variable equals to variableId
        defaultHumiditeShouldBeFound("variableId.equals=" + variableId);

        // Get all the humiditeList where variable equals to (variableId + 1)
        defaultHumiditeShouldNotBeFound("variableId.equals=" + (variableId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHumiditeShouldBeFound(String filter) throws Exception {
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(humidite.getId().intValue())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.intValue())));

        // Check, that the count call also returns 1
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHumiditeShouldNotBeFound(String filter) throws Exception {
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHumiditeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHumidite() throws Exception {
        // Get the humidite
        restHumiditeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHumidite() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();

        // Update the humidite
        Humidite updatedHumidite = humiditeRepository.findById(humidite.getId()).get();
        // Disconnect from session so that the updates on updatedHumidite are not directly saved in db
        em.detach(updatedHumidite);
        updatedHumidite.niveau(UPDATED_NIVEAU);
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(updatedHumidite);

        restHumiditeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humiditeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
        Humidite testHumidite = humiditeList.get(humiditeList.size() - 1);
        assertThat(testHumidite.getNiveau()).isEqualTo(UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void putNonExistingHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, humiditeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(humiditeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHumiditeWithPatch() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();

        // Update the humidite using partial update
        Humidite partialUpdatedHumidite = new Humidite();
        partialUpdatedHumidite.setId(humidite.getId());

        partialUpdatedHumidite.niveau(UPDATED_NIVEAU);

        restHumiditeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumidite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumidite))
            )
            .andExpect(status().isOk());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
        Humidite testHumidite = humiditeList.get(humiditeList.size() - 1);
        assertThat(testHumidite.getNiveau()).isEqualTo(UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void fullUpdateHumiditeWithPatch() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();

        // Update the humidite using partial update
        Humidite partialUpdatedHumidite = new Humidite();
        partialUpdatedHumidite.setId(humidite.getId());

        partialUpdatedHumidite.niveau(UPDATED_NIVEAU);

        restHumiditeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHumidite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHumidite))
            )
            .andExpect(status().isOk());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
        Humidite testHumidite = humiditeList.get(humiditeList.size() - 1);
        assertThat(testHumidite.getNiveau()).isEqualTo(UPDATED_NIVEAU);
    }

    @Test
    @Transactional
    void patchNonExistingHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, humiditeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHumidite() throws Exception {
        int databaseSizeBeforeUpdate = humiditeRepository.findAll().size();
        humidite.setId(count.incrementAndGet());

        // Create the Humidite
        HumiditeDTO humiditeDTO = humiditeMapper.toDto(humidite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHumiditeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(humiditeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Humidite in the database
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHumidite() throws Exception {
        // Initialize the database
        humiditeRepository.saveAndFlush(humidite);

        int databaseSizeBeforeDelete = humiditeRepository.findAll().size();

        // Delete the humidite
        restHumiditeMockMvc
            .perform(delete(ENTITY_API_URL_ID, humidite.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Humidite> humiditeList = humiditeRepository.findAll();
        assertThat(humiditeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
