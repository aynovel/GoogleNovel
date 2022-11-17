package life.forever.cf.activtiy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;


import life.forever.cf.BuildConfig;
import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.manage.DataPointUploadManager;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.MyActivityManager;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.interfaces.AppCrashHandler;
import com.facebook.appevents.AppEventsLogger;
import com.kc.openset.OSETSDK;
import com.kc.openset.ad.OSETRewardVideoCache;
import com.kc.openset.listener.OSETInitListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import life.forever.cf.interfaces.InterFace;
import okhttp3.OkHttpClient;

public class PlotRead extends Application implements Constant {

    public static boolean isTest = false;
    public static final int NORMAL = ZERO;
    public static final int SUCCESS = ONE;
    public static final int INFO = TWO;
    public static final int FAIL = THREE;

    @IntDef({NORMAL, SUCCESS, INFO, FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TipType {
    }


    private static PlotRead application;
    private static OkHttpClient okHttpClient;
    private static SharedPreferences config;
    private static AppUser appUser;

    private static Toast commonToast;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        initLog();

        AppCrashHandler.getInstance().init(this);//崩溃日志收集

        config = SharedPreferencesUtil.getSharedPreferences(APP);
        appUser = AppUser.get(SharedPreferencesUtil.getInt(config, LAST_ID));
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TWENTY, TimeUnit.SECONDS);
        builder.writeTimeout(TWENTY, TimeUnit.SECONDS);
        builder.readTimeout(TWENTY, TimeUnit.SECONDS);
        okHttpClient = builder.build();
        setActivity();
        AppEventsLogger.newLogger(getApplicationContext());
        AppEventsLogger.activateApp(this);

        DataPointUploadManager.getInstance().initThirdSDK(getApplicationContext());
        OSETSDK.getInstance().init(application, "9673174166C9CC81", new OSETInitListener() {
            @Override
            public void onError(String s) {
                Log.e("dddddddsss", "encryptReqParameters:fail ");
            }

            @Override
            public void onSuccess() {
                OSETRewardVideoCache.getInstance(application)
                        .setVerify(false)
                        .setServiceReward(false)
                        .setUserId("1111")
                        .setCacheNumber(2)
                        .setPosId("E403BD094175484AD3F72D59932966F0").setOnLoadFailListener(new OSETRewardVideoCache.OnLoadFailListener() {
                    @Override
                    public void onLoadFail(String s, String s1) {
                        Log.e("dddddddsss", "encryptReqParameters:fail ");
                    }
                }).setOnLoadSuccessListener(new OSETRewardVideoCache.OnLoadSuccessListener() {
                    @Override
                    public void onLoadSuccess() {
                        Log.e("dddddddsss", "encryptReqParameters:succ ");
                    }
                }).startLoad();
            }
        });

    }

    private void initLog()
    {
        if(BuildConfig.DEBUG)
        {
            LogUtils.LOG_SWITCH = true;
//            DLog.DEBUG = true;
        }else{
            // TODO:1.8.1 2021/9/30 1.8.1测试日志
            LogUtils.LOG_SWITCH = false;
        }
    }


    public static PlotRead getContext() {
        return application;
    }

    public static PlotRead getApplication() {
        return application;
    }

    public static String getINDEX(){
        String INDEX = "";
        if (isTest) {
            INDEX  = InterFace.INDEX_TEST;
        }else{
            INDEX = InterFace.INDEX_ONLINE;
        }
        return INDEX;

    }

    public static String getH5_INDEX(){
        String H5_INDEX = "";
        if (isTest) {
            H5_INDEX  = InterFace.H5_INDEX_TEST;
        }else{
            H5_INDEX = InterFace.H5_INDEX_ONLINE;
        }
        return H5_INDEX;

    }
    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static SharedPreferences getConfig() {
        return config;
    }

    public static AppUser getAppUser() {
        return appUser;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ShelfUtil.shelfUploads(application);
    }

    /**
     * 自定义Toast
     *
     * @param type {@link #NORMAL} or {@link #SUCCESS} or {@link #INFO} or {@link #FAIL}
     * @param tip
     */
    public static Toast toast(@TipType int type, String tip) {
        if (commonToast != null) {
            commonToast.cancel();
            commonToast = null;
        }
        View toastView = LayoutInflater.from(application).inflate(R.layout.layout_tip_toast, null, FALSE);
        ImageView tipIcon = toastView.findViewById(R.id.icon);
        TextView tipText = toastView.findViewById(R.id.tip);
        tipText.setText(tip);
        tipIcon.setVisibility(View.VISIBLE);
        switch (type) {
            case SUCCESS:
                tipIcon.setImageResource(R.drawable.tip_icon_success);
                break;
            case INFO:
                tipIcon.setImageResource(R.drawable.tip_icon_info);
                break;
            case FAIL:
                tipIcon.setImageResource(R.drawable.tip_icon_fail);
                break;
            case NORMAL:
            default:
                tipIcon.setVisibility(View.GONE);
                break;
        }
        commonToast = new Toast(application);
        commonToast.setView(toastView);
        commonToast.setGravity(Gravity.CENTER, ZERO, ZERO);
        commonToast.setDuration(Toast.LENGTH_SHORT);
        commonToast.show();
        return commonToast;
    }



    void setActivity(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }

}
