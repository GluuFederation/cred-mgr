package org.gluu.credmgr.service.error;

public class OPException extends Exception {

    public static final String ERROR_RETRIEVE_OPEN_ID_CONFIGURATION = "error.retrieveOpenidConfiguration";
    public static final String ERROR_RETRIEVE_CLIENT_INFO = "error.retrieveClientInfo";
    public static final String ERROR_REGISTER_CLIENT = "error.registerClient";
    public static final String ERROR_RETRIEVE_TOKEN = "error.retrieveToken";
    public static final String ERROR_RETRIEVE_USER_INFO = "error.retrieveUserInfo";
    public static final String ERROR_CREATE_SCIM_USER = "error.createScimUser";
    public static final String ERROR_UPDATE_SCIM_USER = "error.updateScimUser";
    public static final String ERROR_FIND_SCIM_USER = "error.findScimUser";
    public static final String ERROR_DELETE_SCIM_USER = "error.deleteScimUser";
    public static final String ERROR_DELETE_FIDO_DEVICE = "error.deleteFIDODevice";
    public static final String ERROR_RETRIEVE_OP_CONFIG = "error.retrieveOPConfig";
    public static final String ERROR_UPDATE_OP_CONFIG = "error.updateOpConfig";
    public static final String ERROR_EMAIL_OR_LOGIN_ALREADY_EXISTS = "error.emailLoginAlreadyExists";
    public static final String ERROR_ACTIVATE_OP_ADMIN = "error.activateOPAdmin";
    public static final String ERROR_RETRIEVE_LOGOUT_URI = "error.retrieveLogoutUri";
    public static final String ERROR_LOGIN = "error.login";
    public static final String ERROR_PASSWORD_CHANGE = "error.passwordChange";
    public static final String ERROR_SEND_SMS = "error.sendSMS";
    private static final long serialVersionUID = 1L;
    public OPException(String message) {
        super(message);
    }

    public OPException(String message, Throwable cause) {
        super(message, cause);
    }
}
