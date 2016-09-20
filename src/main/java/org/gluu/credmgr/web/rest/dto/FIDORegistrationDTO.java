package org.gluu.credmgr.web.rest.dto;

/**
 * Created by eugeniuparvan on 9/19/16.
 */
public class FIDORegistrationDTO {
    private String appId;
    private String challenge;
    private String version;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
