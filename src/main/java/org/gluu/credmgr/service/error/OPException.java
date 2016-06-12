package org.gluu.credmgr.service.error;

public class OPException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String ERROR_RETRIEVE_OPEN_ID_CONFIGURATION = "error.retrieveOpenidConfiguration";
    public static final String ERROR_CREATE_SCIM_USER = "error.createScimUser";
    public static final String ERROR_RETRIEVE_LOGIN_URI = "error.retrieveLoginUri";
    public static final String ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS = "error.emailLoginAlreadyExists";
    public static final String ERROR_ACTIVATE_OP_ADMIN = "error.activateOPAdmin";

    public OPException(String message) {
        super(message);
    }

    public OPException(String message, Throwable cause) {
        super(message, cause);
    }
}
