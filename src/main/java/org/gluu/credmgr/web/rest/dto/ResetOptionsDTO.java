package org.gluu.credmgr.web.rest.dto;

/**
 * Created by eugeniuparvan on 9/8/16.
 */
public class ResetOptionsDTO {
    private boolean email;
    private boolean mobile;

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }
}
