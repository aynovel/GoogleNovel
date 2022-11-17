package life.forever.cf.publics.tool;

import android.os.Handler;
import android.os.Looper;

import life.forever.cf.activtiy.PlotRead;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class ApkDownloadManager {

    private static ApkDownloadManager mInstance;

    private final Handler mDelivery;

    private ApkDownloadManager() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public interface DownloadCallBack {

        void onError(Request request, Exception e);

        void onResponse(Object response);

        void onProgress(int total, int current);
    }

    public static ApkDownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (ApkDownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new ApkDownloadManager();
                }
            }
        }
        return mInstance;
    }

    public static void downloadFile(String url, DownloadCallBack callback) {
        getInstance().okHttpDownload(url, callback);
    }

    private void okHttpDownload(final String url, final DownloadCallBack callback) {
        final Request request = new Request.Builder().url(url).build();
        PlotRead.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                FileOutputStream fos = null;
                try {
                    int len = -1;
                    int current = 0;
                    int total = (int) response.body().contentLength();
                    is = response.body().byteStream();

                    File downloadFile = new File(PlotRead.getApplication().getExternalCacheDir(), "ZdVersionUpdate.apk");
                    fos = new FileOutputStream(downloadFile);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        sendProgressCallBack(total, current, callback);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendSuccessResultCallback(downloadFile.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }

            }
        });
    }

    /**
     * 下载失败ui线程回调
     *
     * @param request
     * @param e
     * @param callback
     */
    private void sendFailedStringCallback(final Request request, final Exception e, final DownloadCallBack callback) {
        mDelivery.post(new Runnable() {

            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }

    /**
     * 下载成功ui线程回调
     *
     * @param object
     * @param callback
     */
    private void sendSuccessResultCallback(final Object object, final DownloadCallBack callback) {
        mDelivery.post(new Runnable() {

            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    /**
     * 进度信息ui线程回调
     *
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     */
    private void sendProgressCallBack(final int total, final int current, final DownloadCallBack callBack) {
        mDelivery.post(new Runnable() {

            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

}
