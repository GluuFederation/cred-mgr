package org.gluu.credmgr.service;

import gluu.scim.client.ScimResponse;
import gluu.scim2.client.Scim2Client;
import gluu.scim2.client.util.Util;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.repository.OPConfigRepository;
import org.gluu.credmgr.service.error.OPException;
import org.gluu.oxtrust.model.scim2.ListResponse;
import org.gluu.oxtrust.model.scim2.Resource;
import org.gluu.oxtrust.model.scim2.User;
import org.gluu.oxtrust.model.scim2.fido.FidoDevice;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScimService {

    public static final String DOMAIN_SUFFIX = "/identity/seam/resource/restv1";
    public static final String UMA_CONFIGURATION = "/.well-known/uma-configuration";

    @Inject
    private CredmgrProperties credmgrProperties;

    @Inject
    private OPConfigRepository opConfigRepository;

    public User createPerson(User user) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = scimClient.createUser(user, new String[]{});
            if (scimResponse.getStatusCode() == 201) {
                try {
                    return Util.toUser(scimResponse, scimClient.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_CREATE_SCIM_USER);
        }
    }

    public User updatePerson(User user, String uid) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = scimClient.updateUser(user, uid, new String[]{});
            if (scimResponse.getStatusCode() == 200) {
                try {
                    return Util.toUser(scimResponse, scimClient.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_UPDATE_SCIM_USER);
        }
    }

    public User findOneByUsername(String username) throws OPException {
        String filter = "userName eq \"" + username + "\"";
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = search(filter, scimClient);
            if (scimResponse.getStatusCode() == 200) {
                try {
                    ListResponse listResponse = Util.toListResponseUser(scimResponse, scimClient.getUserExtensionSchema());
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

    public List<User> searchUsers(String filter) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = search(filter, scimClient);
            if (scimResponse.getStatusCode() == 200) {
                try {
                    ListResponse listResponse = Util.toListResponseUser(scimResponse, scimClient.getUserExtensionSchema());
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

    public void unregisterFido(String id) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            scimClient.deleteFidoDevice(id);
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_DELETE_FIDO_DEVICE);
        }
    }

    public void updateFido(FidoDevice fidoDevice) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            scimClient.updateFidoDevice(fidoDevice, new String[]{});
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_UPDATE_FIDO_DEVICE);
        }
    }

    public List<FidoDevice> getAllFidoDevices(String userId) throws OPException {
        List<FidoDevice> fidoDevices = new ArrayList<>();
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse response = scimClient.searchFidoDevices(userId, "id pr", 1, 20, "id", "ascending", null);
            ListResponse listResponse = Util.toListResponseFidoDevice(response);
            if (listResponse.getResources().size() > 0) {
                for (Resource resource : listResponse.getResources()) {
                    fidoDevices.add((FidoDevice) resource);
                }
            }
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_RETRIEVE_FIDO_DEVICES);
        }
        return fidoDevices;
    }


    public User retrievePerson(String uid) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = scimClient.retrieveUser(uid, new String[]{});
            if (scimResponse.getStatusCode() == 200) {
                try {
                    return Util.toUser(scimResponse, scimClient.getUserExtensionSchema());
                } catch (Exception e) {
                    throw new OPException(OPException.ERROR_FIND_SCIM_USER);
                }
            }
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    public boolean deletePerson(String uid) throws OPException {
        try {
            Scim2Client scimClient = getScimClient();
            ScimResponse scimResponse = scimClient.deletePerson(uid);
            if (scimResponse.getStatusCode() == 200)
                return true;
            else
                return false;
        } catch (IOException e) {
            throw new OPException(OPException.ERROR_FIND_SCIM_USER);
        }
    }

    private Scim2Client getScimClient() throws OPException {
        OPConfig opConfig = opConfigRepository.get();
        String host = opConfig.getHost();
        String umaAatClientId = opConfig.getUmaAatClientId();
        String jksPath = credmgrProperties.getJksFile();
        String umaAatClientKeyId = opConfig.getUmaAatClientKeyId();
        return Scim2Client.umaInstance(host + DOMAIN_SUFFIX, host + UMA_CONFIGURATION, umaAatClientId, jksPath, "secret", umaAatClientKeyId);
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
