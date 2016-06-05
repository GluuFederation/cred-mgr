package org.gluu.credmgr.service.error;

public class OPException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String CAN_NOT_RETRIEVE_OPEN_ID_CONFIGURATION = "Can't retrieve openid configuration";
    public static final String CAN_NOT_CREATE_SCIM_USER = "Can't create scim user";
    public static final String CAN_NOT_RETRIEVE_LOGIN_URI = "Can't retrieve login uri";

    public OPException(String message) {
        super(message);
    }

    public OPException(String message, Throwable cause) {
        super(message, cause);
    }
}
