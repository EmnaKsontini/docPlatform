package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.DoctorsPlatformApp;

import com.mycompany.myapp.domain.Appointment;
import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.repository.AppointmentRepository;
import com.mycompany.myapp.repository.search.AppointmentSearchRepository;
import com.mycompany.myapp.service.AppointmentService;
import com.mycompany.myapp.service.dto.AppointmentDTO;
import com.mycompany.myapp.service.mapper.AppointmentMapper;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;
import com.mycompany.myapp.service.dto.AppointmentCriteria;
import com.mycompany.myapp.service.AppointmentQueryService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AppointmentResource REST controller.
 *
 * @see AppointmentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorsPlatformApp.class)
public class AppointmentResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE_AND_HOUR = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_AND_HOUR = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private AppointmentService appointmentService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.AppointmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private AppointmentSearchRepository mockAppointmentSearchRepository;

    @Autowired
    private AppointmentQueryService appointmentQueryService;

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

    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AppointmentResource appointmentResource = new AppointmentResource(appointmentService, appointmentQueryService ,appointmentRepository);
        this.restAppointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentResource)
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
    public static Appointment createEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .dateAndHour(DEFAULT_DATE_AND_HOUR);
        // Add required entity
        Request request = RequestResourceIntTest.createEntity(em);
        em.persist(request);
        em.flush();
        appointment.setRequest(request);
        return appointment;
    }

    @Before
    public void initTest() {
        appointment = createEntity(em);
    }

    @Test
    @Transactional
    public void createAppointment() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);
        restAppointmentMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getDateAndHour()).isEqualTo(DEFAULT_DATE_AND_HOUR);

        // Validate the id for MapsId, the ids must be same
        assertThat(testAppointment.getId()).isEqualTo(testAppointment.getRequest().getId());

        // Validate the Appointment in Elasticsearch
        verify(mockAppointmentSearchRepository, times(1)).save(testAppointment);
    }

    @Test
    @Transactional
    public void createAppointmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();

        // Create the Appointment with an existing ID
        appointment.setId(1L);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Appointment in Elasticsearch
        verify(mockAppointmentSearchRepository, times(0)).save(appointment);
    }

    @Test
    @Transactional
    public void checkDateAndHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setDateAndHour(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc.perform(post("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppointments() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList
        restAppointmentMockMvc.perform(get("/api/appointments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAndHour").value(hasItem(sameInstant(DEFAULT_DATE_AND_HOUR))));
    }
    
    @Test
    @Transactional
    public void getAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.dateAndHour").value(sameInstant(DEFAULT_DATE_AND_HOUR)));
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateAndHourIsEqualToSomething() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where dateAndHour equals to DEFAULT_DATE_AND_HOUR
        defaultAppointmentShouldBeFound("dateAndHour.equals=" + DEFAULT_DATE_AND_HOUR);

        // Get all the appointmentList where dateAndHour equals to UPDATED_DATE_AND_HOUR
        defaultAppointmentShouldNotBeFound("dateAndHour.equals=" + UPDATED_DATE_AND_HOUR);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateAndHourIsInShouldWork() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where dateAndHour in DEFAULT_DATE_AND_HOUR or UPDATED_DATE_AND_HOUR
        defaultAppointmentShouldBeFound("dateAndHour.in=" + DEFAULT_DATE_AND_HOUR + "," + UPDATED_DATE_AND_HOUR);

        // Get all the appointmentList where dateAndHour equals to UPDATED_DATE_AND_HOUR
        defaultAppointmentShouldNotBeFound("dateAndHour.in=" + UPDATED_DATE_AND_HOUR);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateAndHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where dateAndHour is not null
        defaultAppointmentShouldBeFound("dateAndHour.specified=true");

        // Get all the appointmentList where dateAndHour is null
        defaultAppointmentShouldNotBeFound("dateAndHour.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateAndHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where dateAndHour greater than or equals to DEFAULT_DATE_AND_HOUR
        defaultAppointmentShouldBeFound("dateAndHour.greaterOrEqualThan=" + DEFAULT_DATE_AND_HOUR);

        // Get all the appointmentList where dateAndHour greater than or equals to UPDATED_DATE_AND_HOUR
        defaultAppointmentShouldNotBeFound("dateAndHour.greaterOrEqualThan=" + UPDATED_DATE_AND_HOUR);
    }

    @Test
    @Transactional
    public void getAllAppointmentsByDateAndHourIsLessThanSomething() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where dateAndHour less than or equals to DEFAULT_DATE_AND_HOUR
        defaultAppointmentShouldNotBeFound("dateAndHour.lessThan=" + DEFAULT_DATE_AND_HOUR);

        // Get all the appointmentList where dateAndHour less than or equals to UPDATED_DATE_AND_HOUR
        defaultAppointmentShouldBeFound("dateAndHour.lessThan=" + UPDATED_DATE_AND_HOUR);
    }


    @Test
    @Transactional
    public void getAllAppointmentsByRequestIsEqualToSomething() throws Exception {
        // Initialize the database
        Request request = RequestResourceIntTest.createEntity(em);
        em.persist(request);
        em.flush();
        appointment.setRequest(request);
        appointmentRepository.saveAndFlush(appointment);
        Long requestId = request.getId();

        // Get all the appointmentList where request equals to requestId
        defaultAppointmentShouldBeFound("requestId.equals=" + requestId);

        // Get all the appointmentList where request equals to requestId + 1
        defaultAppointmentShouldNotBeFound("requestId.equals=" + (requestId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultAppointmentShouldBeFound(String filter) throws Exception {
        restAppointmentMockMvc.perform(get("/api/appointments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAndHour").value(hasItem(sameInstant(DEFAULT_DATE_AND_HOUR))));

        // Check, that the count call also returns 1
        restAppointmentMockMvc.perform(get("/api/appointments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultAppointmentShouldNotBeFound(String filter) throws Exception {
        restAppointmentMockMvc.perform(get("/api/appointments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppointmentMockMvc.perform(get("/api/appointments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get("/api/appointments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).get();
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment);
        updatedAppointment
            .dateAndHour(UPDATED_DATE_AND_HOUR);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(updatedAppointment);

        restAppointmentMockMvc.perform(put("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentDTO)))
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getDateAndHour()).isEqualTo(UPDATED_DATE_AND_HOUR);

        // Validate the Appointment in Elasticsearch
        verify(mockAppointmentSearchRepository, times(1)).save(testAppointment);
    }

    @Test
    @Transactional
    public void updateNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(put("/api/appointments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Appointment in Elasticsearch
        verify(mockAppointmentSearchRepository, times(0)).save(appointment);
    }

    @Test
    @Transactional
    public void deleteAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeDelete = appointmentRepository.findAll().size();

        // Delete the appointment
        restAppointmentMockMvc.perform(delete("/api/appointments/{id}", appointment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Appointment in Elasticsearch
        verify(mockAppointmentSearchRepository, times(1)).deleteById(appointment.getId());
    }

    @Test
    @Transactional
    public void searchAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);
        when(mockAppointmentSearchRepository.search(queryStringQuery("id:" + appointment.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(appointment), PageRequest.of(0, 1), 1));
        // Search the appointment
        restAppointmentMockMvc.perform(get("/api/_search/appointments?query=id:" + appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAndHour").value(hasItem(sameInstant(DEFAULT_DATE_AND_HOUR))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Appointment.class);
        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        Appointment appointment2 = new Appointment();
        appointment2.setId(appointment1.getId());
        assertThat(appointment1).isEqualTo(appointment2);
        appointment2.setId(2L);
        assertThat(appointment1).isNotEqualTo(appointment2);
        appointment1.setId(null);
        assertThat(appointment1).isNotEqualTo(appointment2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppointmentDTO.class);
        AppointmentDTO appointmentDTO1 = new AppointmentDTO();
        appointmentDTO1.setId(1L);
        AppointmentDTO appointmentDTO2 = new AppointmentDTO();
        assertThat(appointmentDTO1).isNotEqualTo(appointmentDTO2);
        appointmentDTO2.setId(appointmentDTO1.getId());
        assertThat(appointmentDTO1).isEqualTo(appointmentDTO2);
        appointmentDTO2.setId(2L);
        assertThat(appointmentDTO1).isNotEqualTo(appointmentDTO2);
        appointmentDTO1.setId(null);
        assertThat(appointmentDTO1).isNotEqualTo(appointmentDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(appointmentMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(appointmentMapper.fromId(null)).isNull();
    }
}
