package life.forever.cf.interfaces;




import life.forever.cf.activtiy.FileUtils;
import life.forever.cf.activtiy.LogUtils;

import java.io.File;

public class AppCrashHandler extends AppCrashLog{


    private static AppCrashHandler mCrashHandler = null;

    private AppCrashHandler(){};

    public static AppCrashHandler getInstance() {

        if(mCrashHandler == null) {
            mCrashHandler = new AppCrashHandler();
        }
        return mCrashHandler;
    }
    @Override
    public void initParams() {
        // TODO Auto-generated method stub
        LogUtils.e("************", "initParams");
        AppCrashLog.CACHE_LOG = FileUtils.getCachePath() +File.separator+"crash_log";

    }


    @Override
    public void sendCrashLogToServer(File file) {
        // TODO Auto-generated method stub

        LogUtils.e("************", "sendCrashLogToServer");
    }

}
