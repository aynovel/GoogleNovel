package life.forever.cf.activtiy;

import static org.apache.commons.lang3.StringUtils.isBlank;

import android.content.Context;
import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bus {

    private static final int HOUR_OF_DAY = 24;
    private static final int DAY_OF_YESTERDAY = 2;
    private static final int TIME_UNIT = 60;

    public static String dateConvert(long time,String pattern){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String dateConvert(String source,String pattern){
        DateFormat format = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = format.parse(source);
            long curTime = calendar.getTimeInMillis();
            calendar.setTime(date);

            long difSec = Math.abs((curTime - date.getTime())/1000);
            long difMin =  difSec/60;
            long difHour = difMin/60;
            long difDate = difHour/60;
            int oldHour = calendar.get(Calendar.HOUR);

            if (oldHour == 0){
                //比日期:昨天今天和明天
                if (difDate == 0){
                    return "今天";
                }
                else if (difDate < DAY_OF_YESTERDAY){
                    return "昨天";
                }
                else {
                    DateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String value = convertFormat.format(date);
                    return value;
                }
            }

            if (difSec < TIME_UNIT){
                return difSec+"秒前";
            }
            else if (difMin < TIME_UNIT){
                return difMin+"分钟前";
            }
            else if (difHour < HOUR_OF_DAY){
                return difHour+"小时前";
            }
            else if (difDate < DAY_OF_YESTERDAY){
                return "昨天";
            }
            else {
                DateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                String value = convertFormat.format(date);
                return value;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    //将日期转换成昨天、今天、明天
    public static String customDateConvert(int seconds){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(seconds * 1000L);
        long curTime = calendar.getTimeInMillis();
        calendar.setTime(date);
        //将MISC 转换成 sec
        long difSec = Math.abs((curTime - date.getTime())/1000);
        long difMin =  difSec/60;
        long difHour = difMin/60;
        long difDate = difHour/60;
        int oldHour = calendar.get(Calendar.HOUR);
        //如果没有时间
        if (oldHour == 0){
            //比日期:昨天今天和明天
            if (difDate == 0){
                return "今天";
            }
            else if (difDate < DAY_OF_YESTERDAY){
                return "昨天";
            }
            else {
                DateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                String value = convertFormat.format(date);
                return value;
            }
        }

        if (difSec < TIME_UNIT){
            return difSec+"秒前";
        }
        else if (difMin < TIME_UNIT){
            return difMin+"分钟前";
        }
        else if (difHour < HOUR_OF_DAY){
            return difHour+"小时前";
        }
        else if (difDate < DAY_OF_YESTERDAY){
            return "昨天";
        }
        else {
            DateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
            String value = convertFormat.format(date);
            return value;
        }
    }


    public static String toFirstCapital(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }

    public static String getString(@StringRes int id){
        return PlotRead.getContext().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs){
        return PlotRead.getContext().getResources().getString(id,formatArgs);
    }

    /**
     * 将文本中的半角字符，转换成全角字符
     * @param input
     * @return
     */
    public static String halfToFull(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i]> 32 && c[i]< 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    //功能：字符串全角转换为半角
    public static String fullToHalf(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    //繁簡轉換
    public static String convertCC(String input, Context context)
    {
        return input;
//        ConversionType currentConversionType = ConversionType.S2TWP;
//        int convertType = SharedPreUtils.getInstance().getInt("", 0);
//
//        if (input.length() == 0)
//            return "";
//
//        switch (convertType) {
//            case 1:
//                currentConversionType = ConversionType.TW2SP;
//                break;
//            case 2:
//                currentConversionType = ConversionType.S2HK;
//                break;
//            case 3:
//                currentConversionType = ConversionType.S2T;
//                break;
//            case 4:
//                currentConversionType = ConversionType.S2TW;
//                break;
//            case 5:
//                currentConversionType = ConversionType.S2TWP;
//                break;
//            case 6:
//                currentConversionType = ConversionType.T2HK;
//                break;
//            case 7:
//                currentConversionType = ConversionType.T2S;
//                break;
//            case 8:
//                currentConversionType = ConversionType.T2TW;
//                break;
//            case 9:
//                currentConversionType = ConversionType.TW2S;
//                break;
//            case 10:
//                currentConversionType = ConversionType.HK2S;
//                break;
//        }
//
//        return (convertType != 0)? ChineseConverter.convert(input, currentConversionType, context):input;
    }

    /**
     * Base64编码
     */
    public static String Base64Encode(String pattern){
        String strBase64 = Base64.encodeToString(pattern.getBytes(), Base64.DEFAULT);
        return strBase64;
    }

    /**
     * Base64解码
     */
    public static String Base64Decode(String pattern){
        String decodeString = new String(Base64.decode(pattern.getBytes(), Base64.DEFAULT));
        return decodeString;
    }


    /**
     * 获取热度，评分基本单位
     */
    public static  String getDimenStr(){
        return "万";
    }

    //格式化金额
    public static String formatToSepara(String data) {
        try {
            double value = Double.parseDouble(data);
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     *  格式化数字
     */
    public static String formatChinaNum(int number) {
        if (number < 10000) {
            return String.valueOf(number);
        } else if (number < 100000) {
            return String.format(Locale.CHINA, "%.1f万", number / 10000f);
        } else {
            int aInt = (number + 5000) / 10000;
            return aInt + "万";
        }
    }

    /**
     * 将连续空格替换为一个空格
     *
     * @param s
     * @return
     */
    public static String clearExtraBlank(String s) {
        s = s.replaceAll(" ", "　");
        return s.replaceAll("(　)\\1+", "$1");
    }


    public static String formatNumStr(String text)
    {
        for(int i = 0; i < 10; i++)
        {
            text = text.replace((char)('0' + i),"零一二三四五六七八九".charAt(i));
        }
        return text;
    }


    /**
     * 清除空格
     *
     * @param s
     * @return
     */
    public static String clearBlank(String s) {
        s = s.replaceAll(" ", "");
        s = s.replaceAll("　", "");
        return s;
    }

    /**
     * 清除换行符及空格
     *
     * @param s
     * @return
     */
    public static String clearFeedAndBlank(String s) {
        s = s.replaceAll(" ", "");
        s = s.replaceAll("　", "");
        return s.replaceAll("\\n", "");
    }

    /**
     * 匹配用户昵称
     *
     * @param name
     * @return
     */
    public static boolean matchNickName(String name) {
        String regExp = "^[a-zA-Z_0-9\u4e00-\u9fa5]+$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * 验证手机号是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|14[579]|15[0-35-9]|17[0-9]|18[0-9])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     *
     * @param context
     * @param spannable
     * @param allContent    内容
     * @param matchContent  匹配关键内容
     * @param foregroundColor 匹配的文字的颜色
     * @param textSize  > 0 会改变标记文字的大小
     * @return
     */
    public static Spannable addNewSpanable(Context context, Spannable spannable, String allContent, String matchContent, @ColorInt int foregroundColor, int textSize) {
        Pattern pattern = Pattern.compile(Pattern.quote(matchContent));
        Matcher matcher = pattern.matcher(allContent);

        while(matcher.find()) {
            int start = matcher.start();
            if (start >= 0) {
                int end = start + matchContent.length();
                if (textSize > 0) {
                    spannable.setSpan(new AbsoluteSizeSpan(ScreenUtils.spToPx(textSize)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }

                spannable.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
            }
        }

        return spannable;
    }


    public static boolean isSpeechable(String s) {
        if (isBlank(s)) {
            return false;
        }
        //【含有英文】true
        String regex1 = ".*[a-zA-z].*";
        boolean containEng = s.matches(regex1);
        //【含有数字】true
        String regex2 = ".*[0-9].*";
        boolean containNum = s.matches(regex2);
        //判断是否为纯中文，不是返回false
        boolean containChinese = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(s);
        if (m.find()) {
            containChinese = true;
        }
        return containChinese||containEng||containNum;
    }


    public static String replace(String content) {
        content = content.replaceAll("&ldquo;", "");
        content = content.replaceAll("&rdquo;", "");
        content = content.replaceAll("&lsquo;", "");
        content = content.replaceAll("&rsquo;", "");
        content = content.replaceAll("&hellip;", "");
        content = content.replaceAll("&mdash;", "");
        content = content.replaceAll("&quot;", "");
        content = content.replaceAll("&nbsp;", "");
        content = content.replaceAll(" ", "");
        content = content.replaceAll("　", "");
        content = content.replaceAll(" ","");
        content = content.replaceAll("\u00A0", "");
        content = content.replaceAll("\u0020", "");
        content = content.replaceAll("\u3000", "");
        content = content.replaceAll("\f", "");
        content = content.replaceAll("\r", "");
        content = content.replaceAll("\t", "");
        content = content.replaceAll("<br/>", "\n");
        return content.replaceAll("(\\n)\\1+", "$1");
    }
}
