package life.forever.cf.publics.tool;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ComYou implements Constant {


    public static void openKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void closeKeyboard(EditText editText, Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), ZERO);
    }

    public static boolean hasLower(String json) {
        if (json == null || "".equals(json)) {
            return false;
        }
        return json.contains("{") && json.contains("}");
    }


    public static int currentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }


    public static String currentTimeFormat(String formatter) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
        return simpleDateFormat.format(new Date());
    }


    public static String timeFormat(int seconds, String formatter) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter, Locale.ENGLISH);
        return simpleDateFormat.format(new Date(seconds * 1000L));
    }


    public static String formatNum(int number) {
        int integer;
        int little;

        integer = number / 1000000;
        if (integer > ZERO) { // 百万、千万
            little = number % 1000000;
            if (little > ZERO) {
                if (little < 10000) {
                    return String.format("%d.0m", integer);
                } else if (little < 100000) {
                    return String.format("%d.0m", integer);
                } else {
                    little = little / 100000;
                    return String.format("%d.%d m", integer, little);
                }

            } else {
                return String.format("%d m", integer);
            }
//            integer = number / 10000;
//            return String.format("%d.%d m", integer, little);
//            return String.format("%dm", integer);
        }
        integer = number / 1000;
        if (integer > ZERO) { // 万、十万
            little = number % 1000;
            if (little > ZERO) {
                if (little < 10) {
                    return String.format("%d.0k", integer);
                } else if (little < 100) {
                    return String.format("%d.0k", integer);
                } else {
                    little = little / 100;
                    return String.format("%d.%d k", integer, little);
                }

            } else {
                return String.format("%d k", integer);
            }
        }
        return String.valueOf(number);
    }


    public static String formatTime(int seconds) {
        int temp = currentTimeSeconds() - seconds;
        if (temp < SIXTY) {
            return application.getString(R.string.just);
        }
        temp = temp / SIXTY;
        if (temp < SIXTY) { // 不足1小时
            return temp + application.getString(R.string.minutes_ago);
        }
        temp = temp / SIXTY;
        if (temp < TWENTY_FOUR) { // 不足1天
            return temp + application.getString(R.string.hours_before);
        }
        temp = temp / TWENTY_FOUR;
        if (temp < THIRTY) { // 不足1个月
            return temp + application.getString(R.string.day_ago);
        }
        temp = temp / THIRTY;
        if (temp < SEVEN) { // 不足7个月
            return temp + application.getString(R.string.money_ago);
        }
        return timeFormat(seconds, DATE_FORMATTER_5);
    }

    /**
     * 设置透明度
     *
     * @param alpha
     */
    public static void setWindowAlpha(Activity activity, float alpha) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = alpha;
        window.setAttributes(params);
    }

    /**
     * 获取一个随机不重复数组
     *
     * @param length
     */
    public static int[] getRandomArray(int length) {
        int[] result = new int[length];

        int[] id = new int[length];
        for (int i = 0; i < length; i++) {
            id[i] = i + 1;
        }
        int last = length - 1;
        Random r = new Random();
        int temp;
        for (int i = 0; i < length - 1; i++) {
            temp = Math.abs(r.nextInt() % last);
            result[i] = id[temp];
            id[temp] = id[last];
            id[last] = result[i];
            last--;
        }
        result[length - 1] = id[0];

        return result;
    }
    /**
     * 判断Activity是否Destroy
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed());
    }

    public static double division(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 网络连接判断
    public static boolean netWorkCheck(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        } else {
            return false;
        }
    }

}
