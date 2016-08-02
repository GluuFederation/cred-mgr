package org.gluu.credmgr.web.rest.dto;

import org.gluu.credmgr.domain.OPConfig;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by eugeniuparvan on 7/20/16.
 */
public class SettingsDTO {

    private OPConfig opConfig;
    private MultipartFile jks;

    public OPConfig getOpConfig() {
        return opConfig;
    }

    public void setOpConfig(OPConfig opConfig) {
        this.opConfig = opConfig;
    }

    public MultipartFile getJks() {
        return jks;
    }

    public void setJks(MultipartFile jks) {
        this.jks = jks;
    }
}
