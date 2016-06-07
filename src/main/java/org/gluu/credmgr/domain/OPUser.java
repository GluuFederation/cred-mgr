package org.gluu.credmgr.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public class OPUser {
    private String login;
    private String host;
    private String idToken;
    private String email;
    private String langKey;
    private Set<OPAuthority> authorities = new HashSet<>();
    private OPConfig opConfig;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public OPConfig getOpConfig() {
        return opConfig;
    }

    public void setOpConfig(OPConfig opConfig) {
        this.opConfig = opConfig;
    }
}
