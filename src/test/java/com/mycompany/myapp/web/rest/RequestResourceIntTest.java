package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.DoctorsPlatformApp;

import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.domain.Doctor;
import com.mycompany.myapp.domain.Appointment;
import com.mycompany.myapp.repository.RequestRepository;
import com.mycompany.myapp.repository.search.RequestSearchRepository;
import com.mycompany.myapp.service.RequestService;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.myapp.service.mapper.RequestMapper;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;
import com.mycompany.myapp.service.dto.RequestCriteria;
import com.mycompany.myapp.service.RequestQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Test class for the RequestResource REST controller.
 *
 * @see RequestResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorsPlatformApp.class)
public class RequestResourceIntTest {

    private static final LocalDate DEFAULT_DATE_1 = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_1 = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_2 = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_2 = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_3 = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_3 = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_CONFIRMATION = false;
    private static final Boolean UPDATED_CONFIRMATION = true;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private RequestService requestService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.RequestSearchRepositoryMockConfiguration
     */
    @Autowired
    private RequestSearchRepository mockRequestSearchRepository;

    @Autowired
    private RequestQueryService requestQueryService;

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

    private MockMvc restRequestMockMvc;

    private Request request;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RequestResource requestResource = new RequestResource(requestService, requestQueryService);
        this.restRequestMockMvc = MockMvcBuilders.standaloneSetup(requestResource)
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
    public static Request createEntity(EntityManager em) {
        Request request = new Request()
            .date1(DEFAULT_DATE_1)
            .date2(DEFAULT_DATE_2)
            .date3(DEFAULT_DATE_3)
            .confirmation(DEFAULT_CONFIRMATION);
        // Add required entity
        Patient patient = PatientResourceIntTest.createEntity(em);
        em.persist(patient);
        em.flush();
        request.setPatient(patient);
        // Add required entity
        Doctor doctor = DoctorResourceIntTest.createEntity(em);
        em.persist(doctor);
        em.flush();
        request.setDoctor(doctor);
        return request;
    }

    @Before
    public void initTest() {
        request = createEntity(em);
    }

    @Test
    @Transactional
    public void createRequest() throws Exception {
        int databaseSizeBeforeCreate = requestRepository.findAll().size();

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);
        restRequestMockMvc.perform(post("/api/requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isCreated());

        // Validate the Request in the database
        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeCreate + 1);
        Request testRequest = requestList.get(requestList.size() - 1);
        assertThat(testRequest.getDate1()).isEqualTo(DEFAULT_DATE_1);
        assertThat(testRequest.getDate2()).isEqualTo(DEFAULT_DATE_2);
        assertThat(testRequest.getDate3()).isEqualTo(DEFAULT_DATE_3);
        assertThat(testRequest.isConfirmation()).isEqualTo(DEFAULT_CONFIRMATION);

        // Validate the Request in Elasticsearch
        verify(mockRequestSearchRepository, times(1)).save(testRequest);
    }

    @Test
    @Transactional
    public void createRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = requestRepository.findAll().size();

