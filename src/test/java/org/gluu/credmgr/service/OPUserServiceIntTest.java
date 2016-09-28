package org.gluu.credmgr.service;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.OPCommonTest;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPAuthority;
import org.gluu.credmgr.domain.OPUser;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.KeyAndPasswordDTO;
import org.gluu.credmgr.web.rest.dto.ResetPasswordDTO;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.ExtensionFieldType;
import org.gluu.oxtrust.model.scim2.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class OPUserServiceIntTest extends OPCommonTest {

    @Inject
    private CredmgrProperties credmgrProperties;

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
    public void getLoginUriTest() throws OPException {
        String loginUri = opUserService.getLoginUri(null);
        Assert.assertNotNull(loginUri);

        OPUser principal = (OPUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Assert.assertNotNull(principal);
        Assert.assertNotNull(principal.getHost());
        Assert.assertEquals(1, principal.getAuthorities().size());
        Assert.assertTrue(principal.getAuthorities().contains(OPAuthority.OP_ANONYMOUS));
    }

    @Test
    public void isAccessTokenValid() throws Exception {
        registerAndLoginUser();

        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        Mockito.when(oxauthServiceMock.isTokenValid(Mockito.any(), Mockito.any())).thenReturn(true);
        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        Assert.assertTrue(opUserService.isAccessTokenValid());
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

        try {
            opUserService.login(null, null, null, null, null);
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

        try {
            opUserService.login(null, null, null, null, null);
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
    public void requestPasswordResetWithEmail() throws OPException {
        User user = register(false);
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setEmail("company@mail.com");

        opUserService.requestPasswordResetWithEmail(resetPasswordDTO);
        user = scimService.findOneByUsername(user.getUserName());

        Assert.assertNotNull(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING));
        Assert.assertNotNull(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING));
    }

    @Test
    public void requestPasswordResetWithMobile() throws OPException {
        User user = register(false);
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setMobile("111111");

        opUserService.requestPasswordResetWithMobile(resetPasswordDTO);
        user = scimService.findOneByUsername(user.getUserName());

        Assert.assertNotNull(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetDate", ExtensionFieldType.STRING));
        Assert.assertNotNull(user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING));
    }

    @Test
    public void completePasswordReset() throws OPException {
        User user = register(false);
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setMobile("111111");

        opUserService.requestPasswordResetWithMobile(resetPasswordDTO);
        user = scimService.findOneByUsername(user.getUserName());

        String resetKey = user.getExtension(Constants.USER_EXT_SCHEMA_ID).getField("resetKey", ExtensionFieldType.STRING);

        KeyAndPasswordDTO keyAndPasswordDTO = new KeyAndPasswordDTO();
        keyAndPasswordDTO.setKey(resetKey);
        keyAndPasswordDTO.setNewPassword("new_password");

        opUserService.completePasswordReset(keyAndPasswordDTO);
    }

    @Test
    public void getPrincipalTest() throws Exception {
        registerAndLoginUser();
        Optional<OPUser> opUser = opUserService.getPrincipal();
        Assert.assertTrue(opUser.isPresent());

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
}
