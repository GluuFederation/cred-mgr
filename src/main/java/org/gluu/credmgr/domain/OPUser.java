package org.gluu.credmgr.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public class OPUser implements Serializable {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;
    private static final long serialVersionUID = 1L;
    private String scimId;
    private String login;
    private String host;
    private String idToken;
    private String langKey;
    private Set<OPAuthority> authorities = new HashSet<>();

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
}
