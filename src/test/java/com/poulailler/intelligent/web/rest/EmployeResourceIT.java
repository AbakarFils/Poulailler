package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Employe;
import com.poulailler.intelligent.domain.User;
import com.poulailler.intelligent.repository.EmployeRepository;
import com.poulailler.intelligent.service.criteria.EmployeCriteria;
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
 * Integration tests for the {@link EmployeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmployeResourceIT {

    private static final String DEFAULT_NUMERO_IDENTITE = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_IDENTITE = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/employes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeMockMvc;

    private Employe employe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employe createEntity(EntityManager em) {
        Employe employe = new Employe().numeroIdentite(DEFAULT_NUMERO_IDENTITE).adresse(DEFAULT_ADRESSE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        employe.setUser(user);
        return employe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employe createUpdatedEntity(EntityManager em) {
        Employe employe = new Employe().numeroIdentite(UPDATED_NUMERO_IDENTITE).adresse(UPDATED_ADRESSE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        employe.setUser(user);
        return employe;
    }

    @BeforeEach
    public void initTest() {
        employe = createEntity(em);
    }

    @Test
    @Transactional
    void createEmploye() throws Exception {
        int databaseSizeBeforeCreate = employeRepository.findAll().size();
        // Create the Employe
        restEmployeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employe)))
            .andExpect(status().isCreated());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeCreate + 1);
        Employe testEmploye = employeList.get(employeList.size() - 1);
        assertThat(testEmploye.getNumeroIdentite()).isEqualTo(DEFAULT_NUMERO_IDENTITE);
        assertThat(testEmploye.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void createEmployeWithExistingId() throws Exception {
        // Create the Employe with an existing ID
        employe.setId(1L);

        int databaseSizeBeforeCreate = employeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employe)))
            .andExpect(status().isBadRequest());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIdentiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeRepository.findAll().size();
        // set the field null
        employe.setNumeroIdentite(null);

        // Create the Employe, which fails.

        restEmployeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employe)))
            .andExpect(status().isBadRequest());

        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmployes() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employe.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroIdentite").value(hasItem(DEFAULT_NUMERO_IDENTITE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));
    }

    @Test
    @Transactional
    void getEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get the employe
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL_ID, employe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employe.getId().intValue()))
            .andExpect(jsonPath("$.numeroIdentite").value(DEFAULT_NUMERO_IDENTITE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE));
    }

    @Test
    @Transactional
    void getEmployesByIdFiltering() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        Long id = employe.getId();

        defaultEmployeShouldBeFound("id.equals=" + id);
        defaultEmployeShouldNotBeFound("id.notEquals=" + id);

        defaultEmployeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEmployeShouldNotBeFound("id.greaterThan=" + id);

        defaultEmployeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEmployeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteIsEqualToSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite equals to DEFAULT_NUMERO_IDENTITE
        defaultEmployeShouldBeFound("numeroIdentite.equals=" + DEFAULT_NUMERO_IDENTITE);

        // Get all the employeList where numeroIdentite equals to UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldNotBeFound("numeroIdentite.equals=" + UPDATED_NUMERO_IDENTITE);
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite not equals to DEFAULT_NUMERO_IDENTITE
        defaultEmployeShouldNotBeFound("numeroIdentite.notEquals=" + DEFAULT_NUMERO_IDENTITE);

        // Get all the employeList where numeroIdentite not equals to UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldBeFound("numeroIdentite.notEquals=" + UPDATED_NUMERO_IDENTITE);
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteIsInShouldWork() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite in DEFAULT_NUMERO_IDENTITE or UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldBeFound("numeroIdentite.in=" + DEFAULT_NUMERO_IDENTITE + "," + UPDATED_NUMERO_IDENTITE);

        // Get all the employeList where numeroIdentite equals to UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldNotBeFound("numeroIdentite.in=" + UPDATED_NUMERO_IDENTITE);
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite is not null
        defaultEmployeShouldBeFound("numeroIdentite.specified=true");

        // Get all the employeList where numeroIdentite is null
        defaultEmployeShouldNotBeFound("numeroIdentite.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteContainsSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite contains DEFAULT_NUMERO_IDENTITE
        defaultEmployeShouldBeFound("numeroIdentite.contains=" + DEFAULT_NUMERO_IDENTITE);

        // Get all the employeList where numeroIdentite contains UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldNotBeFound("numeroIdentite.contains=" + UPDATED_NUMERO_IDENTITE);
    }

    @Test
    @Transactional
    void getAllEmployesByNumeroIdentiteNotContainsSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where numeroIdentite does not contain DEFAULT_NUMERO_IDENTITE
        defaultEmployeShouldNotBeFound("numeroIdentite.doesNotContain=" + DEFAULT_NUMERO_IDENTITE);

        // Get all the employeList where numeroIdentite does not contain UPDATED_NUMERO_IDENTITE
        defaultEmployeShouldBeFound("numeroIdentite.doesNotContain=" + UPDATED_NUMERO_IDENTITE);
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse equals to DEFAULT_ADRESSE
        defaultEmployeShouldBeFound("adresse.equals=" + DEFAULT_ADRESSE);

        // Get all the employeList where adresse equals to UPDATED_ADRESSE
        defaultEmployeShouldNotBeFound("adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse not equals to DEFAULT_ADRESSE
        defaultEmployeShouldNotBeFound("adresse.notEquals=" + DEFAULT_ADRESSE);

        // Get all the employeList where adresse not equals to UPDATED_ADRESSE
        defaultEmployeShouldBeFound("adresse.notEquals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse in DEFAULT_ADRESSE or UPDATED_ADRESSE
        defaultEmployeShouldBeFound("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE);

        // Get all the employeList where adresse equals to UPDATED_ADRESSE
        defaultEmployeShouldNotBeFound("adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse is not null
        defaultEmployeShouldBeFound("adresse.specified=true");

        // Get all the employeList where adresse is null
        defaultEmployeShouldNotBeFound("adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseContainsSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse contains DEFAULT_ADRESSE
        defaultEmployeShouldBeFound("adresse.contains=" + DEFAULT_ADRESSE);

        // Get all the employeList where adresse contains UPDATED_ADRESSE
        defaultEmployeShouldNotBeFound("adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEmployesByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employeList where adresse does not contain DEFAULT_ADRESSE
        defaultEmployeShouldNotBeFound("adresse.doesNotContain=" + DEFAULT_ADRESSE);

        // Get all the employeList where adresse does not contain UPDATED_ADRESSE
        defaultEmployeShouldBeFound("adresse.doesNotContain=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEmployesByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = employe.getUser();
        employeRepository.saveAndFlush(employe);
        Long userId = user.getId();

        // Get all the employeList where user equals to userId
        defaultEmployeShouldBeFound("userId.equals=" + userId);

        // Get all the employeList where user equals to (userId + 1)
        defaultEmployeShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmployeShouldBeFound(String filter) throws Exception {
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employe.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroIdentite").value(hasItem(DEFAULT_NUMERO_IDENTITE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));

        // Check, that the count call also returns 1
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmployeShouldNotBeFound(String filter) throws Exception {
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmployeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmploye() throws Exception {
        // Get the employe
        restEmployeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        int databaseSizeBeforeUpdate = employeRepository.findAll().size();

        // Update the employe
        Employe updatedEmploye = employeRepository.findById(employe.getId()).get();
        // Disconnect from session so that the updates on updatedEmploye are not directly saved in db
        em.detach(updatedEmploye);
        updatedEmploye.numeroIdentite(UPDATED_NUMERO_IDENTITE).adresse(UPDATED_ADRESSE);

        restEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmploye.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmploye))
            )
            .andExpect(status().isOk());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
        Employe testEmploye = employeList.get(employeList.size() - 1);
        assertThat(testEmploye.getNumeroIdentite()).isEqualTo(UPDATED_NUMERO_IDENTITE);
        assertThat(testEmploye.getAdresse()).isEqualTo(UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void putNonExistingEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmployeWithPatch() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        int databaseSizeBeforeUpdate = employeRepository.findAll().size();

        // Update the employe using partial update
        Employe partialUpdatedEmploye = new Employe();
        partialUpdatedEmploye.setId(employe.getId());

        restEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmploye.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmploye))
            )
            .andExpect(status().isOk());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
        Employe testEmploye = employeList.get(employeList.size() - 1);
        assertThat(testEmploye.getNumeroIdentite()).isEqualTo(DEFAULT_NUMERO_IDENTITE);
        assertThat(testEmploye.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void fullUpdateEmployeWithPatch() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        int databaseSizeBeforeUpdate = employeRepository.findAll().size();

        // Update the employe using partial update
        Employe partialUpdatedEmploye = new Employe();
        partialUpdatedEmploye.setId(employe.getId());

        partialUpdatedEmploye.numeroIdentite(UPDATED_NUMERO_IDENTITE).adresse(UPDATED_ADRESSE);

        restEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmploye.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmploye))
            )
            .andExpect(status().isOk());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
        Employe testEmploye = employeList.get(employeList.size() - 1);
        assertThat(testEmploye.getNumeroIdentite()).isEqualTo(UPDATED_NUMERO_IDENTITE);
        assertThat(testEmploye.getAdresse()).isEqualTo(UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void patchNonExistingEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmploye() throws Exception {
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();
        employe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(employe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employe in the database
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        int databaseSizeBeforeDelete = employeRepository.findAll().size();

        // Delete the employe
        restEmployeMockMvc
            .perform(delete(ENTITY_API_URL_ID, employe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Employe> employeList = employeRepository.findAll();
        assertThat(employeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
