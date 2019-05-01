package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.DoctorsPlatformApp;

import com.mycompany.myapp.domain.Doctor;
import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.repository.DoctorRepository;
import com.mycompany.myapp.repository.PatientRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.search.DoctorSearchRepository;
import com.mycompany.myapp.service.DoctorService;
import com.mycompany.myapp.service.dto.DoctorDTO;
import com.mycompany.myapp.service.mapper.DoctorMapper;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;
import com.mycompany.myapp.service.DoctorQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DoctorResource REST controller.
 *
 * @see DoctorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorsPlatformApp.class)
public class DoctorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_CIN = new BigDecimal(1);
    private static final BigDecimal UPDATED_CIN = new BigDecimal(2);

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALITY = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALITY = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "-_@e.bWAYD";
    private static final String UPDATED_EMAIL = "b@FN.SRTtD";

    private static final BigDecimal DEFAULT_PHONE_NUMBER = new BigDecimal(1);
    private static final BigDecimal UPDATED_PHONE_NUMBER = new BigDecimal(2);

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DoctorService doctorService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.DoctorSearchRepositoryMockConfiguration
     */
    @Autowired
    private DoctorSearchRepository mockDoctorSearchRepository;

    @Autowired
    private DoctorQueryService doctorQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restDoctorMockMvc;

    private Doctor doctor;

    private UserRepository userRepository;

    private PatientRepository patientRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DoctorResource doctorResource = new DoctorResource(doctorService, doctorQueryService,userRepository,patientRepository, doctorRepository);
        this.restDoctorMockMvc = MockMvcBuilders.standaloneSetup(doctorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .name(DEFAULT_NAME)
            .cin(DEFAULT_CIN)
            .address(DEFAULT_ADDRESS)
            .speciality(DEFAULT_SPECIALITY)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
        return doctor;
    }

    @Before
    public void initTest() {
        doctor = createEntity(em);
    }

    @Test
    @Transactional
    public void createDoctor() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().size();

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);
        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isCreated());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate + 1);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDoctor.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testDoctor.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testDoctor.getSpeciality()).isEqualTo(DEFAULT_SPECIALITY);
        assertThat(testDoctor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testDoctor.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);

        // Validate the Doctor in Elasticsearch
        verify(mockDoctorSearchRepository, times(1)).save(testDoctor);
    }

    @Test
    @Transactional
    public void createDoctorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().size();

        // Create the Doctor with an existing ID
        doctor.setId(1L);
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Doctor in Elasticsearch
        verify(mockDoctorSearchRepository, times(0)).save(doctor);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setName(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCinIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setCin(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setAddress(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSpecialityIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setSpeciality(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setPhoneNumber(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc.perform(post("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDoctors() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList
        restDoctorMockMvc.perform(get("/api/doctors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.intValue())));
    }

    @Test
    @Transactional
    public void getDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", doctor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(doctor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.cin").value(DEFAULT_CIN.intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.speciality").value(DEFAULT_SPECIALITY.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.intValue()));
    }

    @Test
    @Transactional
    public void getAllDoctorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where name equals to DEFAULT_NAME
        defaultDoctorShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the doctorList where name equals to UPDATED_NAME
        defaultDoctorShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDoctorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDoctorShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the doctorList where name equals to UPDATED_NAME
        defaultDoctorShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDoctorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where name is not null
        defaultDoctorShouldBeFound("name.specified=true");

        // Get all the doctorList where name is null
        defaultDoctorShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsByCinIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where cin equals to DEFAULT_CIN
        defaultDoctorShouldBeFound("cin.equals=" + DEFAULT_CIN);

        // Get all the doctorList where cin equals to UPDATED_CIN
        defaultDoctorShouldNotBeFound("cin.equals=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllDoctorsByCinIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where cin in DEFAULT_CIN or UPDATED_CIN
        defaultDoctorShouldBeFound("cin.in=" + DEFAULT_CIN + "," + UPDATED_CIN);

        // Get all the doctorList where cin equals to UPDATED_CIN
        defaultDoctorShouldNotBeFound("cin.in=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllDoctorsByCinIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where cin is not null
        defaultDoctorShouldBeFound("cin.specified=true");

        // Get all the doctorList where cin is null
        defaultDoctorShouldNotBeFound("cin.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where address equals to DEFAULT_ADDRESS
        defaultDoctorShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the doctorList where address equals to UPDATED_ADDRESS
        defaultDoctorShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllDoctorsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultDoctorShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the doctorList where address equals to UPDATED_ADDRESS
        defaultDoctorShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllDoctorsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where address is not null
        defaultDoctorShouldBeFound("address.specified=true");

        // Get all the doctorList where address is null
        defaultDoctorShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsBySpecialityIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where speciality equals to DEFAULT_SPECIALITY
        defaultDoctorShouldBeFound("speciality.equals=" + DEFAULT_SPECIALITY);

        // Get all the doctorList where speciality equals to UPDATED_SPECIALITY
        defaultDoctorShouldNotBeFound("speciality.equals=" + UPDATED_SPECIALITY);
    }

    @Test
    @Transactional
    public void getAllDoctorsBySpecialityIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where speciality in DEFAULT_SPECIALITY or UPDATED_SPECIALITY
        defaultDoctorShouldBeFound("speciality.in=" + DEFAULT_SPECIALITY + "," + UPDATED_SPECIALITY);

        // Get all the doctorList where speciality equals to UPDATED_SPECIALITY
        defaultDoctorShouldNotBeFound("speciality.in=" + UPDATED_SPECIALITY);
    }

    @Test
    @Transactional
    public void getAllDoctorsBySpecialityIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where speciality is not null
        defaultDoctorShouldBeFound("speciality.specified=true");

        // Get all the doctorList where speciality is null
        defaultDoctorShouldNotBeFound("speciality.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where email equals to DEFAULT_EMAIL
        defaultDoctorShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the doctorList where email equals to UPDATED_EMAIL
        defaultDoctorShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllDoctorsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultDoctorShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the doctorList where email equals to UPDATED_EMAIL
        defaultDoctorShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllDoctorsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where email is not null
        defaultDoctorShouldBeFound("email.specified=true");

        // Get all the doctorList where email is null
        defaultDoctorShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultDoctorShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the doctorList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultDoctorShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDoctorsByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultDoctorShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the doctorList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultDoctorShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDoctorsByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where phoneNumber is not null
        defaultDoctorShouldBeFound("phoneNumber.specified=true");

        // Get all the doctorList where phoneNumber is null
        defaultDoctorShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    public void getAllDoctorsByRequestsIsEqualToSomething() throws Exception {
        // Initialize the database
        Request requests = RequestResourceIntTest.createEntity(em);
        em.persist(requests);
        em.flush();
        doctor.addRequests(requests);
        doctorRepository.saveAndFlush(doctor);
        Long requestsId = requests.getId();

        // Get all the doctorList where requests equals to requestsId
        defaultDoctorShouldBeFound("requestsId.equals=" + requestsId);

        // Get all the doctorList where requests equals to requestsId + 1
        defaultDoctorShouldNotBeFound("requestsId.equals=" + (requestsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultDoctorShouldBeFound(String filter) throws Exception {
        restDoctorMockMvc.perform(get("/api/doctors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.intValue())));

        // Check, that the count call also returns 1
        restDoctorMockMvc.perform(get("/api/doctors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultDoctorShouldNotBeFound(String filter) throws Exception {
        restDoctorMockMvc.perform(get("/api/doctors?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDoctorMockMvc.perform(get("/api/doctors/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingDoctor() throws Exception {
        // Get the doctor
        restDoctorMockMvc.perform(get("/api/doctors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Update the doctor
        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).get();
        // Disconnect from session so that the updates on updatedDoctor are not directly saved in db
        em.detach(updatedDoctor);
        updatedDoctor
            .name(UPDATED_NAME)
            .cin(UPDATED_CIN)
            .address(UPDATED_ADDRESS)
            .speciality(UPDATED_SPECIALITY)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        DoctorDTO doctorDTO = doctorMapper.toDto(updatedDoctor);

        restDoctorMockMvc.perform(put("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isOk());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDoctor.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testDoctor.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testDoctor.getSpeciality()).isEqualTo(UPDATED_SPECIALITY);
        assertThat(testDoctor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testDoctor.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);

        // Validate the Doctor in Elasticsearch
        verify(mockDoctorSearchRepository, times(1)).save(testDoctor);
    }

    @Test
    @Transactional
    public void updateNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc.perform(put("/api/doctors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Doctor in Elasticsearch
        verify(mockDoctorSearchRepository, times(0)).save(doctor);
    }

    @Test
    @Transactional
    public void deleteDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeDelete = doctorRepository.findAll().size();

        // Delete the doctor
        restDoctorMockMvc.perform(delete("/api/doctors/{id}", doctor.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Doctor in Elasticsearch
        verify(mockDoctorSearchRepository, times(1)).deleteById(doctor.getId());
    }

    @Test
    @Transactional
    public void searchDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);
        when(mockDoctorSearchRepository.search(queryStringQuery("id:" + doctor.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(doctor), PageRequest.of(0, 1), 1));
        // Search the doctor
        restDoctorMockMvc.perform(get("/api/_search/doctors?query=id:" + doctor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Doctor.class);
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        Doctor doctor2 = new Doctor();
        doctor2.setId(doctor1.getId());
        assertThat(doctor1).isEqualTo(doctor2);
        doctor2.setId(2L);
        assertThat(doctor1).isNotEqualTo(doctor2);
        doctor1.setId(null);
        assertThat(doctor1).isNotEqualTo(doctor2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DoctorDTO.class);
        DoctorDTO doctorDTO1 = new DoctorDTO();
        doctorDTO1.setId(1L);
        DoctorDTO doctorDTO2 = new DoctorDTO();
        assertThat(doctorDTO1).isNotEqualTo(doctorDTO2);
        doctorDTO2.setId(doctorDTO1.getId());
        assertThat(doctorDTO1).isEqualTo(doctorDTO2);
        doctorDTO2.setId(2L);
        assertThat(doctorDTO1).isNotEqualTo(doctorDTO2);
        doctorDTO1.setId(null);
        assertThat(doctorDTO1).isNotEqualTo(doctorDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(doctorMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(doctorMapper.fromId(null)).isNull();
    }
}
