package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.OPCommonTest;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.OxauthService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
public class OpenidAccountResourceIntTest extends OPCommonTest {

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private OPConfigRepository opConfigRepository;

    @Inject
    private ScimService scimService;

    @Inject
    private OPUserService opUserService;

    @Inject
    private Environment env;

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
        OpenIdAccountResource openidAccountResource = new OpenIdAccountResource();

        ReflectionTestUtils.setField(openidAccountResource, "opUserService", opUserService);
        ReflectionTestUtils.setField(openidAccountResource, "mailService", mailService);
        ReflectionTestUtils.setField(openidAccountResource, "env", env);
        ReflectionTestUtils.setField(openidAccountResource, "opConfigRepository", opConfigRepository);

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
    public void getLoginUri() throws Exception {
        String loginUri = opUserService.getLoginUri(null);

        restOpenidAccountConfigMockMvc.perform(get("/api/openid/login-uri"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.value").value(Matchers.startsWith(loginUri)));
    }

    @Test
    public void getLogoutUri() throws Exception {
        registerAndPreLoginUser();
        String logoutUri = opUserService.getLogoutUri(null);
        restOpenidAccountConfigMockMvc.perform(get("/api/openid/logout-uri"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.value").value(Matchers.startsWith("/#/")));

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
        User scimUser = registerAndPreLoginUser();

        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(new String[]{scimUser.getId()}));
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
        User scimUser = registerAndPreLoginAdmin();

        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(new String[]{scimUser.getId()}));
        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(user.getAuthorities()).doesNotContain(OPAuthority.OP_ADMIN, OPAuthority.OP_USER);

        OPConfig config = opConfigRepository.get();
        config.setSmtpHost(null);

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

        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        Mockito.when(oxauthServiceMock.isTokenValid(Mockito.any(), Mockito.any())).thenReturn(true);
        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

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

        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        Mockito.when(oxauthServiceMock.isTokenValid(Mockito.any(), Mockito.any())).thenReturn(true);
        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

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
        OPConfig opConfig = opConfigRepository.get();

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        restOpenidAccountConfigMockMvc.perform(post("/api/openid/reset_password/init").contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resetPasswordDTO)));

//        User user = scimService.retrievePerson(opConfig.getAdminScimId());
//        String resetKey = user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING);
//        assertThat(resetKey).isNotEmpty();
//        assertThat(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING)).isNotEmpty();
//
//
//        KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
//        keyAndPasswordDTO.setCompanyShortName(opConfig.getCompanyShortName());
//        keyAndPasswordDTO.setKey(resetKey);
//        keyAndPasswordDTO.setNewPassword("helloworld");
//        restOpenidAccountConfigMockMvc.perform(post("/api/openid/reset_password/finish").contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(keyAndPasswordDTO))).andExpect(status().isOk());

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
}
