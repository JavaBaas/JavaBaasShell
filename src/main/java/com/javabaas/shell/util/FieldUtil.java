package com.javabaas.shell.util;

import com.javabaas.shell.entity.JBSFieldType;

/**
 * Created by Codi on 16/7/22.
 */
public class FieldUtil {

    public static String getFieldType(int type) {
        String typeString = "";
        switch (type) {
            case JBSFieldType.STRING:
                typeString = "<STRING>";
                break;
            case JBSFieldType.NUMBER:
                typeString = "<NUMBER>";
                break;
            case JBSFieldType.BOOLEAN:
                typeString = "<BOOLEAN>";
                break;
            case JBSFieldType.DATE:
                typeString = "<DATE>";
                break;
            case JBSFieldType.FILE:
                typeString = "<FILE>";
                break;
            case JBSFieldType.OBJECT:
                typeString = "<OBJECT>";
                break;
            case JBSFieldType.ARRAY:
                typeString = "<ARRAY>";
                break;
            case JBSFieldType.POINTER:
                typeString = "<POINTER>";
                break;
            case JBSFieldType.GEOPOINT:
                typeString = "<GEOPOINT>";
                break;
            default:
                break;
        }
        return typeString;
    }

}
