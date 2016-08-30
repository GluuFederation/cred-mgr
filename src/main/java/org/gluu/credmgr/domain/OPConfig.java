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
    @Column(name = "company_short_name", nullable = false)
    private String companyShortName;

    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated;

    @Column(name = "activation_key")
    private String activationKey;

    @Pattern(regexp = "^https?:\\/\\/[^\\/]*$")
    @Column(name = "host")
    private String host;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "uma_aat_client_id")
    private String umaAatClientId;

    @Column(name = "uma_aat_client_key_id")
    private String umaAatClientKeyId;

    @Column(name = "client_jks")
    private String clientJKS;

    @Column(name = "authentication_level")
    private Integer authenticationLevel;

    @Column(name = "required_open_id_scope")
    private String requiredOpenIdScope;

    @Column(name = "required_claim")
    private String requiredClaim;

    @Column(name = "required_claim_value")
    private String requiredClaimValue;

    @Column(name = "enable_password_management")
    private Boolean enablePasswordManagement = false;

    @Column(name = "enable_admin_page")
    private Boolean enableAdminPage = false;

    @Column(name = "enable_email_management")
    private Boolean enableEmailManagement = false;

    @Column(name = "enable_mobile_management")
    private Boolean enableMobileManagement = false;

    @Column(name = "enable_social_management")
    private Boolean enableSocialManagement = false;

    @Column(name = "enable_u_2_f_management")
    private Boolean enableU2FManagement = false;

    @Column(name = "enable_google_login")
    private Boolean enableGoogleLogin = false;

    @Column(name = "enable_facebook_login")
    private Boolean enableFacebookLogin = false;

    @Column(name = "enable_twitter_login")
    private Boolean enableTwitterLogin = false;

    @Column(name = "enable_linked_in_login")
    private Boolean enableLinkedInLogin = false;

    @Column(name = "enable_windows_live_login")
    private Boolean enableWindowsLiveLogin = false;

    @Column(name = "enable_github_login")
    private Boolean enableGithubLogin = false;

    @Column(name = "enable_dropbox_login")
    private Boolean enableDropboxLogin = false;

    @Column(name = "enable_yahoo_login")
    private Boolean enableYahooLogin = false;

    @Column(name = "smtp_host")
    private String smtpHost;

    @Column(name = "smtp_port")
    private String smtpPort;

    @Column(name = "smtp_username")
    private String smtpUsername;

    @Column(name = "smtp_password")
    private String smtpPassword;

    @Column(name = "smtp_use_ssl")
    private Boolean smtpUseSSL = false;

    @Column(name = "twilio_sid")
    private String twilioSID;

    @Column(name = "twilio_token")
    private String twilioToken;

    @Column(name = "twilio_from_number")
    private String twilioFromNumber;

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

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
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

    public String getUmaAatClientId() {
        return umaAatClientId;
    }

    public void setUmaAatClientId(String umaAatClientId) {
        this.umaAatClientId = umaAatClientId;
    }

    public String getUmaAatClientKeyId() {
        return umaAatClientKeyId;
    }

    public void setUmaAatClientKeyId(String umaAatClientKeyId) {
        this.umaAatClientKeyId = umaAatClientKeyId;
    }

    public String getClientJKS() {
        return clientJKS;
    }

    public void setClientJKS(String clientJKS) {
        this.clientJKS = clientJKS;
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

    public Boolean isEnableMobileManagement() {
        return enableMobileManagement;
    }

    public void setEnableMobileManagement(Boolean enableMobileManagement) {
        this.enableMobileManagement = enableMobileManagement;
    }

    public Boolean isEnableSocialManagement() {
        return enableSocialManagement;
    }

    public void setEnableSocialManagement(Boolean enableSocialManagement) {
        this.enableSocialManagement = enableSocialManagement;
    }

    public Boolean isEnableU2FManagement() {
        return enableU2FManagement;
    }

    public void setEnableU2FManagement(Boolean enableU2FManagement) {
        this.enableU2FManagement = enableU2FManagement;
    }

    public Boolean isEnableGoogleLogin() {
        return enableGoogleLogin;
    }

    public void setEnableGoogleLogin(Boolean enableGoogleLogin) {
        this.enableGoogleLogin = enableGoogleLogin;
    }

    public Boolean isEnableFacebookLogin() {
        return enableFacebookLogin;
    }

    public void setEnableFacebookLogin(Boolean enableFacebookLogin) {
        this.enableFacebookLogin = enableFacebookLogin;
    }

    public Boolean isEnableTwitterLogin() {
        return enableTwitterLogin;
    }

    public void setEnableTwitterLogin(Boolean enableTwitterLogin) {
        this.enableTwitterLogin = enableTwitterLogin;
    }

    public Boolean isEnableLinkedInLogin() {
        return enableLinkedInLogin;
    }

    public void setEnableLinkedInLogin(Boolean enableLinkedInLogin) {
        this.enableLinkedInLogin = enableLinkedInLogin;
    }

    public Boolean isEnableWindowsLiveLogin() {
        return enableWindowsLiveLogin;
    }

    public void setEnableWindowsLiveLogin(Boolean enableWindowsLiveLogin) {
        this.enableWindowsLiveLogin = enableWindowsLiveLogin;
    }

    public Boolean isEnableGithubLogin() {
        return enableGithubLogin;
    }

    public void setEnableGithubLogin(Boolean enableGithubLogin) {
        this.enableGithubLogin = enableGithubLogin;
    }

    public Boolean isEnableDropboxLogin() {
        return enableDropboxLogin;
    }

    public void setEnableDropboxLogin(Boolean enableDropboxLogin) {
        this.enableDropboxLogin = enableDropboxLogin;
    }

    public Boolean isEnableYahooLogin() {
        return enableYahooLogin;
    }

    public void setEnableYahooLogin(Boolean enableYahooLogin) {
        this.enableYahooLogin = enableYahooLogin;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public Boolean isSmtpUseSSL() {
        return smtpUseSSL;
    }

    public void setSmtpUseSSL(Boolean smtpUseSSL) {
        this.smtpUseSSL = smtpUseSSL;
    }

    public String getTwilioSID() {
        return twilioSID;
    }

    public void setTwilioSID(String twilioSID) {
        this.twilioSID = twilioSID;
    }

    public String getTwilioToken() {
        return twilioToken;
    }

    public void setTwilioToken(String twilioToken) {
        this.twilioToken = twilioToken;
    }

    public String getTwilioFromNumber() {
        return twilioFromNumber;
    }

    public void setTwilioFromNumber(String twilioFromNumber) {
        this.twilioFromNumber = twilioFromNumber;
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
            ", adminScimId='" + adminScimId + "'" +
            ", companyName='" + companyName + "'" +
            ", companyShortName='" + companyShortName + "'" +
            ", email='" + email + "'" +
            ", activated='" + activated + "'" +
            ", activationKey='" + activationKey + "'" +
            ", host='" + host + "'" +
            ", clientId='" + clientId + "'" +
            ", clientSecret='" + clientSecret + "'" +
            ", umaAatClientId='" + umaAatClientId + "'" +
            ", umaAatClientKeyId='" + umaAatClientKeyId + "'" +
            ", clientJKS='" + clientJKS + "'" +
            ", authenticationLevel='" + authenticationLevel + "'" +
            ", requiredOpenIdScope='" + requiredOpenIdScope + "'" +
            ", requiredClaim='" + requiredClaim + "'" +
            ", requiredClaimValue='" + requiredClaimValue + "'" +
            ", enablePasswordManagement='" + enablePasswordManagement + "'" +
            ", enableAdminPage='" + enableAdminPage + "'" +
            ", enableEmailManagement='" + enableEmailManagement + "'" +
            ", enableMobileManagement='" + enableMobileManagement + "'" +
            ", enableSocialManagement='" + enableSocialManagement + "'" +
            ", enableU2FManagement='" + enableU2FManagement + "'" +
            ", enableGoogleLogin='" + enableGoogleLogin + "'" +
            ", enableFacebookLogin='" + enableFacebookLogin + "'" +
            ", enableTwitterLogin='" + enableTwitterLogin + "'" +
            ", enableLinkedInLogin='" + enableLinkedInLogin + "'" +
            ", enableWindowsLiveLogin='" + enableWindowsLiveLogin + "'" +
            ", enableGithubLogin='" + enableGithubLogin + "'" +
            ", enableDropboxLogin='" + enableDropboxLogin + "'" +
            ", enableYahooLogin='" + enableYahooLogin + "'" +
            ", smtpHost='" + smtpHost + "'" +
            ", smtpPort='" + smtpPort + "'" +
            ", smtpUsername='" + smtpUsername + "'" +
            ", smtpPassword='" + smtpPassword + "'" +
            ", smtpUseSSL='" + smtpUseSSL + "'" +
            ", twilioSID='" + twilioSID + "'" +
            ", twilioToken='" + twilioToken + "'" +
            ", twilioFromNumber='" + twilioFromNumber + "'" +
            '}';
    }
}
