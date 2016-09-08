package org.gluu.credmgr.domain;

import java.io.Serializable;

/**
 * A OPConfig.
 */
public class OPConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private volatile String host;

    private volatile String clientId;

    private volatile String clientSecret;

    private volatile String umaAatClientId;

    private volatile String umaAatClientKeyId;

    private volatile String clientJKS;

    private volatile Integer authenticationLevel;

    private volatile String requiredOpenIdScope;

    private volatile String requiredClaim;

    private volatile String requiredClaimValue;

    private volatile Boolean enablePasswordManagement = false;

    private volatile Boolean enableAdminPage = false;

    private volatile Boolean enableEmailManagement = false;

    private volatile Boolean enableMobileManagement = false;

    private volatile Boolean enableSocialManagement = false;

    private volatile Boolean enableU2FManagement = false;

    private volatile Boolean enableGoogleLogin = false;

    private volatile Boolean enableFacebookLogin = false;

    private volatile Boolean enableTwitterLogin = false;

    private volatile Boolean enableLinkedInLogin = false;

    private volatile Boolean enableWindowsLiveLogin = false;

    private volatile Boolean enableGithubLogin = false;

    private volatile Boolean enableDropboxLogin = false;

    private volatile Boolean enableYahooLogin = false;

    private volatile String smtpHost;

    private volatile String smtpPort;

    private volatile String smtpUsername;

    private volatile String smtpPassword;

    private volatile Boolean smtpUseSSL = false;

    private volatile String twilioSID;

    private volatile String twilioToken;

    private volatile String twilioFromNumber;

    public String getHost() {
        return host;
    }

    public synchronized void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public synchronized void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public synchronized void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUmaAatClientId() {
        return umaAatClientId;
    }

    public synchronized void setUmaAatClientId(String umaAatClientId) {
        this.umaAatClientId = umaAatClientId;
    }

    public String getUmaAatClientKeyId() {
        return umaAatClientKeyId;
    }

    public synchronized void setUmaAatClientKeyId(String umaAatClientKeyId) {
        this.umaAatClientKeyId = umaAatClientKeyId;
    }

    public String getClientJKS() {
        return clientJKS;
    }

    public synchronized void setClientJKS(String clientJKS) {
        this.clientJKS = clientJKS;
    }

    public Integer getAuthenticationLevel() {
        return authenticationLevel;
    }

    public synchronized void setAuthenticationLevel(Integer authenticationLevel) {
        this.authenticationLevel = authenticationLevel;
    }

    public String getRequiredOpenIdScope() {
        return requiredOpenIdScope;
    }

    public synchronized void setRequiredOpenIdScope(String requiredOpenIdScope) {
        this.requiredOpenIdScope = requiredOpenIdScope;
    }

    public String getRequiredClaim() {
        return requiredClaim;
    }

    public synchronized void setRequiredClaim(String requiredClaim) {
        this.requiredClaim = requiredClaim;
    }

    public String getRequiredClaimValue() {
        return requiredClaimValue;
    }

    public synchronized void setRequiredClaimValue(String requiredClaimValue) {
        this.requiredClaimValue = requiredClaimValue;
    }

    public Boolean isEnablePasswordManagement() {
        return enablePasswordManagement;
    }

    public synchronized void setEnablePasswordManagement(Boolean enablePasswordManagement) {
        this.enablePasswordManagement = enablePasswordManagement;
    }

    public Boolean isEnableAdminPage() {
        return enableAdminPage;
    }

    public synchronized void setEnableAdminPage(Boolean enableAdminPage) {
        this.enableAdminPage = enableAdminPage;
    }

    public Boolean isEnableEmailManagement() {
        return enableEmailManagement;
    }

    public synchronized void setEnableEmailManagement(Boolean enableEmailManagement) {
        this.enableEmailManagement = enableEmailManagement;
    }

    public Boolean isEnableMobileManagement() {
        return enableMobileManagement;
    }

    public synchronized void setEnableMobileManagement(Boolean enableMobileManagement) {
        this.enableMobileManagement = enableMobileManagement;
    }

    public Boolean isEnableSocialManagement() {
        return enableSocialManagement;
    }

    public synchronized void setEnableSocialManagement(Boolean enableSocialManagement) {
        this.enableSocialManagement = enableSocialManagement;
    }

    public Boolean isEnableU2FManagement() {
        return enableU2FManagement;
    }

    public synchronized void setEnableU2FManagement(Boolean enableU2FManagement) {
        this.enableU2FManagement = enableU2FManagement;
    }

    public Boolean isEnableGoogleLogin() {
        return enableGoogleLogin;
    }

    public synchronized void setEnableGoogleLogin(Boolean enableGoogleLogin) {
        this.enableGoogleLogin = enableGoogleLogin;
    }

    public Boolean isEnableFacebookLogin() {
        return enableFacebookLogin;
    }

    public synchronized void setEnableFacebookLogin(Boolean enableFacebookLogin) {
        this.enableFacebookLogin = enableFacebookLogin;
    }

    public Boolean isEnableTwitterLogin() {
        return enableTwitterLogin;
    }

    public synchronized void setEnableTwitterLogin(Boolean enableTwitterLogin) {
        this.enableTwitterLogin = enableTwitterLogin;
    }

    public Boolean isEnableLinkedInLogin() {
        return enableLinkedInLogin;
    }

    public synchronized void setEnableLinkedInLogin(Boolean enableLinkedInLogin) {
        this.enableLinkedInLogin = enableLinkedInLogin;
    }

    public Boolean isEnableWindowsLiveLogin() {
        return enableWindowsLiveLogin;
    }

    public synchronized void setEnableWindowsLiveLogin(Boolean enableWindowsLiveLogin) {
        this.enableWindowsLiveLogin = enableWindowsLiveLogin;
    }

    public Boolean isEnableGithubLogin() {
        return enableGithubLogin;
    }

    public synchronized void setEnableGithubLogin(Boolean enableGithubLogin) {
        this.enableGithubLogin = enableGithubLogin;
    }

    public Boolean isEnableDropboxLogin() {
        return enableDropboxLogin;
    }

    public synchronized void setEnableDropboxLogin(Boolean enableDropboxLogin) {
        this.enableDropboxLogin = enableDropboxLogin;
    }

    public Boolean isEnableYahooLogin() {
        return enableYahooLogin;
    }

    public synchronized void setEnableYahooLogin(Boolean enableYahooLogin) {
        this.enableYahooLogin = enableYahooLogin;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public synchronized void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public synchronized void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public synchronized void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public synchronized void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public Boolean isSmtpUseSSL() {
        return smtpUseSSL;
    }

    public synchronized void setSmtpUseSSL(Boolean smtpUseSSL) {
        this.smtpUseSSL = smtpUseSSL;
    }

    public String getTwilioSID() {
        return twilioSID;
    }

    public synchronized void setTwilioSID(String twilioSID) {
        this.twilioSID = twilioSID;
    }

    public String getTwilioToken() {
        return twilioToken;
    }

    public synchronized void setTwilioToken(String twilioToken) {
        this.twilioToken = twilioToken;
    }

    public String getTwilioFromNumber() {
        return twilioFromNumber;
    }

    public synchronized void setTwilioFromNumber(String twilioFromNumber) {
        this.twilioFromNumber = twilioFromNumber;
    }

    @Override
    public String toString() {
        return "OPConfig{" +
            "host='" + host + '\'' +
            ", clientId='" + clientId + '\'' +
            ", clientSecret='" + clientSecret + '\'' +
            ", umaAatClientId='" + umaAatClientId + '\'' +
            ", umaAatClientKeyId='" + umaAatClientKeyId + '\'' +
            ", clientJKS='" + clientJKS + '\'' +
            ", authenticationLevel=" + authenticationLevel +
            ", requiredOpenIdScope='" + requiredOpenIdScope + '\'' +
            ", requiredClaim='" + requiredClaim + '\'' +
            ", requiredClaimValue='" + requiredClaimValue + '\'' +
            ", enablePasswordManagement=" + enablePasswordManagement +
            ", enableAdminPage=" + enableAdminPage +
            ", enableEmailManagement=" + enableEmailManagement +
            ", enableMobileManagement=" + enableMobileManagement +
            ", enableSocialManagement=" + enableSocialManagement +
            ", enableU2FManagement=" + enableU2FManagement +
            ", enableGoogleLogin=" + enableGoogleLogin +
            ", enableFacebookLogin=" + enableFacebookLogin +
            ", enableTwitterLogin=" + enableTwitterLogin +
            ", enableLinkedInLogin=" + enableLinkedInLogin +
            ", enableWindowsLiveLogin=" + enableWindowsLiveLogin +
            ", enableGithubLogin=" + enableGithubLogin +
            ", enableDropboxLogin=" + enableDropboxLogin +
            ", enableYahooLogin=" + enableYahooLogin +
            ", smtpHost='" + smtpHost + '\'' +
            ", smtpPort='" + smtpPort + '\'' +
            ", smtpUsername='" + smtpUsername + '\'' +
            ", smtpPassword='" + smtpPassword + '\'' +
            ", smtpUseSSL=" + smtpUseSSL +
            ", twilioSID='" + twilioSID + '\'' +
            ", twilioToken='" + twilioToken + '\'' +
            ", twilioFromNumber='" + twilioFromNumber + '\'' +
            '}';
    }
}
