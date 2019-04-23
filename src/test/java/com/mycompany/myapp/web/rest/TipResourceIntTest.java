package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.DoctorsPlatformApp;

import com.mycompany.myapp.domain.Tip;
import com.mycompany.myapp.repository.TipRepository;
import com.mycompany.myapp.repository.search.TipSearchRepository;
import com.mycompany.myapp.service.TipService;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;
import com.mycompany.myapp.service.dto.TipCriteria;
import com.mycompany.myapp.service.TipQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TipResource REST controller.
 *
 * @see TipResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DoctorsPlatformApp.class)
public class TipResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private TipRepository tipRepository;

    @Autowired
    private TipService tipService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.TipSearchRepositoryMockConfiguration
     */
    @Autowired
    private TipSearchRepository mockTipSearchRepository;

    @Autowired
    private TipQueryService tipQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTipMockMvc;

    private Tip tip;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TipResource tipResource = new TipResource(tipService, tipQueryService);
        this.restTipMockMvc = MockMvcBuilders.standaloneSetup(tipResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tip createEntity(EntityManager em) {
        Tip tip = new Tip()
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return tip;
    }

    @Before
    public void initTest() {
        tip = createEntity(em);
    }

    @Test
    @Transactional
    public void createTip() throws Exception {
        int databaseSizeBeforeCreate = tipRepository.findAll().size();

        // Create the Tip
        restTipMockMvc.perform(post("/api/tips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tip)))
            .andExpect(status().isCreated());

        // Validate the Tip in the database
        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeCreate + 1);
        Tip testTip = tipList.get(tipList.size() - 1);
        assertThat(testTip.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTip.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testTip.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testTip.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Tip in Elasticsearch
        verify(mockTipSearchRepository, times(1)).save(testTip);
    }

    @Test
    @Transactional
    public void createTipWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tipRepository.findAll().size();

        // Create the Tip with an existing ID
        tip.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipMockMvc.perform(post("/api/tips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tip)))
            .andExpect(status().isBadRequest());

        // Validate the Tip in the database
        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeCreate);

        // Validate the Tip in Elasticsearch
        verify(mockTipSearchRepository, times(0)).save(tip);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = tipRepository.findAll().size();
        // set the field null
        tip.setTitle(null);

        // Create the Tip, which fails.

        restTipMockMvc.perform(post("/api/tips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tip)))
            .andExpect(status().isBadRequest());

        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTips() throws Exception {
        // Initialize the database
        tipRepository.saveAndFlush(tip);

        // Get all the tipList
        restTipMockMvc.perform(get("/api/tips?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tip.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }
    
    @Test
    @Transactional
    public void getTip() throws Exception {
        // Initialize the database
        tipRepository.saveAndFlush(tip);

        // Get the tip
        restTipMockMvc.perform(get("/api/tips/{id}", tip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tip.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getAllTipsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        tipRepository.saveAndFlush(tip);

        // Get all the tipList where title equals to DEFAULT_TITLE
        defaultTipShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the tipList where title equals to UPDATED_TITLE
        defaultTipShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTipsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        tipRepository.saveAndFlush(tip);

        // Get all the tipList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultTipShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the tipList where title equals to UPDATED_TITLE
        defaultTipShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTipsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        tipRepository.saveAndFlush(tip);

        // Get all the tipList where title is not null
        defaultTipShouldBeFound("title.specified=true");

        // Get all the tipList where title is null
        defaultTipShouldNotBeFound("title.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTipShouldBeFound(String filter) throws Exception {
        restTipMockMvc.perform(get("/api/tips?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tip.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restTipMockMvc.perform(get("/api/tips/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTipShouldNotBeFound(String filter) throws Exception {
        restTipMockMvc.perform(get("/api/tips?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTipMockMvc.perform(get("/api/tips/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingTip() throws Exception {
        // Get the tip
        restTipMockMvc.perform(get("/api/tips/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTip() throws Exception {
        // Initialize the database
        tipService.save(tip);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTipSearchRepository);

        int databaseSizeBeforeUpdate = tipRepository.findAll().size();

        // Update the tip
        Tip updatedTip = tipRepository.findById(tip.getId()).get();
        // Disconnect from session so that the updates on updatedTip are not directly saved in db
        em.detach(updatedTip);
        updatedTip
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restTipMockMvc.perform(put("/api/tips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTip)))
            .andExpect(status().isOk());

        // Validate the Tip in the database
        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeUpdate);
        Tip testTip = tipList.get(tipList.size() - 1);
        assertThat(testTip.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTip.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTip.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testTip.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Tip in Elasticsearch
        verify(mockTipSearchRepository, times(1)).save(testTip);
    }

    @Test
    @Transactional
    public void updateNonExistingTip() throws Exception {
        int databaseSizeBeforeUpdate = tipRepository.findAll().size();

        // Create the Tip

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipMockMvc.perform(put("/api/tips")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tip)))
            .andExpect(status().isBadRequest());

        // Validate the Tip in the database
        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Tip in Elasticsearch
        verify(mockTipSearchRepository, times(0)).save(tip);
    }

    @Test
    @Transactional
    public void deleteTip() throws Exception {
        // Initialize the database
        tipService.save(tip);

        int databaseSizeBeforeDelete = tipRepository.findAll().size();

        // Delete the tip
        restTipMockMvc.perform(delete("/api/tips/{id}", tip.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tip> tipList = tipRepository.findAll();
        assertThat(tipList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Tip in Elasticsearch
        verify(mockTipSearchRepository, times(1)).deleteById(tip.getId());
    }

    @Test
    @Transactional
    public void searchTip() throws Exception {
        // Initialize the database
        tipService.save(tip);
        when(mockTipSearchRepository.search(queryStringQuery("id:" + tip.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tip), PageRequest.of(0, 1), 1));
        // Search the tip
        restTipMockMvc.perform(get("/api/_search/tips?query=id:" + tip.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tip.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tip.class);
        Tip tip1 = new Tip();
        tip1.setId(1L);
        Tip tip2 = new Tip();
        tip2.setId(tip1.getId());
        assertThat(tip1).isEqualTo(tip2);
        tip2.setId(2L);
        assertThat(tip1).isNotEqualTo(tip2);
        tip1.setId(null);
        assertThat(tip1).isNotEqualTo(tip2);
    }
}
