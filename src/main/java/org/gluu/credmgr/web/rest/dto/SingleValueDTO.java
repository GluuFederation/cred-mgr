package org.gluu.credmgr.web.rest.dto;

/**
 * Created by eugeniuparvan on 6/5/16.
 */
public class SingleValueDTO {
    private String value;

    public SingleValueDTO() {
    }

    public SingleValueDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
