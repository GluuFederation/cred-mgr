package org.gluu.credmgr.service;

import org.gluu.credmgr.service.error.OPException;
import org.springframework.stereotype.Service;
import org.xdi.oxauth.client.*;
import org.xdi.oxauth.model.common.AuthorizationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.register.ApplicationType;

import java.util.Arrays;
import java.util.List;

@Service
public class OxauthService {

    private static final String OPEN_ID_CONFIGURATION = "/.well-known/openid-configuration";

    public OpenIdConfigurationResponse getOpenIdConfiguration(String gluuHost) throws OPException {
        try {
            String openIdConfigurationUrl = gluuHost + OPEN_ID_CONFIGURATION;
            OpenIdConfigurationClient openIdConfigurationClient = new OpenIdConfigurationClient(openIdConfigurationUrl);

            OpenIdConfigurationResponse response = openIdConfigurationClient.execOpenIdConfiguration();
            if (response.getStatus() == 200)
                return response;
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION, e);
        }
        throw new OPException(OPException.ERROR_RETRIEVE_OPEN_ID_CONFIGURATION);
    }

    public ClientInfoResponse getClientInfo(String gluuHost, String accessToken) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        ClientInfoClient clientInfoClient = new ClientInfoClient(openIdConfiguration.getClientInfoEndpoint());

        ClientInfoResponse response = clientInfoClient.execClientInfo(accessToken);
        if (response.getStatus() == 200)
            return response;
        else
            throw new OPException(OPException.ERROR_RETRIEVE_CLIENT_INFO);
    }

    public RegisterResponse registerClient(String gluuHost, ApplicationType applicationType, String clientName,
                                           List<String> redirectUris) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        RegisterRequest request = new RegisterRequest(applicationType, clientName, redirectUris);
        RegisterClient client = new RegisterClient(openIdConfiguration.getRegistrationEndpoint());
        client.setRequest(request);

        RegisterResponse response = client.exec();
        if (response.getStatus() == 200)
            return response;
        else
            throw new OPException(OPException.ERROR_REGISTER_CLIENT);
    }

    public String getAuthorizationUri(String gluuHost, String clientId, List<ResponseType> responseTypes,
                                      List<String> scopes, String redirectUri) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        AuthorizationRequest authorizationRequest = new AuthorizationRequest(responseTypes, clientId, scopes,
            redirectUri, null);
        authorizationRequest.setAcrValues(Arrays.asList(new String[]{"basic"}));
        return openIdConfiguration.getAuthorizationEndpoint() + "?" + authorizationRequest.getQueryString();
    }

    public TokenResponse getToken(String gluuHost, GrantType grantType, String clientId, String clientSecret,
                                  String code, String redirectUri, String scope) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        TokenRequest request = new TokenRequest(grantType);
        request.setAuthUsername(clientId);
        request.setAuthPassword(clientSecret);
        request.setCode(code);
        request.setRedirectUri(redirectUri);
        request.setScope(scope);

        TokenClient client = new TokenClient(openIdConfiguration.getTokenEndpoint());
        client.setRequest(request);

        TokenResponse response = client.exec();
        if (response.getStatus() == 200)
            return response;
        else
            throw new OPException(OPException.ERROR_RETRIEVE_TOKEN);

    }

    public UserInfoResponse getUserInfo(String gluuHost, String accessToken, AuthorizationMethod authorizationMethod) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        UserInfoRequest request = new UserInfoRequest(accessToken);
        request.setAuthorizationMethod(authorizationMethod);

        UserInfoClient client = new UserInfoClient(openIdConfiguration.getUserInfoEndpoint());
        client.setRequest(request);

        UserInfoResponse response = client.exec();
        if (response.getStatus() == 200)
            return response;
        else
            throw new OPException(OPException.ERROR_RETRIEVE_USER_INFO);
    }

    public String getLogoutUri(String gluuHost, String idToken, String logoutRedirectUri) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        EndSessionRequest endSessionRequest = new EndSessionRequest(idToken, logoutRedirectUri, null);
        return openIdConfiguration.getEndSessionEndpoint() + "?" + endSessionRequest.getQueryString();
    }
}
