package org.gluu.credmgr.web.rest;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.service.MailService;
import org.gluu.credmgr.service.OPUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class OpenidAccountResourceIntTest {

    @Inject
    private OPUserService opUserService;

    @Inject
    private MailService mailService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOpenidAccountConfigMockMvc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpenidAccountResource openidAccountResource = new OpenidAccountResource();
        ReflectionTestUtils.setField(openidAccountResource, "opUserService", opUserService);
        ReflectionTestUtils.setField(openidAccountResource, "mailService", mailService);
        this.restOpenidAccountConfigMockMvc = MockMvcBuilders.standaloneSetup(openidAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    public void registerAccount() {
    }

    @Test
    public void activateAccount() {
    }

    @Test
    public void getLoginUri() {
    }

    @Test
    public void getLogoutUri() {
    }


    @Test
    public void loginRedirectionHandler() {

    }

    @Test
    public void logoutRedirectionHandler() {
    }

    @Test
    public void getAccount() {
    }

    @Test
    public void changePassword() {
    }

    @Test
    public void requestPasswordReset() {

    }

    @Test
    public void finishPasswordReset() {
    }

}
