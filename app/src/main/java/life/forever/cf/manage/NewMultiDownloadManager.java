package life.forever.cf.manage;

import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.BookChapterContentResult;
import life.forever.cf.entry.BookFreeCachePackage;
import life.forever.cf.internet.ReaderRemoteRepository;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.activtiy.DF;
import life.forever.cf.activtiy.BookManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import life.forever.cf.interfaces.BusC;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewMultiDownloadManager implements Constant {

    public  boolean isDownload = FALSE;
    private  int total;
    private int originTotal;

    private Toast mProgressToast;

    private Work mBookWork;

    public NewMultiDownloadManager(Work mBookWork) {
        this.mBookWork = mBookWork;
    }


    public  void startFreeIdsDownload(String wid) {
        isDownload = TRUE;
        getFreeIds(wid);
    }

    public  void startIdsDownload(String wid, List<String> chapterIds) {
        isDownload = TRUE;
        total = chapterIds.size();
        originTotal = total;
        fetchContent(wid, chapterIds);

        freashProgress();
    }

    private void freashProgress()
    {
        int progress = originTotal - total;
        String progressSTr =PlotRead.getContext().getString(R.string.download_loading) + progress + "/" + originTotal;

//        if(mProgressToast != null)
//        {
//            mProgressToast.setText(progressSTr);
//        }else {
//            mProgressToast = Toast.makeText(PlotRead.getContext(),progressSTr, Toast.LENGTH_SHORT);
//        }
//        mProgressToast.show();

        Message message = Message.obtain();
        message.what = BusC.BUS_NOTIFY_MULIT_DOWN_PROGRESS;
        message.obj = progressSTr;
        EventBus.getDefault().post(message);
    }

    private  void getFreeIds(final String wid) {

        Disposable disposable =  ReaderRemoteRepository.getInstance()
                .ReaderFreeCacheContent(wid, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookFreeCachePackage chapterCacheResult) -> {

                            if(chapterCacheResult != null)
                            {
                                isDownload = FALSE;
                                if (SN004.equals(chapterCacheResult.ServerNo) || SN006.equals(chapterCacheResult.ServerNo) || SN009.equals(chapterCacheResult.ServerNo)) {
                                    Intent intent = new Intent(PlotRead.getApplication(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    PlotRead.getApplication().startActivity(intent);
                                }else{
                                    if(chapterCacheResult.ids != null)
                                    {
                                        startIdsDownload(wid, chapterCacheResult.ids);
                                    }else {
                                        isDownload = FALSE;
                                    }
                                }
                            }else {
                                isDownload = FALSE;
                            }

                        },
                        (e) -> {
                            isDownload = FALSE;
                        }
                );

//        NetRequest.freeDownloadIds(wid, new OkHttpResult() {
//
//            @Override
//            public void onSuccess(JSONObject data) {
//                String serverNo = JSONUtil.getString(data, "ServerNo");
//                if (SN000.equals(serverNo)) {
//                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
//                    int status = JSONUtil.getInt(result, "status");
//                    if (status == ONE) {
//                        JSONArray ids = JSONUtil.getJSONArray(result, "ids");
//                        List<Integer> list = new ArrayList<>();
//                        for (int i = ZERO; ids != null && i < ids.length(); i++) {
//                            list.add(JSONUtil.getInt(ids, i));
//                        }
//                        startIdsDownload(wid, list);
//                    } else {
//                        isDownload = FALSE;
//                    }
//                } else {
//                    isDownload = FALSE;
//                    if (SN004.equals(serverNo) || SN006.equals(serverNo) || SN009.equals(serverNo)) {
//                        Intent intent = new Intent(PlotRead.getApplication(), LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        PlotRead.getApplication().startActivity(intent);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                isDownload = FALSE;
//            }
//        });
    }

    private  void fetchContent(final String wid, final List<String> chapterIds) {

        for (int i = ZERO; i < chapterIds.size(); i++) {


            String cid = chapterIds.get(i);
            boolean hasCache = BookManager.isChapterCached(wid, "0", cid);
            if (hasCache) {
                updateTotal(wid);
                continue;
            }

           Disposable disposable =  ReaderRemoteRepository.getInstance()
                    .ReaderChapterContent(wid, cid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (BookChapterContentResult chapterContentResult) -> {

                                if (chapterContentResult.getResult() != null) {

                                    String contentStr = chapterContentResult.getResult().getChapterContent();
                                    fetchReallyContent(contentStr,wid,chapterContentResult.ServerNo,cid);
                                } else {

                                    updateTotal(wid);
                                }

                            },
                            (e) -> {
                                updateTotal(wid);
                            }
                    );

        }
    }

    private void fetchReallyContent(String url, final String wid, String serverNo,String cid) {



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

                        String contentStr = response.body().string();
                        contentStr = DF.decrypt(contentStr);

                        if(contentStr != null && contentStr.length() > 0)
                        {
                            BookManager.getInstance().saveChapterInfo(wid, "0", cid,
                                    contentStr);
                        }

                    }
                    updateTotal(wid);

                }
            });
        }


    }

    private  void updateTotal(String wid) {
        synchronized (MultiDownloadManager.class) {
            total--;
            freashProgress();

            if (total == ZERO) {
                isDownload = FALSE;


                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        message.what = BUS_MULTI_DOWNLOAD_COMPLETE;
                        message.obj = wid;
                        EventBus.getDefault().post(message);
                    }
                }).start();

            }
        }
    }
}
