package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Ventilateur;
import com.poulailler.intelligent.repository.VentilateurRepository;
import com.poulailler.intelligent.service.criteria.VentilateurCriteria;
import com.poulailler.intelligent.service.dto.VentilateurDTO;
import com.poulailler.intelligent.service.mapper.VentilateurMapper;
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
 * Integration tests for the {@link VentilateurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VentilateurResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_VITESSE = 1;
    private static final Integer UPDATED_VITESSE = 2;
    private static final Integer SMALLER_VITESSE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/ventilateurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VentilateurRepository ventilateurRepository;

    @Autowired
    private VentilateurMapper ventilateurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVentilateurMockMvc;

    private Ventilateur ventilateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ventilateur createEntity(EntityManager em) {
        Ventilateur ventilateur = new Ventilateur().libelle(DEFAULT_LIBELLE).vitesse(DEFAULT_VITESSE);
        return ventilateur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ventilateur createUpdatedEntity(EntityManager em) {
        Ventilateur ventilateur = new Ventilateur().libelle(UPDATED_LIBELLE).vitesse(UPDATED_VITESSE);
        return ventilateur;
    }

    @BeforeEach
    public void initTest() {
        ventilateur = createEntity(em);
    }

    @Test
    @Transactional
    void createVentilateur() throws Exception {
        int databaseSizeBeforeCreate = ventilateurRepository.findAll().size();
        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);
        restVentilateurMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeCreate + 1);
        Ventilateur testVentilateur = ventilateurList.get(ventilateurList.size() - 1);
        assertThat(testVentilateur.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testVentilateur.getVitesse()).isEqualTo(DEFAULT_VITESSE);
    }

    @Test
    @Transactional
    void createVentilateurWithExistingId() throws Exception {
        // Create the Ventilateur with an existing ID
        ventilateur.setId(1L);
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        int databaseSizeBeforeCreate = ventilateurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVentilateurMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVentilateurs() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ventilateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].vitesse").value(hasItem(DEFAULT_VITESSE)));
    }

    @Test
    @Transactional
    void getVentilateur() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get the ventilateur
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL_ID, ventilateur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ventilateur.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.vitesse").value(DEFAULT_VITESSE));
    }

    @Test
    @Transactional
    void getVentilateursByIdFiltering() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        Long id = ventilateur.getId();

        defaultVentilateurShouldBeFound("id.equals=" + id);
        defaultVentilateurShouldNotBeFound("id.notEquals=" + id);

        defaultVentilateurShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVentilateurShouldNotBeFound("id.greaterThan=" + id);

        defaultVentilateurShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVentilateurShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle equals to DEFAULT_LIBELLE
        defaultVentilateurShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the ventilateurList where libelle equals to UPDATED_LIBELLE
        defaultVentilateurShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle not equals to DEFAULT_LIBELLE
        defaultVentilateurShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the ventilateurList where libelle not equals to UPDATED_LIBELLE
        defaultVentilateurShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultVentilateurShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the ventilateurList where libelle equals to UPDATED_LIBELLE
        defaultVentilateurShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle is not null
        defaultVentilateurShouldBeFound("libelle.specified=true");

        // Get all the ventilateurList where libelle is null
        defaultVentilateurShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleContainsSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle contains DEFAULT_LIBELLE
        defaultVentilateurShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the ventilateurList where libelle contains UPDATED_LIBELLE
        defaultVentilateurShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllVentilateursByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where libelle does not contain DEFAULT_LIBELLE
        defaultVentilateurShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the ventilateurList where libelle does not contain UPDATED_LIBELLE
        defaultVentilateurShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse equals to DEFAULT_VITESSE
        defaultVentilateurShouldBeFound("vitesse.equals=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse equals to UPDATED_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.equals=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse not equals to DEFAULT_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.notEquals=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse not equals to UPDATED_VITESSE
        defaultVentilateurShouldBeFound("vitesse.notEquals=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsInShouldWork() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse in DEFAULT_VITESSE or UPDATED_VITESSE
        defaultVentilateurShouldBeFound("vitesse.in=" + DEFAULT_VITESSE + "," + UPDATED_VITESSE);

        // Get all the ventilateurList where vitesse equals to UPDATED_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.in=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsNullOrNotNull() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse is not null
        defaultVentilateurShouldBeFound("vitesse.specified=true");

        // Get all the ventilateurList where vitesse is null
        defaultVentilateurShouldNotBeFound("vitesse.specified=false");
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse is greater than or equal to DEFAULT_VITESSE
        defaultVentilateurShouldBeFound("vitesse.greaterThanOrEqual=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse is greater than or equal to UPDATED_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.greaterThanOrEqual=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse is less than or equal to DEFAULT_VITESSE
        defaultVentilateurShouldBeFound("vitesse.lessThanOrEqual=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse is less than or equal to SMALLER_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.lessThanOrEqual=" + SMALLER_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsLessThanSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse is less than DEFAULT_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.lessThan=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse is less than UPDATED_VITESSE
        defaultVentilateurShouldBeFound("vitesse.lessThan=" + UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void getAllVentilateursByVitesseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        // Get all the ventilateurList where vitesse is greater than DEFAULT_VITESSE
        defaultVentilateurShouldNotBeFound("vitesse.greaterThan=" + DEFAULT_VITESSE);

        // Get all the ventilateurList where vitesse is greater than SMALLER_VITESSE
        defaultVentilateurShouldBeFound("vitesse.greaterThan=" + SMALLER_VITESSE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVentilateurShouldBeFound(String filter) throws Exception {
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ventilateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].vitesse").value(hasItem(DEFAULT_VITESSE)));

        // Check, that the count call also returns 1
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVentilateurShouldNotBeFound(String filter) throws Exception {
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVentilateurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVentilateur() throws Exception {
        // Get the ventilateur
        restVentilateurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVentilateur() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();

        // Update the ventilateur
        Ventilateur updatedVentilateur = ventilateurRepository.findById(ventilateur.getId()).get();
        // Disconnect from session so that the updates on updatedVentilateur are not directly saved in db
        em.detach(updatedVentilateur);
        updatedVentilateur.libelle(UPDATED_LIBELLE).vitesse(UPDATED_VITESSE);
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(updatedVentilateur);

        restVentilateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventilateurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
        Ventilateur testVentilateur = ventilateurList.get(ventilateurList.size() - 1);
        assertThat(testVentilateur.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testVentilateur.getVitesse()).isEqualTo(UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void putNonExistingVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ventilateurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ventilateurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVentilateurWithPatch() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();

        // Update the ventilateur using partial update
        Ventilateur partialUpdatedVentilateur = new Ventilateur();
        partialUpdatedVentilateur.setId(ventilateur.getId());

        partialUpdatedVentilateur.libelle(UPDATED_LIBELLE);

        restVentilateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentilateur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVentilateur))
            )
            .andExpect(status().isOk());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
        Ventilateur testVentilateur = ventilateurList.get(ventilateurList.size() - 1);
        assertThat(testVentilateur.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testVentilateur.getVitesse()).isEqualTo(DEFAULT_VITESSE);
    }

    @Test
    @Transactional
    void fullUpdateVentilateurWithPatch() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();

        // Update the ventilateur using partial update
        Ventilateur partialUpdatedVentilateur = new Ventilateur();
        partialUpdatedVentilateur.setId(ventilateur.getId());

        partialUpdatedVentilateur.libelle(UPDATED_LIBELLE).vitesse(UPDATED_VITESSE);

        restVentilateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVentilateur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVentilateur))
            )
            .andExpect(status().isOk());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
        Ventilateur testVentilateur = ventilateurList.get(ventilateurList.size() - 1);
        assertThat(testVentilateur.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testVentilateur.getVitesse()).isEqualTo(UPDATED_VITESSE);
    }

    @Test
    @Transactional
    void patchNonExistingVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ventilateurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVentilateur() throws Exception {
        int databaseSizeBeforeUpdate = ventilateurRepository.findAll().size();
        ventilateur.setId(count.incrementAndGet());

        // Create the Ventilateur
        VentilateurDTO ventilateurDTO = ventilateurMapper.toDto(ventilateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVentilateurMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ventilateurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ventilateur in the database
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVentilateur() throws Exception {
        // Initialize the database
        ventilateurRepository.saveAndFlush(ventilateur);

        int databaseSizeBeforeDelete = ventilateurRepository.findAll().size();

        // Delete the ventilateur
        restVentilateurMockMvc
            .perform(delete(ENTITY_API_URL_ID, ventilateur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ventilateur> ventilateurList = ventilateurRepository.findAll();
        assertThat(ventilateurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
