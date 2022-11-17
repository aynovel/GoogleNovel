package life.forever.cf.publics.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {


    public static int currentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }


    public static String currentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return  simpleDateFormat.format(date);
    }

    public static String currentYMDDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HHmmss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return  simpleDateFormat.format(date);
    }

}
