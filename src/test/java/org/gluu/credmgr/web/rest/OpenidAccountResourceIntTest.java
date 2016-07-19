package org.gluu.credmgr.web.rest;

import org.apache.commons.io.IOUtils;
import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.OPCommonTest;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.OxauthService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class OpenidAccountResourceIntTest extends OPCommonTest {

    @Value("${credmgr.gluuIdpOrg.umaAatClientId}")
    private String umaAatClientId;

    @Value("${credmgr.gluuIdpOrg.umaAatClientKeyId}")
    private String umaAatClientKeyId;

    @Value("${credmgr.gluuIdpOrg.host}")
    private String host;

    @Value("${credmgr.gluuIdpOrg.companyShortName}")
    private String companyShortName;

    @Value("${credmgr.gluuIdpOrg.requiredOPAdminClaimValue}")
    private String adminClaimValue;

    @Value("${credmgr.gluuIdpOrg.requiredOPSuperAdminClaimValue}")
    private String superAdminClaimValue;

    @Inject
    private OPConfigRepository opConfigRepository;

    @Inject
    private ScimService scimService;

    @Inject
    private OPUserService opUserService;


    @Inject
    private MailService mailService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOpenidAccountConfigMockMvc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpenidAccountResource openidAccountResource = new OpenidAccountResource();
        ReflectionTestUtils.setField(openidAccountResource, "opUserService", opUserService);
        ReflectionTestUtils.setField(openidAccountResource, "mailService", mailService);
        this.restOpenidAccountConfigMockMvc = MockMvcBuilders.standaloneSetup(openidAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void setUp() throws Exception {
        //saving original oxauthService
        oxauthServiceOriginal = (OxauthService) ReflectionTestUtils.getField(unwrapOPUserService(), "oxauthService");
    }

    @After
    public void tearDown() throws Exception {
        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceOriginal);
        cleanUp();
    }


    @Test
    public void registerAccount() throws Exception {
        int databaseSizeBeforeCreate = opConfigRepository.findAll().size();

        RegistrationDTO registrationDTO = getRegistrationDTO();
        restOpenidAccountConfigMockMvc.perform(post("/api/openid/register")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
            .andExpect(status().isOk());

        List<OPConfig> opConfigs = opConfigRepository.findAll();
        assertThat(opConfigs).hasSize(databaseSizeBeforeCreate + 1);
        OPConfig opConfig = opConfigs.get(opConfigs.size() - 1);
        assertThat(opConfig.getAdminScimId()).isNotEmpty();
        assertThat(opConfig.getCompanyName()).isNotEmpty();
        assertThat(opConfig.getCompanyShortName()).isNotEmpty();
        assertThat(opConfig.getEmail()).isNotEmpty();
        assertThat(opConfig.getActivationKey()).isNotEmpty();
        assertThat(opConfig.isActivated()).isFalse();

        opConfigRepository.delete(opConfig.getId());

        assertThat(scimService.deletePerson(opConfig.getAdminScimId())).isTrue();

        registrationDTO.setEmail("wrong_email");
        try {
            restOpenidAccountConfigMockMvc.perform(post("/api/openid/register")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(registrationDTO)))
                .andExpect(status().isInternalServerError());
            Assert.fail();
        } catch (NestedServletException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void activateAccount() throws Exception {
        OPConfig opConfig = register();

        assertThat(opConfig.getActivationKey()).isNotNull();
        assertThat(opConfig.isActivated()).isFalse();
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/activate").param("key", opConfig.getActivationKey()))
            .andExpect(status().isOk());

        OPConfig config = opConfigRepository.findOne(opConfig.getId());
        assertThat(config.getActivationKey()).isNull();
        assertThat(config.isActivated()).isTrue();

        try {
            restOpenidAccountConfigMockMvc.perform(get("/api/openid/activate").param("key", "not_existed_key"))
                .andExpect(status().isOk());
        } catch (NestedServletException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void getLoginUri() throws Exception {
        OPConfig opConfig = register();
        opConfig.setHost(host);
        opConfigRepository.save(opConfig);
        String loginUri = opUserService.getLoginUri(opConfig.getCompanyShortName(), null);

        restOpenidAccountConfigMockMvc.perform(get("/api/openid/login-uri").param("companyShortName", opConfig.getCompanyShortName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.value").value(Matchers.startsWith(loginUri)));


        try {
            restOpenidAccountConfigMockMvc.perform(get("/api/openid/login-uri").param("companyShortName", "not_existed_company_short_name"))
                .andExpect(status().isInternalServerError());
        } catch (NestedServletException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void getLogoutUri() throws Exception {
        registerAndPreLoginUser();
        String logoutUri = opUserService.getLogoutUri(null);
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/logout-uri"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.value").value(Matchers.startsWith(logoutUri)));

        SecurityContextHolder.getContext().setAuthentication(null);
        try {
            restOpenidAccountConfigMockMvc.perform(get("/api/openid/logout-uri"))
                .andExpect(status().isInternalServerError());
        } catch (NestedServletException e) {
            assertThat(e).isNotNull();
        }
    }


    @Test
    public void loginUserRedirectionHandler() throws Exception {
        OPConfig opConfig = registerAndPreLoginUser();
        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(opConfig.getAdminScimId()));
        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(user.getAuthorities()).doesNotContain(OPAuthority.OP_ADMIN, OPAuthority.OP_USER);

        restOpenidAccountConfigMockMvc.perform(get("/api/openid/login-redirect").param("code", "code")).andExpect(redirectedUrl("/#/reset-password/"));
        user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(user.getAuthorities()).contains(OPAuthority.OP_USER);
    }

    @Test
    public void loginAdminRedirectionHandler() throws Exception {
        OPConfig opConfig = registerAndPreLoginAdmin();
        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(opConfig.getAdminScimId()));
        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(user.getAuthorities()).doesNotContain(OPAuthority.OP_ADMIN, OPAuthority.OP_USER);

        restOpenidAccountConfigMockMvc.perform(get("/api/openid/login-redirect").param("code", "code")).andExpect(redirectedUrl("/#/settings"));
        user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(user.getAuthorities()).contains(OPAuthority.OP_ADMIN);
    }

    @Test
    public void logoutRedirectionHandler() throws Exception {
        registerAndLoginUser();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/logout-redirect")).andExpect(redirectedUrl("/#/"));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void getUserAccount() throws Exception {
        registerAndLoginUser();
        OPUser opUser = opUserService.getPrincipal().get();
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/account"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.scimId").value(opUser.getScimId()))
            .andExpect(jsonPath("$.login").value(opUser.getLogin()))
            .andExpect(jsonPath("$.host").value(opUser.getHost()))
            .andExpect(jsonPath("$.idToken").value(opUser.getIdToken()))
            .andExpect(jsonPath("$.langKey").value(opUser.getLangKey()))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItem(OPAuthority.OP_USER.toString())));
    }

    @Test
    public void getAdminAccount() throws Exception {
        registerAndLoginAdmin();
        OPUser opUser = opUserService.getPrincipal().get();
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/account"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.scimId").value(opUser.getScimId()))
            .andExpect(jsonPath("$.login").value(opUser.getLogin()))
            .andExpect(jsonPath("$.host").value(opUser.getHost()))
            .andExpect(jsonPath("$.idToken").value(opUser.getIdToken()))
            .andExpect(jsonPath("$.langKey").value(opUser.getLangKey()))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItem(OPAuthority.OP_ADMIN.toString())));
    }

    @Test
    public void getSuperAdminAccount() throws Exception {
        registerAndLoginSuperAdmin();
        OPUser opUser = opUserService.getPrincipal().get();
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/account"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.scimId").value(opUser.getScimId()))
            .andExpect(jsonPath("$.login").value(opUser.getLogin()))
            .andExpect(jsonPath("$.host").value(opUser.getHost()))
            .andExpect(jsonPath("$.idToken").value(opUser.getIdToken()))
            .andExpect(jsonPath("$.langKey").value(opUser.getLangKey()))
            .andExpect(jsonPath("$.authorities").value(Matchers.hasItem(OPAuthority.OP_SUPER_ADMIN.toString())));

    }

    @Test
    public void changePassword() throws Exception {
        registerAndLoginUser();
        restOpenidAccountConfigMockMvc.perform(post("/api/openid/change_password").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("new_password"))).andExpect(status().isOk());

        SecurityContextHolder.getContext().setAuthentication(null);
        try {
            restOpenidAccountConfigMockMvc.perform(post("/api/openid/change_password").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes("new_password"))).andExpect(status().isOk());
        } catch (NestedServletException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void requestAndFinishPasswordReset() throws Exception {
        OPConfig opConfig = register();
        opConfig.setActivated(true);
        opConfig.setHost(host);
        opConfig.setUmaAatClientId(umaAatClientId);
        opConfig.setUmaAatClientKeyId(umaAatClientKeyId);
        opConfig.setClientJKS(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("scim-rp-openid-keys.json")));
        opConfigRepository.save(opConfig);

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setEmail(opConfig.getEmail());
        resetPasswordDTO.setCompanyShortName(opConfig.getCompanyShortName());
        restOpenidAccountConfigMockMvc.perform(post("/api/openid/reset_password/init").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resetPasswordDTO)));

        User user = scimService.retrievePerson(opConfig.getAdminScimId());
        String resetKey = user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING);
        assertThat(resetKey).isNotEmpty();
        assertThat(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING)).isNotEmpty();


        KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
        keyAndPasswordDTO.setCompanyShortName(opConfig.getCompanyShortName());
        keyAndPasswordDTO.setKey(resetKey);
        keyAndPasswordDTO.setNewPassword("helloworld");
        restOpenidAccountConfigMockMvc.perform(post("/api/openid/reset_password/finish").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))).andExpect(status().isOk());

    }

    @Override
    public OPUserService getOPUserService() {
        return opUserService;
    }

    @Override
    public OPConfigRepository getOPConfigRepository() {
        return opConfigRepository;
    }

    @Override
    public ScimService getScimService() {
        return scimService;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getAdminClaimValue() {
        return adminClaimValue;
    }

    @Override
    public String getSuperAdminClaimValue() {
        return superAdminClaimValue;
    }

    @Override
    public String getCompanyShortName() {
        return companyShortName;
    }
}
