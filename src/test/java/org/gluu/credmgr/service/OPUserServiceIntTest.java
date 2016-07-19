package org.gluu.credmgr.service;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.OPCommonTest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class OPUserServiceIntTest extends OPCommonTest {

    @Value("${credmgr.gluuIdpOrg.host}")
    private String host;

    @Value("${credmgr.gluuIdpOrg.companyShortName}")
    private String companyShortName;

    @Value("${credmgr.gluuIdpOrg.requiredOPAdminClaimValue}")
    private String adminClaimValue;

    @Value("${credmgr.gluuIdpOrg.requiredOPSuperAdminClaimValue}")
    private String superAdminClaimValue;

    @Inject
    private OPUserService opUserService;

    @Inject
    private ScimService scimService;

    @Inject
    private OPConfigRepository opConfigRepository;


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
    public void createOPAdminInformationTest() throws OPException, IOException, JAXBException {
        RegistrationDTO registrationDTO = getRegistrationDTO();

        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);

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
            opUserService.createOPAdminInformation(registrationDTO).getAdminScimId();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof DataIntegrityViolationException);
        }
        opConfigRepository.delete(opConfig.getId());
    }

    @Test
    public void activateOPAdminRegistrationTest() throws OPException {
        OPConfig opConfig = register();

        String activationKey = "activationKey" + new Date().getTime();
        opConfig.setActivationKey(activationKey);
        opConfigRepository.save(opConfig);

        User scimUser = scimService.retrievePerson(opConfig.getAdminScimId());

        Assert.assertTrue(scimUser.isActive());
        Assert.assertFalse(opConfig.isActivated());

        opUserService.activateOPAdminRegistration(activationKey);

        scimUser = scimService.retrievePerson(opConfig.getAdminScimId());

        Assert.assertTrue(scimUser.isActive());

        opConfig = opConfigRepository.findOne(opConfig.getId());
        Assert.assertTrue(opConfig.isActivated());
    }

    @Test
    public void getLoginUriTest() throws OPException {
        OPConfig opConfig = register();
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
    public void getLogoutUriTest() throws OPException {
        registerAndPreLoginUser();

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
    public void loginUserTest() throws Exception {
        registerAndLoginUser();

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getScimId());
        Assert.assertNotNull(user.getLogin());
        Assert.assertNotNull(user.getHost());
        Assert.assertNotNull(user.getIdToken());

        Assert.assertEquals(1, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_USER));

        Assert.assertNotNull(user.getLoginOpConfigId());
        Assert.assertNull(user.getOpConfigId());
        Assert.assertNull(user.getOpConfig());

        user.setLoginOpConfigId(Long.MAX_VALUE);
        try {
            opUserService.login(null, null, null, null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_LOGIN, e.getMessage());
        }

    }

    @Test
    public void loginAdminTest() throws Exception {
        registerAndLoginAdmin();

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getScimId());
        Assert.assertNotNull(user.getLogin());
        Assert.assertNotNull(user.getHost());
        Assert.assertNotNull(user.getIdToken());

        Assert.assertEquals(1, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_ADMIN));

        Assert.assertNotNull(user.getLoginOpConfigId());
        Assert.assertNotNull(user.getOpConfigId());
        Assert.assertEquals(user.getOpConfigId(), user.getLoginOpConfigId());
        Assert.assertNull(user.getOpConfig());

        user.setLoginOpConfigId(Long.MAX_VALUE);
        try {
            opUserService.login(null, null, null, null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_LOGIN, e.getMessage());
        }

    }

    @Test
    public void loginAdminWithGluuAccountTest() throws Exception {
        registerAndLoginAdminGluuAccount();

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getScimId());
        Assert.assertNotNull(user.getLogin());
        Assert.assertNotNull(user.getHost());
        Assert.assertNotNull(user.getIdToken());

        Assert.assertEquals(1, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_ADMIN));

        Assert.assertNotNull(user.getLoginOpConfigId());
        Assert.assertNotNull(user.getOpConfigId());
        Assert.assertNotEquals(user.getOpConfigId(), user.getLoginOpConfigId());
        Assert.assertNull(user.getOpConfig());

        user.setLoginOpConfigId(Long.MAX_VALUE);
        try {
            opUserService.login(null, null, null, null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_LOGIN, e.getMessage());
        }

    }


    @Test
    public void loginSuperAdminTest() throws Exception {
        registerAndLoginSuperAdmin();

        OPUser user = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getScimId());
        Assert.assertNotNull(user.getLogin());
        Assert.assertNotNull(user.getHost());
        Assert.assertNotNull(user.getIdToken());

        Assert.assertEquals(1, user.getAuthorities().size());
        Assert.assertTrue(user.getAuthorities().contains(OPAuthority.OP_SUPER_ADMIN));

        Assert.assertNotNull(user.getLoginOpConfigId());
        Assert.assertNull(user.getOpConfigId());
        Assert.assertNull(user.getOpConfig());

        user.setLoginOpConfigId(Long.MAX_VALUE);
        try {
            opUserService.login(null, null, null, null);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_LOGIN, e.getMessage());
        }

    }

    @Test
    public void logoutTest() throws OPException {
        registerAndPreLoginUser();
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        opUserService.logout(null, null);
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void changePasswordTest() throws Exception {
        registerAndLoginUser();
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
        registerAndLoginUser();
        Optional<OPUser> opUser = opUserService.getPrincipal();
        Assert.assertTrue(opUser.isPresent());
        Assert.assertTrue(opUser.map(user -> user.getOpConfig()).isPresent());

        SecurityContextHolder.getContext().setAuthentication(null);
        opUser = opUserService.getPrincipal();
        Assert.assertFalse(opUser.isPresent());
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
