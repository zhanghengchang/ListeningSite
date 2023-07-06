package com.boll.audiolib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * created by zoro at 2023/5/15
 */
public class TimeUtil {

    /**
     * long时间差转换为分钟
     *
     * @param ms
     * @return
     */
    public static int diffToMin(long ms) {
        long minutes = (ms / 1000) / 60;
        return (int) minutes;
    }

    public static int getDiffTime(long start, long end) {
        int diff = 0;
        Date startDate = new Date(start);
        Date endDate = new Date(end);
        long yy = endDate.getTime() - startDate.getTime();
        diff = (int) (yy / 1000 / 60);//相差多少分钟
        return diff;
    }

    /**
     * 毫秒转00:00格式
     *
     * @param ms
     * @return
     */
    public static String formatTime(long ms) {
        long minutes = (ms / 1000) / 60;
        long seconds = (ms / 1000) % 60;
        StringBuffer sb = new StringBuffer();
        if (minutes > 0) {
            if (minutes >= 10) {
                sb.append(minutes + ":");
            } else {
                sb.append("0" + minutes + ":");
            }
        } else {
            sb.append("00:");
        }
        if (seconds > 0) {
            if (seconds >= 10) {
                sb.append(seconds);
            } else {
                sb.append("0" + seconds);
            }
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    /**
     * 单句开始、结束时间转毫秒
     *
     * @param time
     * @return
     */
    public static long timeToMilli(String time) {
        long milli = 0;
        String[] split = time.split(",");
        if (split != null && split.length == 2) {
            time = split[0];
            String s = split[1];
            milli = Long.parseLong(s);
        }
        long longMillis = 0;
        String[] split1 = time.split(":");
        if (split1 != null && split1.length == 3) {
            long h = Long.parseLong(split1[0]) * 1000 * 1000 * 1000;
            long m = Long.parseLong(split1[1]) * 1000 * 1000;
            long s = Long.parseLong(split1[2]) * 1000;
            longMillis = h + m + s;
        }
        return longMillis + milli;
    }

}
