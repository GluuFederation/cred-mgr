package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.CredmgrApp;
import org.gluu.oxtrust.model.scim2.Constants;
import org.gluu.oxtrust.model.scim2.Extension;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;

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

    private User user;

    @Before
    public void setUp() throws Exception {
        cleanUp();
        ScimResponse scimResponse = scimService.createPerson(getUser());
        ObjectMapper objectMapper = new ObjectMapper();
        user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    @Test
    public void retrievePersonTest() throws IOException, JAXBException {
        ScimResponse scimResponse = scimService.retrievePerson(user.getId());
        Assert.assertEquals(200, scimResponse.getStatusCode());
    }

    @Test
    public void updatePersonTest() throws IOException, JAXBException {
        try {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            extensionBuilder.setField("resetKey", "valueOne");
            extensionBuilder.setField("resetDate", new Date());
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
        }
        ScimResponse scimResponse = scimService.updatePerson(user, user.getId());
        Assert.assertEquals(200, scimResponse.getStatusCode());
        scimResponse = scimService.retrievePerson(user.getId());
        Assert.assertEquals(200, scimResponse.getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        Object o = objectMapper.readValue(scimResponse.getResponseBodyString(), Object.class);
        Assert.assertNotNull(o);

        scimResponse = scimService.searchUsers("resetKey", "valueOne");
        Assert.assertEquals(200, scimResponse.getStatusCode());
    }

    @Test
    public void findPersonTest() throws IOException, JAXBException {
        ScimResponse scimResponse = scimService.findOneByUsername("12test");
        Assert.assertEquals(200, scimResponse.getStatusCode());
        scimResponse = scimService.searchUsers("displayName", "Test User");
        Assert.assertEquals(200, scimResponse.getStatusCode());
    }

    private User getUser() {
        User user = new User();

        Name name = new Name();
        name.setGivenName("Test");
        name.setFamilyName("User");
        user.setName(name);

        user.setActive(true);

        user.setUserName("12test");
        user.setPassword("test");
        user.setDisplayName("Test User");
        user.setNickName("test");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("Test");

        return user;
    }

    private void cleanUp() throws Exception {
        ScimResponse scimResponse = null;
        do {
            scimResponse = scimService.findOneByUsername("12test");
            if (scimResponse.getStatusCode() != 200) break;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
            scimService.deletePerson(scimUser.getId());
        } while (scimResponse.getStatusCode() == 200);
    }
}
