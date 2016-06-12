package org.gluu.credmgr.service;

import org.springframework.stereotype.Service;
import org.xdi.oxauth.client.*;
import org.xdi.oxauth.model.common.AuthorizationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.register.ApplicationType;

import java.util.List;
import java.util.Optional;

@Service
public class OxauthService {

    private static final String OPEN_ID_CONFIGURATION = "/.well-known/openid-configuration";

    public Optional<OpenIdConfigurationResponse> getOpenIdConfiguration(String gluuHost) {
        try {
            String openIdConfigurationUrl = gluuHost + OPEN_ID_CONFIGURATION;
            OpenIdConfigurationClient openIdConfigurationClient = new OpenIdConfigurationClient(openIdConfigurationUrl);

            OpenIdConfigurationResponse response = openIdConfigurationClient.execOpenIdConfiguration();
            if (response.getStatus() == 200)
                return Optional.of(response);
            else
                return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<ClientInfoResponse> getClientInfo(String gluuHost, String accessToken) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
            ClientInfoClient clientInfoClient = new ClientInfoClient(openIdConfiguration.getClientInfoEndpoint());

            ClientInfoResponse response = clientInfoClient.execClientInfo(accessToken);
            if (response.getStatus() == 200)
                return response;
            else
                return null;
        });
    }

    public Optional<RegisterResponse> registerClient(String gluuHost, ApplicationType applicationType, String clientName,
                                                     List<String> redirectUris) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
            RegisterRequest request = new RegisterRequest(applicationType, clientName, redirectUris);
            RegisterClient client = new RegisterClient(openIdConfiguration.getRegistrationEndpoint());
            client.setRequest(request);

            RegisterResponse response = client.exec();
            if (response.getStatus() == 200)
                return response;
            else
                return null;
        });
    }

    public Optional<String> getAuthorizationUri(String gluuHost, String clientId, List<ResponseType> responseTypes,
                                                List<String> scopes, String redirectUri) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
            AuthorizationRequest authorizationRequest = new AuthorizationRequest(responseTypes, clientId, scopes,
                redirectUri, null);
            return openIdConfiguration.getAuthorizationEndpoint() + "?" + authorizationRequest.getQueryString();
        });

    }

    public Optional<TokenResponse> getToken(String gluuHost, GrantType grantType, String clientId, String clientSecret,
                                            String code, String redirectUri, String scope) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
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
                return null;
        });

    }

    public Optional<UserInfoResponse> getUserInfo(String gluuHost, String accessToken, AuthorizationMethod authorizationMethod) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
            UserInfoRequest request = new UserInfoRequest(accessToken);
            request.setAuthorizationMethod(authorizationMethod);

            UserInfoClient client = new UserInfoClient(openIdConfiguration.getUserInfoEndpoint());
            client.setRequest(request);

            UserInfoResponse response = client.exec();
            if (response.getStatus() == 200)
                return response;
            else
                return null;
        });

    }

    public Optional<String> getLogoutUri(String gluuHost, String idToken, String logoutRedirectUri) {
        return getOpenIdConfiguration(gluuHost).map(openIdConfiguration -> {
            EndSessionRequest endSessionRequest = new EndSessionRequest(idToken, logoutRedirectUri, null);
            return openIdConfiguration.getEndSessionEndpoint() + "?" + endSessionRequest.getQueryString();
        });
    }
}
