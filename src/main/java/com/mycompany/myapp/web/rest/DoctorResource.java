package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.domain.Doctor;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.PatientRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.DoctorService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import com.mycompany.myapp.service.dto.DoctorDTO;
import com.mycompany.myapp.service.dto.DoctorCriteria;
import com.mycompany.myapp.service.DoctorQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Doctor.
 */
@RestController
@RequestMapping("/api")
public class DoctorResource {

    private final Logger log = LoggerFactory.getLogger(DoctorResource.class);

    private static final String ENTITY_NAME = "doctor";

    private final DoctorService doctorService;

    private final UserRepository userRepository;

    private final PatientRepository patientRepository;


    private final DoctorQueryService doctorQueryService;

    public DoctorResource(DoctorService doctorService, DoctorQueryService doctorQueryService,UserRepository userRepository,PatientRepository patientRepository) {
        this.doctorService = doctorService;
        this.doctorQueryService = doctorQueryService;
        this.userRepository=userRepository;
        this.patientRepository=patientRepository;
    }

    /**
     * POST  /doctors : Create a new doctor.
     *
     * @param doctorDTO the doctorDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new doctorDTO, or with status 400 (Bad Request) if the doctor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/doctors")
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) throws URISyntaxException {
        log.debug("REST request to save Doctor : {}", doctorDTO);
        if (doctorDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DoctorDTO result = doctorService.save(doctorDTO);
        return ResponseEntity.created(new URI("/api/doctors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /doctors : Updates an existing doctor.
     *
     * @param doctorDTO the doctorDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated doctorDTO,
     * or with status 400 (Bad Request) if the doctorDTO is not valid,
     * or with status 500 (Internal Server Error) if the doctorDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/doctors")
    public ResponseEntity<DoctorDTO> updateDoctor(@Valid @RequestBody DoctorDTO doctorDTO) throws URISyntaxException {
        log.debug("REST request to update Doctor : {}", doctorDTO);
        if (doctorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DoctorDTO result = doctorService.save(doctorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, doctorDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /doctors : get all the doctors.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of doctors in body
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors(DoctorCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Doctors by criteria: {}", criteria);
        Page<DoctorDTO> page = doctorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/doctors");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    @PostMapping("/doctorByName")
    public ResponseEntity<Doctor> getAllDoctors(@RequestBody String name) {
        //log.debug("REST request to get Doctors by criteria: {}", criteria);
        //Page<DoctorDTO> page = doctorQueryService.findByCriteria(criteria, pageable);
        //HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/doctors");s
        System.out.println("ismoo "+ name);

        Doctor doc=null;
        List<Doctor> doctors=doctorService.findAllDoctors();
        for (int i=0; i<doctors.size() ; i++)
        {
            if(doctors.get(i).getName().equals(name))
            {
                doc=doctors.get(i);
                break;
            }
        }
        return ResponseEntity.ok().body(doc);
    }
    public String getCurrentUserLogin() {
        org.springframework.security.core.context.SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String login = null;
        if (authentication != null)
            if (authentication.getPrincipal() instanceof UserDetails)
                login = ((UserDetails) authentication.getPrincipal()).getUsername();
            else if (authentication.getPrincipal() instanceof String)
                login = (String) authentication.getPrincipal();

        return login;
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<Patient> getCurrentUser() {
        User user=userRepository.findOneByLogin(getCurrentUserLogin()).get();
        Long l= new Long(user.getId());
        Patient patient=patientRepository.findOneByCin(l).get();
        return ResponseEntity.ok().body(patient);
    }

    /**
    * GET  /doctors/count : count all the doctors.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/doctors/count")
    public ResponseEntity<Long> countDoctors(DoctorCriteria criteria) {
        log.debug("REST request to count Doctors by criteria: {}", criteria);
        return ResponseEntity.ok().body(doctorQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /doctors/:id : get the "id" doctor.
     *f
     * @param id the id of the doctorDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the doctorDTO, or with status 404 (Not Found)
     */
    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable Long id) {
        log.debug("REST request to get Doctor : {}", id);
        Optional<DoctorDTO> doctorDTO = doctorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorDTO);
    }

    /**
     * DELETE  /doctors/:id : delete the "id" doctor.
     *
     * @param id the id of the doctorDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.debug("REST request to delete Doctor : {}", id);
        doctorService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/doctors?query=:query : search for the doctor corresponding
     * to the query.
     *
     * @param query the query of the doctor search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/doctors")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Doctors for query {}", query);
        Page<DoctorDTO> page = doctorService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/doctors");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
