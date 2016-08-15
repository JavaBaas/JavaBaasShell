package com.javabaas.shell.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Staryet on 15/6/15.
 */
public class JBSimpleResult extends HashMap<String, Object> {

    public static int SUCCESS = 0;

    public JBSimpleResult() {
    }

    public int getCode() {
        return (Integer) get("code");
    }

    public void setCode(int code) {
        put("code", code);
    }

    public String getMessage() {
        return (String) get("message");
    }

    public void setMessage(String message) {
        put("message", message);
    }


    public Object getData(String key) {
        if (get("data") != null) {
            return ((Map<String, Object>) get("data")).get(key);
        } else {
            return null;
        }
    }
}
