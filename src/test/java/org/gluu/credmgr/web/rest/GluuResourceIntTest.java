package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.Gluu;
import org.gluu.credmgr.repository.GluuRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the GluuResource REST controller.
 *
 * @see GluuResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class GluuResourceIntTest {

    private static final String DEFAULT_HOST = "AAAAA";
    private static final String UPDATED_HOST = "BBBBB";
    private static final String DEFAULT_CLIENT_ID = "AAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBB";
    private static final String DEFAULT_CLIENT_SECRET = "AAAAA";
    private static final String UPDATED_CLIENT_SECRET = "BBBBB";
    private static final String DEFAULT_LOGIN_REDIRECT_URI = "AAAAA";
    private static final String UPDATED_LOGIN_REDIRECT_URI = "BBBBB";
    private static final String DEFAULT_LOGOUT_REDIRECT_URI = "AAAAA";
    private static final String UPDATED_LOGOUT_REDIRECT_URI = "BBBBB";

    @Inject
    private GluuRepository gluuRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restGluuMockMvc;

    private Gluu gluu;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GluuResource gluuResource = new GluuResource();
        ReflectionTestUtils.setField(gluuResource, "gluuRepository", gluuRepository);
        this.restGluuMockMvc = MockMvcBuilders.standaloneSetup(gluuResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        gluu = new Gluu();
        gluu.setHost(DEFAULT_HOST);
        gluu.setClientId(DEFAULT_CLIENT_ID);
        gluu.setClientSecret(DEFAULT_CLIENT_SECRET);
        gluu.setLoginRedirectUri(DEFAULT_LOGIN_REDIRECT_URI);
        gluu.setLogoutRedirectUri(DEFAULT_LOGOUT_REDIRECT_URI);
    }

    @Test
    @Transactional
    public void createGluu() throws Exception {
        int databaseSizeBeforeCreate = gluuRepository.findAll().size();

        // Create the Gluu

        restGluuMockMvc.perform(post("/api/gluus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gluu)))
                .andExpect(status().isCreated());

        // Validate the Gluu in the database
        List<Gluu> gluus = gluuRepository.findAll();
        assertThat(gluus).hasSize(databaseSizeBeforeCreate + 1);
        Gluu testGluu = gluus.get(gluus.size() - 1);
        assertThat(testGluu.getHost()).isEqualTo(DEFAULT_HOST);
        assertThat(testGluu.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testGluu.getClientSecret()).isEqualTo(DEFAULT_CLIENT_SECRET);
        assertThat(testGluu.getLoginRedirectUri()).isEqualTo(DEFAULT_LOGIN_REDIRECT_URI);
        assertThat(testGluu.getLogoutRedirectUri()).isEqualTo(DEFAULT_LOGOUT_REDIRECT_URI);
    }

    @Test
    @Transactional
    public void getAllGluus() throws Exception {
        // Initialize the database
        gluuRepository.saveAndFlush(gluu);

        // Get all the gluus
        restGluuMockMvc.perform(get("/api/gluus?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(gluu.getId().intValue())))
                .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST.toString())))
                .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.toString())))
                .andExpect(jsonPath("$.[*].clientSecret").value(hasItem(DEFAULT_CLIENT_SECRET.toString())))
                .andExpect(jsonPath("$.[*].loginRedirectUri").value(hasItem(DEFAULT_LOGIN_REDIRECT_URI.toString())))
                .andExpect(jsonPath("$.[*].logoutRedirectUri").value(hasItem(DEFAULT_LOGOUT_REDIRECT_URI.toString())));
    }

    @Test
    @Transactional
    public void getGluu() throws Exception {
        // Initialize the database
        gluuRepository.saveAndFlush(gluu);

        // Get the gluu
        restGluuMockMvc.perform(get("/api/gluus/{id}", gluu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(gluu.getId().intValue()))
            .andExpect(jsonPath("$.host").value(DEFAULT_HOST.toString()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.clientSecret").value(DEFAULT_CLIENT_SECRET.toString()))
            .andExpect(jsonPath("$.loginRedirectUri").value(DEFAULT_LOGIN_REDIRECT_URI.toString()))
            .andExpect(jsonPath("$.logoutRedirectUri").value(DEFAULT_LOGOUT_REDIRECT_URI.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGluu() throws Exception {
        // Get the gluu
        restGluuMockMvc.perform(get("/api/gluus/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGluu() throws Exception {
        // Initialize the database
        gluuRepository.saveAndFlush(gluu);
        int databaseSizeBeforeUpdate = gluuRepository.findAll().size();

        // Update the gluu
        Gluu updatedGluu = new Gluu();
        updatedGluu.setId(gluu.getId());
        updatedGluu.setHost(UPDATED_HOST);
        updatedGluu.setClientId(UPDATED_CLIENT_ID);
        updatedGluu.setClientSecret(UPDATED_CLIENT_SECRET);
        updatedGluu.setLoginRedirectUri(UPDATED_LOGIN_REDIRECT_URI);
        updatedGluu.setLogoutRedirectUri(UPDATED_LOGOUT_REDIRECT_URI);

        restGluuMockMvc.perform(put("/api/gluus")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedGluu)))
                .andExpect(status().isOk());

        // Validate the Gluu in the database
        List<Gluu> gluus = gluuRepository.findAll();
        assertThat(gluus).hasSize(databaseSizeBeforeUpdate);
        Gluu testGluu = gluus.get(gluus.size() - 1);
        assertThat(testGluu.getHost()).isEqualTo(UPDATED_HOST);
        assertThat(testGluu.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testGluu.getClientSecret()).isEqualTo(UPDATED_CLIENT_SECRET);
        assertThat(testGluu.getLoginRedirectUri()).isEqualTo(UPDATED_LOGIN_REDIRECT_URI);
        assertThat(testGluu.getLogoutRedirectUri()).isEqualTo(UPDATED_LOGOUT_REDIRECT_URI);
    }

    @Test
    @Transactional
    public void deleteGluu() throws Exception {
        // Initialize the database
        gluuRepository.saveAndFlush(gluu);
        int databaseSizeBeforeDelete = gluuRepository.findAll().size();

        // Get the gluu
        restGluuMockMvc.perform(delete("/api/gluus/{id}", gluu.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Gluu> gluus = gluuRepository.findAll();
        assertThat(gluus).hasSize(databaseSizeBeforeDelete - 1);
    }
}
