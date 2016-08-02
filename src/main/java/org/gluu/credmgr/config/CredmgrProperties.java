package org.gluu.credmgr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by eugeniuparvan on 7/27/16.
 */
@ConfigurationProperties(prefix = "credmgr", ignoreUnknownFields = false)
public class CredmgrProperties {

    private final GluuIdpOrg gluuIdpOrg = new GluuIdpOrg();

    private String jksStorePath = "";

    public GluuIdpOrg getGluuIdpOrg() {
        return gluuIdpOrg;
    }

    public String getJksStorePath() {
        return jksStorePath;
    }

    public void setJksStorePath(String jksStorePath) {
        this.jksStorePath = jksStorePath;
    }

    public static class GluuIdpOrg {
        private String host = "";
        private String umaAatClientId = "";
        private String umaAatClientKeyId = "";
        private String companyShortName = "";
        private String requiredOPSuperAdminClaimValue = "";

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
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

        public String getCompanyShortName() {
            return companyShortName;
        }

        public void setCompanyShortName(String companyShortName) {
            this.companyShortName = companyShortName;
        }

        public String getRequiredOPSuperAdminClaimValue() {
            return requiredOPSuperAdminClaimValue;
        }

        public void setRequiredOPSuperAdminClaimValue(String requiredOPSuperAdminClaimValue) {
            this.requiredOPSuperAdminClaimValue = requiredOPSuperAdminClaimValue;
        }
    }
}
