package org.gluu.credmgr.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Gluu.
 */
@Entity
@Table(name = "gluu")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Gluu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "host")
    private String host;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "login_redirect_uri")
    private String loginRedirectUri;

    @Column(name = "logout_redirect_uri")
    private String logoutRedirectUri;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getLoginRedirectUri() {
        return loginRedirectUri;
    }

    public void setLoginRedirectUri(String loginRedirectUri) {
        this.loginRedirectUri = loginRedirectUri;
    }

    public String getLogoutRedirectUri() {
        return logoutRedirectUri;
    }

    public void setLogoutRedirectUri(String logoutRedirectUri) {
        this.logoutRedirectUri = logoutRedirectUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Gluu gluu = (Gluu) o;
        if(gluu.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, gluu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Gluu{" +
            "id=" + id +
            ", host='" + host + "'" +
            ", clientId='" + clientId + "'" +
            ", clientSecret='" + clientSecret + "'" +
            ", loginRedirectUri='" + loginRedirectUri + "'" +
            ", logoutRedirectUri='" + logoutRedirectUri + "'" +
            '}';
    }
}
