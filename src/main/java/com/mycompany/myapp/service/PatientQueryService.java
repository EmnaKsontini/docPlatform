package com.mycompany.myapp.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.repository.PatientRepository;
import com.mycompany.myapp.repository.search.PatientSearchRepository;
import com.mycompany.myapp.service.dto.PatientCriteria;
import com.mycompany.myapp.service.dto.PatientDTO;
import com.mycompany.myapp.service.mapper.PatientMapper;

/**
 * Service for executing complex queries for Patient entities in the database.
 * The main input is a {@link PatientCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PatientDTO} or a {@link Page} of {@link PatientDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PatientQueryService extends QueryService<Patient> {

    private final Logger log = LoggerFactory.getLogger(PatientQueryService.class);

    private final PatientRepository patientRepository;

    private final PatientMapper patientMapper;

    private final PatientSearchRepository patientSearchRepository;

    public PatientQueryService(PatientRepository patientRepository, PatientMapper patientMapper, PatientSearchRepository patientSearchRepository) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.patientSearchRepository = patientSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PatientDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> findByCriteria(PatientCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Patient> specification = createSpecification(criteria);
        return patientMapper.toDto(patientRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PatientDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PatientDTO> findByCriteria(PatientCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Patient> specification = createSpecification(criteria);
        return patientRepository.findAll(specification, page)
            .map(patientMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PatientCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Patient> specification = createSpecification(criteria);
        return patientRepository.count(specification);
    }

    /**
     * Function to convert PatientCriteria to a {@link Specification}
     */
    private Specification<Patient> createSpecification(PatientCriteria criteria) {
        Specification<Patient> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Patient_.id));
            }
            if (criteria.getCin() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCin(), Patient_.cin));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Patient_.name));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Patient_.email));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPhoneNumber(), Patient_.phoneNumber));
            }
            if (criteria.getRequestsId() != null) {
                specification = specification.and(buildSpecification(criteria.getRequestsId(),
                    root -> root.join(Patient_.requests, JoinType.LEFT).get(Request_.id)));
            }
            if (criteria.getDoctorId() != null) {
                specification = specification.and(buildSpecification(criteria.getDoctorId(),
                    root -> root.join(Patient_.doctors, JoinType.LEFT).get(Doctor_.id)));
            }
        }
        return specification;
    }
}
