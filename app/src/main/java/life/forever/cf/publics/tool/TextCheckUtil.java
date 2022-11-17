package life.forever.cf.publics.tool;

import android.text.TextUtils;


public class TextCheckUtil {


    public static boolean isEmpty(String s) {
        s = s.replaceAll(" ", "");
        s = s.replaceAll("　", "");
        s = s.replaceAll("\\n", "");
        return TextUtils.isEmpty(s);
    }

    public static String clearFeed(String s) {
        return s.replaceAll("\\n", "");
    }




    public static String clearBlank(String s) {
        s = s.replaceAll(" ", "");
        s = s.replaceAll("　", "");
        return s;
    }

    public static int length(String s) {
        int length = 0;
        if (!TextUtils.isEmpty(s)) {
            String chinese = "[\u4e00-\u9fa5]";
            for (int i = 0; i < s.length(); i++) {
                length += s.substring(i, i + 1).matches(chinese) ? 2 : 1;
            }
        }
        return length;
    }
}
