package life.forever.cf.publics.tool;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import life.forever.cf.activtiy.PlotRead;


public class AndroidManifestUtil {

    private static String apiVersion = "v1.0";
    private static String channel = "3";
    private static String versionName = "1.0.0";
    private static int versionCode = 1;

    static {
        try {
            PlotRead application = PlotRead.getApplication();
            PackageManager packageManager = application.getPackageManager();
            String packageName = application.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle data = applicationInfo.metaData;
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
            apiVersion = data.getString("API_VERSION", apiVersion);
            channel = data.getString("UMENG_CHANNEL", channel);
            switch (channel){
                case "google":
                    channel = "3";
                    break;
                case "vivo":
                    channel = "4";
                    break;
                case "oppo":
                    channel = "5";
                    break;
                case "huawei":
                    channel = "6";
                    break;
                case "sanxing":
                    channel = "9";
                    break;
                default:
                    channel = "3";
                    break;

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getApiVersion() {
        return apiVersion;
    }

    public static String getChannel() {
        return channel;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static int getVersionCode() {
        return versionCode;
    }



    /**
     * 获取包名
     * @param context
     * @return
     */
    public static String getPackageName(Context context){
        return context.getPackageName();
    }

    /**
     * 获取VersionName(版本名称)
     * @param context
     * @return
     * 失败时返回""
     */
    public static String getVersionName(Context context){
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取VersionCode(版本号)
     * @param context
     * @return
     * 失败时返回-1
     */
    public static int getVersionCode(Context context){
        PackageManager packageManager = getPackageManager(context);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(context), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }



    /**
     * 获取PackageManager对象
     * @param context
     * @return
     */
    private static PackageManager getPackageManager(Context context){
        return context.getPackageManager();
    }

}
