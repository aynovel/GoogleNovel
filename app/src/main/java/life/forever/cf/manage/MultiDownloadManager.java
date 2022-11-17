package life.forever.cf.manage;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.sql.CacheSQLiteHelper;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.activtiy.DF;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 批量下载管理
 *
 * @author haojie
 *         Created  on 2018/3/27.
 */
public class MultiDownloadManager implements Constant {

    public static boolean isDownload = FALSE;
    private static int total;

    public static void startDownload(Activity mActivity,int wid) {
        isDownload = TRUE;
        getIds(mActivity,wid);
    }

    public static void startDownload(Activity mActivity,int wid, List<Integer> chapterIds) {
        isDownload = TRUE;
        total = chapterIds.size();
        fetchContent(mActivity,wid, chapterIds);
    }

    private static void getIds(Activity mActivity,final int wid) {
        NetRequest.freeDownloadIds(wid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONArray ids = JSONUtil.getJSONArray(result, "ids");
                        List<Integer> list = new ArrayList<>();
                        for (int i = ZERO; ids != null && i < ids.length(); i++) {
                            list.add(JSONUtil.getInt(ids, i));
                        }
                        startDownload(mActivity,wid, list);
                    } else {
                        isDownload = FALSE;
                    }
                } else {
                    isDownload = FALSE;
                    if (SN004.equals(serverNo) || SN006.equals(serverNo) || SN009.equals(serverNo)) {
                        Intent intent = new Intent(PlotRead.getApplication(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PlotRead.getApplication().startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                isDownload = FALSE;
            }
        });
    }

    private static void fetchContent(Activity mActivity,final int wid, final List<Integer> chapterIds) {
        if (mActivity == null || ComYou.isDestroy(mActivity)){
            return;
        }
        final CacheSQLiteHelper helper = CacheSQLiteHelper.get(mActivity, wid);
        for (int i = ZERO; i < chapterIds.size(); i++) {
            final int cid = chapterIds.get(i);
            String local = "";
            // TODO: 2021/10/18   1.8.1 缓存格式

//            if (ReadActivity.isFree == 1){
//                local = helper.query(cid+Constant.READ_CECHE);
//            }else{
//                local = helper.query(cid);
//            }


            if (!TextUtils.isEmpty(local)) {
                updateTotal(wid);
                continue;
            }
            NetRequest.workContent(wid, cid, new OkHttpResult() {

                @Override
                public void onSuccess(JSONObject data) {
                    String serverNo = JSONUtil.getString(data, "ServerNo");
                    if (SN000.equals(serverNo)) {
                        JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                        String content = JSONUtil.getString(result, "content");
                        fetchReallyContent(content,wid,serverNo,helper,cid);
//                        content = AES.decrypt(content);
//                        helper.insert(cid, content);
                    }
//                    updateTotal(wid);
                }

                @Override
                public void onFailure(String error) {
                    updateTotal(wid);
                }
            });
        }
    }

    public static  void fetchReallyContent(String url, final int wid, String serverNo,CacheSQLiteHelper helper,int cid) {



        OkHttpClient okHttpClient = new OkHttpClient();
        if(url.contains("http")){
            final Request request1 = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call1 = okHttpClient.newCall(request1);
            call1.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    updateTotal(wid);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
//                String  content = response.body().string();
//                String content1 = AES.decrypt(content);

                    if (SN000.equals(serverNo)) {

                        String content = response.body().string();
                        content = DF.decrypt(content);

                        // TODO: 2021/10/18   1.8.1 缓存格式

//                        if (ReadActivity.isFree == 1){
//                            helper.insert(cid+Constant.READ_CECHE, content);
//                        }else{
//                            helper.insert(cid, content);
//                        }
                    }
                    updateTotal(wid);

                }
            });
        }


    }
    private static void updateTotal(int wid) {
        synchronized (MultiDownloadManager.class) {
            total--;
            if (total == ZERO) {
                isDownload = FALSE;
                Message message = Message.obtain();
                message.what = BUS_MULTI_DOWNLOAD_COMPLETE;
                message.obj = wid;
                EventBus.getDefault().post(message);
            }
        }
    }
}
