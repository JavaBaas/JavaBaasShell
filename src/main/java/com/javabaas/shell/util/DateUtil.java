package com.javabaas.shell.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Codi on 16/7/22.
 */
public class DateUtil {

    public static String format(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String format(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

}
