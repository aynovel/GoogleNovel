package life.forever.cf.publics.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;


public class OssUtil {

    private final OSS oss;
    private static OssUtil instance;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private OssUtil(Context context) {
        String endpoint = "https://content-cdn.Reader.top";
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAI4GFf7xUzT6imvTWYdU3C",
                "W4pnSMBvfkvXUCLYegP1d9OibyIqsU");
        oss = new OSSClient(context, endpoint, credentialProvider);
    }

    public static synchronized OssUtil with(Context context) {
        if (instance == null) {
            synchronized (OssUtil.class) {
                if (instance == null) {
                    instance = new OssUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 上传文件到oss
     *
     * @param local       文件的本地地址
     * @param online      oss服务器的相对路径
     * @param ossCallback
     */
    public void post(String local, String online, final OssCallback ossCallback) {
        String bucketName = "novel-star";
        PutObjectRequest put = new PutObjectRequest(bucketName, online, local);
        if (online.contains(".jpg") || online.contains(".jpeg") || online.contains(".png")) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");
            put.setMetadata(metadata);
        }

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            @Override
            public void onSuccess(final PutObjectRequest request, PutObjectResult result) {
                if (ossCallback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ossCallback.onSuccess(request.getObjectKey().trim());
                        }
                    });
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion,
                                  ServiceException serviceException) {
                if (ossCallback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ossCallback.onFailure();
                        }
                    });
                }
            }
        });
    }

    /**
     * 阿里云上传结果回调
     *
     * @author haojie
     */
    public interface OssCallback {

        void onSuccess(String url);

        void onFailure();
    }

}
