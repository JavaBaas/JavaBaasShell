package com.javabaas.shell.util.table;

import java.io.UnsupportedEncodingException;

/**
 * Created by Codi on 16/7/27.
 */
public class StringUtil {
    public static int getLength(String string) {
        try {
            return string.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
