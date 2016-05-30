package org.gluu.credmgr.service.error;

public class OxauthException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String CAN_NOT_RETRIEVE_OPEN_ID_CONFIGURATION = "Can't retrieve openid configuration";

    public OxauthException(String message) {
	super(message);
    }

    public OxauthException(String message, Throwable cause) {
	super(message, cause);
    }
}