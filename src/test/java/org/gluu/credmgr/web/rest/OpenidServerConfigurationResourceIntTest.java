package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OpenidServerConfiguration;
import org.gluu.credmgr.repository.OpenidServerConfigurationRepository;

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
 * Test class for the OpenidServerConfigurationResource REST controller.
 *
 * @see OpenidServerConfigurationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class OpenidServerConfigurationResourceIntTest {

    private static final String DEFAULT_HOST = "AAAAA";
    private static final String UPDATED_HOST = "BBBBB";
    private static final String DEFAULT_CLIENT_ID = "AAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBB";
    private static final String DEFAULT_CLIENT_JWKS = "AAAAA";
    private static final String UPDATED_CLIENT_JWKS = "BBBBB";

    private static final Boolean DEFAULT_ENABLE_ADMIN_PAGE = false;
    private static final Boolean UPDATED_ENABLE_ADMIN_PAGE = true;

    private static final Integer DEFAULT_AUTHENTICATION_LEVEL = 1;
    private static final Integer UPDATED_AUTHENTICATION_LEVEL = 2;
    private static final String DEFAULT_REQUIRED_OPEN_ID_SCOPE = "AAAAA";
    private static final String UPDATED_REQUIRED_OPEN_ID_SCOPE = "BBBBB";
    private static final String DEFAULT_REQUIRED_CLAIM = "AAAAA";
    private static final String UPDATED_REQUIRED_CLAIM = "BBBBB";
    private static final String DEFAULT_REQUIRED_CLAIM_VALUE = "AAAAA";
    private static final String UPDATED_REQUIRED_CLAIM_VALUE = "BBBBB";

    private static final Boolean DEFAULT_ENABLE_PASSWORD_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_PASSWORD_MANAGEMENT = true;

    private static final Boolean DEFAULT_ENABLE_EMAIL_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_EMAIL_MANAGEMENT = true;

    @Inject
    private OpenidServerConfigurationRepository openidServerConfigurationRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOpenidServerConfigurationMockMvc;

    private OpenidServerConfiguration openidServerConfiguration;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpenidServerConfigurationResource openidServerConfigurationResource = new OpenidServerConfigurationResource();
        ReflectionTestUtils.setField(openidServerConfigurationResource, "openidServerConfigurationRepository", openidServerConfigurationRepository);
        this.restOpenidServerConfigurationMockMvc = MockMvcBuilders.standaloneSetup(openidServerConfigurationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        openidServerConfiguration = new OpenidServerConfiguration();
        openidServerConfiguration.setHost(DEFAULT_HOST);
        openidServerConfiguration.setClientId(DEFAULT_CLIENT_ID);
        openidServerConfiguration.setClientJWKS(DEFAULT_CLIENT_JWKS);
        openidServerConfiguration.setEnableAdminPage(DEFAULT_ENABLE_ADMIN_PAGE);
        openidServerConfiguration.setAuthenticationLevel(DEFAULT_AUTHENTICATION_LEVEL);
        openidServerConfiguration.setRequiredOpenIdScope(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        openidServerConfiguration.setRequiredClaim(DEFAULT_REQUIRED_CLAIM);
        openidServerConfiguration.setRequiredClaimValue(DEFAULT_REQUIRED_CLAIM_VALUE);
        openidServerConfiguration.setEnablePasswordManagement(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        openidServerConfiguration.setEnableEmailManagement(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
    }

    @Test
    @Transactional
    public void createOpenidServerConfiguration() throws Exception {
        int databaseSizeBeforeCreate = openidServerConfigurationRepository.findAll().size();

        // Create the OpenidServerConfiguration

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isCreated());

        // Validate the OpenidServerConfiguration in the database
        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeCreate + 1);
        OpenidServerConfiguration testOpenidServerConfiguration = openidServerConfigurations.get(openidServerConfigurations.size() - 1);
        assertThat(testOpenidServerConfiguration.getHost()).isEqualTo(DEFAULT_HOST);
        assertThat(testOpenidServerConfiguration.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testOpenidServerConfiguration.getClientJWKS()).isEqualTo(DEFAULT_CLIENT_JWKS);
        assertThat(testOpenidServerConfiguration.isEnableAdminPage()).isEqualTo(DEFAULT_ENABLE_ADMIN_PAGE);
        assertThat(testOpenidServerConfiguration.getAuthenticationLevel()).isEqualTo(DEFAULT_AUTHENTICATION_LEVEL);
        assertThat(testOpenidServerConfiguration.getRequiredOpenIdScope()).isEqualTo(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOpenidServerConfiguration.getRequiredClaim()).isEqualTo(DEFAULT_REQUIRED_CLAIM);
        assertThat(testOpenidServerConfiguration.getRequiredClaimValue()).isEqualTo(DEFAULT_REQUIRED_CLAIM_VALUE);
        assertThat(testOpenidServerConfiguration.isEnablePasswordManagement()).isEqualTo(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOpenidServerConfiguration.isEnableEmailManagement()).isEqualTo(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
    }

    @Test
    @Transactional
    public void checkHostIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setHost(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkClientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setClientId(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkClientJWKSIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setClientJWKS(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredOpenIdScopeIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setRequiredOpenIdScope(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredClaimIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setRequiredClaim(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredClaimValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = openidServerConfigurationRepository.findAll().size();
        // set the field null
        openidServerConfiguration.setRequiredClaimValue(null);

        // Create the OpenidServerConfiguration, which fails.

        restOpenidServerConfigurationMockMvc.perform(post("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(openidServerConfiguration)))
                .andExpect(status().isBadRequest());

        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOpenidServerConfigurations() throws Exception {
        // Initialize the database
        openidServerConfigurationRepository.saveAndFlush(openidServerConfiguration);

        // Get all the openidServerConfigurations
        restOpenidServerConfigurationMockMvc.perform(get("/api/openid-server-configurations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(openidServerConfiguration.getId().intValue())))
                .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST.toString())))
                .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.toString())))
                .andExpect(jsonPath("$.[*].clientJWKS").value(hasItem(DEFAULT_CLIENT_JWKS.toString())))
                .andExpect(jsonPath("$.[*].enableAdminPage").value(hasItem(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue())))
                .andExpect(jsonPath("$.[*].authenticationLevel").value(hasItem(DEFAULT_AUTHENTICATION_LEVEL)))
                .andExpect(jsonPath("$.[*].requiredOpenIdScope").value(hasItem(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString())))
                .andExpect(jsonPath("$.[*].requiredClaim").value(hasItem(DEFAULT_REQUIRED_CLAIM.toString())))
                .andExpect(jsonPath("$.[*].requiredClaimValue").value(hasItem(DEFAULT_REQUIRED_CLAIM_VALUE.toString())))
                .andExpect(jsonPath("$.[*].enablePasswordManagement").value(hasItem(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableEmailManagement").value(hasItem(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue())));
    }

    @Test
    @Transactional
    public void getOpenidServerConfiguration() throws Exception {
        // Initialize the database
        openidServerConfigurationRepository.saveAndFlush(openidServerConfiguration);

        // Get the openidServerConfiguration
        restOpenidServerConfigurationMockMvc.perform(get("/api/openid-server-configurations/{id}", openidServerConfiguration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(openidServerConfiguration.getId().intValue()))
            .andExpect(jsonPath("$.host").value(DEFAULT_HOST.toString()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.clientJWKS").value(DEFAULT_CLIENT_JWKS.toString()))
            .andExpect(jsonPath("$.enableAdminPage").value(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue()))
            .andExpect(jsonPath("$.authenticationLevel").value(DEFAULT_AUTHENTICATION_LEVEL))
            .andExpect(jsonPath("$.requiredOpenIdScope").value(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString()))
            .andExpect(jsonPath("$.requiredClaim").value(DEFAULT_REQUIRED_CLAIM.toString()))
            .andExpect(jsonPath("$.requiredClaimValue").value(DEFAULT_REQUIRED_CLAIM_VALUE.toString()))
            .andExpect(jsonPath("$.enablePasswordManagement").value(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableEmailManagement").value(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingOpenidServerConfiguration() throws Exception {
        // Get the openidServerConfiguration
        restOpenidServerConfigurationMockMvc.perform(get("/api/openid-server-configurations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOpenidServerConfiguration() throws Exception {
        // Initialize the database
        openidServerConfigurationRepository.saveAndFlush(openidServerConfiguration);
        int databaseSizeBeforeUpdate = openidServerConfigurationRepository.findAll().size();

        // Update the openidServerConfiguration
        OpenidServerConfiguration updatedOpenidServerConfiguration = new OpenidServerConfiguration();
        updatedOpenidServerConfiguration.setId(openidServerConfiguration.getId());
        updatedOpenidServerConfiguration.setHost(UPDATED_HOST);
        updatedOpenidServerConfiguration.setClientId(UPDATED_CLIENT_ID);
        updatedOpenidServerConfiguration.setClientJWKS(UPDATED_CLIENT_JWKS);
        updatedOpenidServerConfiguration.setEnableAdminPage(UPDATED_ENABLE_ADMIN_PAGE);
        updatedOpenidServerConfiguration.setAuthenticationLevel(UPDATED_AUTHENTICATION_LEVEL);
        updatedOpenidServerConfiguration.setRequiredOpenIdScope(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        updatedOpenidServerConfiguration.setRequiredClaim(UPDATED_REQUIRED_CLAIM);
        updatedOpenidServerConfiguration.setRequiredClaimValue(UPDATED_REQUIRED_CLAIM_VALUE);
        updatedOpenidServerConfiguration.setEnablePasswordManagement(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        updatedOpenidServerConfiguration.setEnableEmailManagement(UPDATED_ENABLE_EMAIL_MANAGEMENT);

        restOpenidServerConfigurationMockMvc.perform(put("/api/openid-server-configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOpenidServerConfiguration)))
                .andExpect(status().isOk());

        // Validate the OpenidServerConfiguration in the database
        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeUpdate);
        OpenidServerConfiguration testOpenidServerConfiguration = openidServerConfigurations.get(openidServerConfigurations.size() - 1);
        assertThat(testOpenidServerConfiguration.getHost()).isEqualTo(UPDATED_HOST);
        assertThat(testOpenidServerConfiguration.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOpenidServerConfiguration.getClientJWKS()).isEqualTo(UPDATED_CLIENT_JWKS);
        assertThat(testOpenidServerConfiguration.isEnableAdminPage()).isEqualTo(UPDATED_ENABLE_ADMIN_PAGE);
        assertThat(testOpenidServerConfiguration.getAuthenticationLevel()).isEqualTo(UPDATED_AUTHENTICATION_LEVEL);
        assertThat(testOpenidServerConfiguration.getRequiredOpenIdScope()).isEqualTo(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOpenidServerConfiguration.getRequiredClaim()).isEqualTo(UPDATED_REQUIRED_CLAIM);
        assertThat(testOpenidServerConfiguration.getRequiredClaimValue()).isEqualTo(UPDATED_REQUIRED_CLAIM_VALUE);
        assertThat(testOpenidServerConfiguration.isEnablePasswordManagement()).isEqualTo(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOpenidServerConfiguration.isEnableEmailManagement()).isEqualTo(UPDATED_ENABLE_EMAIL_MANAGEMENT);
    }

    @Test
    @Transactional
    public void deleteOpenidServerConfiguration() throws Exception {
        // Initialize the database
        openidServerConfigurationRepository.saveAndFlush(openidServerConfiguration);
        int databaseSizeBeforeDelete = openidServerConfigurationRepository.findAll().size();

        // Get the openidServerConfiguration
        restOpenidServerConfigurationMockMvc.perform(delete("/api/openid-server-configurations/{id}", openidServerConfiguration.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OpenidServerConfiguration> openidServerConfigurations = openidServerConfigurationRepository.findAll();
        assertThat(openidServerConfigurations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
