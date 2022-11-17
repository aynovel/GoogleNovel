package life.forever.cf.publics.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 屏幕相关的工具类
 *
 * @author Haojie.Dai
 */
public class ScreenUtil {

    /**
     * 透明状态栏
     * <p>
     * 如果使用了自定义键盘请使用{@link #translucentStatusBar(Activity)}
     *
     * @param activity
     */
    public static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上，状态栏全透明
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4以上，状态栏半透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Android 4.4以上透明状态栏
     * <p>
     * 如果使用了自定义键盘请使用该方法而不能使用{@link #transparentStatusBar(Activity)}
     *
     * @param activity
     */
    public static void translucentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 透明状态栏和导航栏
     *
     * @param activity
     */
    public static void transparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 基于Android 4.4的全屏
     * <p>
     * Android 4.4以上隐藏状态栏和虚拟导航栏，Android 4.4以下只隐藏状态栏
     * <p>
     * 如果使用了自定义键盘，请使用{@link #fullBaseOnAndroidAll(Activity)}
     *
     * @param activity
     */
    public static void fullBaseOnAndroid19(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 适用于Android所有版本的全屏
     * <p>
     * 该方法只隐藏状态栏，但虚拟导航栏不隐藏
     * <p>
     * 如果使用了自定义键盘请使用该方法而不能使用{@link #fullBaseOnAndroid19(Activity)}
     *
     * @param activity
     */
    public static void fullBaseOnAndroidAll(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Android6.0以上修改状态栏颜色及图标色系
     *
     * @param color 状态栏颜色
     * @param light true为浅色，false为暗色
     */
    public void setStatusBar(Activity activity, View rootView, int color, boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rootView.setFitsSystemWindows(true);
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            // 全屏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            // 设置状态栏颜色
            window.setStatusBarColor(color);
            // 设置状态栏图标
            if (light) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        }
    }

    /**
     * 设置状态栏图标为浅色系（Android6.0以上）
     *
     * @param activity
     */
    public static void setStatusBarLight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 设置状态栏图标为暗色系（Android6.0以上）
     *
     * @param activity
     */
    public static void setStatusBarDark(Activity activity,boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ComYou.isDestroy(activity)) {
                return;
            }
            View decorView = activity.getWindow().getDecorView();
            if (light) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

//    /**
//     * 设置状态栏颜色（Android5.0以上）
//     *
//     * @param activity
//     * @param color
//     */
//    public static void setStatusBarColor(Activity activity, int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().setStatusBarColor(color);
//        }
//    }

    /**
     * 获取屏幕高度
     *
     * @param ctx
     * @return
     */
    public static int getScreenHeight(Context ctx) {
        WindowManager localWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param ctx
     * @return
     */
    public static int getScreenWidth(Context ctx) {
        WindowManager localWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object localObject = clazz.newInstance();
            int i = Integer.parseInt(clazz.getField("status_bar_height").get(localObject).toString());
            statusHeight = context.getResources().getDimensionPixelSize(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusHeight;
    }

    /**
     * 判断是否存在NavigationBar
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return hasNavigationBar;
    }

    /**
     * 获取底部导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        return resources.getDimensionPixelSize(resourceId);
    }
    // 获取状态栏高度
    @SuppressWarnings({"unchecked","deprecation"})
    public static int getBarHeight(Activity activity)
    {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0)
        {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 判断是否是刘海屏
     * @return
     */
    public static boolean hasNotchScreen(Activity activity){
        //TODO 各种品牌
        return getInt("ro.miui.notch", activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity) || isAndroidP(activity);
    }

    /**
     * 判断是否是刘海屏
     * @return
     */
    public static boolean hasNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
//            Log.e("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
//            Log.e("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
//            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }
    /**
     * Android P 刘海屏判断
     * @param activity
     * @return
     */
    public static boolean isAndroidP(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28){
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            return windowInsets != null;
        }
        return false;
    }

    /**
     * 小米刘海屏判断.
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key,Activity activity) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = key;
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     * @return
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }
    /**
     * OPPO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtOPPO(Context context) {
        return  context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    // 是否是小米手机
    public static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    /**
     * 修改状态栏字体颜色只能在android6.0以上原生系统中，同时小米和魅族提供有方法，其他厂商可能无效过
     * 设置Android状态栏的字体颜色，状态栏为亮色的时候字体和图标是黑色，状态栏为暗色的时候字体和图标为白色
     *
     * @param dark 状态栏字体和图标是否为深色
     */
    public static void setStatusBarTextDark(boolean dark,Activity mActivity) {
        if (OsUtil.isMIUI()) {
            // 小米MIUI
            try {
                Window window = mActivity.getWindow();
                Class clazz = mActivity.getWindow().getClass();
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {    //状态栏亮色且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {       //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (OsUtil.isFlyme()) {
            // 魅族FlymeUI
            try {
                Window window = mActivity.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
}
