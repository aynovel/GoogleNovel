package life.forever.cf.publics.tool;

import java.util.Calendar;

public class DataString {


    public static int currentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private static int mweek;

    public static int StringData() {
        final Calendar c = Calendar.getInstance();
//        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        switch (mWay) {
            case "1"://星期天
                mweek = 7;
                break;
            case "2"://星期一
                mweek = 1;
                break;
            case "3"://星期二
                mweek = 2;
                break;
            case "4"://星期三
                mweek = 3;
                break;
            case "5"://星期四
                mweek = 4;
                break;
            case "6"://星期五
                mweek = 5;
                break;
            case "7"://星期六
                mweek = 6;
                break;
        }
        return mweek;
    }

}
