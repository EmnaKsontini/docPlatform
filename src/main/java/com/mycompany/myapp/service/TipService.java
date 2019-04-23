package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Tip;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Tip.
 */
public interface TipService {

    /**
     * Get all the Tips.
     *
     * @return the list of entities
     */
    List<Tip> findAll();


    /**
     * Save a tip.
     *
     * @param tip the entity to save
     * @return the persisted entity
     */
    Tip save(Tip tip);

    /**
     * Get all the tips.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Tip> findAll(Pageable pageable);


    /**
     * Get the "id" tip.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Tip> findOne(Long id);

    /**
     * Delete the "id" tip.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tip corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Tip> search(String query, Pageable pageable);


}
