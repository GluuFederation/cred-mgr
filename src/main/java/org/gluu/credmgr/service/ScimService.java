package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;
import gluu.scim2.client.util.Util;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScimService {

    public static final String DOMAIN_SUFFIX = "/identity/seam/resource/restv1";
    public static final String UMA_CONFIGURATION = "/.well-known/uma-configuration";
    public static final String GLUU_IDP_ORG_JWKS_FILE_NAME = "scim-rp.jks";

    @Inject
    private CredmgrProperties credmgrProperties;

    private Scim2Client gluuIdpOrgScim2Client;

    @PostConstruct
    public void initIt() throws IOException {
        String umaAatClientJwks = getClass().getClassLoader().getResource(GLUU_IDP_ORG_JWKS_FILE_NAME).getFile();
        String host = credmgrProperties.getGluuIdpOrg().getHost();
        String umaAatClientId = credmgrProperties.getGluuIdpOrg().getUmaAatClientId();
        String umaAatClientKeyId = credmgrProperties.getGluuIdpOrg().getUmaAatClientKeyId();

        gluuIdpOrgScim2Client = getScimClient(host, umaAatClientId, umaAatClientJwks, umaAatClientKeyId);
    }

    public User createPerson(User user) throws OPException {
        return createPersonCommon(user, gluuIdpOrgScim2Client);
    }

    public User createPerson(User user, Scim2Client scimClient) throws OPException {
        return createPersonCommon(user, scimClient);
    }

    public User updatePerson(User user, String uid) throws OPException {
        return updatePersonCommon(user, uid, gluuIdpOrgScim2Client);
    }

    public User updatePerson(User user, String uid, Scim2Client scimClient) throws OPException {
        return updatePersonCommon(user, uid, scimClient);
    }

    public User findOneByUsername(String username) throws OPException {
        return findOneByUsernameCommon(username, gluuIdpOrgScim2Client);
    }

    public User findOneByUsername(String username, Scim2Client scimClient) throws OPException {
        return findOneByUsernameCommon(username, scimClient);
    }

    public List<User> searchUsers(String filter) throws OPException {
        return searchUsersCommon(filter, gluuIdpOrgScim2Client);
    }

    public List<User> searchUsers(String filter, Scim2Client scimClient) throws OPException {
        return searchUsersCommon(filter, scimClient);
    }

    public User retrievePerson(String uid) throws OPException {
        return retrievePersonCommon(uid, gluuIdpOrgScim2Client);
    }

    public User retrievePerson(String uid, Scim2Client scimClient) throws OPException {
        return retrievePersonCommon(uid, scimClient);
    }

    public boolean deletePerson(String uid) throws OPException {
        return deletePersonCommon(uid, gluuIdpOrgScim2Client);
    }

    public boolean deletePerson(String uid, Scim2Client scimClient) throws OPException {
        return deletePersonCommon(uid, scimClient);
    }

    public Scim2Client getScimClient() {
        return gluuIdpOrgScim2Client;
    }

    public Scim2Client getScimClient(String host, String umaAatClientId, String umaAatClientJks, String umaAatClientKeyId) {
        return Scim2Client.umaInstance(host + DOMAIN_SUFFIX, host + UMA_CONFIGURATION, umaAatClientId, umaAatClientJks, "secret", umaAatClientKeyId);
    }

    private User createPersonCommon(User user, Scim2Client client) throws OPException {
        try {
            ScimResponse scimResponse = client.createUser(user, new String[]{});
            if (scimResponse.getStatusCode() == 201) {
                try {
                    return Util.toUser(scimResponse, client.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        }
    }

    private User updatePersonCommon(User user, String uid, Scim2Client client) throws OPException {
        try {
            ScimResponse scimResponse = client.updateUser(user, uid, new String[]{});
            if (scimResponse.getStatusCode() == 200) {
                try {
                    return Util.toUser(scimResponse, client.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
        }
    }

    private User findOneByUsernameCommon(String username, Scim2Client client) throws OPException {
        String filter = "userName eq \"" + username + "\"";
        try {
            ScimResponse scimResponse = search(filter, client);
            if (scimResponse.getStatusCode() == 200) {
                try {
                    ListResponse listResponse = Util.toListResponseUser(scimResponse, client.getUserExtensionSchema());
                    return listResponse.getResources().stream()
                        .filter(User.class::isInstance)
                        .findFirst()
                        .map(User.class::cast).orElseThrow(() -> new OPException(OPException.ERROR_FIND_SCIM_USER));
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_FIND_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    private List<User> searchUsersCommon(String filter, Scim2Client client) throws OPException {
        try {
            ScimResponse scimResponse = search(filter, client);
            if (scimResponse.getStatusCode() == 200) {
                try {
                    ListResponse listResponse = Util.toListResponseUser(scimResponse, client.getUserExtensionSchema());
                    return listResponse.getResources().stream()
                        .filter(User.class::isInstance)
                        .map(User.class::cast).collect(Collectors.toList());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_FIND_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    private User retrievePersonCommon(String uid, Scim2Client client) throws OPException {
        try {
            ScimResponse scimResponse = client.retrieveUser(uid, new String[]{});
            if (scimResponse.getStatusCode() == 200) {
                try {
                    return Util.toUser(scimResponse, client.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_FIND_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    private boolean deletePersonCommon(String uid, Scim2Client client) throws OPException {
        try {
            ScimResponse scimResponse = client.deletePerson(uid);
            if (scimResponse.getStatusCode() == 200)
                return true;
            else
                return false;
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    private ScimResponse search(String filter, Scim2Client client) throws IOException {
        int startIndex = 1;
        int count = 1;
        String sortBy = "userName";
        String sortOrder = "ascending";
        String[] attributes = null;
        return client.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes);
    }
}
