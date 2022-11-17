package life.forever.cf.activtiy;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;


public class SystemBarUtils {

    private static final int UNSTABLE_STATUS = View.SYSTEM_UI_FLAG_FULLSCREEN;
    private static final int UNSTABLE_NAV = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private static final int STABLE_STATUS = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private static final int STABLE_NAV = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private static final int EXPAND_STATUS = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    private static final int EXPAND_NAV = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;


    //设置隐藏StatusBar(点击任意地方会恢复)
    public static void hideUnStableStatusBar(Activity activity){
        //App全屏，隐藏StatusBar
        setFlag(activity,UNSTABLE_STATUS);
    }

    public static void showUnStableStatusBar(Activity activity){
        clearFlag(activity,UNSTABLE_STATUS);
    }

    //隐藏NavigationBar(点击任意地方会恢复)
    public static void hideUnStableNavBar(Activity activity){
        setFlag(activity,UNSTABLE_NAV);
    }

    public static void showUnStableNavBar(Activity activity){
        clearFlag(activity,UNSTABLE_NAV);
    }

    public static void hideStableStatusBar(Activity activity){
        //App全屏，隐藏StatusBar
        setFlag(activity,STABLE_STATUS);
    }

    public static void showStableStatusBar(Activity activity){
        clearFlag(activity,STABLE_STATUS);
    }

    public static void hideStableNavBar(Activity activity){
        //App全屏，隐藏StatusBar
        setFlag(activity,STABLE_NAV);
    }

    public static void showStableNavBar(Activity activity){
        clearFlag(activity,STABLE_NAV);
    }

    /**
     * 视图扩充到StatusBar
     */
    public static void expandStatusBar(Activity activity){
        setFlag(activity, EXPAND_STATUS);
    }

    /**
     * 视图扩充到NavBar
     * @param activity
     */
    public static void expandNavBar(Activity activity){
        setFlag(activity, EXPAND_NAV);
    }

    public static void transparentStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21){
            expandStatusBar(activity);
            activity.getWindow()
                    .setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
        else if (Build.VERSION.SDK_INT >= 19){
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | attrs.flags);
            activity.getWindow().setAttributes(attrs);
        }
    }

    public static void transparentNavBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21){
            expandNavBar(activity);
            //下面这个方法在sdk:21以上才有
            activity.getWindow()
                    .setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }

    public static void setFlag(Activity activity, int flag){
        if (Build.VERSION.SDK_INT >= 19){
            View decorView = activity.getWindow().getDecorView();
            int option = decorView.getSystemUiVisibility() | flag;
            decorView.setSystemUiVisibility(option);
        }
    }

    //取消flag
    public static void clearFlag(Activity activity, int flag){
        if (Build.VERSION.SDK_INT >= 19){
            View decorView = activity.getWindow().getDecorView();
            int option = decorView.getSystemUiVisibility() & (~flag);
            decorView.setSystemUiVisibility(option);
        }
    }

    public static void setToggleFlag(Activity activity, int option){
        if (Build.VERSION.SDK_INT >= 19){
            if (isFlagUsed(activity,option)){
                clearFlag(activity,option);
            }
            else {
                setFlag(activity,option);
            }
        }
    }

    /**
     * @param activity
     * @return flag是否已被使用
     */
    public static boolean isFlagUsed(Activity activity, int flag) {
        int currentFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
        return (currentFlag & flag)
                == flag;
    }
}
