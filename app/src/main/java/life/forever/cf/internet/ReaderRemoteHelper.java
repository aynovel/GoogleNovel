package life.forever.cf.internet;

import android.os.Build;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.entry.BasePackageBean;
import life.forever.cf.activtiy.LogUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ReaderRemoteHelper {
    private static final String TAG = "sdfsdfsd";
    private static volatile ReaderRemoteHelper sInstance;
    private Retrofit mRetrofit;

    private Retrofit mTestRetrofit;

    private Retrofit mOssRetrofit;


    private OkHttpClient mOkHttpClient;
    private OkHttpClient mOkHttpDownLoadClient;
    private OkHttpClient mOssClient;


    private ReaderRemoteHelper() {

        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new JuYueInterceptor()).build();


        OkHttpClient.Builder ossBuilder = new OkHttpClient.Builder();
        ossBuilder.connectTimeout(15, TimeUnit.SECONDS);
        ossBuilder.writeTimeout(15, TimeUnit.SECONDS);
        ossBuilder.readTimeout(15, TimeUnit.SECONDS);


        mOssClient = ossBuilder.build();



        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            builder.connectTimeout(1, TimeUnit.MINUTES);
            builder.readTimeout(1, TimeUnit.MINUTES);
            builder.writeTimeout(1, TimeUnit.MINUTES);
        }

        mOkHttpDownLoadClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(ReaderCustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(PlotRead.getINDEX())
                .build();

        mTestRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(ReaderCustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://Reader-app.yn.damairead.com")
                .build();

        mOssRetrofit = new Retrofit.Builder()
                .client(mOssClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://content-cdn.Reader.top")
                .build();
    }

    public static ReaderRemoteHelper getInstance() {
        if (sInstance == null) {
            synchronized (ReaderRemoteHelper.class) {
                if (sInstance == null) {
                    sInstance = new ReaderRemoteHelper();
                }
            }
        }
        return sInstance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Retrofit getTestRetrofit() {
        return mTestRetrofit;
    }

    public Retrofit getOssRetrofit() {
        return mOssRetrofit;
    }

    public void checkResponSign(String responseStr) throws IOException {
        if(responseStr != null)
        {
            Gson repsonGson = new Gson();
            BasePackageBean packageBean = repsonGson.fromJson(responseStr,BasePackageBean.class);
            if(packageBean != null)
            {
                switch (packageBean.ServerNo){
                    case "SN009"://token过期
                    case "SN006"://
                    case "SN005"://签名错误
                    case "SN004"://
                    case "SN401":
                    case "SN411":
                    {
                        LogUtils.e("checkResponSign ===== "+ packageBean.ServerNo);

//                        JuYueAppUserHelper.getInstance().resetUser();
//                        JuYueAppUserHelper.getInstance().appUserDeviceLogin();
                    }
                        break;
                }
            }
        }


    }


    public class JuYueInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            LogUtils.d(TAG, "JuYueApi_URL: " + request.url().toString());

            Response response = chain.proceed(request);
            // 输出返回结果
            try {
//                Charset charset;
//                charset = Charset.forName("UTF-8");
//                ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
//
//                String temp = responseBody.toString();
//
////                LogUtils.d("temp response: " + temp);
//
//                Reader jsonReader = new InputStreamReader(responseBody.byteStream(), charset);
//                BufferedReader reader = new BufferedReader(jsonReader);
//                StringBuilder sbJson = new StringBuilder();
//                String line = reader.readLine();
//                do {
//                    sbJson.append(line);
//                    line = reader.readLine();
//                } while (line != null);

                String sbJson = bufferBody(response);

//                LogUtils.d("response: " + sbJson);
                checkResponSign(sbJson);

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(e.getMessage(), e);
            }
//        saveCookies(response, request.url().toString());
            return response;
        }


        private String bufferBody(Response response) throws IOException {
            Headers headers = response.headers();

            ResponseBody responseBody = response.body();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            // 判断是否有压缩
            if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                GzipSource gzippedResponseBody = null;
                try {
                    gzippedResponseBody = new GzipSource(buffer.clone());
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                } finally {
                    if (gzippedResponseBody != null) {
                        gzippedResponseBody.close();
                    }
                }
            }

            return buffer.clone().readString(Charset.forName("UTF-8"));
        }
    }


}
