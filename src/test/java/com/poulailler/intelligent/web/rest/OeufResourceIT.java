package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Oeuf;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.OeufRepository;
import com.poulailler.intelligent.service.criteria.OeufCriteria;
import com.poulailler.intelligent.service.dto.OeufDTO;
import com.poulailler.intelligent.service.mapper.OeufMapper;
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
 * Integration tests for the {@link OeufResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OeufResourceIT {

    private static final Long DEFAULT_NOMBRE_JOURNALIER = 1L;
    private static final Long UPDATED_NOMBRE_JOURNALIER = 2L;
    private static final Long SMALLER_NOMBRE_JOURNALIER = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/oeufs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OeufRepository oeufRepository;

    @Autowired
    private OeufMapper oeufMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOeufMockMvc;

    private Oeuf oeuf;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Oeuf createEntity(EntityManager em) {
        Oeuf oeuf = new Oeuf().nombreJournalier(DEFAULT_NOMBRE_JOURNALIER);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        oeuf.setVariable(variable);
        return oeuf;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Oeuf createUpdatedEntity(EntityManager em) {
        Oeuf oeuf = new Oeuf().nombreJournalier(UPDATED_NOMBRE_JOURNALIER);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createUpdatedEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        oeuf.setVariable(variable);
        return oeuf;
    }

    @BeforeEach
    public void initTest() {
        oeuf = createEntity(em);
    }

    @Test
    @Transactional
    void createOeuf() throws Exception {
        int databaseSizeBeforeCreate = oeufRepository.findAll().size();
        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);
        restOeufMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oeufDTO)))
            .andExpect(status().isCreated());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeCreate + 1);
        Oeuf testOeuf = oeufList.get(oeufList.size() - 1);
        assertThat(testOeuf.getNombreJournalier()).isEqualTo(DEFAULT_NOMBRE_JOURNALIER);

        // Validate the id for MapsId, the ids must be same
        assertThat(testOeuf.getId()).isEqualTo(testOeuf.getVariable().getId());
    }

    @Test
    @Transactional
    void createOeufWithExistingId() throws Exception {
        // Create the Oeuf with an existing ID
        oeuf.setId(1L);
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        int databaseSizeBeforeCreate = oeufRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOeufMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oeufDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateOeufMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);
        int databaseSizeBeforeCreate = oeufRepository.findAll().size();

        // Add a new parent entity
        Variable variable = VariableResourceIT.createUpdatedEntity(em);
        em.persist(variable);
        em.flush();

        // Load the oeuf
        Oeuf updatedOeuf = oeufRepository.findById(oeuf.getId()).get();
        assertThat(updatedOeuf).isNotNull();
        // Disconnect from session so that the updates on updatedOeuf are not directly saved in db
        em.detach(updatedOeuf);

        // Update the Variable with new association value
        updatedOeuf.setVariable(variable);
        OeufDTO updatedOeufDTO = oeufMapper.toDto(updatedOeuf);
        assertThat(updatedOeufDTO).isNotNull();

        // Update the entity
        restOeufMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOeufDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOeufDTO))
            )
            .andExpect(status().isOk());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeCreate);
        Oeuf testOeuf = oeufList.get(oeufList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testOeuf.getId()).isEqualTo(testOeuf.getVariable().getId());
    }

    @Test
    @Transactional
    void getAllOeufs() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList
        restOeufMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oeuf.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombreJournalier").value(hasItem(DEFAULT_NOMBRE_JOURNALIER.intValue())));
    }

    @Test
    @Transactional
    void getOeuf() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get the oeuf
        restOeufMockMvc
            .perform(get(ENTITY_API_URL_ID, oeuf.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(oeuf.getId().intValue()))
            .andExpect(jsonPath("$.nombreJournalier").value(DEFAULT_NOMBRE_JOURNALIER.intValue()));
    }

    @Test
    @Transactional
    void getOeufsByIdFiltering() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        Long id = oeuf.getId();

        defaultOeufShouldBeFound("id.equals=" + id);
        defaultOeufShouldNotBeFound("id.notEquals=" + id);

        defaultOeufShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOeufShouldNotBeFound("id.greaterThan=" + id);

        defaultOeufShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOeufShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsEqualToSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier equals to DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.equals=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier equals to UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.equals=" + UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsNotEqualToSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier not equals to DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.notEquals=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier not equals to UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.notEquals=" + UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsInShouldWork() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier in DEFAULT_NOMBRE_JOURNALIER or UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.in=" + DEFAULT_NOMBRE_JOURNALIER + "," + UPDATED_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier equals to UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.in=" + UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsNullOrNotNull() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier is not null
        defaultOeufShouldBeFound("nombreJournalier.specified=true");

        // Get all the oeufList where nombreJournalier is null
        defaultOeufShouldNotBeFound("nombreJournalier.specified=false");
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier is greater than or equal to DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.greaterThanOrEqual=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier is greater than or equal to UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.greaterThanOrEqual=" + UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier is less than or equal to DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.lessThanOrEqual=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier is less than or equal to SMALLER_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.lessThanOrEqual=" + SMALLER_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsLessThanSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier is less than DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.lessThan=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier is less than UPDATED_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.lessThan=" + UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByNombreJournalierIsGreaterThanSomething() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        // Get all the oeufList where nombreJournalier is greater than DEFAULT_NOMBRE_JOURNALIER
        defaultOeufShouldNotBeFound("nombreJournalier.greaterThan=" + DEFAULT_NOMBRE_JOURNALIER);

        // Get all the oeufList where nombreJournalier is greater than SMALLER_NOMBRE_JOURNALIER
        defaultOeufShouldBeFound("nombreJournalier.greaterThan=" + SMALLER_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void getAllOeufsByVariableIsEqualToSomething() throws Exception {
        // Get already existing entity
        Variable variable = oeuf.getVariable();
        oeufRepository.saveAndFlush(oeuf);
        Long variableId = variable.getId();

        // Get all the oeufList where variable equals to variableId
        defaultOeufShouldBeFound("variableId.equals=" + variableId);

        // Get all the oeufList where variable equals to (variableId + 1)
        defaultOeufShouldNotBeFound("variableId.equals=" + (variableId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOeufShouldBeFound(String filter) throws Exception {
        restOeufMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oeuf.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombreJournalier").value(hasItem(DEFAULT_NOMBRE_JOURNALIER.intValue())));

        // Check, that the count call also returns 1
        restOeufMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOeufShouldNotBeFound(String filter) throws Exception {
        restOeufMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOeufMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOeuf() throws Exception {
        // Get the oeuf
        restOeufMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOeuf() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();

        // Update the oeuf
        Oeuf updatedOeuf = oeufRepository.findById(oeuf.getId()).get();
        // Disconnect from session so that the updates on updatedOeuf are not directly saved in db
        em.detach(updatedOeuf);
        updatedOeuf.nombreJournalier(UPDATED_NOMBRE_JOURNALIER);
        OeufDTO oeufDTO = oeufMapper.toDto(updatedOeuf);

        restOeufMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oeufDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oeufDTO))
            )
            .andExpect(status().isOk());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
        Oeuf testOeuf = oeufList.get(oeufList.size() - 1);
        assertThat(testOeuf.getNombreJournalier()).isEqualTo(UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void putNonExistingOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oeufDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oeufDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oeufDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oeufDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOeufWithPatch() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();

        // Update the oeuf using partial update
        Oeuf partialUpdatedOeuf = new Oeuf();
        partialUpdatedOeuf.setId(oeuf.getId());

        restOeufMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOeuf.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOeuf))
            )
            .andExpect(status().isOk());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
        Oeuf testOeuf = oeufList.get(oeufList.size() - 1);
        assertThat(testOeuf.getNombreJournalier()).isEqualTo(DEFAULT_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void fullUpdateOeufWithPatch() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();

        // Update the oeuf using partial update
        Oeuf partialUpdatedOeuf = new Oeuf();
        partialUpdatedOeuf.setId(oeuf.getId());

        partialUpdatedOeuf.nombreJournalier(UPDATED_NOMBRE_JOURNALIER);

        restOeufMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOeuf.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOeuf))
            )
            .andExpect(status().isOk());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
        Oeuf testOeuf = oeufList.get(oeufList.size() - 1);
        assertThat(testOeuf.getNombreJournalier()).isEqualTo(UPDATED_NOMBRE_JOURNALIER);
    }

    @Test
    @Transactional
    void patchNonExistingOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, oeufDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oeufDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oeufDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOeuf() throws Exception {
        int databaseSizeBeforeUpdate = oeufRepository.findAll().size();
        oeuf.setId(count.incrementAndGet());

        // Create the Oeuf
        OeufDTO oeufDTO = oeufMapper.toDto(oeuf);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOeufMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(oeufDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Oeuf in the database
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOeuf() throws Exception {
        // Initialize the database
        oeufRepository.saveAndFlush(oeuf);

        int databaseSizeBeforeDelete = oeufRepository.findAll().size();

        // Delete the oeuf
        restOeufMockMvc
            .perform(delete(ENTITY_API_URL_ID, oeuf.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Oeuf> oeufList = oeufRepository.findAll();
        assertThat(oeufList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
