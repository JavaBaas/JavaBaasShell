package com.javabaas.shell.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Staryet on 15/8/21.
 */
@JsonIgnoreProperties({"clazz"})
public class JBSField {

    private String id;
    private String name;
    private boolean internal;
    private boolean security;
    private boolean required;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
