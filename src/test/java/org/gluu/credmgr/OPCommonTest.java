package org.gluu.credmgr;

import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.OxauthService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.*;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eugeniuparvan on 6/13/16.
 */
public abstract class OPCommonTest {

    protected OxauthService oxauthServiceOriginal;

    public abstract OPUserService getOPUserService();

    public abstract OPConfigRepository getOPConfigRepository();

    public abstract ScimService getScimService();

    protected User register(boolean isAdmin) throws OPException {
        OPConfig opConfig = getOPConfigRepository().get();

        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setCompanyName("Company name");
        registrationDTO.setEmail("company@mail.com");
        registrationDTO.setFirstName("firstname");
        registrationDTO.setLastName("lastname");
        registrationDTO.setPassword("password");

        User user = new User();
        user.setUserName("testUser");
        user.setPassword(registrationDTO.getPassword());
        user.setDisplayName(registrationDTO.getCompanyName());
        user.setNickName("");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("");

        Name name = new Name();
        name.setGivenName(registrationDTO.getFirstName());
        name.setFamilyName(registrationDTO.getLastName());
        user.setName(name);

        Email email = new Email();
        email.setType(Email.Type.WORK);
        email.setPrimary(true);
        email.setValue(registrationDTO.getEmail());
        email.setDisplay(registrationDTO.getEmail());
        email.setOperation("CREATE");
        email.setReference("");
        user.setEmails(Arrays.asList(new Email[]{email}));

        try {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            if (isAdmin)
                extensionBuilder.setField(opConfig.getRequiredClaim(), opConfig.getRequiredClaimValue());
            else
                extensionBuilder.setField(opConfig.getRequiredClaim(), "OP_USER");
            extensionBuilder.setField("resetPhoneNumber", "111111");
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        }
        user.setActive(true);

        User scimUser = getScimService().createPerson(user);

        return scimUser;
    }

    protected User registerAndPreLoginUser() throws OPException {
        User user = register(false);
        getOPUserService().getLoginUri(null);
        return user;
    }

    protected User registerAndPreLoginAdmin() throws OPException {
        User user = register(true);
        getOPUserService().getLoginUri(null);
        return user;
    }

    protected User registerAndLoginUser() throws Exception {
        User user = registerAndPreLoginUser();
        loginCommon(user);
        return user;
    }

    protected User registerAndLoginAdmin() throws Exception {
        User user = registerAndPreLoginAdmin();
        loginCommon(user);
        return user;
    }

    protected void cleanUp() throws OPException {
        cleanScimUsers();
        cleanAuthentications();
    }

    protected void cleanScimUsers() throws OPException {
        List<User> users = getScimService().searchUsers("mail eq \"company@mail.com\"");
        for (User user : users)
            getScimService().deletePerson(user.getId());
    }

    protected void cleanAuthentications() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }


    protected OPUserService unwrapOPUserService() throws Exception {
        if (AopUtils.isAopProxy(getOPUserService()) && getOPUserService() instanceof Advised) {
            Object target = ((Advised) getOPUserService()).getTargetSource().getTarget();
            return (OPUserService) target;
        }
        return getOPUserService();
    }

    private void loginCommon(User scimUser) throws Exception {
        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(scimUser.getId()));
        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        getOPUserService().login(null, null, null, null, null);
    }
}
