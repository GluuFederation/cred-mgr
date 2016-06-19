package org.gluu.credmgr.service;

import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.oxtrust.model.scim2.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        user = scimService.createPerson(getUser());
    }

    @After
    public void tearDown() throws Exception {
        cleanUp();
    }

    @Test
    public void retrievePersonTest() throws OPException {
        User scimUser = scimService.retrievePerson(user.getId());
        assertThat(scimUser).isNotNull();
    }

    @Test
    public void updatePersonTest() throws OPException {
        try {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            extensionBuilder.setField("opRole", "valueTwo");
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
        }
        User scimUser = scimService.updatePerson(user, user.getId());
        assertThat(scimUser).isNotNull();

        scimUser = scimService.retrievePerson(scimUser.getId());
        assertThat(scimUser).isNotNull();
    }

    @Test
    public void findPersonTest() throws OPException {
        User scimUser = scimService.findOneByUsername("12test");
        assertThat(scimUser).isNotNull();
        List<User> users = scimService.searchUsers("displayName eq \"Test User\"");
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(1);
    }

    private User getUser() {
        User user = new User();

        Name name = new Name();
        name.setGivenName("Test");
        name.setFamilyName("User");
        user.setName(name);

        user.setActive(true);

        Email email = new Email();
        email.setType(Email.Type.WORK);
        email.setPrimary(true);
        email.setValue("company@mail.com");
        email.setDisplay("company@mail.com");
        email.setOperation("CREATE");
        email.setReference("");
        user.setEmails(Arrays.asList(new Email[]{email}));

        user.setUserName("12test");
        user.setPassword("test");
        user.setDisplayName("Test User");
        user.setNickName("test");
        user.setProfileUrl("");
        user.setLocale("en");
        user.setPreferredLanguage("US_en");
        user.setTitle("Test");
        try {
            Extension.Builder extensionBuilder = new Extension.Builder(Constants.USER_EXT_SCHEMA_ID);
            extensionBuilder.setField("opRole", "valueOne");
            user.addExtension(extensionBuilder.build());
        } catch (Exception e) {
        }
        return user;
    }

    private void cleanUp() throws Exception {
        List<User> users = scimService.searchUsers("mail eq \"company@mail.com\"");
        for (User user : users)
            scimService.deletePerson(user.getId());
    }
}
