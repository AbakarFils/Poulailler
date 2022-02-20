package com.poulailler.intelligent.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.NH3;
import com.poulailler.intelligent.domain.Variable;
import com.poulailler.intelligent.repository.NH3Repository;
import com.poulailler.intelligent.service.criteria.NH3Criteria;
import com.poulailler.intelligent.service.dto.NH3DTO;
import com.poulailler.intelligent.service.mapper.NH3Mapper;
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
 * Integration tests for the {@link NH3Resource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NH3ResourceIT {

    private static final Long DEFAULT_VOLUME = 1L;
    private static final Long UPDATED_VOLUME = 2L;
    private static final Long SMALLER_VOLUME = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/nh-3-s";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NH3Repository nH3Repository;

    @Autowired
    private NH3Mapper nH3Mapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNH3MockMvc;

    private NH3 nH3;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NH3 createEntity(EntityManager em) {
        NH3 nH3 = new NH3().volume(DEFAULT_VOLUME);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        nH3.setVariable(variable);
        return nH3;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NH3 createUpdatedEntity(EntityManager em) {
        NH3 nH3 = new NH3().volume(UPDATED_VOLUME);
        // Add required entity
        Variable variable;
        if (TestUtil.findAll(em, Variable.class).isEmpty()) {
            variable = VariableResourceIT.createUpdatedEntity(em);
            em.persist(variable);
            em.flush();
        } else {
            variable = TestUtil.findAll(em, Variable.class).get(0);
        }
        nH3.setVariable(variable);
        return nH3;
    }

    @BeforeEach
    public void initTest() {
        nH3 = createEntity(em);
    }

    @Test
    @Transactional
    void createNH3() throws Exception {
        int databaseSizeBeforeCreate = nH3Repository.findAll().size();
        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);
        restNH3MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nH3DTO)))
            .andExpect(status().isCreated());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeCreate + 1);
        NH3 testNH3 = nH3List.get(nH3List.size() - 1);
        assertThat(testNH3.getVolume()).isEqualTo(DEFAULT_VOLUME);

        // Validate the id for MapsId, the ids must be same
        assertThat(testNH3.getId()).isEqualTo(testNH3.getVariable().getId());
    }

    @Test
    @Transactional
    void createNH3WithExistingId() throws Exception {
        // Create the NH3 with an existing ID
        nH3.setId(1L);
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        int databaseSizeBeforeCreate = nH3Repository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNH3MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nH3DTO)))
            .andExpect(status().isBadRequest());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateNH3MapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);
        int databaseSizeBeforeCreate = nH3Repository.findAll().size();

        // Add a new parent entity
        Variable variable = VariableResourceIT.createUpdatedEntity(em);
        em.persist(variable);
        em.flush();

        // Load the nH3
        NH3 updatedNH3 = nH3Repository.findById(nH3.getId()).get();
        assertThat(updatedNH3).isNotNull();
        // Disconnect from session so that the updates on updatedNH3 are not directly saved in db
        em.detach(updatedNH3);

        // Update the Variable with new association value
        updatedNH3.setVariable(variable);
        NH3DTO updatedNH3DTO = nH3Mapper.toDto(updatedNH3);
        assertThat(updatedNH3DTO).isNotNull();

        // Update the entity
        restNH3MockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNH3DTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNH3DTO))
            )
            .andExpect(status().isOk());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeCreate);
        NH3 testNH3 = nH3List.get(nH3List.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testNH3.getId()).isEqualTo(testNH3.getVariable().getId());
    }

    @Test
    @Transactional
    void getAllNH3s() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List
        restNH3MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nH3.getId().intValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.intValue())));
    }

    @Test
    @Transactional
    void getNH3() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get the nH3
        restNH3MockMvc
            .perform(get(ENTITY_API_URL_ID, nH3.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nH3.getId().intValue()))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.intValue()));
    }

    @Test
    @Transactional
    void getNH3sByIdFiltering() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        Long id = nH3.getId();

        defaultNH3ShouldBeFound("id.equals=" + id);
        defaultNH3ShouldNotBeFound("id.notEquals=" + id);

        defaultNH3ShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNH3ShouldNotBeFound("id.greaterThan=" + id);

        defaultNH3ShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNH3ShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume equals to DEFAULT_VOLUME
        defaultNH3ShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume equals to UPDATED_VOLUME
        defaultNH3ShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume not equals to DEFAULT_VOLUME
        defaultNH3ShouldNotBeFound("volume.notEquals=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume not equals to UPDATED_VOLUME
        defaultNH3ShouldBeFound("volume.notEquals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultNH3ShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the nH3List where volume equals to UPDATED_VOLUME
        defaultNH3ShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume is not null
        defaultNH3ShouldBeFound("volume.specified=true");

        // Get all the nH3List where volume is null
        defaultNH3ShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume is greater than or equal to DEFAULT_VOLUME
        defaultNH3ShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume is greater than or equal to UPDATED_VOLUME
        defaultNH3ShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume is less than or equal to DEFAULT_VOLUME
        defaultNH3ShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume is less than or equal to SMALLER_VOLUME
        defaultNH3ShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume is less than DEFAULT_VOLUME
        defaultNH3ShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume is less than UPDATED_VOLUME
        defaultNH3ShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        // Get all the nH3List where volume is greater than DEFAULT_VOLUME
        defaultNH3ShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the nH3List where volume is greater than SMALLER_VOLUME
        defaultNH3ShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    void getAllNH3sByVariableIsEqualToSomething() throws Exception {
        // Get already existing entity
        Variable variable = nH3.getVariable();
        nH3Repository.saveAndFlush(nH3);
        Long variableId = variable.getId();

        // Get all the nH3List where variable equals to variableId
        defaultNH3ShouldBeFound("variableId.equals=" + variableId);

        // Get all the nH3List where variable equals to (variableId + 1)
        defaultNH3ShouldNotBeFound("variableId.equals=" + (variableId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNH3ShouldBeFound(String filter) throws Exception {
        restNH3MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nH3.getId().intValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.intValue())));

        // Check, that the count call also returns 1
        restNH3MockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNH3ShouldNotBeFound(String filter) throws Exception {
        restNH3MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNH3MockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNH3() throws Exception {
        // Get the nH3
        restNH3MockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNH3() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();

        // Update the nH3
        NH3 updatedNH3 = nH3Repository.findById(nH3.getId()).get();
        // Disconnect from session so that the updates on updatedNH3 are not directly saved in db
        em.detach(updatedNH3);
        updatedNH3.volume(UPDATED_VOLUME);
        NH3DTO nH3DTO = nH3Mapper.toDto(updatedNH3);

        restNH3MockMvc
            .perform(
                put(ENTITY_API_URL_ID, nH3DTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nH3DTO))
            )
            .andExpect(status().isOk());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
        NH3 testNH3 = nH3List.get(nH3List.size() - 1);
        assertThat(testNH3.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void putNonExistingNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(
                put(ENTITY_API_URL_ID, nH3DTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nH3DTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(nH3DTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(nH3DTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNH3WithPatch() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();

        // Update the nH3 using partial update
        NH3 partialUpdatedNH3 = new NH3();
        partialUpdatedNH3.setId(nH3.getId());

        restNH3MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNH3.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNH3))
            )
            .andExpect(status().isOk());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
        NH3 testNH3 = nH3List.get(nH3List.size() - 1);
        assertThat(testNH3.getVolume()).isEqualTo(DEFAULT_VOLUME);
    }

    @Test
    @Transactional
    void fullUpdateNH3WithPatch() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();

        // Update the nH3 using partial update
        NH3 partialUpdatedNH3 = new NH3();
        partialUpdatedNH3.setId(nH3.getId());

        partialUpdatedNH3.volume(UPDATED_VOLUME);

        restNH3MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNH3.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNH3))
            )
            .andExpect(status().isOk());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
        NH3 testNH3 = nH3List.get(nH3List.size() - 1);
        assertThat(testNH3.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    void patchNonExistingNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nH3DTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nH3DTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(nH3DTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNH3() throws Exception {
        int databaseSizeBeforeUpdate = nH3Repository.findAll().size();
        nH3.setId(count.incrementAndGet());

        // Create the NH3
        NH3DTO nH3DTO = nH3Mapper.toDto(nH3);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNH3MockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(nH3DTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NH3 in the database
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNH3() throws Exception {
        // Initialize the database
        nH3Repository.saveAndFlush(nH3);

        int databaseSizeBeforeDelete = nH3Repository.findAll().size();

        // Delete the nH3
        restNH3MockMvc.perform(delete(ENTITY_API_URL_ID, nH3.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NH3> nH3List = nH3Repository.findAll();
        assertThat(nH3List).hasSize(databaseSizeBeforeDelete - 1);
    }
}
