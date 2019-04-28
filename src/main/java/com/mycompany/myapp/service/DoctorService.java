package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Doctor;
import com.mycompany.myapp.repository.DoctorRepository;
import com.mycompany.myapp.repository.search.DoctorSearchRepository;
import com.mycompany.myapp.service.dto.DoctorDTO;
import com.mycompany.myapp.service.mapper.DoctorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Doctor.
 */
@Service
@Transactional
public class DoctorService {

    private final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    private final DoctorSearchRepository doctorSearchRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper, DoctorSearchRepository doctorSearchRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.doctorSearchRepository = doctorSearchRepository;
    }

    /**
     * Save a doctor.
     *
     * @param doctorDTO the entity to save
     * @return the persisted entity
     */
    public DoctorDTO save(DoctorDTO doctorDTO) {
        log.debug("Request to save Doctor : {}", doctorDTO);
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        doctor = doctorRepository.save(doctor);
        DoctorDTO result = doctorMapper.toDto(doctor);
        doctorSearchRepository.save(doctor);
        return result;
    }

    /**
     * Get all the doctors.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Doctors");
        return doctorRepository.findAll(pageable)
            .map(doctorMapper::toDto);
    }


    /**
     * Get one doctor by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<DoctorDTO> findOne(Long id) {
        log.debug("Request to get Doctor : {}", id);
        return doctorRepository.findById(id)
            .map(doctorMapper::toDto);
    }

    /**
     * Delete the doctor by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Doctor : {}", id);
        doctorRepository.deleteById(id);
        doctorSearchRepository.deleteById(id);
    }

    /**
     * Search for the doctor corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Doctors for query {}", query);
        return doctorSearchRepository.search(queryStringQuery(query), pageable)
            .map(doctorMapper::toDto);
    }


    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }
}
