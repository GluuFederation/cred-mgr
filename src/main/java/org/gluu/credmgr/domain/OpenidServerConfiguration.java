package org.gluu.credmgr.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A OpenidServerConfiguration.
 */
@Entity
@Table(name = "openid_server_configuration")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OpenidServerConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = "^https?:.*$")
    @Column(name = "host", nullable = false)
    private String host;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private String clientId;

    @NotNull
    @Column(name = "client_jwks", nullable = false)
    private String clientJWKS;

    @Column(name = "enable_admin_page")
    private Boolean enableAdminPage;

    @Column(name = "authentication_level")
    private Integer authenticationLevel;

    @NotNull
    @Column(name = "required_open_id_scope", nullable = false)
    private String requiredOpenIdScope;

    @NotNull
    @Column(name = "required_claim", nullable = false)
    private String requiredClaim;

    @NotNull
    @Column(name = "required_claim_value", nullable = false)
    private String requiredClaimValue;

    @Column(name = "enable_password_management")
    private Boolean enablePasswordManagement;

    @Column(name = "enable_email_management")
    private Boolean enableEmailManagement;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

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

    public String getClientJWKS() {
        return clientJWKS;
    }

    public void setClientJWKS(String clientJWKS) {
        this.clientJWKS = clientJWKS;
    }

    public Boolean isEnableAdminPage() {
        return enableAdminPage;
    }

    public void setEnableAdminPage(Boolean enableAdminPage) {
        this.enableAdminPage = enableAdminPage;
    }

    public Integer getAuthenticationLevel() {
        return authenticationLevel;
    }

    public void setAuthenticationLevel(Integer authenticationLevel) {
        this.authenticationLevel = authenticationLevel;
    }

    public String getRequiredOpenIdScope() {
        return requiredOpenIdScope;
    }

    public void setRequiredOpenIdScope(String requiredOpenIdScope) {
        this.requiredOpenIdScope = requiredOpenIdScope;
    }

    public String getRequiredClaim() {
        return requiredClaim;
    }

    public void setRequiredClaim(String requiredClaim) {
        this.requiredClaim = requiredClaim;
    }

    public String getRequiredClaimValue() {
        return requiredClaimValue;
    }

    public void setRequiredClaimValue(String requiredClaimValue) {
        this.requiredClaimValue = requiredClaimValue;
    }

    public Boolean isEnablePasswordManagement() {
        return enablePasswordManagement;
    }

    public void setEnablePasswordManagement(Boolean enablePasswordManagement) {
        this.enablePasswordManagement = enablePasswordManagement;
    }

    public Boolean isEnableEmailManagement() {
        return enableEmailManagement;
    }

    public void setEnableEmailManagement(Boolean enableEmailManagement) {
        this.enableEmailManagement = enableEmailManagement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenidServerConfiguration openidServerConfiguration = (OpenidServerConfiguration) o;
        if(openidServerConfiguration.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, openidServerConfiguration.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OpenidServerConfiguration{" +
            "id=" + id +
            ", host='" + host + "'" +
            ", clientId='" + clientId + "'" +
            ", clientJWKS='" + clientJWKS + "'" +
            ", enableAdminPage='" + enableAdminPage + "'" +
            ", authenticationLevel='" + authenticationLevel + "'" +
            ", requiredOpenIdScope='" + requiredOpenIdScope + "'" +
            ", requiredClaim='" + requiredClaim + "'" +
            ", requiredClaimValue='" + requiredClaimValue + "'" +
            ", enablePasswordManagement='" + enablePasswordManagement + "'" +
            ", enableEmailManagement='" + enableEmailManagement + "'" +
            '}';
    }
}
