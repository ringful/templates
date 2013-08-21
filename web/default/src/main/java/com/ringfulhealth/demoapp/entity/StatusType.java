package com.ringfulhealth.demoapp.entity;

import java.io.Serializable;

public enum StatusType implements Serializable {

    UNKNOWN ( "UNKNOWN" ),
    USER ( "USER" ),
    ADMIN ( "ADMIN" );

    private String type;

    private StatusType (String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
