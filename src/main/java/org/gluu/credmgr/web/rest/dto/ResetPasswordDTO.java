package org.gluu.credmgr.web.rest.dto;

/**
 * Created by eugeniuparvan on 6/18/16.
 */
public class ResetPasswordDTO {
    private String mobile;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
