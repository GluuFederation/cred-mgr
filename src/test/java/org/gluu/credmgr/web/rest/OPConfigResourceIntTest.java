package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;

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
 * Test class for the OPConfigResource REST controller.
 *
 * @see OPConfigResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class OPConfigResourceIntTest {

    private static final String DEFAULT_ADMIN_SCIM_ID = "AAAAA";
    private static final String UPDATED_ADMIN_SCIM_ID = "BBBBB";
    private static final String DEFAULT_COMPANY_NAME = "AAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBB";
    private static final String DEFAULT_COMPANY_SHORT_NAME = "AAAAA";
    private static final String UPDATED_COMPANY_SHORT_NAME = "BBBBB";
    private static final String DEFAULT_HOST = "AAAAA";
    private static final String UPDATED_HOST = "BBBBB";
    private static final String DEFAULT_CLIENT_ID = "AAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBB";
    private static final String DEFAULT_CLIENT_JWKS = "AAAAA";
    private static final String UPDATED_CLIENT_JWKS = "BBBBB";

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

    private static final Boolean DEFAULT_ENABLE_ADMIN_PAGE = false;
    private static final Boolean UPDATED_ENABLE_ADMIN_PAGE = true;

    private static final Boolean DEFAULT_ENABLE_EMAIL_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_EMAIL_MANAGEMENT = true;
    private static final String DEFAULT_ACTIVATION_KEY = "AAAAA";
    private static final String UPDATED_ACTIVATION_KEY = "BBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;
    private static final String DEFAULT_CLIENT_SECRET = "AAAAA";
    private static final String UPDATED_CLIENT_SECRET = "BBBBB";

    @Inject
    private OPConfigRepository oPConfigRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOPConfigMockMvc;

    private OPConfig oPConfig;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OPConfigResource oPConfigResource = new OPConfigResource();
        ReflectionTestUtils.setField(oPConfigResource, "oPConfigRepository", oPConfigRepository);
        this.restOPConfigMockMvc = MockMvcBuilders.standaloneSetup(oPConfigResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        oPConfig = new OPConfig();
        oPConfig.setAdminScimId(DEFAULT_ADMIN_SCIM_ID);
        oPConfig.setCompanyName(DEFAULT_COMPANY_NAME);
        oPConfig.setCompanyShortName(DEFAULT_COMPANY_SHORT_NAME);
        oPConfig.setHost(DEFAULT_HOST);
        oPConfig.setClientId(DEFAULT_CLIENT_ID);
        oPConfig.setClientJWKS(DEFAULT_CLIENT_JWKS);
        oPConfig.setAuthenticationLevel(DEFAULT_AUTHENTICATION_LEVEL);
        oPConfig.setRequiredOpenIdScope(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        oPConfig.setRequiredClaim(DEFAULT_REQUIRED_CLAIM);
        oPConfig.setRequiredClaimValue(DEFAULT_REQUIRED_CLAIM_VALUE);
        oPConfig.setEnablePasswordManagement(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        oPConfig.setEnableAdminPage(DEFAULT_ENABLE_ADMIN_PAGE);
        oPConfig.setEnableEmailManagement(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
        oPConfig.setActivationKey(DEFAULT_ACTIVATION_KEY);
        oPConfig.setEmail(DEFAULT_EMAIL);
        oPConfig.setActivated(DEFAULT_ACTIVATED);
        oPConfig.setClientSecret(DEFAULT_CLIENT_SECRET);
    }

    @Test
    @Transactional
    public void createOPConfig() throws Exception {
        int databaseSizeBeforeCreate = oPConfigRepository.findAll().size();

        // Create the OPConfig

        restOPConfigMockMvc.perform(post("/api/o-p-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(oPConfig)))
                .andExpect(status().isCreated());

        // Validate the OPConfig in the database
        List<OPConfig> oPConfigs = oPConfigRepository.findAll();
        assertThat(oPConfigs).hasSize(databaseSizeBeforeCreate + 1);
        OPConfig testOPConfig = oPConfigs.get(oPConfigs.size() - 1);
        assertThat(testOPConfig.getAdminScimId()).isEqualTo(DEFAULT_ADMIN_SCIM_ID);
        assertThat(testOPConfig.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testOPConfig.getCompanyShortName()).isEqualTo(DEFAULT_COMPANY_SHORT_NAME);
        assertThat(testOPConfig.getHost()).isEqualTo(DEFAULT_HOST);
        assertThat(testOPConfig.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testOPConfig.getClientJWKS()).isEqualTo(DEFAULT_CLIENT_JWKS);
        assertThat(testOPConfig.getAuthenticationLevel()).isEqualTo(DEFAULT_AUTHENTICATION_LEVEL);
        assertThat(testOPConfig.getRequiredOpenIdScope()).isEqualTo(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOPConfig.getRequiredClaim()).isEqualTo(DEFAULT_REQUIRED_CLAIM);
        assertThat(testOPConfig.getRequiredClaimValue()).isEqualTo(DEFAULT_REQUIRED_CLAIM_VALUE);
        assertThat(testOPConfig.isEnablePasswordManagement()).isEqualTo(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOPConfig.isEnableAdminPage()).isEqualTo(DEFAULT_ENABLE_ADMIN_PAGE);
        assertThat(testOPConfig.isEnableEmailManagement()).isEqualTo(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
        assertThat(testOPConfig.getActivationKey()).isEqualTo(DEFAULT_ACTIVATION_KEY);
        assertThat(testOPConfig.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testOPConfig.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testOPConfig.getClientSecret()).isEqualTo(DEFAULT_CLIENT_SECRET);
    }

    @Test
    @Transactional
    public void checkActivatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = oPConfigRepository.findAll().size();
        // set the field null
        oPConfig.setActivated(null);

        // Create the OPConfig, which fails.

        restOPConfigMockMvc.perform(post("/api/o-p-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(oPConfig)))
                .andExpect(status().isBadRequest());

        List<OPConfig> oPConfigs = oPConfigRepository.findAll();
        assertThat(oPConfigs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOPConfigs() throws Exception {
        // Initialize the database
        oPConfigRepository.saveAndFlush(oPConfig);

        // Get all the oPConfigs
        restOPConfigMockMvc.perform(get("/api/o-p-configs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(oPConfig.getId().intValue())))
                .andExpect(jsonPath("$.[*].adminScimId").value(hasItem(DEFAULT_ADMIN_SCIM_ID.toString())))
                .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME.toString())))
                .andExpect(jsonPath("$.[*].companyShortName").value(hasItem(DEFAULT_COMPANY_SHORT_NAME.toString())))
                .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST.toString())))
                .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.toString())))
                .andExpect(jsonPath("$.[*].clientJWKS").value(hasItem(DEFAULT_CLIENT_JWKS.toString())))
                .andExpect(jsonPath("$.[*].authenticationLevel").value(hasItem(DEFAULT_AUTHENTICATION_LEVEL)))
                .andExpect(jsonPath("$.[*].requiredOpenIdScope").value(hasItem(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString())))
                .andExpect(jsonPath("$.[*].requiredClaim").value(hasItem(DEFAULT_REQUIRED_CLAIM.toString())))
                .andExpect(jsonPath("$.[*].requiredClaimValue").value(hasItem(DEFAULT_REQUIRED_CLAIM_VALUE.toString())))
                .andExpect(jsonPath("$.[*].enablePasswordManagement").value(hasItem(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableAdminPage").value(hasItem(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue())))
                .andExpect(jsonPath("$.[*].enableEmailManagement").value(hasItem(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].activationKey").value(hasItem(DEFAULT_ACTIVATION_KEY.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
                .andExpect(jsonPath("$.[*].clientSecret").value(hasItem(DEFAULT_CLIENT_SECRET.toString())));
    }

    @Test
    @Transactional
    public void getOPConfig() throws Exception {
        // Initialize the database
        oPConfigRepository.saveAndFlush(oPConfig);

        // Get the oPConfig
        restOPConfigMockMvc.perform(get("/api/o-p-configs/{id}", oPConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(oPConfig.getId().intValue()))
            .andExpect(jsonPath("$.adminScimId").value(DEFAULT_ADMIN_SCIM_ID.toString()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME.toString()))
            .andExpect(jsonPath("$.companyShortName").value(DEFAULT_COMPANY_SHORT_NAME.toString()))
            .andExpect(jsonPath("$.host").value(DEFAULT_HOST.toString()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.clientJWKS").value(DEFAULT_CLIENT_JWKS.toString()))
            .andExpect(jsonPath("$.authenticationLevel").value(DEFAULT_AUTHENTICATION_LEVEL))
            .andExpect(jsonPath("$.requiredOpenIdScope").value(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString()))
            .andExpect(jsonPath("$.requiredClaim").value(DEFAULT_REQUIRED_CLAIM.toString()))
            .andExpect(jsonPath("$.requiredClaimValue").value(DEFAULT_REQUIRED_CLAIM_VALUE.toString()))
            .andExpect(jsonPath("$.enablePasswordManagement").value(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableAdminPage").value(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue()))
            .andExpect(jsonPath("$.enableEmailManagement").value(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.activationKey").value(DEFAULT_ACTIVATION_KEY.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.clientSecret").value(DEFAULT_CLIENT_SECRET.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOPConfig() throws Exception {
        // Get the oPConfig
        restOPConfigMockMvc.perform(get("/api/o-p-configs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOPConfig() throws Exception {
        // Initialize the database
        oPConfigRepository.saveAndFlush(oPConfig);
        int databaseSizeBeforeUpdate = oPConfigRepository.findAll().size();

        // Update the oPConfig
        OPConfig updatedOPConfig = new OPConfig();
        updatedOPConfig.setId(oPConfig.getId());
        updatedOPConfig.setAdminScimId(UPDATED_ADMIN_SCIM_ID);
        updatedOPConfig.setCompanyName(UPDATED_COMPANY_NAME);
        updatedOPConfig.setCompanyShortName(UPDATED_COMPANY_SHORT_NAME);
        updatedOPConfig.setHost(UPDATED_HOST);
        updatedOPConfig.setClientId(UPDATED_CLIENT_ID);
        updatedOPConfig.setClientJWKS(UPDATED_CLIENT_JWKS);
        updatedOPConfig.setAuthenticationLevel(UPDATED_AUTHENTICATION_LEVEL);
        updatedOPConfig.setRequiredOpenIdScope(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        updatedOPConfig.setRequiredClaim(UPDATED_REQUIRED_CLAIM);
        updatedOPConfig.setRequiredClaimValue(UPDATED_REQUIRED_CLAIM_VALUE);
        updatedOPConfig.setEnablePasswordManagement(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        updatedOPConfig.setEnableAdminPage(UPDATED_ENABLE_ADMIN_PAGE);
        updatedOPConfig.setEnableEmailManagement(UPDATED_ENABLE_EMAIL_MANAGEMENT);
        updatedOPConfig.setActivationKey(UPDATED_ACTIVATION_KEY);
        updatedOPConfig.setEmail(UPDATED_EMAIL);
        updatedOPConfig.setActivated(UPDATED_ACTIVATED);
        updatedOPConfig.setClientSecret(UPDATED_CLIENT_SECRET);

        restOPConfigMockMvc.perform(put("/api/o-p-configs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOPConfig)))
                .andExpect(status().isOk());

        // Validate the OPConfig in the database
        List<OPConfig> oPConfigs = oPConfigRepository.findAll();
        assertThat(oPConfigs).hasSize(databaseSizeBeforeUpdate);
        OPConfig testOPConfig = oPConfigs.get(oPConfigs.size() - 1);
        assertThat(testOPConfig.getAdminScimId()).isEqualTo(UPDATED_ADMIN_SCIM_ID);
        assertThat(testOPConfig.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testOPConfig.getCompanyShortName()).isEqualTo(UPDATED_COMPANY_SHORT_NAME);
        assertThat(testOPConfig.getHost()).isEqualTo(UPDATED_HOST);
        assertThat(testOPConfig.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOPConfig.getClientJWKS()).isEqualTo(UPDATED_CLIENT_JWKS);
        assertThat(testOPConfig.getAuthenticationLevel()).isEqualTo(UPDATED_AUTHENTICATION_LEVEL);
        assertThat(testOPConfig.getRequiredOpenIdScope()).isEqualTo(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOPConfig.getRequiredClaim()).isEqualTo(UPDATED_REQUIRED_CLAIM);
        assertThat(testOPConfig.getRequiredClaimValue()).isEqualTo(UPDATED_REQUIRED_CLAIM_VALUE);
        assertThat(testOPConfig.isEnablePasswordManagement()).isEqualTo(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOPConfig.isEnableAdminPage()).isEqualTo(UPDATED_ENABLE_ADMIN_PAGE);
        assertThat(testOPConfig.isEnableEmailManagement()).isEqualTo(UPDATED_ENABLE_EMAIL_MANAGEMENT);
        assertThat(testOPConfig.getActivationKey()).isEqualTo(UPDATED_ACTIVATION_KEY);
        assertThat(testOPConfig.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testOPConfig.isActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testOPConfig.getClientSecret()).isEqualTo(UPDATED_CLIENT_SECRET);
    }

    @Test
    @Transactional
    public void deleteOPConfig() throws Exception {
        // Initialize the database
        oPConfigRepository.saveAndFlush(oPConfig);
        int databaseSizeBeforeDelete = oPConfigRepository.findAll().size();

        // Get the oPConfig
        restOPConfigMockMvc.perform(delete("/api/o-p-configs/{id}", oPConfig.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OPConfig> oPConfigs = oPConfigRepository.findAll();
        assertThat(oPConfigs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
