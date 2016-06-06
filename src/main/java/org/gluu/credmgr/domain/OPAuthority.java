package org.gluu.credmgr.domain;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public enum OPAuthority {
    OP_SUPER_ADMIN("OP_SUPER_ADMIN"),
    OP_ADMIN("OP_ADMIN"),
    OP_USER("OP_USER"),
    OP_ANONYMOUS("OP_ANONYMOUS");

    private final String value;

    OPAuthority(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
