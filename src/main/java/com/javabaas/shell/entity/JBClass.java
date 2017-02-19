package com.javabaas.shell.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Staryet on 15/8/21.
 */
@JsonIgnoreProperties({"app"})
public class JBClass {

    private String id;
    private String name;
    private Object acl;
    private boolean internal;
    private long count;

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

    public Object getAcl() {
        return acl;
    }

    public void setAcl(Object acl) {
        this.acl = acl;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
