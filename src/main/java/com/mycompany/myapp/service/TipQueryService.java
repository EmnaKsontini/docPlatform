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

import com.mycompany.myapp.domain.Tip;
import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.repository.TipRepository;
import com.mycompany.myapp.repository.search.TipSearchRepository;
import com.mycompany.myapp.service.dto.TipCriteria;

/**
 * Service for executing complex queries for Tip entities in the database.
 * The main input is a {@link TipCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Tip} or a {@link Page} of {@link Tip} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TipQueryService extends QueryService<Tip> {

    private final Logger log = LoggerFactory.getLogger(TipQueryService.class);

    private final TipRepository tipRepository;

    private final TipSearchRepository tipSearchRepository;

    public TipQueryService(TipRepository tipRepository, TipSearchRepository tipSearchRepository) {
        this.tipRepository = tipRepository;
        this.tipSearchRepository = tipSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Tip} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Tip> findByCriteria(TipCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tip> specification = createSpecification(criteria);
        return tipRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Tip} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Tip> findByCriteria(TipCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tip> specification = createSpecification(criteria);
        return tipRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TipCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Tip> specification = createSpecification(criteria);
        return tipRepository.count(specification);
    }

    /**
     * Function to convert TipCriteria to a {@link Specification}
     */
    private Specification<Tip> createSpecification(TipCriteria criteria) {
        Specification<Tip> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Tip_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Tip_.title));
            }
        }
        return specification;
    }
}
