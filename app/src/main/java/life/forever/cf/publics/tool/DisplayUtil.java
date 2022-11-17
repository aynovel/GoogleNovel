package life.forever.cf.publics.tool;

import android.content.Context;



public class DisplayUtil {


    public static int dp2px(Context ctx, float dpValue) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context ctx, float spValue) {
        float scale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

}
