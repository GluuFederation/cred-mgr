package org.gluu.credmgr.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String GLUU_ADMIN = "ROLE_GLUU_ADMIN";
    
    public static final String GLUU_USER = "ROLE_GLUU_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}