        // Create the Request with an existing ID
        request.setId(1L);
        RequestDTO requestDTO = requestMapper.toDto(request);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRequestMockMvc.perform(post("/api/requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeCreate);

        // Validate the Request in Elasticsearch
        verify(mockRequestSearchRepository, times(0)).save(request);
    }

    @Test
    @Transactional
    public void checkDate1IsRequired() throws Exception {
        int databaseSizeBeforeTest = requestRepository.findAll().size();
        // set the field null
        request.setDate1(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        restRequestMockMvc.perform(post("/api/requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRequests() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList
        restRequestMockMvc.perform(get("/api/requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
            .andExpect(jsonPath("$.[*].date1").value(hasItem(DEFAULT_DATE_1.toString())))
            .andExpect(jsonPath("$.[*].date2").value(hasItem(DEFAULT_DATE_2.toString())))
            .andExpect(jsonPath("$.[*].date3").value(hasItem(DEFAULT_DATE_3.toString())))
            .andExpect(jsonPath("$.[*].confirmation").value(hasItem(DEFAULT_CONFIRMATION.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get the request
        restRequestMockMvc.perform(get("/api/requests/{id}", request.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(request.getId().intValue()))
            .andExpect(jsonPath("$.date1").value(DEFAULT_DATE_1.toString()))
            .andExpect(jsonPath("$.date2").value(DEFAULT_DATE_2.toString()))
            .andExpect(jsonPath("$.date3").value(DEFAULT_DATE_3.toString()))
            .andExpect(jsonPath("$.confirmation").value(DEFAULT_CONFIRMATION.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllRequestsByDate1IsEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date1 equals to DEFAULT_DATE_1
        defaultRequestShouldBeFound("date1.equals=" + DEFAULT_DATE_1);

        // Get all the requestList where date1 equals to UPDATED_DATE_1
        defaultRequestShouldNotBeFound("date1.equals=" + UPDATED_DATE_1);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate1IsInShouldWork() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date1 in DEFAULT_DATE_1 or UPDATED_DATE_1
        defaultRequestShouldBeFound("date1.in=" + DEFAULT_DATE_1 + "," + UPDATED_DATE_1);

        // Get all the requestList where date1 equals to UPDATED_DATE_1
        defaultRequestShouldNotBeFound("date1.in=" + UPDATED_DATE_1);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate1IsNullOrNotNull() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date1 is not null
        defaultRequestShouldBeFound("date1.specified=true");

        // Get all the requestList where date1 is null
        defaultRequestShouldNotBeFound("date1.specified=false");
    }

    @Test
    @Transactional
    public void getAllRequestsByDate1IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date1 greater than or equals to DEFAULT_DATE_1
        defaultRequestShouldBeFound("date1.greaterOrEqualThan=" + DEFAULT_DATE_1);

        // Get all the requestList where date1 greater than or equals to UPDATED_DATE_1
        defaultRequestShouldNotBeFound("date1.greaterOrEqualThan=" + UPDATED_DATE_1);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate1IsLessThanSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date1 less than or equals to DEFAULT_DATE_1
        defaultRequestShouldNotBeFound("date1.lessThan=" + DEFAULT_DATE_1);

        // Get all the requestList where date1 less than or equals to UPDATED_DATE_1
        defaultRequestShouldBeFound("date1.lessThan=" + UPDATED_DATE_1);
    }


    @Test
    @Transactional
    public void getAllRequestsByDate2IsEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date2 equals to DEFAULT_DATE_2
        defaultRequestShouldBeFound("date2.equals=" + DEFAULT_DATE_2);

        // Get all the requestList where date2 equals to UPDATED_DATE_2
        defaultRequestShouldNotBeFound("date2.equals=" + UPDATED_DATE_2);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate2IsInShouldWork() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date2 in DEFAULT_DATE_2 or UPDATED_DATE_2
        defaultRequestShouldBeFound("date2.in=" + DEFAULT_DATE_2 + "," + UPDATED_DATE_2);

        // Get all the requestList where date2 equals to UPDATED_DATE_2
        defaultRequestShouldNotBeFound("date2.in=" + UPDATED_DATE_2);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate2IsNullOrNotNull() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date2 is not null
        defaultRequestShouldBeFound("date2.specified=true");

        // Get all the requestList where date2 is null
        defaultRequestShouldNotBeFound("date2.specified=false");
    }

    @Test
    @Transactional
    public void getAllRequestsByDate2IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date2 greater than or equals to DEFAULT_DATE_2
        defaultRequestShouldBeFound("date2.greaterOrEqualThan=" + DEFAULT_DATE_2);

        // Get all the requestList where date2 greater than or equals to UPDATED_DATE_2
        defaultRequestShouldNotBeFound("date2.greaterOrEqualThan=" + UPDATED_DATE_2);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate2IsLessThanSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date2 less than or equals to DEFAULT_DATE_2
        defaultRequestShouldNotBeFound("date2.lessThan=" + DEFAULT_DATE_2);

        // Get all the requestList where date2 less than or equals to UPDATED_DATE_2
        defaultRequestShouldBeFound("date2.lessThan=" + UPDATED_DATE_2);
    }


    @Test
    @Transactional
    public void getAllRequestsByDate3IsEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date3 equals to DEFAULT_DATE_3
        defaultRequestShouldBeFound("date3.equals=" + DEFAULT_DATE_3);

        // Get all the requestList where date3 equals to UPDATED_DATE_3
        defaultRequestShouldNotBeFound("date3.equals=" + UPDATED_DATE_3);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate3IsInShouldWork() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date3 in DEFAULT_DATE_3 or UPDATED_DATE_3
        defaultRequestShouldBeFound("date3.in=" + DEFAULT_DATE_3 + "," + UPDATED_DATE_3);

        // Get all the requestList where date3 equals to UPDATED_DATE_3
        defaultRequestShouldNotBeFound("date3.in=" + UPDATED_DATE_3);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate3IsNullOrNotNull() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date3 is not null
        defaultRequestShouldBeFound("date3.specified=true");

        // Get all the requestList where date3 is null
        defaultRequestShouldNotBeFound("date3.specified=false");
    }

    @Test
    @Transactional
    public void getAllRequestsByDate3IsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date3 greater than or equals to DEFAULT_DATE_3
        defaultRequestShouldBeFound("date3.greaterOrEqualThan=" + DEFAULT_DATE_3);

        // Get all the requestList where date3 greater than or equals to UPDATED_DATE_3
        defaultRequestShouldNotBeFound("date3.greaterOrEqualThan=" + UPDATED_DATE_3);
    }

    @Test
    @Transactional
    public void getAllRequestsByDate3IsLessThanSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where date3 less than or equals to DEFAULT_DATE_3
        defaultRequestShouldNotBeFound("date3.lessThan=" + DEFAULT_DATE_3);

        // Get all the requestList where date3 less than or equals to UPDATED_DATE_3
        defaultRequestShouldBeFound("date3.lessThan=" + UPDATED_DATE_3);
    }


    @Test
    @Transactional
    public void getAllRequestsByConfirmationIsEqualToSomething() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where confirmation equals to DEFAULT_CONFIRMATION
        defaultRequestShouldBeFound("confirmation.equals=" + DEFAULT_CONFIRMATION);

        // Get all the requestList where confirmation equals to UPDATED_CONFIRMATION
        defaultRequestShouldNotBeFound("confirmation.equals=" + UPDATED_CONFIRMATION);
    }

    @Test
    @Transactional
    public void getAllRequestsByConfirmationIsInShouldWork() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where confirmation in DEFAULT_CONFIRMATION or UPDATED_CONFIRMATION
        defaultRequestShouldBeFound("confirmation.in=" + DEFAULT_CONFIRMATION + "," + UPDATED_CONFIRMATION);

        // Get all the requestList where confirmation equals to UPDATED_CONFIRMATION
        defaultRequestShouldNotBeFound("confirmation.in=" + UPDATED_CONFIRMATION);
    }

    @Test
    @Transactional
    public void getAllRequestsByConfirmationIsNullOrNotNull() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        // Get all the requestList where confirmation is not null
        defaultRequestShouldBeFound("confirmation.specified=true");

        // Get all the requestList where confirmation is null
        defaultRequestShouldNotBeFound("confirmation.specified=false");
    }

    @Test
    @Transactional
    public void getAllRequestsByPatientIsEqualToSomething() throws Exception {
        // Initialize the database
        Patient patient = PatientResourceIntTest.createEntity(em);
        em.persist(patient);
        em.flush();
        request.setPatient(patient);
        requestRepository.saveAndFlush(request);
        Long patientId = patient.getId();

        // Get all the requestList where patient equals to patientId
        defaultRequestShouldBeFound("patientId.equals=" + patientId);

        // Get all the requestList where patient equals to patientId + 1
        defaultRequestShouldNotBeFound("patientId.equals=" + (patientId + 1));
    }


    @Test
    @Transactional
    public void getAllRequestsByDoctorIsEqualToSomething() throws Exception {
        // Initialize the database
        Doctor doctor = DoctorResourceIntTest.createEntity(em);
        em.persist(doctor);
        em.flush();
        request.setDoctor(doctor);
        requestRepository.saveAndFlush(request);
        Long doctorId = doctor.getId();

        // Get all the requestList where doctor equals to doctorId
        defaultRequestShouldBeFound("doctorId.equals=" + doctorId);

        // Get all the requestList where doctor equals to doctorId + 1
        defaultRequestShouldNotBeFound("doctorId.equals=" + (doctorId + 1));
    }


    @Test
    @Transactional
    public void getAllRequestsByAppointmentIsEqualToSomething() throws Exception {
        // Initialize the database
        Appointment appointment = AppointmentResourceIntTest.createEntity(em);
        em.persist(appointment);
        em.flush();
        request.setAppointment(appointment);
        appointment.setRequest(request);
        requestRepository.saveAndFlush(request);
        Long appointmentId = appointment.getId();

        // Get all the requestList where appointment equals to appointmentId
        defaultRequestShouldBeFound("appointmentId.equals=" + appointmentId);

        // Get all the requestList where appointment equals to appointmentId + 1
        defaultRequestShouldNotBeFound("appointmentId.equals=" + (appointmentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultRequestShouldBeFound(String filter) throws Exception {
        restRequestMockMvc.perform(get("/api/requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
            .andExpect(jsonPath("$.[*].date1").value(hasItem(DEFAULT_DATE_1.toString())))
            .andExpect(jsonPath("$.[*].date2").value(hasItem(DEFAULT_DATE_2.toString())))
            .andExpect(jsonPath("$.[*].date3").value(hasItem(DEFAULT_DATE_3.toString())))
            .andExpect(jsonPath("$.[*].confirmation").value(hasItem(DEFAULT_CONFIRMATION.booleanValue())));

        // Check, that the count call also returns 1
        restRequestMockMvc.perform(get("/api/requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultRequestShouldNotBeFound(String filter) throws Exception {
        restRequestMockMvc.perform(get("/api/requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRequestMockMvc.perform(get("/api/requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRequest() throws Exception {
        // Get the request
        restRequestMockMvc.perform(get("/api/requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        int databaseSizeBeforeUpdate = requestRepository.findAll().size();

        // Update the request
        Request updatedRequest = requestRepository.findById(request.getId()).get();
        // Disconnect from session so that the updates on updatedRequest are not directly saved in db
        em.detach(updatedRequest);
        updatedRequest
            .date1(UPDATED_DATE_1)
            .date2(UPDATED_DATE_2)
            .date3(UPDATED_DATE_3)
            .confirmation(UPDATED_CONFIRMATION);
        RequestDTO requestDTO = requestMapper.toDto(updatedRequest);

        restRequestMockMvc.perform(put("/api/requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isOk());

        // Validate the Request in the database
        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeUpdate);
        Request testRequest = requestList.get(requestList.size() - 1);
        assertThat(testRequest.getDate1()).isEqualTo(UPDATED_DATE_1);
        assertThat(testRequest.getDate2()).isEqualTo(UPDATED_DATE_2);
        assertThat(testRequest.getDate3()).isEqualTo(UPDATED_DATE_3);
        assertThat(testRequest.isConfirmation()).isEqualTo(UPDATED_CONFIRMATION);

        // Validate the Request in Elasticsearch
        verify(mockRequestSearchRepository, times(1)).save(testRequest);
    }

    @Test
    @Transactional
    public void updateNonExistingRequest() throws Exception {
        int databaseSizeBeforeUpdate = requestRepository.findAll().size();

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestMockMvc.perform(put("/api/requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Request in Elasticsearch
        verify(mockRequestSearchRepository, times(0)).save(request);
    }

    @Test
    @Transactional
    public void deleteRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);

        int databaseSizeBeforeDelete = requestRepository.findAll().size();

        // Delete the request
        restRequestMockMvc.perform(delete("/api/requests/{id}", request.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Request> requestList = requestRepository.findAll();
        assertThat(requestList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Request in Elasticsearch
        verify(mockRequestSearchRepository, times(1)).deleteById(request.getId());
    }

    @Test
    @Transactional
    public void searchRequest() throws Exception {
        // Initialize the database
        requestRepository.saveAndFlush(request);
        when(mockRequestSearchRepository.search(queryStringQuery("id:" + request.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(request), PageRequest.of(0, 1), 1));
        // Search the request
        restRequestMockMvc.perform(get("/api/_search/requests?query=id:" + request.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
            .andExpect(jsonPath("$.[*].date1").value(hasItem(DEFAULT_DATE_1.toString())))
            .andExpect(jsonPath("$.[*].date2").value(hasItem(DEFAULT_DATE_2.toString())))
            .andExpect(jsonPath("$.[*].date3").value(hasItem(DEFAULT_DATE_3.toString())))
            .andExpect(jsonPath("$.[*].confirmation").value(hasItem(DEFAULT_CONFIRMATION.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Request.class);
        Request request1 = new Request();
        request1.setId(1L);
        Request request2 = new Request();
        request2.setId(request1.getId());
        assertThat(request1).isEqualTo(request2);
        request2.setId(2L);
        assertThat(request1).isNotEqualTo(request2);
        request1.setId(null);
        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequestDTO.class);
        RequestDTO requestDTO1 = new RequestDTO();
        requestDTO1.setId(1L);
        RequestDTO requestDTO2 = new RequestDTO();
        assertThat(requestDTO1).isNotEqualTo(requestDTO2);
        requestDTO2.setId(requestDTO1.getId());
        assertThat(requestDTO1).isEqualTo(requestDTO2);
        requestDTO2.setId(2L);
        assertThat(requestDTO1).isNotEqualTo(requestDTO2);
        requestDTO1.setId(null);
        assertThat(requestDTO1).isNotEqualTo(requestDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(requestMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(requestMapper.fromId(null)).isNull();
    }
}
