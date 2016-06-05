package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.credmgr.service.util.RandomUtil;
import org.gluu.credmgr.web.rest.dto.RegistrationDTO;
import org.gluu.oxtrust.model.scim2.Email;
import org.gluu.oxtrust.model.scim2.Name;
import org.gluu.oxtrust.model.scim2.Role;
import org.gluu.oxtrust.model.scim2.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xdi.oxauth.model.common.ResponseType;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by eugeniuparvan on 6/3/16.
 */
@Service
@Transactional
public class OPUserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${credmgr.gluuIdpOrg.requiredOPAdminClaimValue}")
    private String roleValue;

    @Inject
    private ScimService scimService;

    @Inject
    private OxauthService oxauthService;

    @Inject
    private OPConfigRepository opConfigRepository;


    public OPConfig createOPAdminInformation(RegistrationDTO registrationDTO) throws IOException, JAXBException, OPException {
        User user = new User();
        user.setUserName(registrationDTO.getCompanyShortName());
        user.setPassword(registrationDTO.getPassword());
        user.setDisplayName(registrationDTO.getCompanyName());

        Name name = new Name();
        name.setGivenName(registrationDTO.getFirstName());
        name.setFamilyName(registrationDTO.getLastName());
        user.setName(name);

        Role role = new Role();
        role.setValue(roleValue);
        user.setRoles(Arrays.asList(new Role[]{role}));

        Email email = new Email();
        email.setType(Email.Type.WORK);
        email.setPrimary(true);
        email.setValue(registrationDTO.getEmail());
        email.setDisplay(registrationDTO.getEmail());
        email.setOperation("CREATE");
        email.setReference("");
        user.setEmails(Arrays.asList(new Email[]{email}));

        user.setActive(false);

        ScimResponse scimResponse = scimService.createPerson(user);
        if (scimResponse.getStatusCode() != 201)
            throw new OPException(OPException.CAN_NOT_CREATE_SCIM_USER);

        ObjectMapper objectMapper = new ObjectMapper();
        user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);

        OPConfig opConfig = new OPConfig();
        opConfig.setInum(user.getId());
        opConfig.setActivated(false);
        opConfig.setEmail(registrationDTO.getEmail());
        opConfig.setActivationKey(RandomUtil.generateActivationKey());
        opConfig.setCompanyName(registrationDTO.getCompanyName());
        opConfig.setCompanyShortName(registrationDTO.getCompanyShortName());
        opConfigRepository.save(opConfig);

        return opConfig;
    }

    public Optional<OPConfig> activateOPAdminRegistration(String key) {
        log.debug("Activating OP Admin configuration for activation key {}", key);
        return opConfigRepository.findOneByActivationKey(key)
            .map(opConfig -> {
                try {
                    ScimResponse scimResponse = scimService.retrievePerson(opConfig.getInum());
                    if (scimResponse.getStatusCode() == 200) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        User user = objectMapper.readValue(scimResponse.getResponseBodyString(), User.class);
                        user.setActive(true);
                        scimResponse = scimService.updatePerson(user, user.getId());
                        if (scimResponse.getStatusCode() != 200)
                            return null;

                        opConfig.setActivated(true);
                        opConfig.setActivationKey(null);
                        opConfigRepository.save(opConfig);
                        log.debug("Activated OP Admin configuration: {}", opConfig);
                        return opConfig;
                    }
                } catch (IOException | JAXBException e) {
                    return null;
                }
                return null;
            });
    }

    public Optional<String> getLoginUri(String companyShortName, String redirectUri) {
        return opConfigRepository.findOneByCompanyShortName(companyShortName).map(opConfig -> {
            String host = opConfig.getHost();
            String clientId = opConfig.getClientId();
            List<ResponseType> responseTypes = Arrays.asList(new ResponseType[]{ResponseType.CODE});
            List<String> scopes = Arrays.asList(new String[]{"openid"});
            try {
                return oxauthService.getAuthorizationUrl(host, clientId, responseTypes, scopes, redirectUri);
            } catch (OPException e) {
                return null;
            }
        });
    }
}
