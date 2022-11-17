package life.forever.cf.publics.tool;

import android.content.Context;
import android.os.Environment;


public class ImageUtil {


    public static String getImageCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if(null != context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)){
                cachePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
            }else{
                cachePath = context.getCacheDir().getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

}
