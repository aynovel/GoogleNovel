package life.forever.cf.publics.tool;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class MyActivityManager {
    private static final MyActivityManager sInstance = new MyActivityManager();

    private WeakReference<Activity> sCurrentActivityWeakRef;

    private final Object activityUpdateLock = new Object();
    private MyActivityManager() {

    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized (activityUpdateLock){
            if (sCurrentActivityWeakRef != null) {
                currentActivity = sCurrentActivityWeakRef.get();
            }
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        synchronized (activityUpdateLock){
            sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
        }

    }
}