package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.repository.RequestRepository;
import com.mycompany.myapp.repository.search.RequestSearchRepository;
import com.mycompany.myapp.service.dto.RequestDTO;
import com.mycompany.myapp.service.mapper.RequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Request.
 */
@Service
@Transactional
public class RequestService {

    private final Logger log = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final RequestSearchRepository requestSearchRepository;

    public RequestService(RequestRepository requestRepository, RequestMapper requestMapper, RequestSearchRepository requestSearchRepository) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.requestSearchRepository = requestSearchRepository;
    }

    /**
     * Save a request.
     *
     * @param requestDTO the entity to save
     * @return the persisted entity
     */
    public RequestDTO save(RequestDTO requestDTO) {
        log.debug("Request to save Request : {}", requestDTO);
        Request request = requestMapper.toEntity(requestDTO);
        request = requestRepository.save(request);
        RequestDTO result = requestMapper.toDto(request);
        requestSearchRepository.save(request);
        return result;
    }

    /**
     * Get all the requests.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Requests");
        return requestRepository.findAll(pageable)
            .map(requestMapper::toDto);
    }



    /**
     *  get all the requests where Appointment is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<RequestDTO> findAllWhereAppointmentIsNull() {
        log.debug("Request to get all requests where Appointment is null");
        return StreamSupport
            .stream(requestRepository.findAll().spliterator(), false)
            .filter(request -> request.getAppointment() == null)
            .map(requestMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one request by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<RequestDTO> findOne(Long id) {
        log.debug("Request to get Request : {}", id);
        return requestRepository.findById(id)
            .map(requestMapper::toDto);
    }

    /**
     * Delete the request by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Request : {}", id);
        requestRepository.deleteById(id);
        requestSearchRepository.deleteById(id);
    }

    /**
     * Search for the request corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RequestDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Requests for query {}", query);
        return requestSearchRepository.search(queryStringQuery(query), pageable)
            .map(requestMapper::toDto);
    }

    public List<Request> findAll() {
        return requestRepository.findAll();
    }
}
