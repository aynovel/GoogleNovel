package life.forever.cf.activtiy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import java.lang.reflect.Method;



public class ScreenUtils {

    public static int dpToPx(int dp){
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,metrics);
    }

    public static int pxToDp(int px){
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (px / metrics.density);
    }

    public static int spToPx(int sp){
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,metrics);
    }

    public static int pxToSp(int px){
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (px / metrics.scaledDensity);
    }

    /**
     * 获取手机显示App区域的大小（头部导航栏+ActionBar+根布局），不包括虚拟按钮
     * @return
     */
    public static int[] getAppSize(){
        int[] size = new int[2];
        DisplayMetrics metrics = getDisplayMetrics();
        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }

    /**
     * 获取整个手机屏幕的大小(包括虚拟按钮)
     * 必须在onWindowFocus方法之后使用
     * @param activity
     * @return
     */
    public static int[] getScreenSize(AppCompatActivity activity){
        int[] size = new int[2];
        View decorView = activity.getWindow().getDecorView();
        size[0] = decorView.getWidth();
        size[1] = decorView.getHeight();
        return size;
    }

    /**
     * 获取导航栏的高度
     * @return
     */
    public static int getStatusBarHeight(){
        Resources resources = PlotRead.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height","dimen","android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取虚拟按键的高度
     * @return
     */
    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        Resources rs = PlotRead.getContext().getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && hasNavigationBar()) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 是否存在虚拟按键
     * @return
     */
    private static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = PlotRead.getContext().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static DisplayMetrics getDisplayMetrics(){
        DisplayMetrics metrics = PlotRead
                .getContext()
                .getResources()
                .getDisplayMetrics();
        return metrics;
    }

    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public static void changeAppBrightness(Activity activity, int brightness) {

        int maxBright = getBrightnessMax();

        LogUtils.d("maxBright ======== " + maxBright);

//        if(DeviceBrandTools.getInstance().isMiui())
//        {
////            changeBrightness(activity,brightness);
//            setBrightness(activity,brightness);
//        }else{
//            Window window = activity.getWindow();
//            WindowManager.LayoutParams lp = window.getAttributes();
//            if (brightness == -1) {
//                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
//            } else {
//                lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
//            }
//            window.setAttributes(lp);
//        }

    }

    /**
     * 设置亮度:通过设置 Windows 的 screenBrightness 来修改当前 Windows 的亮度
     * lp.screenBrightness:参数范围为 0~1
     */
    public static void setBrightness(Activity activity, int brightness) {
        try{
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            //将 0~255 范围内的数据，转换为 0~1
            lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
            activity.getWindow().setAttributes(lp);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static public void changeBrightness(Activity activity,  float change) {
        Window window = activity.getWindow();
        float old = window.getAttributes().screenBrightness;
        float none = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; // -1.0f
        if (old == none) {
            // 取到了默认值
            try {
                int current = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                if (current <= getBrightnessMax()) {
                    old = (current * 1f) / getBrightnessMax();
                }
            } catch (Exception ignore) {
            }
        }
        if (old == none|| old <= 0) {
            // 如果没有取值成功，那么就默认设置为一半亮度，防止突然变得很亮或很暗
            old = 0.5f;
        }

        float newBrightness = MathUtils.clamp(old + change, 0.01f, 1f);
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = newBrightness;
        window.setAttributes(params);
    }

    /**
     * 获取最大亮度
     * @return max
     */
    static public int getBrightnessMax() {
        try {
            Resources system = Resources.getSystem();
            int resId = system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
            if (resId != 0) {
                return system.getInteger(resId);
            }
        }catch (Exception ignore){}
        return 255;
    }

    /**
     * 获取屏幕宽度
     * @return 像素
     */
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) PlotRead.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @return 像素
     */
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) PlotRead.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }




    /**
     * 隐藏actionbar
     * @param activity
     */
    public static void hideActionbar(AppCompatActivity activity) {
        androidx.appcompat.app.ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 设置navigationBar的背景颜色为透明
     * @param activity
     */
    public static void setNavigationBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 退出全屏
     * @param activity
     */
    public static void exitFullScreen(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 进入全屏
     * @param activity
     */
    public static void enterFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置StatusBar的显示和隐藏
     * @param activity
     * @param show
     */
    public static void setStatusBarVisible(Activity activity, boolean show) {
        if (show) {
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 设置NavigationBar的显示和隐藏
     * @param activity
     * @param show
     */
    public static void setNavigationBarVisible(Activity activity, boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * 同时显示和隐藏M01中控
     * StatusBar 和 NavigationBar
     * @param activity this
     * @param show true:显示
     *             false:隐藏
     */
    public static void setSystemBarsVisible(Activity activity, boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**
     * 应用的页面占满全屏，同时StatusBar显示
     * @param activity activity
     * @param show true:全屏 false:预留Statusbar的空间
     */
    public static void setPagFullScreen(Activity activity, boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    public static boolean isPointInRectFrame(int x, int y, Rect frame){
        return x>=frame.left && (x<=frame.right) && y>=frame.top && (y <= frame.bottom);
    }

//
//    private void changeBrightness(Activity activity, float change) {
//        float old = activity.getWindow().getAttributes().screenBrightness;
//        float none = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE; // -1.0f
//        if (old == none) {
//            // 取到了默认值
//            try {
//                int current = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                if (current <= getBrightnessMax()) {
//                    old = (current * 1f) / getBrightnessMax();
//                }
//            } catch (Exception ignore) {
//            }
//        }
//        if (old == none|| old <= 0) {
//            // 如果没有取值成功，那么就默认设置为一半亮度，防止突然变得很亮或很暗
//            old = 0.5f;
//        }
//
//        float newBrightness = MathUtils.clamp(old + change, 0.01f, 1f);
//        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
//        params.screenBrightness = newBrightness;
//        activity.getWindow().setAttributes(params);
//    }
//
//    /**
//     * 获取最大亮度
//     * @return max
//     */
//    private int getBrightnessMax() {
//        try {
//            Resources system = Resources.getSystem();
//            int resId = system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
//            if (resId != 0) {
//                return system.getInteger(resId);
//            }
//        }catch (Exception ignore){}
//        return 255;
//    }
}
