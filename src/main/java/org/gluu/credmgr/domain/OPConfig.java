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

    @Column(name = "inum")
    private String inum;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_short_name")
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

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OPConfig oPConfig = (OPConfig) o;
        if (oPConfig.id == null || id == null) {
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
            ", inum='" + inum + "'" +
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
            '}';
    }
}
