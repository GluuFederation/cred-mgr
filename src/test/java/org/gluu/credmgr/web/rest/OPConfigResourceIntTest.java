package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import static org.hamcrest.Matchers.hasItem;
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
    private static final String DEFAULT_EMAIL = "AAAAA@mail.com";
    private static final String UPDATED_EMAIL = "BBBBB@mail.com";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;
    private static final String DEFAULT_ACTIVATION_KEY = "AAAAA";
    private static final String UPDATED_ACTIVATION_KEY = "BBBBB";
    private static final String DEFAULT_HOST = "https://AAAAA";
    private static final String UPDATED_HOST = "https://BBBBB";
    private static final String DEFAULT_CLIENT_ID = "AAAAA";
    private static final String UPDATED_CLIENT_ID = "BBBBB";
    private static final String DEFAULT_CLIENT_SECRET = "AAAAA";
    private static final String UPDATED_CLIENT_SECRET = "BBBBB";
    private static final String DEFAULT_UMA_AAT_CLIENT_ID = "AAAAA";
    private static final String UPDATED_UMA_AAT_CLIENT_ID = "BBBBB";
    private static final String DEFAULT_UMA_AAT_CLIENT_KEY_ID = "AAAAA";
    private static final String UPDATED_UMA_AAT_CLIENT_KEY_ID = "BBBBB";
    private static final String DEFAULT_CLIENT_JKS = "AAAAA";
    private static final String UPDATED_CLIENT_JKS = "BBBBB";

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

    private static final Boolean DEFAULT_ENABLE_MOBILE_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_MOBILE_MANAGEMENT = true;

    private static final Boolean DEFAULT_ENABLE_SOCIAL_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_SOCIAL_MANAGEMENT = true;

    private static final Boolean DEFAULT_ENABLE_U_2_F_MANAGEMENT = false;
    private static final Boolean UPDATED_ENABLE_U_2_F_MANAGEMENT = true;

    private static final Boolean DEFAULT_ENABLE_GOOGLE_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_GOOGLE_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_FACEBOOK_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_FACEBOOK_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_TWITTER_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_TWITTER_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_LINKED_IN_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_LINKED_IN_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_WINDOWS_LIVE_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_WINDOWS_LIVE_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_GITHUB_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_GITHUB_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_DROPBOX_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_DROPBOX_LOGIN = true;

    private static final Boolean DEFAULT_ENABLE_YAHOO_LOGIN = false;
    private static final Boolean UPDATED_ENABLE_YAHOO_LOGIN = true;
    private static final String DEFAULT_SMTP_HOST = "AAAAA";
    private static final String UPDATED_SMTP_HOST = "BBBBB";
    private static final String DEFAULT_SMTP_PORT = "AAAAA";
    private static final String UPDATED_SMTP_PORT = "BBBBB";
    private static final String DEFAULT_SMTP_USERNAME = "AAAAA";
    private static final String UPDATED_SMTP_USERNAME = "BBBBB";
    private static final String DEFAULT_SMTP_PASSWORD = "AAAAA";
    private static final String UPDATED_SMTP_PASSWORD = "BBBBB";

    private static final Boolean DEFAULT_SMTP_USE_SSL = false;
    private static final Boolean UPDATED_SMTP_USE_SSL = true;
    private static final String DEFAULT_TWILIO_SID = "AAAAA";
    private static final String UPDATED_TWILIO_SID = "BBBBB";
    private static final String DEFAULT_TWILIO_TOKEN = "AAAAA";
    private static final String UPDATED_TWILIO_TOKEN = "BBBBB";
    private static final String DEFAULT_TWILIO_FROM_NUMBER = "AAAAA";
    private static final String UPDATED_TWILIO_FROM_NUMBER = "BBBBB";

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
        oPConfig.setEmail(DEFAULT_EMAIL);
        oPConfig.setActivated(DEFAULT_ACTIVATED);
        oPConfig.setActivationKey(DEFAULT_ACTIVATION_KEY);
        oPConfig.setHost(DEFAULT_HOST);
        oPConfig.setClientId(DEFAULT_CLIENT_ID);
        oPConfig.setClientSecret(DEFAULT_CLIENT_SECRET);
        oPConfig.setUmaAatClientId(DEFAULT_UMA_AAT_CLIENT_ID);
        oPConfig.setUmaAatClientKeyId(DEFAULT_UMA_AAT_CLIENT_KEY_ID);
        oPConfig.setClientJKS(DEFAULT_CLIENT_JKS);
        oPConfig.setAuthenticationLevel(DEFAULT_AUTHENTICATION_LEVEL);
        oPConfig.setRequiredOpenIdScope(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        oPConfig.setRequiredClaim(DEFAULT_REQUIRED_CLAIM);
        oPConfig.setRequiredClaimValue(DEFAULT_REQUIRED_CLAIM_VALUE);
        oPConfig.setEnablePasswordManagement(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        oPConfig.setEnableAdminPage(DEFAULT_ENABLE_ADMIN_PAGE);
        oPConfig.setEnableEmailManagement(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
        oPConfig.setEnableMobileManagement(DEFAULT_ENABLE_MOBILE_MANAGEMENT);
        oPConfig.setEnableSocialManagement(DEFAULT_ENABLE_SOCIAL_MANAGEMENT);
        oPConfig.setEnableU2FManagement(DEFAULT_ENABLE_U_2_F_MANAGEMENT);
        oPConfig.setEnableGoogleLogin(DEFAULT_ENABLE_GOOGLE_LOGIN);
        oPConfig.setEnableFacebookLogin(DEFAULT_ENABLE_FACEBOOK_LOGIN);
        oPConfig.setEnableTwitterLogin(DEFAULT_ENABLE_TWITTER_LOGIN);
        oPConfig.setEnableLinkedInLogin(DEFAULT_ENABLE_LINKED_IN_LOGIN);
        oPConfig.setEnableWindowsLiveLogin(DEFAULT_ENABLE_WINDOWS_LIVE_LOGIN);
        oPConfig.setEnableGithubLogin(DEFAULT_ENABLE_GITHUB_LOGIN);
        oPConfig.setEnableDropboxLogin(DEFAULT_ENABLE_DROPBOX_LOGIN);
        oPConfig.setEnableYahooLogin(DEFAULT_ENABLE_YAHOO_LOGIN);
        oPConfig.setSmtpHost(DEFAULT_SMTP_HOST);
        oPConfig.setSmtpPort(DEFAULT_SMTP_PORT);
        oPConfig.setSmtpUsername(DEFAULT_SMTP_USERNAME);
        oPConfig.setSmtpPassword(DEFAULT_SMTP_PASSWORD);
        oPConfig.setSmtpUseSSL(DEFAULT_SMTP_USE_SSL);
        oPConfig.setTwilioSID(DEFAULT_TWILIO_SID);
        oPConfig.setTwilioToken(DEFAULT_TWILIO_TOKEN);
        oPConfig.setTwilioFromNumber(DEFAULT_TWILIO_FROM_NUMBER);
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
        assertThat(testOPConfig.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testOPConfig.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testOPConfig.getActivationKey()).isEqualTo(DEFAULT_ACTIVATION_KEY);
        assertThat(testOPConfig.getHost()).isEqualTo(DEFAULT_HOST);
        assertThat(testOPConfig.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testOPConfig.getClientSecret()).isEqualTo(DEFAULT_CLIENT_SECRET);
        assertThat(testOPConfig.getUmaAatClientId()).isEqualTo(DEFAULT_UMA_AAT_CLIENT_ID);
        assertThat(testOPConfig.getUmaAatClientKeyId()).isEqualTo(DEFAULT_UMA_AAT_CLIENT_KEY_ID);
        assertThat(testOPConfig.getClientJKS()).isEqualTo(DEFAULT_CLIENT_JKS);
        assertThat(testOPConfig.getAuthenticationLevel()).isEqualTo(DEFAULT_AUTHENTICATION_LEVEL);
        assertThat(testOPConfig.getRequiredOpenIdScope()).isEqualTo(DEFAULT_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOPConfig.getRequiredClaim()).isEqualTo(DEFAULT_REQUIRED_CLAIM);
        assertThat(testOPConfig.getRequiredClaimValue()).isEqualTo(DEFAULT_REQUIRED_CLAIM_VALUE);
        assertThat(testOPConfig.isEnablePasswordManagement()).isEqualTo(DEFAULT_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOPConfig.isEnableAdminPage()).isEqualTo(DEFAULT_ENABLE_ADMIN_PAGE);
        assertThat(testOPConfig.isEnableEmailManagement()).isEqualTo(DEFAULT_ENABLE_EMAIL_MANAGEMENT);
        assertThat(testOPConfig.isEnableMobileManagement()).isEqualTo(DEFAULT_ENABLE_MOBILE_MANAGEMENT);
        assertThat(testOPConfig.isEnableSocialManagement()).isEqualTo(DEFAULT_ENABLE_SOCIAL_MANAGEMENT);
        assertThat(testOPConfig.isEnableU2FManagement()).isEqualTo(DEFAULT_ENABLE_U_2_F_MANAGEMENT);
        assertThat(testOPConfig.isEnableGoogleLogin()).isEqualTo(DEFAULT_ENABLE_GOOGLE_LOGIN);
        assertThat(testOPConfig.isEnableFacebookLogin()).isEqualTo(DEFAULT_ENABLE_FACEBOOK_LOGIN);
        assertThat(testOPConfig.isEnableTwitterLogin()).isEqualTo(DEFAULT_ENABLE_TWITTER_LOGIN);
        assertThat(testOPConfig.isEnableLinkedInLogin()).isEqualTo(DEFAULT_ENABLE_LINKED_IN_LOGIN);
        assertThat(testOPConfig.isEnableWindowsLiveLogin()).isEqualTo(DEFAULT_ENABLE_WINDOWS_LIVE_LOGIN);
        assertThat(testOPConfig.isEnableGithubLogin()).isEqualTo(DEFAULT_ENABLE_GITHUB_LOGIN);
        assertThat(testOPConfig.isEnableDropboxLogin()).isEqualTo(DEFAULT_ENABLE_DROPBOX_LOGIN);
        assertThat(testOPConfig.isEnableYahooLogin()).isEqualTo(DEFAULT_ENABLE_YAHOO_LOGIN);
        assertThat(testOPConfig.getSmtpHost()).isEqualTo(DEFAULT_SMTP_HOST);
        assertThat(testOPConfig.getSmtpPort()).isEqualTo(DEFAULT_SMTP_PORT);
        assertThat(testOPConfig.getSmtpUsername()).isEqualTo(DEFAULT_SMTP_USERNAME);
        assertThat(testOPConfig.getSmtpPassword()).isEqualTo(DEFAULT_SMTP_PASSWORD);
        assertThat(testOPConfig.isSmtpUseSSL()).isEqualTo(DEFAULT_SMTP_USE_SSL);
        assertThat(testOPConfig.getTwilioSID()).isEqualTo(DEFAULT_TWILIO_SID);
        assertThat(testOPConfig.getTwilioToken()).isEqualTo(DEFAULT_TWILIO_TOKEN);
        assertThat(testOPConfig.getTwilioFromNumber()).isEqualTo(DEFAULT_TWILIO_FROM_NUMBER);
    }

    @Test
    @Transactional
    public void checkAdminScimIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = oPConfigRepository.findAll().size();
        // set the field null
        oPConfig.setAdminScimId(null);

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
    public void checkCompanyNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = oPConfigRepository.findAll().size();
        // set the field null
        oPConfig.setCompanyName(null);

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
    public void checkCompanyShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = oPConfigRepository.findAll().size();
        // set the field null
        oPConfig.setCompanyShortName(null);

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
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = oPConfigRepository.findAll().size();
        // set the field null
        oPConfig.setEmail(null);

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
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
                .andExpect(jsonPath("$.[*].activationKey").value(hasItem(DEFAULT_ACTIVATION_KEY.toString())))
                .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST.toString())))
                .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.toString())))
                .andExpect(jsonPath("$.[*].clientSecret").value(hasItem(DEFAULT_CLIENT_SECRET.toString())))
                .andExpect(jsonPath("$.[*].umaAatClientId").value(hasItem(DEFAULT_UMA_AAT_CLIENT_ID.toString())))
                .andExpect(jsonPath("$.[*].umaAatClientKeyId").value(hasItem(DEFAULT_UMA_AAT_CLIENT_KEY_ID.toString())))
                .andExpect(jsonPath("$.[*].clientJKS").value(hasItem(DEFAULT_CLIENT_JKS.toString())))
                .andExpect(jsonPath("$.[*].authenticationLevel").value(hasItem(DEFAULT_AUTHENTICATION_LEVEL)))
                .andExpect(jsonPath("$.[*].requiredOpenIdScope").value(hasItem(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString())))
                .andExpect(jsonPath("$.[*].requiredClaim").value(hasItem(DEFAULT_REQUIRED_CLAIM.toString())))
                .andExpect(jsonPath("$.[*].requiredClaimValue").value(hasItem(DEFAULT_REQUIRED_CLAIM_VALUE.toString())))
                .andExpect(jsonPath("$.[*].enablePasswordManagement").value(hasItem(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableAdminPage").value(hasItem(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue())))
                .andExpect(jsonPath("$.[*].enableEmailManagement").value(hasItem(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableMobileManagement").value(hasItem(DEFAULT_ENABLE_MOBILE_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableSocialManagement").value(hasItem(DEFAULT_ENABLE_SOCIAL_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableU2FManagement").value(hasItem(DEFAULT_ENABLE_U_2_F_MANAGEMENT.booleanValue())))
                .andExpect(jsonPath("$.[*].enableGoogleLogin").value(hasItem(DEFAULT_ENABLE_GOOGLE_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableFacebookLogin").value(hasItem(DEFAULT_ENABLE_FACEBOOK_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableTwitterLogin").value(hasItem(DEFAULT_ENABLE_TWITTER_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableLinkedInLogin").value(hasItem(DEFAULT_ENABLE_LINKED_IN_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableWindowsLiveLogin").value(hasItem(DEFAULT_ENABLE_WINDOWS_LIVE_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableGithubLogin").value(hasItem(DEFAULT_ENABLE_GITHUB_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableDropboxLogin").value(hasItem(DEFAULT_ENABLE_DROPBOX_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].enableYahooLogin").value(hasItem(DEFAULT_ENABLE_YAHOO_LOGIN.booleanValue())))
                .andExpect(jsonPath("$.[*].smtpHost").value(hasItem(DEFAULT_SMTP_HOST.toString())))
                .andExpect(jsonPath("$.[*].smtpPort").value(hasItem(DEFAULT_SMTP_PORT.toString())))
                .andExpect(jsonPath("$.[*].smtpUsername").value(hasItem(DEFAULT_SMTP_USERNAME.toString())))
                .andExpect(jsonPath("$.[*].smtpPassword").value(hasItem(DEFAULT_SMTP_PASSWORD.toString())))
                .andExpect(jsonPath("$.[*].smtpUseSSL").value(hasItem(DEFAULT_SMTP_USE_SSL.booleanValue())))
                .andExpect(jsonPath("$.[*].twilioSID").value(hasItem(DEFAULT_TWILIO_SID.toString())))
                .andExpect(jsonPath("$.[*].twilioToken").value(hasItem(DEFAULT_TWILIO_TOKEN.toString())))
                .andExpect(jsonPath("$.[*].twilioFromNumber").value(hasItem(DEFAULT_TWILIO_FROM_NUMBER.toString())));
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
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.activationKey").value(DEFAULT_ACTIVATION_KEY.toString()))
            .andExpect(jsonPath("$.host").value(DEFAULT_HOST.toString()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.clientSecret").value(DEFAULT_CLIENT_SECRET.toString()))
            .andExpect(jsonPath("$.umaAatClientId").value(DEFAULT_UMA_AAT_CLIENT_ID.toString()))
            .andExpect(jsonPath("$.umaAatClientKeyId").value(DEFAULT_UMA_AAT_CLIENT_KEY_ID.toString()))
            .andExpect(jsonPath("$.clientJKS").value(DEFAULT_CLIENT_JKS.toString()))
            .andExpect(jsonPath("$.authenticationLevel").value(DEFAULT_AUTHENTICATION_LEVEL))
            .andExpect(jsonPath("$.requiredOpenIdScope").value(DEFAULT_REQUIRED_OPEN_ID_SCOPE.toString()))
            .andExpect(jsonPath("$.requiredClaim").value(DEFAULT_REQUIRED_CLAIM.toString()))
            .andExpect(jsonPath("$.requiredClaimValue").value(DEFAULT_REQUIRED_CLAIM_VALUE.toString()))
            .andExpect(jsonPath("$.enablePasswordManagement").value(DEFAULT_ENABLE_PASSWORD_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableAdminPage").value(DEFAULT_ENABLE_ADMIN_PAGE.booleanValue()))
            .andExpect(jsonPath("$.enableEmailManagement").value(DEFAULT_ENABLE_EMAIL_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableMobileManagement").value(DEFAULT_ENABLE_MOBILE_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableSocialManagement").value(DEFAULT_ENABLE_SOCIAL_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableU2FManagement").value(DEFAULT_ENABLE_U_2_F_MANAGEMENT.booleanValue()))
            .andExpect(jsonPath("$.enableGoogleLogin").value(DEFAULT_ENABLE_GOOGLE_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableFacebookLogin").value(DEFAULT_ENABLE_FACEBOOK_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableTwitterLogin").value(DEFAULT_ENABLE_TWITTER_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableLinkedInLogin").value(DEFAULT_ENABLE_LINKED_IN_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableWindowsLiveLogin").value(DEFAULT_ENABLE_WINDOWS_LIVE_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableGithubLogin").value(DEFAULT_ENABLE_GITHUB_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableDropboxLogin").value(DEFAULT_ENABLE_DROPBOX_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.enableYahooLogin").value(DEFAULT_ENABLE_YAHOO_LOGIN.booleanValue()))
            .andExpect(jsonPath("$.smtpHost").value(DEFAULT_SMTP_HOST.toString()))
            .andExpect(jsonPath("$.smtpPort").value(DEFAULT_SMTP_PORT.toString()))
            .andExpect(jsonPath("$.smtpUsername").value(DEFAULT_SMTP_USERNAME.toString()))
            .andExpect(jsonPath("$.smtpPassword").value(DEFAULT_SMTP_PASSWORD.toString()))
            .andExpect(jsonPath("$.smtpUseSSL").value(DEFAULT_SMTP_USE_SSL.booleanValue()))
            .andExpect(jsonPath("$.twilioSID").value(DEFAULT_TWILIO_SID.toString()))
            .andExpect(jsonPath("$.twilioToken").value(DEFAULT_TWILIO_TOKEN.toString()))
            .andExpect(jsonPath("$.twilioFromNumber").value(DEFAULT_TWILIO_FROM_NUMBER.toString()));
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
        updatedOPConfig.setEmail(UPDATED_EMAIL);
        updatedOPConfig.setActivated(UPDATED_ACTIVATED);
        updatedOPConfig.setActivationKey(UPDATED_ACTIVATION_KEY);
        updatedOPConfig.setHost(UPDATED_HOST);
        updatedOPConfig.setClientId(UPDATED_CLIENT_ID);
        updatedOPConfig.setClientSecret(UPDATED_CLIENT_SECRET);
        updatedOPConfig.setUmaAatClientId(UPDATED_UMA_AAT_CLIENT_ID);
        updatedOPConfig.setUmaAatClientKeyId(UPDATED_UMA_AAT_CLIENT_KEY_ID);
        updatedOPConfig.setClientJKS(UPDATED_CLIENT_JKS);
        updatedOPConfig.setAuthenticationLevel(UPDATED_AUTHENTICATION_LEVEL);
        updatedOPConfig.setRequiredOpenIdScope(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        updatedOPConfig.setRequiredClaim(UPDATED_REQUIRED_CLAIM);
        updatedOPConfig.setRequiredClaimValue(UPDATED_REQUIRED_CLAIM_VALUE);
        updatedOPConfig.setEnablePasswordManagement(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        updatedOPConfig.setEnableAdminPage(UPDATED_ENABLE_ADMIN_PAGE);
        updatedOPConfig.setEnableEmailManagement(UPDATED_ENABLE_EMAIL_MANAGEMENT);
        updatedOPConfig.setEnableMobileManagement(UPDATED_ENABLE_MOBILE_MANAGEMENT);
        updatedOPConfig.setEnableSocialManagement(UPDATED_ENABLE_SOCIAL_MANAGEMENT);
        updatedOPConfig.setEnableU2FManagement(UPDATED_ENABLE_U_2_F_MANAGEMENT);
        updatedOPConfig.setEnableGoogleLogin(UPDATED_ENABLE_GOOGLE_LOGIN);
        updatedOPConfig.setEnableFacebookLogin(UPDATED_ENABLE_FACEBOOK_LOGIN);
        updatedOPConfig.setEnableTwitterLogin(UPDATED_ENABLE_TWITTER_LOGIN);
        updatedOPConfig.setEnableLinkedInLogin(UPDATED_ENABLE_LINKED_IN_LOGIN);
        updatedOPConfig.setEnableWindowsLiveLogin(UPDATED_ENABLE_WINDOWS_LIVE_LOGIN);
        updatedOPConfig.setEnableGithubLogin(UPDATED_ENABLE_GITHUB_LOGIN);
        updatedOPConfig.setEnableDropboxLogin(UPDATED_ENABLE_DROPBOX_LOGIN);
        updatedOPConfig.setEnableYahooLogin(UPDATED_ENABLE_YAHOO_LOGIN);
        updatedOPConfig.setSmtpHost(UPDATED_SMTP_HOST);
        updatedOPConfig.setSmtpPort(UPDATED_SMTP_PORT);
        updatedOPConfig.setSmtpUsername(UPDATED_SMTP_USERNAME);
        updatedOPConfig.setSmtpPassword(UPDATED_SMTP_PASSWORD);
        updatedOPConfig.setSmtpUseSSL(UPDATED_SMTP_USE_SSL);
        updatedOPConfig.setTwilioSID(UPDATED_TWILIO_SID);
        updatedOPConfig.setTwilioToken(UPDATED_TWILIO_TOKEN);
        updatedOPConfig.setTwilioFromNumber(UPDATED_TWILIO_FROM_NUMBER);

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
        assertThat(testOPConfig.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testOPConfig.isActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testOPConfig.getActivationKey()).isEqualTo(UPDATED_ACTIVATION_KEY);
        assertThat(testOPConfig.getHost()).isEqualTo(UPDATED_HOST);
        assertThat(testOPConfig.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOPConfig.getClientSecret()).isEqualTo(UPDATED_CLIENT_SECRET);
        assertThat(testOPConfig.getUmaAatClientId()).isEqualTo(UPDATED_UMA_AAT_CLIENT_ID);
        assertThat(testOPConfig.getUmaAatClientKeyId()).isEqualTo(UPDATED_UMA_AAT_CLIENT_KEY_ID);
        assertThat(testOPConfig.getClientJKS()).isEqualTo(UPDATED_CLIENT_JKS);
        assertThat(testOPConfig.getAuthenticationLevel()).isEqualTo(UPDATED_AUTHENTICATION_LEVEL);
        assertThat(testOPConfig.getRequiredOpenIdScope()).isEqualTo(UPDATED_REQUIRED_OPEN_ID_SCOPE);
        assertThat(testOPConfig.getRequiredClaim()).isEqualTo(UPDATED_REQUIRED_CLAIM);
        assertThat(testOPConfig.getRequiredClaimValue()).isEqualTo(UPDATED_REQUIRED_CLAIM_VALUE);
        assertThat(testOPConfig.isEnablePasswordManagement()).isEqualTo(UPDATED_ENABLE_PASSWORD_MANAGEMENT);
        assertThat(testOPConfig.isEnableAdminPage()).isEqualTo(UPDATED_ENABLE_ADMIN_PAGE);
        assertThat(testOPConfig.isEnableEmailManagement()).isEqualTo(UPDATED_ENABLE_EMAIL_MANAGEMENT);
        assertThat(testOPConfig.isEnableMobileManagement()).isEqualTo(UPDATED_ENABLE_MOBILE_MANAGEMENT);
        assertThat(testOPConfig.isEnableSocialManagement()).isEqualTo(UPDATED_ENABLE_SOCIAL_MANAGEMENT);
        assertThat(testOPConfig.isEnableU2FManagement()).isEqualTo(UPDATED_ENABLE_U_2_F_MANAGEMENT);
        assertThat(testOPConfig.isEnableGoogleLogin()).isEqualTo(UPDATED_ENABLE_GOOGLE_LOGIN);
        assertThat(testOPConfig.isEnableFacebookLogin()).isEqualTo(UPDATED_ENABLE_FACEBOOK_LOGIN);
        assertThat(testOPConfig.isEnableTwitterLogin()).isEqualTo(UPDATED_ENABLE_TWITTER_LOGIN);
        assertThat(testOPConfig.isEnableLinkedInLogin()).isEqualTo(UPDATED_ENABLE_LINKED_IN_LOGIN);
        assertThat(testOPConfig.isEnableWindowsLiveLogin()).isEqualTo(UPDATED_ENABLE_WINDOWS_LIVE_LOGIN);
        assertThat(testOPConfig.isEnableGithubLogin()).isEqualTo(UPDATED_ENABLE_GITHUB_LOGIN);
        assertThat(testOPConfig.isEnableDropboxLogin()).isEqualTo(UPDATED_ENABLE_DROPBOX_LOGIN);
        assertThat(testOPConfig.isEnableYahooLogin()).isEqualTo(UPDATED_ENABLE_YAHOO_LOGIN);
        assertThat(testOPConfig.getSmtpHost()).isEqualTo(UPDATED_SMTP_HOST);
        assertThat(testOPConfig.getSmtpPort()).isEqualTo(UPDATED_SMTP_PORT);
        assertThat(testOPConfig.getSmtpUsername()).isEqualTo(UPDATED_SMTP_USERNAME);
        assertThat(testOPConfig.getSmtpPassword()).isEqualTo(UPDATED_SMTP_PASSWORD);
        assertThat(testOPConfig.isSmtpUseSSL()).isEqualTo(UPDATED_SMTP_USE_SSL);
        assertThat(testOPConfig.getTwilioSID()).isEqualTo(UPDATED_TWILIO_SID);
        assertThat(testOPConfig.getTwilioToken()).isEqualTo(UPDATED_TWILIO_TOKEN);
        assertThat(testOPConfig.getTwilioFromNumber()).isEqualTo(UPDATED_TWILIO_FROM_NUMBER);
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
