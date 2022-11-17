package life.forever.cf.manage;

import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DataPointUploadManager {

    private static final String TAG = "DataPointUpload";
    private static volatile DataPointUploadManager sInstance;

    private CompositeDisposable mTaskDisposable;

    private FirebaseAnalytics mFirebaseAnalytics;
    private AppEventsLogger mFBlogger;


    public DataPointUploadManager() {
        if (this.mTaskDisposable == null) {
            this.mTaskDisposable = new CompositeDisposable();
        }
    }

    private void addDisposable(Disposable disposable) {
        if (mTaskDisposable != null) {
            mTaskDisposable.add(disposable);
        }
    }

    public void initThirdSDK(Context context)
    {
        if (null == mFirebaseAnalytics) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        if (null == mFBlogger) {
            mFBlogger = AppEventsLogger.newLogger(context);
        }
    }


    public static DataPointUploadManager getInstance() {
        if (sInstance == null) {
            synchronized (DataPointUploadManager.class) {
                if (sInstance == null) {
                    sInstance = new DataPointUploadManager();
                }
            }
        }
        return sInstance;
    }


    public void reportDataBean(String eventName,Bundle reportIntent)
    {
        if(reportIntent != null)
        {
            if(mFirebaseAnalytics != null)
            {
                mFirebaseAnalytics.logEvent(eventName,reportIntent);
            }

            if(mFBlogger != null)
            {
                mFBlogger.logEvent(eventName,reportIntent);
            }
        }
    }
}
