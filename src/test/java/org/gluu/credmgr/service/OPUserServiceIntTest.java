package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.CredmgrApp;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by eugeniuparvan on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CredmgrApp.class)
@IntegrationTest
public class OPUserServiceIntTest {

    @Inject
    private OPUserService opUserService;

    @Inject
    private ScimService scimService;

    @Inject
    private OPConfigRepository opConfigRepository;

    @Test
    public void createOPAdminInformationTest() throws OPException, IOException, JAXBException {
        RegistrationDTO registrationDTO = getRegistrationDTO();

        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);
        Assert.assertNotNull(opConfig);

        //Attempt to create user with existing username(oxTrust returns error status)
        try {
            opUserService.createOPAdminInformation(registrationDTO);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_CREATE_SCIM_USER, e.getMessage());
        }

        //Attempt to create user with existing email(spring returns data integrity violation exception)
        registrationDTO.setCompanyShortName(registrationDTO.getCompanyShortName() + "test");
        try {
            opUserService.createOPAdminInformation(registrationDTO);
        } catch (OPException e) {
            Assert.assertEquals(OPException.ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS, e.getMessage());
        }

        //clean up
        //TODO: remove Scim user with username=(registrationDTO.getCompanyShortName() + "test")
        ScimResponse scimResponse = scimService.deletePerson(opConfig.getAdminScimId());
        Assert.assertEquals(200, scimResponse.getStatusCode());
        opConfigRepository.delete(opConfig.getId());
    }

    @Test
    public void activateOPAdminRegistrationTest() throws OPException, IOException, JAXBException {
        RegistrationDTO registrationDTO = getRegistrationDTO();
        OPConfig opConfig = opUserService.createOPAdminInformation(registrationDTO);

        String activationKey = "activationKey" + new Date().getTime();
        opConfig.setActivationKey(activationKey);
        opConfigRepository.save(opConfig);

        ScimResponse scimResponse = scimService.retrievePerson(opConfig.getAdminScimId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);

        Assert.assertFalse(scimUser.isActive());
        Assert.assertFalse(opConfig.isActivated());

        opUserService.activateOPAdminRegistration(activationKey);

        scimResponse = scimService.retrievePerson(opConfig.getAdminScimId());
        scimUser = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);

        Assert.assertTrue(scimUser.isActive());

        opConfig = opConfigRepository.findOne(opConfig.getId());
        Assert.assertTrue(opConfig.isActivated());

        //clean up
        scimResponse = scimService.deletePerson(opConfig.getAdminScimId());
        Assert.assertEquals(200, scimResponse.getStatusCode());
        opConfigRepository.delete(opConfig.getId());
    }

    @Test
    public void getLoginUriTest() {

    }

    @Test
    public void getLogoutUriTest() {

    }

    @Test
    public void loginTest() {

    }

    @Test
    public void logoutTest() {

    }

    @Test
    public void changePasswordTest() {

    }

    @Test
    public void getPrincipalTest() {

    }

    private RegistrationDTO getRegistrationDTO() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setCompanyName("Company name");
        registrationDTO.setCompanyShortName("Company short name" + new Date().getTime());
        registrationDTO.setEmail("company@mail.com");
        registrationDTO.setFirstName("firstname");
        registrationDTO.setLastName("lastname");
        registrationDTO.setPassword("password");
        return registrationDTO;
    }
}
