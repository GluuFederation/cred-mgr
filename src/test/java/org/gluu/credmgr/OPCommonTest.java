package org.gluu.credmgr;

import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.OxauthService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.User;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;

import java.util.*;

/**
 * Created by eugeniuparvan on 6/13/16.
 */
public abstract class OPCommonTest {

    public abstract OPUserService getOPUserService();

    public abstract OPConfigRepository getOPConfigRepository();

    public abstract ScimService getScimService();

    public abstract String getHost();

    public abstract String getAdminClaimValue();

    public abstract String getSuperAdminClaimValue();

    public abstract String getCompanyShortName();

    protected OxauthService oxauthServiceOriginal;

    protected RegistrationDTO getRegistrationDTO() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setCompanyName("Company name");
        registrationDTO.setCompanyShortName("Company short name" + new Date().getTime());
        registrationDTO.setEmail("company@mail.com");
        registrationDTO.setFirstName("firstname");
        registrationDTO.setLastName("lastname");
        registrationDTO.setPassword("password");
        return registrationDTO;
    }

    protected OPConfig register() throws OPException {
        RegistrationDTO registrationDTO = getRegistrationDTO();
        OPConfig opConfig = getOPUserService().createOPAdminInformation(registrationDTO);
        return opConfig;
    }

    protected OPConfig registerAndPreLoginUser() throws OPException {
        OPConfig opConfig = register();
        opConfig.setHost(getHost());

        getOPConfigRepository().save(opConfig);
        getOPUserService().getLoginUri(opConfig.getCompanyShortName(), null);
        return opConfig;
    }

    protected OPConfig registerAndPreLoginAdmin() throws OPException {
        OPConfig opConfig = register();
        opConfig.setHost(getHost());
        opConfig.setRequiredClaim("opRole");
        opConfig.setRequiredClaimValue(getAdminClaimValue());
        getOPConfigRepository().save(opConfig);
        getOPUserService().getLoginUri(opConfig.getCompanyShortName(), null);
        return opConfig;
    }

    protected OPConfig registerAndPreLoginSuperAdmin() throws OPException {
        OPConfig opConfig = register();
        opConfig.setHost(getHost());
        opConfig.setRequiredClaim("opRole");
        opConfig.setRequiredClaimValue(getSuperAdminClaimValue());
        User user = getScimService().retrievePerson(opConfig.getAdminScimId());
        user.addExtension(Optional.of(user.getExtensions())
            .map(extensions -> extensions.get(Constants.USER_EXT_SCHEMA_ID))
            .map(extension -> new Extension.Builder(extension))
            .map(eBuilder -> eBuilder.setField("opRole", getSuperAdminClaimValue()).build())
            .orElse(null));
        getScimService().updatePerson(user, user.getId());
        getOPConfigRepository().save(opConfig);
        getOPUserService().getLoginUri(opConfig.getCompanyShortName(), null);
        return opConfig;
    }

    protected OPConfig registerAndLoginUser() throws Exception {
        return loginCommon(registerAndPreLoginUser());
    }

    protected OPConfig registerAndLoginAdmin() throws Exception {
        return loginCommon(registerAndPreLoginAdmin());
    }

    protected OPConfig registerAndLoginAdminGluuAccount() throws Exception {
        OPConfig newConfig = register();
        newConfig.setHost(getHost());
        newConfig.setEmail("company@mail.com");
        newConfig.setRequiredClaim("opRole");
        newConfig.setRequiredClaimValue(getAdminClaimValue());
        getOPConfigRepository().save(newConfig);

        getOPConfigRepository().findOneByCompanyShortName(getCompanyShortName()).map(opConfig -> {
            opConfig.setHost(getHost());
            opConfig.setEmail("gluu@mail.com");
            opConfig.setCompanyShortName(getCompanyShortName());
            opConfig.setRequiredClaim("opRole");
            opConfig.setRequiredClaimValue(getAdminClaimValue());
            return getOPConfigRepository().save(opConfig);
        });
        getOPUserService().getLoginUri(getCompanyShortName(), null);

        return loginCommon(newConfig);
    }

    protected OPConfig registerAndLoginSuperAdmin() throws Exception {
        return loginCommon(registerAndPreLoginSuperAdmin());
    }

    protected void cleanUp() throws OPException {
        cleanScimUsers();
        cleanOPConfigs();
        cleanAuthentications();
    }

    protected void cleanScimUsers() throws OPException {
        List<User> users = getScimService().searchUsers("mail eq \"company@mail.com\"");
        for (User user : users)
            getScimService().deletePerson(user.getId());
    }

    protected void cleanOPConfigs() {
        try {
            getOPConfigRepository().deleteAll();
        } catch (Exception e) {
        }
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
        Map<String, List<String>> claims = new HashMap<>();
        claims.put("inum", Arrays.asList(opConfig.getAdminScimId()));
        userInfoResponse.setClaims(claims);
        Mockito.when(oxauthServiceMock.getUserInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(userInfoResponse);

        ReflectionTestUtils.setField(unwrapOPUserService(), "oxauthService", oxauthServiceMock);

        getOPUserService().login(null, null, null, null);
        return opConfig;
    }
}
