package com.poulailler.intelligent.web.rest;

import static com.poulailler.intelligent.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.poulailler.intelligent.IntegrationTest;
import com.poulailler.intelligent.domain.Directeur;
import com.poulailler.intelligent.domain.Employe;
import com.poulailler.intelligent.domain.Equipement;
import com.poulailler.intelligent.domain.enumeration.TypeEquipement;
import com.poulailler.intelligent.repository.EquipementRepository;
import com.poulailler.intelligent.service.EquipementService;
import com.poulailler.intelligent.service.criteria.EquipementCriteria;
import com.poulailler.intelligent.service.dto.EquipementDTO;
import com.poulailler.intelligent.service.mapper.EquipementMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EquipementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EquipementResourceIT {

    private static final Boolean DEFAULT_STATUT = false;
    private static final Boolean UPDATED_STATUT = true;

    private static final String DEFAULT_REF_ARDUINO = "AAAAAAAAAA";
    private static final String UPDATED_REF_ARDUINO = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_CREA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_CREA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_CREA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_MARQUE = "AAAAAAAAAA";
    private static final String UPDATED_MARQUE = "BBBBBBBBBB";

    private static final TypeEquipement DEFAULT_TYPE = TypeEquipement.CAPTEUR;
    private static final TypeEquipement UPDATED_TYPE = TypeEquipement.ACTUATEUR;

    private static final String ENTITY_API_URL = "/api/equipements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EquipementRepository equipementRepository;

    @Mock
    private EquipementRepository equipementRepositoryMock;

    @Autowired
    private EquipementMapper equipementMapper;

    @Mock
    private EquipementService equipementServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEquipementMockMvc;

    private Equipement equipement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipement createEntity(EntityManager em) {
        Equipement equipement = new Equipement()
            .statut(DEFAULT_STATUT)
            .refArduino(DEFAULT_REF_ARDUINO)
            .dateCrea(DEFAULT_DATE_CREA)
            .libelle(DEFAULT_LIBELLE)
            .marque(DEFAULT_MARQUE)
            .type(DEFAULT_TYPE);
        return equipement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipement createUpdatedEntity(EntityManager em) {
        Equipement equipement = new Equipement()
            .statut(UPDATED_STATUT)
            .refArduino(UPDATED_REF_ARDUINO)
            .dateCrea(UPDATED_DATE_CREA)
            .libelle(UPDATED_LIBELLE)
            .marque(UPDATED_MARQUE)
            .type(UPDATED_TYPE);
        return equipement;
    }

    @BeforeEach
    public void initTest() {
        equipement = createEntity(em);
    }

    @Test
    @Transactional
    void createEquipement() throws Exception {
        int databaseSizeBeforeCreate = equipementRepository.findAll().size();
        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);
        restEquipementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipementDTO)))
            .andExpect(status().isCreated());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeCreate + 1);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getStatut()).isEqualTo(DEFAULT_STATUT);
        assertThat(testEquipement.getRefArduino()).isEqualTo(DEFAULT_REF_ARDUINO);
        assertThat(testEquipement.getDateCrea()).isEqualTo(DEFAULT_DATE_CREA);
        assertThat(testEquipement.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testEquipement.getMarque()).isEqualTo(DEFAULT_MARQUE);
        assertThat(testEquipement.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createEquipementWithExistingId() throws Exception {
        // Create the Equipement with an existing ID
        equipement.setId(1L);
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        int databaseSizeBeforeCreate = equipementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEquipementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRefArduinoIsRequired() throws Exception {
        int databaseSizeBeforeTest = equipementRepository.findAll().size();
        // set the field null
        equipement.setRefArduino(null);

        // Create the Equipement, which fails.
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        restEquipementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipementDTO)))
            .andExpect(status().isBadRequest());

        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = equipementRepository.findAll().size();
        // set the field null
        equipement.setLibelle(null);

        // Create the Equipement, which fails.
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        restEquipementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipementDTO)))
            .andExpect(status().isBadRequest());

        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEquipements() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipement.getId().intValue())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.booleanValue())))
            .andExpect(jsonPath("$.[*].refArduino").value(hasItem(DEFAULT_REF_ARDUINO)))
            .andExpect(jsonPath("$.[*].dateCrea").value(hasItem(sameInstant(DEFAULT_DATE_CREA))))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].marque").value(hasItem(DEFAULT_MARQUE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(equipementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(equipementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getEquipement() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get the equipement
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL_ID, equipement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(equipement.getId().intValue()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.booleanValue()))
            .andExpect(jsonPath("$.refArduino").value(DEFAULT_REF_ARDUINO))
            .andExpect(jsonPath("$.dateCrea").value(sameInstant(DEFAULT_DATE_CREA)))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.marque").value(DEFAULT_MARQUE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getEquipementsByIdFiltering() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        Long id = equipement.getId();

        defaultEquipementShouldBeFound("id.equals=" + id);
        defaultEquipementShouldNotBeFound("id.notEquals=" + id);

        defaultEquipementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEquipementShouldNotBeFound("id.greaterThan=" + id);

        defaultEquipementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEquipementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEquipementsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where statut equals to DEFAULT_STATUT
        defaultEquipementShouldBeFound("statut.equals=" + DEFAULT_STATUT);

        // Get all the equipementList where statut equals to UPDATED_STATUT
        defaultEquipementShouldNotBeFound("statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllEquipementsByStatutIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where statut not equals to DEFAULT_STATUT
        defaultEquipementShouldNotBeFound("statut.notEquals=" + DEFAULT_STATUT);

        // Get all the equipementList where statut not equals to UPDATED_STATUT
        defaultEquipementShouldBeFound("statut.notEquals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllEquipementsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where statut in DEFAULT_STATUT or UPDATED_STATUT
        defaultEquipementShouldBeFound("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT);

        // Get all the equipementList where statut equals to UPDATED_STATUT
        defaultEquipementShouldNotBeFound("statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllEquipementsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where statut is not null
        defaultEquipementShouldBeFound("statut.specified=true");

        // Get all the equipementList where statut is null
        defaultEquipementShouldNotBeFound("statut.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino equals to DEFAULT_REF_ARDUINO
        defaultEquipementShouldBeFound("refArduino.equals=" + DEFAULT_REF_ARDUINO);

        // Get all the equipementList where refArduino equals to UPDATED_REF_ARDUINO
        defaultEquipementShouldNotBeFound("refArduino.equals=" + UPDATED_REF_ARDUINO);
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino not equals to DEFAULT_REF_ARDUINO
        defaultEquipementShouldNotBeFound("refArduino.notEquals=" + DEFAULT_REF_ARDUINO);

        // Get all the equipementList where refArduino not equals to UPDATED_REF_ARDUINO
        defaultEquipementShouldBeFound("refArduino.notEquals=" + UPDATED_REF_ARDUINO);
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino in DEFAULT_REF_ARDUINO or UPDATED_REF_ARDUINO
        defaultEquipementShouldBeFound("refArduino.in=" + DEFAULT_REF_ARDUINO + "," + UPDATED_REF_ARDUINO);

        // Get all the equipementList where refArduino equals to UPDATED_REF_ARDUINO
        defaultEquipementShouldNotBeFound("refArduino.in=" + UPDATED_REF_ARDUINO);
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino is not null
        defaultEquipementShouldBeFound("refArduino.specified=true");

        // Get all the equipementList where refArduino is null
        defaultEquipementShouldNotBeFound("refArduino.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino contains DEFAULT_REF_ARDUINO
        defaultEquipementShouldBeFound("refArduino.contains=" + DEFAULT_REF_ARDUINO);

        // Get all the equipementList where refArduino contains UPDATED_REF_ARDUINO
        defaultEquipementShouldNotBeFound("refArduino.contains=" + UPDATED_REF_ARDUINO);
    }

    @Test
    @Transactional
    void getAllEquipementsByRefArduinoNotContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where refArduino does not contain DEFAULT_REF_ARDUINO
        defaultEquipementShouldNotBeFound("refArduino.doesNotContain=" + DEFAULT_REF_ARDUINO);

        // Get all the equipementList where refArduino does not contain UPDATED_REF_ARDUINO
        defaultEquipementShouldBeFound("refArduino.doesNotContain=" + UPDATED_REF_ARDUINO);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea equals to DEFAULT_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.equals=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea equals to UPDATED_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.equals=" + UPDATED_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea not equals to DEFAULT_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.notEquals=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea not equals to UPDATED_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.notEquals=" + UPDATED_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea in DEFAULT_DATE_CREA or UPDATED_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.in=" + DEFAULT_DATE_CREA + "," + UPDATED_DATE_CREA);

        // Get all the equipementList where dateCrea equals to UPDATED_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.in=" + UPDATED_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea is not null
        defaultEquipementShouldBeFound("dateCrea.specified=true");

        // Get all the equipementList where dateCrea is null
        defaultEquipementShouldNotBeFound("dateCrea.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea is greater than or equal to DEFAULT_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.greaterThanOrEqual=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea is greater than or equal to UPDATED_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.greaterThanOrEqual=" + UPDATED_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea is less than or equal to DEFAULT_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.lessThanOrEqual=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea is less than or equal to SMALLER_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.lessThanOrEqual=" + SMALLER_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsLessThanSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea is less than DEFAULT_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.lessThan=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea is less than UPDATED_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.lessThan=" + UPDATED_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByDateCreaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where dateCrea is greater than DEFAULT_DATE_CREA
        defaultEquipementShouldNotBeFound("dateCrea.greaterThan=" + DEFAULT_DATE_CREA);

        // Get all the equipementList where dateCrea is greater than SMALLER_DATE_CREA
        defaultEquipementShouldBeFound("dateCrea.greaterThan=" + SMALLER_DATE_CREA);
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle equals to DEFAULT_LIBELLE
        defaultEquipementShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the equipementList where libelle equals to UPDATED_LIBELLE
        defaultEquipementShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle not equals to DEFAULT_LIBELLE
        defaultEquipementShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the equipementList where libelle not equals to UPDATED_LIBELLE
        defaultEquipementShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultEquipementShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the equipementList where libelle equals to UPDATED_LIBELLE
        defaultEquipementShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle is not null
        defaultEquipementShouldBeFound("libelle.specified=true");

        // Get all the equipementList where libelle is null
        defaultEquipementShouldNotBeFound("libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle contains DEFAULT_LIBELLE
        defaultEquipementShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the equipementList where libelle contains UPDATED_LIBELLE
        defaultEquipementShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllEquipementsByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where libelle does not contain DEFAULT_LIBELLE
        defaultEquipementShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the equipementList where libelle does not contain UPDATED_LIBELLE
        defaultEquipementShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque equals to DEFAULT_MARQUE
        defaultEquipementShouldBeFound("marque.equals=" + DEFAULT_MARQUE);

        // Get all the equipementList where marque equals to UPDATED_MARQUE
        defaultEquipementShouldNotBeFound("marque.equals=" + UPDATED_MARQUE);
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque not equals to DEFAULT_MARQUE
        defaultEquipementShouldNotBeFound("marque.notEquals=" + DEFAULT_MARQUE);

        // Get all the equipementList where marque not equals to UPDATED_MARQUE
        defaultEquipementShouldBeFound("marque.notEquals=" + UPDATED_MARQUE);
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque in DEFAULT_MARQUE or UPDATED_MARQUE
        defaultEquipementShouldBeFound("marque.in=" + DEFAULT_MARQUE + "," + UPDATED_MARQUE);

        // Get all the equipementList where marque equals to UPDATED_MARQUE
        defaultEquipementShouldNotBeFound("marque.in=" + UPDATED_MARQUE);
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque is not null
        defaultEquipementShouldBeFound("marque.specified=true");

        // Get all the equipementList where marque is null
        defaultEquipementShouldNotBeFound("marque.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque contains DEFAULT_MARQUE
        defaultEquipementShouldBeFound("marque.contains=" + DEFAULT_MARQUE);

        // Get all the equipementList where marque contains UPDATED_MARQUE
        defaultEquipementShouldNotBeFound("marque.contains=" + UPDATED_MARQUE);
    }

    @Test
    @Transactional
    void getAllEquipementsByMarqueNotContainsSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where marque does not contain DEFAULT_MARQUE
        defaultEquipementShouldNotBeFound("marque.doesNotContain=" + DEFAULT_MARQUE);

        // Get all the equipementList where marque does not contain UPDATED_MARQUE
        defaultEquipementShouldBeFound("marque.doesNotContain=" + UPDATED_MARQUE);
    }

    @Test
    @Transactional
    void getAllEquipementsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where type equals to DEFAULT_TYPE
        defaultEquipementShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the equipementList where type equals to UPDATED_TYPE
        defaultEquipementShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllEquipementsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where type not equals to DEFAULT_TYPE
        defaultEquipementShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the equipementList where type not equals to UPDATED_TYPE
        defaultEquipementShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllEquipementsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultEquipementShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the equipementList where type equals to UPDATED_TYPE
        defaultEquipementShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllEquipementsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        // Get all the equipementList where type is not null
        defaultEquipementShouldBeFound("type.specified=true");

        // Get all the equipementList where type is null
        defaultEquipementShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipementsByEmployeIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);
        Employe employe;
        if (TestUtil.findAll(em, Employe.class).isEmpty()) {
            employe = EmployeResourceIT.createEntity(em);
            em.persist(employe);
            em.flush();
        } else {
            employe = TestUtil.findAll(em, Employe.class).get(0);
        }
        em.persist(employe);
        em.flush();
        equipement.addEmploye(employe);
        equipementRepository.saveAndFlush(equipement);
        Long employeId = employe.getId();

        // Get all the equipementList where employe equals to employeId
        defaultEquipementShouldBeFound("employeId.equals=" + employeId);

        // Get all the equipementList where employe equals to (employeId + 1)
        defaultEquipementShouldNotBeFound("employeId.equals=" + (employeId + 1));
    }

    @Test
    @Transactional
    void getAllEquipementsByGestionnaireIsEqualToSomething() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);
        Directeur gestionnaire;
        if (TestUtil.findAll(em, Directeur.class).isEmpty()) {
            gestionnaire = DirecteurResourceIT.createEntity(em);
            em.persist(gestionnaire);
            em.flush();
        } else {
            gestionnaire = TestUtil.findAll(em, Directeur.class).get(0);
        }
        em.persist(gestionnaire);
        em.flush();
        equipement.addGestionnaire(gestionnaire);
        equipementRepository.saveAndFlush(equipement);
        Long gestionnaireId = gestionnaire.getId();

        // Get all the equipementList where gestionnaire equals to gestionnaireId
        defaultEquipementShouldBeFound("gestionnaireId.equals=" + gestionnaireId);

        // Get all the equipementList where gestionnaire equals to (gestionnaireId + 1)
        defaultEquipementShouldNotBeFound("gestionnaireId.equals=" + (gestionnaireId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEquipementShouldBeFound(String filter) throws Exception {
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipement.getId().intValue())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.booleanValue())))
            .andExpect(jsonPath("$.[*].refArduino").value(hasItem(DEFAULT_REF_ARDUINO)))
            .andExpect(jsonPath("$.[*].dateCrea").value(hasItem(sameInstant(DEFAULT_DATE_CREA))))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].marque").value(hasItem(DEFAULT_MARQUE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEquipementShouldNotBeFound(String filter) throws Exception {
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEquipementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEquipement() throws Exception {
        // Get the equipement
        restEquipementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEquipement() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();

        // Update the equipement
        Equipement updatedEquipement = equipementRepository.findById(equipement.getId()).get();
        // Disconnect from session so that the updates on updatedEquipement are not directly saved in db
        em.detach(updatedEquipement);
        updatedEquipement
            .statut(UPDATED_STATUT)
            .refArduino(UPDATED_REF_ARDUINO)
            .dateCrea(UPDATED_DATE_CREA)
            .libelle(UPDATED_LIBELLE)
            .marque(UPDATED_MARQUE)
            .type(UPDATED_TYPE);
        EquipementDTO equipementDTO = equipementMapper.toDto(updatedEquipement);

        restEquipementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getStatut()).isEqualTo(UPDATED_STATUT);
        assertThat(testEquipement.getRefArduino()).isEqualTo(UPDATED_REF_ARDUINO);
        assertThat(testEquipement.getDateCrea()).isEqualTo(UPDATED_DATE_CREA);
        assertThat(testEquipement.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testEquipement.getMarque()).isEqualTo(UPDATED_MARQUE);
        assertThat(testEquipement.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEquipementWithPatch() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();

        // Update the equipement using partial update
        Equipement partialUpdatedEquipement = new Equipement();
        partialUpdatedEquipement.setId(equipement.getId());

        partialUpdatedEquipement.statut(UPDATED_STATUT).dateCrea(UPDATED_DATE_CREA).marque(UPDATED_MARQUE);

        restEquipementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipement))
            )
            .andExpect(status().isOk());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getStatut()).isEqualTo(UPDATED_STATUT);
        assertThat(testEquipement.getRefArduino()).isEqualTo(DEFAULT_REF_ARDUINO);
        assertThat(testEquipement.getDateCrea()).isEqualTo(UPDATED_DATE_CREA);
        assertThat(testEquipement.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testEquipement.getMarque()).isEqualTo(UPDATED_MARQUE);
        assertThat(testEquipement.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateEquipementWithPatch() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();

        // Update the equipement using partial update
        Equipement partialUpdatedEquipement = new Equipement();
        partialUpdatedEquipement.setId(equipement.getId());

        partialUpdatedEquipement
            .statut(UPDATED_STATUT)
            .refArduino(UPDATED_REF_ARDUINO)
            .dateCrea(UPDATED_DATE_CREA)
            .libelle(UPDATED_LIBELLE)
            .marque(UPDATED_MARQUE)
            .type(UPDATED_TYPE);

        restEquipementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipement))
            )
            .andExpect(status().isOk());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
        Equipement testEquipement = equipementList.get(equipementList.size() - 1);
        assertThat(testEquipement.getStatut()).isEqualTo(UPDATED_STATUT);
        assertThat(testEquipement.getRefArduino()).isEqualTo(UPDATED_REF_ARDUINO);
        assertThat(testEquipement.getDateCrea()).isEqualTo(UPDATED_DATE_CREA);
        assertThat(testEquipement.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testEquipement.getMarque()).isEqualTo(UPDATED_MARQUE);
        assertThat(testEquipement.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, equipementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEquipement() throws Exception {
        int databaseSizeBeforeUpdate = equipementRepository.findAll().size();
        equipement.setId(count.incrementAndGet());

        // Create the Equipement
        EquipementDTO equipementDTO = equipementMapper.toDto(equipement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipementMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(equipementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipement in the database
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEquipement() throws Exception {
        // Initialize the database
        equipementRepository.saveAndFlush(equipement);

        int databaseSizeBeforeDelete = equipementRepository.findAll().size();

        // Delete the equipement
        restEquipementMockMvc
            .perform(delete(ENTITY_API_URL_ID, equipement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Equipement> equipementList = equipementRepository.findAll();
        assertThat(equipementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
