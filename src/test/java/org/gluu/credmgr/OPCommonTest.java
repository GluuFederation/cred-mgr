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
import java.util.List;

/**
 * Created by eugeniuparvan on 6/13/16.
 */
public abstract class OPCommonTest {

    protected OxauthService oxauthServiceOriginal;

    public abstract OPUserService getOPUserService();

    public abstract OPConfigRepository getOPConfigRepository();

    public abstract ScimService getScimService();

    protected OPConfig register() throws OPException {
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
            extensionBuilder.setField(opConfig.getRequiredClaim(), opConfig.getRequiredClaimValue());
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        }
        user.setActive(false);

        getScimService().createPerson(user);

        return opConfig;
    }

    protected OPConfig registerAndPreLoginUser() throws OPException {
        OPConfig opConfig = getOPConfigRepository().get();
        getOPUserService().getLoginUri(null);
        return opConfig;
    }

    protected OPConfig registerAndPreLoginAdmin() throws OPException {
        OPConfig opConfig = getOPConfigRepository().get();
        getOPUserService().getLoginUri(null);
        return opConfig;
    }

    protected OPConfig registerAndLoginUser() throws Exception {
        return loginCommon(registerAndPreLoginUser());
    }

    protected OPConfig registerAndLoginAdmin() throws Exception {
        return loginCommon(registerAndPreLoginAdmin());
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

    private OPConfig loginCommon(OPConfig opConfig) throws Exception {
        //mocking oxauthService
        OxauthService oxauthServiceMock = Mockito.mock(OxauthService.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setIdToken("id_token");
        Mockito.when(oxauthServiceMock.getToken(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(tokenResponse);

        UserInfoResponse userInfoResponse = new UserInfoResponse(200);
//        Map<String, List<String>> claims = new HashMap<>();
//        claims.put("inum", Arrays.asList(opConfig.getAdminScimId()));
//        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        getOPUserService().login(null, null, null, null, null);
        return opConfig;
    }
}
