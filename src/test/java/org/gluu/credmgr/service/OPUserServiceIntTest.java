package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class OPUserServiceIntTest {

    @Value("${credmgr.gluuIdpOrg.host}")
    private String host;

    @Inject
    private OPUserService opUserService;

    @Inject
    private ScimService scimService;

    @Inject
    private OPConfigRepository opConfigRepository;


    private OPConfig opConfig;

    private OxauthService oxauthServiceOriginal;

    @Before
    public void setUp() throws Exception {
        //saving original oxauthService
        oxauthServiceOriginal = (OxauthService) ReflectionTestUtils.getField(unwrapOPUserService(), "oxauthService");
    }

    @After
    public void tearDown() throws Exception {
        //TODO: clean up scim users by username pattern
        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceOriginal);
        cleanUp(opConfig);
    }

    @Test
    public void createOPAdminInformationTest() throws OPException, IOException, JAXBException {
        RegistrationDTO registrationDTO = getRegistrationDTO();

        opConfig = opUserService.createOPAdminInformation(registrationDTO);

        Assert.assertNotNull(opConfig);
        Assert.assertNotNull(opConfig.getId());
        Assert.assertNotNull(opConfig.getAdminScimId());
        Assert.assertNotNull(opConfig.getCompanyName());
        Assert.assertNotNull(opConfig.getCompanyShortName());
        Assert.assertNotNull(opConfig.getEmail());
        Assert.assertNotNull(opConfig.getActivationKey());
        Assert.assertFalse(opConfig.isActivated());

        //Attempt to create user with existing username(oxTrust returns error status)
        try {
            opUserService.createOPAdminInformation(registrationDTO);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_CREATE_SCIM_USER, e.getMessage());
        }

        //Attempt to create user with existing email(spring returns data integrity violation exception)
        registrationDTO.setCompanyShortName(registrationDTO.getCompanyShortName() + "test");
        try {
            opUserService.createOPAdminInformation(registrationDTO);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS, e.getMessage());
        }
    }

    @Test
    public void activateOPAdminRegistrationTest() throws OPException, IOException, JAXBException {
        opConfig = register();

        String activationKey = "activationKey" + new Date().getTime();
        opConfig.setActivationKey(activationKey);
        opConfigRepository.save(opConfig);

        ScimResponse scimResponse = scimService.retrievePerson(opConfig.getAdminScimId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);

        Assert.assertFalse(scimUser.isActive());
        Assert.assertFalse(opConfig.isActivated());

        opUserService.activateOPAdminRegistration(activationKey);

        scimResponse = scimService.retrievePerson(opConfig.getAdminScimId());
        scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);

        Assert.assertTrue(scimUser.isActive());

        opConfig = opConfigRepository.findOne(opConfig.getId());
        Assert.assertTrue(opConfig.isActivated());
    }

    @Test
    @Transactional
    public void getLoginUriTest() throws OPException, IOException, JAXBException {
        opConfig = register();
        try {
            opUserService.getLoginUri(opConfig.getCompanyShortName(), null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION, e.getMessage());
        }
        opConfig.setHost(host);
        opConfigRepository.save(opConfig);

        String loginUri = opUserService.getLoginUri(opConfig.getCompanyShortName(), null);
        Assert.assertNotNull(loginUri);

        OPUser principal = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(principal);
        Assert.assertNotNull(principal.getHost());
        Assert.assertNotNull(principal.getLoginOpConfigId());
        Assert.assertEquals(1, principal.getAuthorities().size());
        Assert.assertTrue(principal.getAuthorities().contains(OPAuthority.OP_ANONYMOUS));
    }

    @Test
    public void getLogoutUriTest() throws OPException, IOException, JAXBException {
        opConfig = registerAndPreLogin();

        Assert.assertNotNull(opUserService.getLogoutUri(null));

        SecurityContextHolder.getContext().setAuthentication(null);
        try {
            opUserService.getLogoutUri(null);
            Assert.fail();
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_RETRIEVE_LOGOUT_URI, e.getMessage());
        }
    }

    @Test
    public void loginTest() throws Exception {
        opConfig = registerAndLogin();

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getScimId());
        Assert.assertNotNull(user.getLogin());
        Assert.assertNotNull(user.getHost());
        Assert.assertNotNull(user.getIdToken());

        Assert.assertEquals(3, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_ANONYMOUS));
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_USER));
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_ADMIN));

        Assert.assertNotNull(user.getLoginOpConfigId());
        Assert.assertNotNull(user.getOpConfigId());
        Assert.assertNull(user.getOpConfig());

        user.setLoginOpConfigId(Long.MAX_VALUE);
        try {
            opUserService.login(null, null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_LOGIN, e.getMessage());
        }

    }

    @Test
    public void logoutTest() throws OPException {
        opConfig = registerAndPreLogin();
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        opUserService.logout(null, null);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void changePasswordTest() throws Exception {
        opConfig = registerAndLogin();
        Assert.assertNotNull(opUserService.changePassword("new_password"));
        SecurityContextHolder.getContext().setAuthentication(null);
        try {
            opUserService.changePassword("new_password");
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_PASSWORD_CHANGE, e.getMessage());
        }
    }

    @Test
    public void getPrincipalTest() throws Exception {
        opConfig = registerAndLogin();

        Optional<OPUser> opUser = opUserService.getPrincipal();
        Assert.assertTrue(opUser.isPresent());
        Assert.assertTrue(opUser.map(user -> user.getOpConfig()).isPresent());

        SecurityContextHolder.getContext().setAuthentication(null);
        opUser = opUserService.getPrincipal();
        Assert.assertFalse(opUser.isPresent());
    }

    private RegistrationDTO getRegistrationDTO() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setCompanyName("Company name");
        registrationDTO.setCompanyShortName("Company short name" + new Date().getTime());
        registrationDTO.setEmail("company@mail.com");
        registrationDTO.setFirstName("firstname");
        registrationDTO.setLastName("lastname");
        registrationDTO.setPassword("password");
        return registrationDTO;
    }

    private OPConfig register() throws OPException {
        RegistrationDTO registrationDTO = getRegistrationDTO();
        return opUserService.createOPAdminInformation(registrationDTO);
    }

    private OPConfig registerAndPreLogin() throws OPException {
        OPConfig opConfig = register();
        opConfig.setHost(host);
        opConfigRepository.save(opConfig);
        opUserService.getLoginUri(opConfig.getCompanyShortName(), null);
        return opConfig;
    }

    private OPConfig registerAndLogin() throws Exception {
        OPConfig opConfig = registerAndPreLogin();

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

        opUserService.login(null, null);
        return opConfig;
    }

    private void cleanUp(OPConfig opConfig) throws IOException, JAXBException {
        if (opConfig == null) return;
        ScimResponse scimResponse = scimService.deletePerson(opConfig.getAdminScimId());
        Assert.assertEquals(200, scimResponse.getStatusCode());
        opConfigRepository.delete(opConfig.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private OPUserService unwrapOPUserService() throws Exception {
        if (AopUtils.isAopProxy(opUserService) && opUserService instanceof Advised) {
            Object target = ((Advised) opUserService).getTargetSource().getTarget();
            return (OPUserService) target;
        }
        return null;
    }
}
