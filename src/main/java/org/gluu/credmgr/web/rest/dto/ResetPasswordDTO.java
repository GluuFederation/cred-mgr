package org.gluu.credmgr.web.rest.dto;

/**
 * Created by eugeniuparvan on 6/18/16.
 */
public class ResetPasswordDTO {
    private String email;
    private String companyShortName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

}
