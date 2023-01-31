package com.meeting.intelligent.utils;

import java.util.Date;

public class TimeUtil {
    /**
     * 比较d1和d2的时间部分
     */
    public static int compareTime(Date d1, Date d2)
    {
        int     t1;
        int     t2;

        t1 = (int) (d1.getTime() % (24*60*60*1000L));
        t2 = (int) (d2.getTime() % (24*60*60*1000L));
        return (t1 - t2);
    }

    /**
     * 将d1的日期设置为d2的
     */
    public static void setDateTime(Date d1, Date d2) {
        d1.setTime(d1.getTime() - (d1.getTime() % (24*60*60*1000L)) + d2.getTime() % (24*60*60*1000L));
    }
}
