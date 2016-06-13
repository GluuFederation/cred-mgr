package org.gluu.credmgr.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;

/**
 * A OPConfig.
 */
@Entity
@Table(name = "op_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OPConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "admin_scim_id", nullable = false)
    private String adminScimId;

    @NotNull
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotNull
    @Column(name = "company_short_name", nullable = false, unique = true)
    private String companyShortName;

    @Pattern(regexp = "^https?:\\/\\/[^\\/]*$")
    @Column(name = "host")
    private String host;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_jwks")
    private String clientJWKS;

    @Column(name = "authentication_level")
    private Integer authenticationLevel;

    @Column(name = "required_open_id_scope")
    private String requiredOpenIdScope;

    @Column(name = "required_claim")
    private String requiredClaim;

    @Column(name = "required_claim_value")
    private String requiredClaimValue;

    @Column(name = "enable_password_management")
    private Boolean enablePasswordManagement;

    @Column(name = "enable_admin_page")
    private Boolean enableAdminPage;

    @Column(name = "enable_email_management")
    private Boolean enableEmailManagement;

    @Column(name = "activation_key")
    private String activationKey;

    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated;

    @Column(name = "client_secret")
    private String clientSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminScimId() {
        return adminScimId;
    }

    public void setAdminScimId(String adminScimId) {
        this.adminScimId = adminScimId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
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

    public Boolean isEnableAdminPage() {
        return enableAdminPage;
    }

    public void setEnableAdminPage(Boolean enableAdminPage) {
        this.enableAdminPage = enableAdminPage;
    }

    public Boolean isEnableEmailManagement() {
        return enableEmailManagement;
    }

    public void setEnableEmailManagement(Boolean enableEmailManagement) {
        this.enableEmailManagement = enableEmailManagement;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OPConfig oPConfig = (OPConfig) o;
        if(oPConfig.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, oPConfig.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OPConfig{" +
            "id=" + id +
            ", adminScimId='" + adminScimId + "'" +
            ", companyName='" + companyName + "'" +
            ", companyShortName='" + companyShortName + "'" +
            ", host='" + host + "'" +
            ", clientId='" + clientId + "'" +
            ", clientJWKS='" + clientJWKS + "'" +
            ", authenticationLevel='" + authenticationLevel + "'" +
            ", requiredOpenIdScope='" + requiredOpenIdScope + "'" +
            ", requiredClaim='" + requiredClaim + "'" +
            ", requiredClaimValue='" + requiredClaimValue + "'" +
            ", enablePasswordManagement='" + enablePasswordManagement + "'" +
            ", enableAdminPage='" + enableAdminPage + "'" +
            ", enableEmailManagement='" + enableEmailManagement + "'" +
            ", activationKey='" + activationKey + "'" +
            ", email='" + email + "'" +
            ", activated='" + activated + "'" +
            ", clientSecret='" + clientSecret + "'" +
            '}';
    }
}
