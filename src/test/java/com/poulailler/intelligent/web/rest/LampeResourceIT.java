package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.domain.Lampe;
import com.poulailler.intelligent.repository.LampeRepository;
import com.poulailler.intelligent.service.criteria.LampeCriteria;
import com.poulailler.intelligent.service.dto.LampeDTO;
import com.poulailler.intelligent.service.mapper.LampeMapper;
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
 * Integration tests for the {@link LampeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LampeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lampes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LampeRepository lampeRepository;

    @Autowired
    private LampeMapper lampeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLampeMockMvc;

    private Lampe lampe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lampe createEntity(EntityManager em) {
        Lampe lampe = new Lampe().libelle(DEFAULT_LIBELLE);
        // Add required entity
        Equipement equipement;
        if (TestUtil.findAll(em, Equipement.class).isEmpty()) {
            equipement = EquipementResourceIT.createEntity(em);
            em.persist(equipement);
            em.flush();
        } else {
            equipement = TestUtil.findAll(em, Equipement.class).get(0);
        }
        lampe.setEquipement(equipement);
        return lampe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lampe createUpdatedEntity(EntityManager em) {
        Lampe lampe = new Lampe().libelle(UPDATED_LIBELLE);
        // Add required entity
        Equipement equipement;
        if (TestUtil.findAll(em, Equipement.class).isEmpty()) {
            equipement = EquipementResourceIT.createUpdatedEntity(em);
            em.persist(equipement);
            em.flush();
        } else {
            equipement = TestUtil.findAll(em, Equipement.class).get(0);
        }
        lampe.setEquipement(equipement);
        return lampe;
    }

    @BeforeEach
    public void initTest() {
        lampe = createEntity(em);
    }

    @Test
    @Transactional
    void createLampe() throws Exception {
        int databaseSizeBeforeCreate = lampeRepository.findAll().size();
        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);
        restLampeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lampeDTO)))
            .andExpect(status().isCreated());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeCreate + 1);
        Lampe testLampe = lampeList.get(lampeList.size() - 1);
        assertThat(testLampe.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testLampe.getId()).isEqualTo(testLampe.getEquipement().getId());
    }

    @Test
    @Transactional
    void createLampeWithExistingId() throws Exception {
        // Create the Lampe with an existing ID
        lampe.setId(1L);
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        int databaseSizeBeforeCreate = lampeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLampeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lampeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateLampeMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);
        int databaseSizeBeforeCreate = lampeRepository.findAll().size();

        // Add a new parent entity
        Equipement equipement = EquipementResourceIT.createUpdatedEntity(em);
        em.persist(equipement);
        em.flush();

        // Load the lampe
        Lampe updatedLampe = lampeRepository.findById(lampe.getId()).get();
        assertThat(updatedLampe).isNotNull();
        // Disconnect from session so that the updates on updatedLampe are not directly saved in db
        em.detach(updatedLampe);

        // Update the Equipement with new association value
        updatedLampe.setEquipement(equipement);
        LampeDTO updatedLampeDTO = lampeMapper.toDto(updatedLampe);
        assertThat(updatedLampeDTO).isNotNull();

        // Update the entity
        restLampeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLampeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLampeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeCreate);
        Lampe testLampe = lampeList.get(lampeList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testLampe.getId()).isEqualTo(testLampe.getEquipement().getId());
    }

    @Test
    @Transactional
    void getAllLampes() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList
        restLampeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lampe.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getLampe() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get the lampe
        restLampeMockMvc
            .perform(get(ENTITY_API_URL_ID, lampe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lampe.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getLampesByIdFiltering() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        Long id = lampe.getId();

        defaultLampeShouldBeFound("id.equals=" + id);
        defaultLampeShouldNotBeFound("id.notEquals=" + id);

        defaultLampeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLampeShouldNotBeFound("id.greaterThan=" + id);

        defaultLampeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLampeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLampesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle equals to DEFAULT_LIBELLE
        defaultLampeShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the lampeList where libelle equals to UPDATED_LIBELLE
        defaultLampeShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllLampesByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle not equals to DEFAULT_LIBELLE
        defaultLampeShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the lampeList where libelle not equals to UPDATED_LIBELLE
        defaultLampeShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllLampesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultLampeShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the lampeList where libelle equals to UPDATED_LIBELLE
        defaultLampeShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllLampesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle is not null
        defaultLampeShouldBeFound("libelle.specified=true");

        // Get all the lampeList where libelle is null
        defaultLampeShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllLampesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle contains DEFAULT_LIBELLE
        defaultLampeShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the lampeList where libelle contains UPDATED_LIBELLE
        defaultLampeShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllLampesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        // Get all the lampeList where libelle does not contain DEFAULT_LIBELLE
        defaultLampeShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the lampeList where libelle does not contain UPDATED_LIBELLE
        defaultLampeShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllLampesByEquipementIsEqualToSomething() throws Exception {
        // Get already existing entity
        Equipement equipement = lampe.getEquipement();
        lampeRepository.saveAndFlush(lampe);
        Long equipementId = equipement.getId();

        // Get all the lampeList where equipement equals to equipementId
        defaultLampeShouldBeFound("equipementId.equals=" + equipementId);

        // Get all the lampeList where equipement equals to (equipementId + 1)
        defaultLampeShouldNotBeFound("equipementId.equals=" + (equipementId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLampeShouldBeFound(String filter) throws Exception {
        restLampeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lampe.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));

        // Check, that the count call also returns 1
        restLampeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLampeShouldNotBeFound(String filter) throws Exception {
        restLampeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLampeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLampe() throws Exception {
        // Get the lampe
        restLampeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLampe() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();

        // Update the lampe
        Lampe updatedLampe = lampeRepository.findById(lampe.getId()).get();
        // Disconnect from session so that the updates on updatedLampe are not directly saved in db
        em.detach(updatedLampe);
        updatedLampe.libelle(UPDATED_LIBELLE);
        LampeDTO lampeDTO = lampeMapper.toDto(updatedLampe);

        restLampeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lampeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lampeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
        Lampe testLampe = lampeList.get(lampeList.size() - 1);
        assertThat(testLampe.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void putNonExistingLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lampeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lampeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lampeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lampeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLampeWithPatch() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();

        // Update the lampe using partial update
        Lampe partialUpdatedLampe = new Lampe();
        partialUpdatedLampe.setId(lampe.getId());

        restLampeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLampe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLampe))
            )
            .andExpect(status().isOk());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
        Lampe testLampe = lampeList.get(lampeList.size() - 1);
        assertThat(testLampe.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateLampeWithPatch() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();

        // Update the lampe using partial update
        Lampe partialUpdatedLampe = new Lampe();
        partialUpdatedLampe.setId(lampe.getId());

        partialUpdatedLampe.libelle(UPDATED_LIBELLE);

        restLampeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLampe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLampe))
            )
            .andExpect(status().isOk());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
        Lampe testLampe = lampeList.get(lampeList.size() - 1);
        assertThat(testLampe.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lampeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lampeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lampeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLampe() throws Exception {
        int databaseSizeBeforeUpdate = lampeRepository.findAll().size();
        lampe.setId(count.incrementAndGet());

        // Create the Lampe
        LampeDTO lampeDTO = lampeMapper.toDto(lampe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLampeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lampeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lampe in the database
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLampe() throws Exception {
        // Initialize the database
        lampeRepository.saveAndFlush(lampe);

        int databaseSizeBeforeDelete = lampeRepository.findAll().size();

        // Delete the lampe
        restLampeMockMvc
            .perform(delete(ENTITY_API_URL_ID, lampe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lampe> lampeList = lampeRepository.findAll();
        assertThat(lampeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
