package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;
import org.apache.commons.io.IOUtils;
import org.gluu.oxtrust.model.scim2.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.IOException;

@Service
public class ScimService {

    private static final String DOMAIN_SUFFIX = "/identity/seam/resource/restv1";
    private static final String UMA_CONFIGURATION = "/.well-known/uma-configuration";
    private static final String GLUU_IDP_ORG_JWKS_FILE_NAME = "scim-rp-openid-keys.json";

    @Value("${credmgr.gluuIdpOrg.host}")
    private String gluuIdpOrgHost;

    @Value("${credmgr.gluuIdpOrg.umaAatClientId}")
    private String gluuIdpOrgUmaAatClientId;

    @Value("${credmgr.gluuIdpOrg.umaAatClientKeyId}")
    private String gluuIdpOrgUmaAatClientKeyId;

    private Scim2Client gluuIdpOrgScim2Client;

    @PostConstruct
    public void initIt() throws IOException {
        final String umaAatClientJwks = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(GLUU_IDP_ORG_JWKS_FILE_NAME));
        gluuIdpOrgScim2Client = Scim2Client.umaInstance(gluuIdpOrgHost + DOMAIN_SUFFIX, gluuIdpOrgHost + UMA_CONFIGURATION, gluuIdpOrgUmaAatClientId, umaAatClientJwks, gluuIdpOrgUmaAatClientKeyId);
    }

    public ScimResponse createPerson(User user) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.createPerson(user, MediaType.APPLICATION_JSON_VALUE);
    }

    public ScimResponse updatePerson(User user, String uid) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.updatePerson(user, uid, MediaType.APPLICATION_JSON_VALUE);
    }

    public ScimResponse findOneByUsername(String username) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.personSearch("uid", username, MediaType.APPLICATION_JSON_VALUE);
    }

    public ScimResponse searchUsers(String attribute, String value) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.searchPersons(attribute, value, MediaType.APPLICATION_JSON_VALUE);
    }

    public ScimResponse retrievePerson(String uid) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.retrievePerson(uid, MediaType.APPLICATION_JSON_VALUE);
    }

    public ScimResponse deletePerson(String uid) throws IOException, JAXBException {
        return gluuIdpOrgScim2Client.deletePerson(uid);
    }
}
