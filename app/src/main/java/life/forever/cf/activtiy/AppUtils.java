package life.forever.cf.activtiy;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtils {


    public static boolean is_AppCheck = false;


    public static boolean is_FirstInstall = false;


    public static boolean is_FirstActive = false;






    public static synchronized String getPackageName(Context context) {

        try {

            PackageManager packageManager = context.getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(

                    context.getPackageName(), 0);

            return packageInfo.packageName;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }



}
