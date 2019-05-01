package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.domain.Appointment;
import com.mycompany.myapp.repository.AppointmentRepository;
import com.mycompany.myapp.service.AppointmentService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import com.mycompany.myapp.service.dto.AppointmentDTO;
import com.mycompany.myapp.service.dto.AppointmentCriteria;
import com.mycompany.myapp.service.AppointmentQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Appointment.
 */
@RestController
@RequestMapping("/api")
public class AppointmentResource {

    private final Logger log = LoggerFactory.getLogger(AppointmentResource.class);

    private static final String ENTITY_NAME = "appointment";

    private final AppointmentService appointmentService;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentQueryService appointmentQueryService;

    public AppointmentResource(AppointmentService appointmentService, AppointmentQueryService appointmentQueryService,  AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.appointmentQueryService = appointmentQueryService;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * POST  /appointments : Create a new appointment.
     *
     * @param appointmentDTO the appointmentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new appointmentDTO, or with status 400 (Bad Request) if the appointment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) throws URISyntaxException {
        log.debug("REST request to save Appointment : {}", appointmentDTO);
        if (appointmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new appointment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(appointmentDTO.getRequestId())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        AppointmentDTO result = appointmentService.save(appointmentDTO);
        return ResponseEntity.created(new URI("/api/appointments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /appointments : Updates an existing appointment.
     *
     * @param appointmentDTO the appointmentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated appointmentDTO,
     * or with status 400 (Bad Request) if the appointmentDTO is not valid,
     * or with status 500 (Internal Server Error) if the appointmentDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/appointments")
    public ResponseEntity<AppointmentDTO> updateAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) throws URISyntaxException {
        log.debug("REST request to update Appointment : {}", appointmentDTO);
        if (appointmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AppointmentDTO result = appointmentService.save(appointmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, appointmentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /appointments : get all the appointments.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of appointments in body
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments(AppointmentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Appointments by criteria: {}", criteria);
        Page<AppointmentDTO> page = appointmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/appointments");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /appointments/count : count all the appointments.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/appointments/count")
    public ResponseEntity<Long> countAppointments(AppointmentCriteria criteria) {
        log.debug("REST request to count Appointments by criteria: {}", criteria);
        return ResponseEntity.ok().body(appointmentQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /appointments/:id : get the "id" appointment.
     *
     * @param id the id of the appointmentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the appointmentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable Long id) {
        log.debug("REST request to get Appointment : {}", id);
        Optional<AppointmentDTO> appointmentDTO = appointmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appointmentDTO);
    }

    /**
     * DELETE  /appointments/:id : delete the "id" appointment.
     *
     * @param id the id of the appointmentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        log.debug("REST request to delete Appointment : {}", id);
        appointmentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/appointments?query=:query : search for the appointment corresponding
     * to the query.
     *
     * @param query the query of the appointment search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/appointments")
    public ResponseEntity<List<AppointmentDTO>> searchAppointments(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Appointments for query {}", query);
        Page<AppointmentDTO> page = appointmentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/appointments");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * @return a string list of the all of the appointments
     */
    @GetMapping("/appointment/list")
    @Timed
    public List<Appointment> geAppointmentsList() {
        return appointmentRepository.findAll();
    }

}
