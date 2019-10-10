package com.daoyu.chat.module.system.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    //时间戳转字符串
    public static String getStrTime3(long timeStamp) {
        String timeString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timeString = sdf.format(new Date(timeStamp));//单位秒
        return timeString;
    }
}
