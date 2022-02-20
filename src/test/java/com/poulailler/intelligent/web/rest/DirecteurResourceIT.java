package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.domain.User;
import com.poulailler.intelligent.repository.DirecteurRepository;
import com.poulailler.intelligent.service.criteria.DirecteurCriteria;
import com.poulailler.intelligent.service.dto.DirecteurDTO;
import com.poulailler.intelligent.service.mapper.DirecteurMapper;
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
 * Integration tests for the {@link DirecteurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DirecteurResourceIT {

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/directeurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirecteurRepository directeurRepository;

    @Autowired
    private DirecteurMapper directeurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirecteurMockMvc;

    private Directeur directeur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Directeur createEntity(EntityManager em) {
        Directeur directeur = new Directeur().adresse(DEFAULT_ADRESSE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        directeur.setUser(user);
        return directeur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Directeur createUpdatedEntity(EntityManager em) {
        Directeur directeur = new Directeur().adresse(UPDATED_ADRESSE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        directeur.setUser(user);
        return directeur;
    }

    @BeforeEach
    public void initTest() {
        directeur = createEntity(em);
    }

    @Test
    @Transactional
    void createDirecteur() throws Exception {
        int databaseSizeBeforeCreate = directeurRepository.findAll().size();
        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);
        restDirecteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directeurDTO)))
            .andExpect(status().isCreated());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeCreate + 1);
        Directeur testDirecteur = directeurList.get(directeurList.size() - 1);
        assertThat(testDirecteur.getAdresse()).isEqualTo(DEFAULT_ADRESSE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testDirecteur.getId()).isEqualTo(testDirecteur.getUser().getId());
    }

    @Test
    @Transactional
    void createDirecteurWithExistingId() throws Exception {
        // Create the Directeur with an existing ID
        directeur.setId(1L);
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        int databaseSizeBeforeCreate = directeurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirecteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directeurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateDirecteurMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);
        int databaseSizeBeforeCreate = directeurRepository.findAll().size();

        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the directeur
        Directeur updatedDirecteur = directeurRepository.findById(directeur.getId()).get();
        assertThat(updatedDirecteur).isNotNull();
        // Disconnect from session so that the updates on updatedDirecteur are not directly saved in db
        em.detach(updatedDirecteur);

        // Update the User with new association value
        updatedDirecteur.setUser(user);
        DirecteurDTO updatedDirecteurDTO = directeurMapper.toDto(updatedDirecteur);
        assertThat(updatedDirecteurDTO).isNotNull();

        // Update the entity
        restDirecteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDirecteurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDirecteurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeCreate);
        Directeur testDirecteur = directeurList.get(directeurList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testDirecteur.getId()).isEqualTo(testDirecteur.getUser().getId());
    }

    @Test
    @Transactional
    void getAllDirecteurs() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directeur.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));
    }

    @Test
    @Transactional
    void getDirecteur() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get the directeur
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL_ID, directeur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(directeur.getId().intValue()))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE));
    }

    @Test
    @Transactional
    void getDirecteursByIdFiltering() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        Long id = directeur.getId();

        defaultDirecteurShouldBeFound("id.equals=" + id);
        defaultDirecteurShouldNotBeFound("id.notEquals=" + id);

        defaultDirecteurShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDirecteurShouldNotBeFound("id.greaterThan=" + id);

        defaultDirecteurShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDirecteurShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse equals to DEFAULT_ADRESSE
        defaultDirecteurShouldBeFound("adresse.equals=" + DEFAULT_ADRESSE);

        // Get all the directeurList where adresse equals to UPDATED_ADRESSE
        defaultDirecteurShouldNotBeFound("adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse not equals to DEFAULT_ADRESSE
        defaultDirecteurShouldNotBeFound("adresse.notEquals=" + DEFAULT_ADRESSE);

        // Get all the directeurList where adresse not equals to UPDATED_ADRESSE
        defaultDirecteurShouldBeFound("adresse.notEquals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse in DEFAULT_ADRESSE or UPDATED_ADRESSE
        defaultDirecteurShouldBeFound("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE);

        // Get all the directeurList where adresse equals to UPDATED_ADRESSE
        defaultDirecteurShouldNotBeFound("adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse is not null
        defaultDirecteurShouldBeFound("adresse.specified=true");

        // Get all the directeurList where adresse is null
        defaultDirecteurShouldNotBeFound("adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseContainsSomething() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse contains DEFAULT_ADRESSE
        defaultDirecteurShouldBeFound("adresse.contains=" + DEFAULT_ADRESSE);

        // Get all the directeurList where adresse contains UPDATED_ADRESSE
        defaultDirecteurShouldNotBeFound("adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllDirecteursByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        // Get all the directeurList where adresse does not contain DEFAULT_ADRESSE
        defaultDirecteurShouldNotBeFound("adresse.doesNotContain=" + DEFAULT_ADRESSE);

        // Get all the directeurList where adresse does not contain UPDATED_ADRESSE
        defaultDirecteurShouldBeFound("adresse.doesNotContain=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllDirecteursByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = directeur.getUser();
        directeurRepository.saveAndFlush(directeur);
        Long userId = user.getId();

        // Get all the directeurList where user equals to userId
        defaultDirecteurShouldBeFound("userId.equals=" + userId);

        // Get all the directeurList where user equals to (userId + 1)
        defaultDirecteurShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDirecteurShouldBeFound(String filter) throws Exception {
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(directeur.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)));

        // Check, that the count call also returns 1
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDirecteurShouldNotBeFound(String filter) throws Exception {
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDirecteurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDirecteur() throws Exception {
        // Get the directeur
        restDirecteurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDirecteur() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();

        // Update the directeur
        Directeur updatedDirecteur = directeurRepository.findById(directeur.getId()).get();
        // Disconnect from session so that the updates on updatedDirecteur are not directly saved in db
        em.detach(updatedDirecteur);
        updatedDirecteur.adresse(UPDATED_ADRESSE);
        DirecteurDTO directeurDTO = directeurMapper.toDto(updatedDirecteur);

        restDirecteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directeurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
        Directeur testDirecteur = directeurList.get(directeurList.size() - 1);
        assertThat(testDirecteur.getAdresse()).isEqualTo(UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void putNonExistingDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directeurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(directeurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDirecteurWithPatch() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();

        // Update the directeur using partial update
        Directeur partialUpdatedDirecteur = new Directeur();
        partialUpdatedDirecteur.setId(directeur.getId());

        restDirecteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirecteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirecteur))
            )
            .andExpect(status().isOk());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
        Directeur testDirecteur = directeurList.get(directeurList.size() - 1);
        assertThat(testDirecteur.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void fullUpdateDirecteurWithPatch() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();

        // Update the directeur using partial update
        Directeur partialUpdatedDirecteur = new Directeur();
        partialUpdatedDirecteur.setId(directeur.getId());

        partialUpdatedDirecteur.adresse(UPDATED_ADRESSE);

        restDirecteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirecteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDirecteur))
            )
            .andExpect(status().isOk());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
        Directeur testDirecteur = directeurList.get(directeurList.size() - 1);
        assertThat(testDirecteur.getAdresse()).isEqualTo(UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void patchNonExistingDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, directeurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirecteur() throws Exception {
        int databaseSizeBeforeUpdate = directeurRepository.findAll().size();
        directeur.setId(count.incrementAndGet());

        // Create the Directeur
        DirecteurDTO directeurDTO = directeurMapper.toDto(directeur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirecteurMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(directeurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Directeur in the database
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDirecteur() throws Exception {
        // Initialize the database
        directeurRepository.saveAndFlush(directeur);

        int databaseSizeBeforeDelete = directeurRepository.findAll().size();

        // Delete the directeur
        restDirecteurMockMvc
            .perform(delete(ENTITY_API_URL_ID, directeur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Directeur> directeurList = directeurRepository.findAll();
        assertThat(directeurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
