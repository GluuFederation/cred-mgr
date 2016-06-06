package org.gluu.credmgr.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public class OPUser {
    private String login;
    private String firstName;
    private String lastName;
    private String sessionState;
    private String scope;
    private String state;
    private String code;
    private String email;
    private boolean activated = false;
    private String langKey;
    private Set<OPAuthority> authorities = new HashSet<>();
    private OPConfig opConfig;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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
