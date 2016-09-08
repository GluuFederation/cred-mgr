package org.gluu.credmgr.repository;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.gluu.credmgr.config.CredmgrProperties;
import org.gluu.credmgr.domain.OPConfig;
import org.gluu.credmgr.service.error.OPException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Spring Data JPA repository for the OPConfig entity.
 */
@Service
public class OPConfigRepository {

    @Inject
    private CredmgrProperties credmgrProperties;

    private volatile OPConfig opConfig;

    public OPConfig get() throws OPException {
        if (opConfig == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                opConfig = mapper.readValue(new File(credmgrProperties.getConfigFile()), OPConfig.class);
            } catch (JsonMappingException e) {
                try {
                    opConfig = new OPConfig();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.writeValue(new File(credmgrProperties.getConfigFile()), opConfig);
                } catch (IOException ex) {
                    throw new OPException(OPException.ERROR_RETRIEVE_OP_CONFIG);
                }
            } catch (IOException e) {
                throw new OPException(OPException.ERROR_RETRIEVE_OP_CONFIG);
            }
        }
        return opConfig;
    }

    public synchronized void save(OPConfig config) throws OPException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(credmgrProperties.getConfigFile()), config);
            opConfig = config;
        } catch (Exception e) {
            throw new OPException(OPException.ERROR_UPDATE_OP_CONFIG);
        }
    }
}
