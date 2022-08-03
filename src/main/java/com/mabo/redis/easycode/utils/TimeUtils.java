package com.mabo.redis.easycode.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String formatTime(Date date){
        String format = sdf.format(date);
        return format;
    }
}
