package org.gluu.credmgr.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public class OPUser implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private String scimId;
    private String login;
    private String host;
    private String idToken;
    private String langKey;
    private Set<OPAuthority> authorities = new HashSet<>();
    private Long loginOpConfigId;
    private Long opConfigId;

    //Not stored in session
    private OPConfig opConfig;

    public String getScimId() {
        return scimId;
    }

    public void setScimId(String scimId) {
        this.scimId = scimId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<OPAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<OPAuthority> authorities) {
        this.authorities = authorities;
    }

    public Long getOpConfigId() {
        return opConfigId;
    }

    public void setOpConfigId(Long opConfigId) {
        this.opConfigId = opConfigId;
    }

    public Long getLoginOpConfigId() {
        return loginOpConfigId;
    }

    public void setLoginOpConfigId(Long loginOpConfigId) {
        this.loginOpConfigId = loginOpConfigId;
    }

    public OPConfig getOpConfig() {
        return opConfig;
    }

    public void setOpConfig(OPConfig opConfig) {
        this.opConfig = opConfig;
    }
}
