package life.forever.cf.publics.net;

import android.os.Handler;
import android.os.Looper;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.publics.tool.JSONUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpUtil {
    private static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void get(String url, final OkHttpResult okHttpResult) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        PlotRead.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject = JSONUtil.newJSONObject(response.body().string());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onSuccess(jsonObject);
                        }
                    }
                });
            }
        });
    }

    public static void post(String url, String param, final OkHttpResult okHttpResult) {
        RequestBody requestBody = FormBody.create(JSON, param);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        PlotRead.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject = JSONUtil.newJSONObject(response.body().string());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onSuccess(jsonObject);
                        }
                    }
                });
            }
        });
    }


    public static void cardPaypost(String url,String api_key, String card_seri, String card_code, String request_id, String card_type,int card_amount,String signature, final OkHttpResult okHttpResult) {
//        RequestBody requestBody = FormBody.create(JSON, param);
                RequestBody body = new FormBody.Builder()
                .add("api_key",api_key)
                .add("card_seri",card_seri)
                .add("card_code",card_code)
                .add("request_id",request_id)
                .add("card_type",card_type)
                .add("card_amount",card_amount+"")
                .add("signature",signature).build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        PlotRead.getOkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONObject jsonObject = JSONUtil.newJSONObject(response.body().string());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (okHttpResult != null) {
                            okHttpResult.onSuccess(jsonObject);
                        }
                    }
                });
            }
        });
    }
}
