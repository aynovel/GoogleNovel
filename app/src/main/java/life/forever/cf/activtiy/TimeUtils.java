package life.forever.cf.activtiy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static int getCurrentTimestamp(){
        Date currentDate = new Date();
        return getTimestamp(currentDate);
    }

    public static int getTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }


    public static String currentTimeFormat(long date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(new Date(date));
    }


    public static String timeFormat(int seconds, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(new Date(seconds * 1000L));
    }




}
