package org.gluu.credmgr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by eugeniuparvan on 7/27/16.
 */
@ConfigurationProperties(prefix = "credmgr", ignoreUnknownFields = false)
public class CredmgrProperties {

    private String jksFile = "";

    private String configFile = "";

    public String getJksFile() {
        return jksFile;
    }

    public void setJksFile(String jksFile) {
        this.jksFile = jksFile;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
