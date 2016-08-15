package com.javabaas.shell.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Staryet on 15/9/17.
 */
@JsonIgnoreProperties({"cloudSetting", "pushAccount", "userCount", "yesterday", "currentMonth"})
public class JBApp {

    private String id;
    private String name;
    private String key;
    private String masterKey;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

}
