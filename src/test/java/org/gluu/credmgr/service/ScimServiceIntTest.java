package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.CredmgrApp;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created by eugeniuparvan on 6/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@WebAppConfiguration
@IntegrationTest
public class ScimServiceIntTest {

    @Inject
    private ScimService scimService;

    private String userId;

    @Before
    public void setUp() throws IOException, JAXBException {
        ScimResponse scimResponse = scimService.createPerson(getUser());
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
        userId = user.getId();
    }

    @After
    public void tearDown() throws IOException, JAXBException {
        scimService.deletePerson(userId);
    }

    @Test
    public void retrievePersonTest() throws IOException, JAXBException {
        ScimResponse scimResponse = scimService.retrievePerson(userId);
        Assert.assertEquals(scimResponse.getStatusCode(), 200);
    }

    private User getUser() {
        User user = new User();

        Name name = new Name();
        name.setGivenName("Test");
        name.setFamilyName("User");
        user.setName(name);

        user.setActive(true);

        user.setUserName("test");
        user.setPassword("test");
        user.setDisplayName("Test User");
        user.setNickName("test");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("Test");
        return user;
    }
}
