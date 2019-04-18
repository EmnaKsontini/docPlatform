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

import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.repository.RequestRepository;
import com.mycompany.myapp.repository.search.RequestSearchRepository;
import com.mycompany.myapp.service.dto.RequestCriteria;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.myapp.service.mapper.RequestMapper;

/**
 * Service for executing complex queries for Request entities in the database.
 * The main input is a {@link RequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RequestDTO} or a {@link Page} of {@link RequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RequestQueryService extends QueryService<Request> {

    private final Logger log = LoggerFactory.getLogger(RequestQueryService.class);

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final RequestSearchRepository requestSearchRepository;

    public RequestQueryService(RequestRepository requestRepository, RequestMapper requestMapper, RequestSearchRepository requestSearchRepository) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.requestSearchRepository = requestSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RequestDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RequestDTO> findByCriteria(RequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Request> specification = createSpecification(criteria);
        return requestMapper.toDto(requestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RequestDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RequestDTO> findByCriteria(RequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Request> specification = createSpecification(criteria);
        return requestRepository.findAll(specification, page)
            .map(requestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Request> specification = createSpecification(criteria);
        return requestRepository.count(specification);
    }

    /**
     * Function to convert RequestCriteria to a {@link Specification}
     */
    private Specification<Request> createSpecification(RequestCriteria criteria) {
        Specification<Request> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Request_.id));
            }
            if (criteria.getDate1() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate1(), Request_.date1));
            }
            if (criteria.getDate2() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate2(), Request_.date2));
            }
            if (criteria.getDate3() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate3(), Request_.date3));
            }
            if (criteria.getConfirmation() != null) {
                specification = specification.and(buildSpecification(criteria.getConfirmation(), Request_.confirmation));
            }
            if (criteria.getPatientId() != null) {
                specification = specification.and(buildSpecification(criteria.getPatientId(),
                    root -> root.join(Request_.patient, JoinType.LEFT).get(Patient_.id)));
            }
            if (criteria.getDoctorId() != null) {
                specification = specification.and(buildSpecification(criteria.getDoctorId(),
                    root -> root.join(Request_.doctor, JoinType.LEFT).get(Doctor_.id)));
            }
            if (criteria.getAppointmentId() != null) {
                specification = specification.and(buildSpecification(criteria.getAppointmentId(),
                    root -> root.join(Request_.appointment, JoinType.LEFT).get(Appointment_.id)));
            }
        }
        return specification;
    }
}
