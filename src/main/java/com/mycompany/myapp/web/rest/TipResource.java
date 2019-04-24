package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.domain.Tip;
import com.mycompany.myapp.service.TipService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import com.mycompany.myapp.service.dto.TipCriteria;
import com.mycompany.myapp.service.TipQueryService;
import io.github.jhipster.web.util.ResponseUtil;
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
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Tip.
 */
@RestController
@RequestMapping("/api")
public class TipResource {

    private final Logger log = LoggerFactory.getLogger(TipResource.class);

    private static final String ENTITY_NAME = "tip";

    private final TipService tipService;

    private final TipQueryService tipQueryService;

    public TipResource(TipService tipService, TipQueryService tipQueryService) {
        this.tipService = tipService;
        this.tipQueryService = tipQueryService;
    }

    /**
     * POST  /tips : Create a new tip.
     *
     * @param tip the tip to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tip, or with status 400 (Bad Request) if the tip has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tips")
    public ResponseEntity<Tip> createTip(@Valid @RequestBody Tip tip) throws URISyntaxException {
        log.debug("REST request to save Tip : {}", tip);
        if (tip.getId() != null) {
            throw new BadRequestAlertException("A new tip cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tip result = tipService.save(tip);
        return ResponseEntity.created(new URI("/api/tips/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tips : Updates an existing tip.
     *
     * @param tip the tip to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tip,
     * or with status 400 (Bad Request) if the tip is not valid,
     * or with status 500 (Internal Server Error) if the tip couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tips")
    public ResponseEntity<Tip> updateTip(@Valid @RequestBody Tip tip) throws URISyntaxException {
        log.debug("REST request to update Tip : {}", tip);
        if (tip.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Tip result = tipService.save(tip);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tip.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tips : get all the tips.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tips in body
     */
    @GetMapping("/tips")
    public ResponseEntity<List<Tip>> getAllTips(TipCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tips by criteria: {}", criteria);
        Page<Tip> page = tipQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tips");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /tipsAll : get all the tips.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tips in body
     */
    @GetMapping("/tipsAll")
    public List<Tip> getAllTipsSimple(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get Tips by criteria: {}");
        List<Tip> Tips = tipService.findAll();

        return Tips ;
    }

    /**
    * GET  /tips/count : count all the tips.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/tips/count")
    public ResponseEntity<Long> countTips(TipCriteria criteria) {
        log.debug("REST request to count Tips by criteria: {}", criteria);
        return ResponseEntity.ok().body(tipQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /tips/:id : get the "id" tip.
     *
     * @param id the id of the tip to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tip, or with status 404 (Not Found)
     */
    @GetMapping("/tips/{id}")
    public ResponseEntity<Tip> getTip(@PathVariable Long id) {
        log.debug("REST request to get Tip : {}", id);
        Optional<Tip> tip = tipService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tip);
    }

    /**
     * DELETE  /tips/:id : delete the "id" tip.
     *
     * @param id the id of the tip to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tips/{id}")
    public ResponseEntity<Void> deleteTip(@PathVariable Long id) {
        log.debug("REST request to delete Tip : {}", id);
        tipService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tips?query=:query : search for the tip corresponding
     * to the query.
     *
     * @param query the query of the tip search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/tips")
    public ResponseEntity<List<Tip>> searchTips(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Tips for query {}", query);
        Page<Tip> page = tipService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tips");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
