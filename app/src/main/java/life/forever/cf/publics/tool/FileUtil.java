package life.forever.cf.publics.tool;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {


    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            closeStream(fileInputStream);
//            closeStream(fileOutputStream);
        }
    }

    /*
     * 获取图片缓存路径
     * @return
     */
    public static File getPictureCacheDir(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir == null) {
            externalCacheDir = context.getCacheDir();
        }
        return externalCacheDir;
    }
}
