package org.gluu.credmgr;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.OPUserService;
import org.gluu.credmgr.service.OxauthService;
import org.gluu.credmgr.service.ScimService;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.client.UserInfoResponse;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;

/**
 * Created by eugeniuparvan on 6/13/16.
 */
public abstract class OPCommonTest {

    public abstract OPUserService getOPUserService();

    public abstract OPConfigRepository getOPConfigRepository();

    public abstract ScimService getScimService();

    public abstract String getHost();

    protected OPConfig opConfig;

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

    protected OPConfig registerAndPreLogin() throws OPException {
        OPConfig opConfig = register();
        opConfig.setHost(getHost());
        getOPConfigRepository().save(opConfig);
        getOPUserService().getLoginUri(opConfig.getCompanyShortName(), null);
        return opConfig;
    }

    protected OPConfig registerAndLogin() throws Exception {
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

        getOPUserService().login(null, null);
        return opConfig;
    }

    protected void cleanUp(OPConfig opConfig) throws IOException, JAXBException {
        ScimResponse scimResponse = null;
        do {
            scimResponse = getScimService().searchUsers("mail", "company@mail.com");
            if (scimResponse.getStatusCode() != 200) break;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
            getScimService().deletePerson(scimUser.getId());
        } while (scimResponse.getStatusCode() == 200);
        if (opConfig != null)
            getOPConfigRepository().delete(opConfig.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    protected OPUserService unwrapOPUserService() throws Exception {
        if (AopUtils.isAopProxy(getOPUserService()) && getOPUserService() instanceof Advised) {
            Object target = ((Advised) getOPUserService()).getTargetSource().getTarget();
            return (OPUserService) target;
        }
        return null;
    }
}
