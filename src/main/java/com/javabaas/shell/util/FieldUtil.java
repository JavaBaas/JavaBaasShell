package com.javabaas.shell.util;

import com.javabaas.shell.entity.JBFieldType;

/**
 * Created by Codi on 16/7/22.
 */
public class FieldUtil {

    public static String getFieldType(int type) {
        String typeString = "";
        switch (type) {
            case JBFieldType.STRING:
                typeString = "<STRING>";
                break;
            case JBFieldType.NUMBER:
                typeString = "<NUMBER>";
                break;
            case JBFieldType.BOOLEAN:
                typeString = "<BOOLEAN>";
                break;
            case JBFieldType.DATE:
                typeString = "<DATE>";
                break;
            case JBFieldType.FILE:
                typeString = "<FILE>";
                break;
            case JBFieldType.OBJECT:
                typeString = "<OBJECT>";
                break;
            case JBFieldType.ARRAY:
                typeString = "<ARRAY>";
                break;
            case JBFieldType.POINTER:
                typeString = "<POINTER>";
                break;
            case JBFieldType.GEOPOINT:
                typeString = "<GEOPOINT>";
                break;
            default:
                break;
        }
        return typeString;
    }

}
