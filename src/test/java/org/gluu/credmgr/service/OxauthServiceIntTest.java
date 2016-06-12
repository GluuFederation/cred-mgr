package org.gluu.credmgr.service;

import org.gluu.credmgr.CredmgrApp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@IntegrationTest
public class OxauthServiceIntTest {

    @Value("${credmgr.gluuIdpOrg.host}")
    private String host;

    @Inject
    private OxauthService oxauthService;

    @Test
    public void getOpenIdConfigurationTest() {
        Optional<OpenIdConfigurationResponse> configuration = oxauthService.getOpenIdConfiguration(host);
        Assert.assertTrue(configuration.isPresent());
    }


    @Test
    public void getAuthorizationUriTest() {
        Optional<String> authorizationUri = oxauthService.getAuthorizationUri(host, null, null, null, null);
        Assert.assertTrue(authorizationUri.isPresent());
    }

    @Test
    public void getLogoutUriTest() {
        Optional<String> logoutUri = oxauthService.getLogoutUri(host, null, null);
        Assert.assertTrue(logoutUri.isPresent());

    }
}
