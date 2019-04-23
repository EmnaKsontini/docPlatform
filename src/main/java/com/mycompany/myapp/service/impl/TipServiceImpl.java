package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.TipService;
import com.mycompany.myapp.domain.Tip;
import com.mycompany.myapp.repository.TipRepository;
import com.mycompany.myapp.repository.search.TipSearchRepository;
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
 * Service Implementation for managing Tip.
 */
@Service
@Transactional
public class TipServiceImpl implements TipService {

    private final Logger log = LoggerFactory.getLogger(TipServiceImpl.class);

    private final TipRepository tipRepository;

    private final TipSearchRepository tipSearchRepository;

    public TipServiceImpl(TipRepository tipRepository, TipSearchRepository tipSearchRepository) {
        this.tipRepository = tipRepository;
        this.tipSearchRepository = tipSearchRepository;
    }

    @Override
    public List<Tip> findAll() {
        log.debug("Request to load all tips ");


        return tipRepository.findAll();
    }

    /**
     * Save a tip.
     *
     * @param tip the entity to save
     * @return the persisted entity
     */
    @Override
    public Tip save(Tip tip) {
        log.debug("Request to save Tip : {}", tip);
        Tip result = tipRepository.save(tip);
        tipSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the tips.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Tip> findAll(Pageable pageable) {
        log.debug("Request to get all Tips");
        return tipRepository.findAll(pageable);
    }


    /**
     * Get one tip by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Tip> findOne(Long id) {
        log.debug("Request to get Tip : {}", id);
        return tipRepository.findById(id);
    }

    /**
     * Delete the tip by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tip : {}", id);
        tipRepository.deleteById(id);
        tipSearchRepository.deleteById(id);
    }


    /**
     * Search for the tip corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Tip> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tips for query {}", query);
        return tipSearchRepository.search(queryStringQuery(query), pageable);    }


}
