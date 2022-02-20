package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.domain.Serrure;
import com.poulailler.intelligent.repository.SerrureRepository;
import com.poulailler.intelligent.service.criteria.SerrureCriteria;
import com.poulailler.intelligent.service.dto.SerrureDTO;
import com.poulailler.intelligent.service.mapper.SerrureMapper;
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
 * Integration tests for the {@link SerrureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SerrureResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final Long DEFAULT_DIMENSION = 1L;
    private static final Long UPDATED_DIMENSION = 2L;
    private static final Long SMALLER_DIMENSION = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/serrures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SerrureRepository serrureRepository;

    @Autowired
    private SerrureMapper serrureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSerrureMockMvc;

    private Serrure serrure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Serrure createEntity(EntityManager em) {
        Serrure serrure = new Serrure().libelle(DEFAULT_LIBELLE).dimension(DEFAULT_DIMENSION);
        // Add required entity
        Equipement equipement;
        if (TestUtil.findAll(em, Equipement.class).isEmpty()) {
            equipement = EquipementResourceIT.createEntity(em);
            em.persist(equipement);
            em.flush();
        } else {
            equipement = TestUtil.findAll(em, Equipement.class).get(0);
        }
        serrure.setEquipement(equipement);
        return serrure;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Serrure createUpdatedEntity(EntityManager em) {
        Serrure serrure = new Serrure().libelle(UPDATED_LIBELLE).dimension(UPDATED_DIMENSION);
        // Add required entity
        Equipement equipement;
        if (TestUtil.findAll(em, Equipement.class).isEmpty()) {
            equipement = EquipementResourceIT.createUpdatedEntity(em);
            em.persist(equipement);
            em.flush();
        } else {
            equipement = TestUtil.findAll(em, Equipement.class).get(0);
        }
        serrure.setEquipement(equipement);
        return serrure;
    }

    @BeforeEach
    public void initTest() {
        serrure = createEntity(em);
    }

    @Test
    @Transactional
    void createSerrure() throws Exception {
        int databaseSizeBeforeCreate = serrureRepository.findAll().size();
        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);
        restSerrureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serrureDTO)))
            .andExpect(status().isCreated());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeCreate + 1);
        Serrure testSerrure = serrureList.get(serrureList.size() - 1);
        assertThat(testSerrure.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testSerrure.getDimension()).isEqualTo(DEFAULT_DIMENSION);

        // Validate the id for MapsId, the ids must be same
        assertThat(testSerrure.getId()).isEqualTo(testSerrure.getEquipement().getId());
    }

    @Test
    @Transactional
    void createSerrureWithExistingId() throws Exception {
        // Create the Serrure with an existing ID
        serrure.setId(1L);
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        int databaseSizeBeforeCreate = serrureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSerrureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serrureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateSerrureMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);
        int databaseSizeBeforeCreate = serrureRepository.findAll().size();

        // Add a new parent entity
        Equipement equipement = EquipementResourceIT.createUpdatedEntity(em);
        em.persist(equipement);
        em.flush();

        // Load the serrure
        Serrure updatedSerrure = serrureRepository.findById(serrure.getId()).get();
        assertThat(updatedSerrure).isNotNull();
        // Disconnect from session so that the updates on updatedSerrure are not directly saved in db
        em.detach(updatedSerrure);

        // Update the Equipement with new association value
        updatedSerrure.setEquipement(equipement);
        SerrureDTO updatedSerrureDTO = serrureMapper.toDto(updatedSerrure);
        assertThat(updatedSerrureDTO).isNotNull();

        // Update the entity
        restSerrureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSerrureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSerrureDTO))
            )
            .andExpect(status().isOk());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeCreate);
        Serrure testSerrure = serrureList.get(serrureList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testSerrure.getId()).isEqualTo(testSerrure.getEquipement().getId());
    }

    @Test
    @Transactional
    void getAllSerrures() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serrure.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].dimension").value(hasItem(DEFAULT_DIMENSION.intValue())));
    }

    @Test
    @Transactional
    void getSerrure() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get the serrure
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL_ID, serrure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serrure.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.dimension").value(DEFAULT_DIMENSION.intValue()));
    }

    @Test
    @Transactional
    void getSerruresByIdFiltering() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        Long id = serrure.getId();

        defaultSerrureShouldBeFound("id.equals=" + id);
        defaultSerrureShouldNotBeFound("id.notEquals=" + id);

        defaultSerrureShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSerrureShouldNotBeFound("id.greaterThan=" + id);

        defaultSerrureShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSerrureShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle equals to DEFAULT_LIBELLE
        defaultSerrureShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the serrureList where libelle equals to UPDATED_LIBELLE
        defaultSerrureShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle not equals to DEFAULT_LIBELLE
        defaultSerrureShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the serrureList where libelle not equals to UPDATED_LIBELLE
        defaultSerrureShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultSerrureShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the serrureList where libelle equals to UPDATED_LIBELLE
        defaultSerrureShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle is not null
        defaultSerrureShouldBeFound("libelle.specified=true");

        // Get all the serrureList where libelle is null
        defaultSerrureShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleContainsSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle contains DEFAULT_LIBELLE
        defaultSerrureShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the serrureList where libelle contains UPDATED_LIBELLE
        defaultSerrureShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSerruresByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where libelle does not contain DEFAULT_LIBELLE
        defaultSerrureShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the serrureList where libelle does not contain UPDATED_LIBELLE
        defaultSerrureShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension equals to DEFAULT_DIMENSION
        defaultSerrureShouldBeFound("dimension.equals=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension equals to UPDATED_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.equals=" + UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension not equals to DEFAULT_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.notEquals=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension not equals to UPDATED_DIMENSION
        defaultSerrureShouldBeFound("dimension.notEquals=" + UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsInShouldWork() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension in DEFAULT_DIMENSION or UPDATED_DIMENSION
        defaultSerrureShouldBeFound("dimension.in=" + DEFAULT_DIMENSION + "," + UPDATED_DIMENSION);

        // Get all the serrureList where dimension equals to UPDATED_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.in=" + UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsNullOrNotNull() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension is not null
        defaultSerrureShouldBeFound("dimension.specified=true");

        // Get all the serrureList where dimension is null
        defaultSerrureShouldNotBeFound("dimension.specified=false");
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension is greater than or equal to DEFAULT_DIMENSION
        defaultSerrureShouldBeFound("dimension.greaterThanOrEqual=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension is greater than or equal to UPDATED_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.greaterThanOrEqual=" + UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension is less than or equal to DEFAULT_DIMENSION
        defaultSerrureShouldBeFound("dimension.lessThanOrEqual=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension is less than or equal to SMALLER_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.lessThanOrEqual=" + SMALLER_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsLessThanSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension is less than DEFAULT_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.lessThan=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension is less than UPDATED_DIMENSION
        defaultSerrureShouldBeFound("dimension.lessThan=" + UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByDimensionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        // Get all the serrureList where dimension is greater than DEFAULT_DIMENSION
        defaultSerrureShouldNotBeFound("dimension.greaterThan=" + DEFAULT_DIMENSION);

        // Get all the serrureList where dimension is greater than SMALLER_DIMENSION
        defaultSerrureShouldBeFound("dimension.greaterThan=" + SMALLER_DIMENSION);
    }

    @Test
    @Transactional
    void getAllSerruresByEquipementIsEqualToSomething() throws Exception {
        // Get already existing entity
        Equipement equipement = serrure.getEquipement();
        serrureRepository.saveAndFlush(serrure);
        Long equipementId = equipement.getId();

        // Get all the serrureList where equipement equals to equipementId
        defaultSerrureShouldBeFound("equipementId.equals=" + equipementId);

        // Get all the serrureList where equipement equals to (equipementId + 1)
        defaultSerrureShouldNotBeFound("equipementId.equals=" + (equipementId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSerrureShouldBeFound(String filter) throws Exception {
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serrure.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].dimension").value(hasItem(DEFAULT_DIMENSION.intValue())));

        // Check, that the count call also returns 1
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSerrureShouldNotBeFound(String filter) throws Exception {
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSerrureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSerrure() throws Exception {
        // Get the serrure
        restSerrureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSerrure() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();

        // Update the serrure
        Serrure updatedSerrure = serrureRepository.findById(serrure.getId()).get();
        // Disconnect from session so that the updates on updatedSerrure are not directly saved in db
        em.detach(updatedSerrure);
        updatedSerrure.libelle(UPDATED_LIBELLE).dimension(UPDATED_DIMENSION);
        SerrureDTO serrureDTO = serrureMapper.toDto(updatedSerrure);

        restSerrureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serrureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isOk());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
        Serrure testSerrure = serrureList.get(serrureList.size() - 1);
        assertThat(testSerrure.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testSerrure.getDimension()).isEqualTo(UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void putNonExistingSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serrureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(serrureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSerrureWithPatch() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();

        // Update the serrure using partial update
        Serrure partialUpdatedSerrure = new Serrure();
        partialUpdatedSerrure.setId(serrure.getId());

        partialUpdatedSerrure.dimension(UPDATED_DIMENSION);

        restSerrureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSerrure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSerrure))
            )
            .andExpect(status().isOk());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
        Serrure testSerrure = serrureList.get(serrureList.size() - 1);
        assertThat(testSerrure.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testSerrure.getDimension()).isEqualTo(UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void fullUpdateSerrureWithPatch() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();

        // Update the serrure using partial update
        Serrure partialUpdatedSerrure = new Serrure();
        partialUpdatedSerrure.setId(serrure.getId());

        partialUpdatedSerrure.libelle(UPDATED_LIBELLE).dimension(UPDATED_DIMENSION);

        restSerrureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSerrure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSerrure))
            )
            .andExpect(status().isOk());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
        Serrure testSerrure = serrureList.get(serrureList.size() - 1);
        assertThat(testSerrure.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testSerrure.getDimension()).isEqualTo(UPDATED_DIMENSION);
    }

    @Test
    @Transactional
    void patchNonExistingSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serrureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSerrure() throws Exception {
        int databaseSizeBeforeUpdate = serrureRepository.findAll().size();
        serrure.setId(count.incrementAndGet());

        // Create the Serrure
        SerrureDTO serrureDTO = serrureMapper.toDto(serrure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSerrureMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(serrureDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Serrure in the database
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSerrure() throws Exception {
        // Initialize the database
        serrureRepository.saveAndFlush(serrure);

        int databaseSizeBeforeDelete = serrureRepository.findAll().size();

        // Delete the serrure
        restSerrureMockMvc
            .perform(delete(ENTITY_API_URL_ID, serrure.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Serrure> serrureList = serrureRepository.findAll();
        assertThat(serrureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
