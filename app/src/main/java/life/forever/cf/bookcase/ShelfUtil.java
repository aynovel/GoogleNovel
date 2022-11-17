package life.forever.cf.bookcase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.sql.FirstShelfSQLiteHelper;
import life.forever.cf.sql.SQLiteManager;
import life.forever.cf.sql.ShelfSQLiteHelper;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.LOG;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ShelfUtil implements Constant {


    public static boolean exist(int wid) {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        return helper.exist(wid);
    }

    public static boolean existWidStr(String widStr) {
        boolean existFlag = false;
        if(widStr != null)
        {
            ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
            try {
                int wid  =  Integer.parseInt(widStr);
                existFlag = helper.exist(wid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return existFlag;
    }


    /**
     * 插入首推作品集合
     *
     * @param mActivity
     * @param Works
     */
    public static void firstinsert(Activity mActivity, List<Work> Works) {
        try {
            FirstShelfSQLiteHelper helper = (FirstShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_FIRST_HELPER);
            helper.firstinsert(Works);
        } catch (Exception ignored) {
        }

    }

    /**
     * 插入作品集合
     *
     * @param mActivity
     * @param works
     */
    public static void insert(Activity mActivity, List<Work> works,boolean isPush) {
        try {
            //大量作品加入书架
            if (!isPush){
                for (int i = 0; i < works.size(); i++) {
                    works.get(i).push = "1";
//                    setPush(works.get(i));
                }
            }
            Log.e("shelfDownload", "insert: 保存到数据库" );

            ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
            helper.insert(works);
            // 更新时间戳
            SharedPreferencesUtil.putInt(PlotRead.getConfig(), SHELF_TIME, ComYou.currentTimeSeconds());
            // 发送书架变更的通知
            Message message = Message.obtain();
            message.what = BUS_SHELF_CHANGE;
            EventBus.getDefault().post(message);
            //同步
            shelfUploadByWifi(mActivity);
            // 加入书架上报、开启展示更新推送
            if (PlotRead.getAppUser().login()) {
                NetRequest.reportAddShelf(null);
                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), SHOW_SHELF_UPDATE_PUSH + PlotRead.getAppUser().uid, TRUE);
            }
        } catch (Exception e) {
        }

    }

    /**
     * 插入单个作品（服务器默认打开push）
     *
     * @param mActivity
     * @param work
     */
    public static void insert(Activity mActivity, Work work,boolean isPush) {
        if (!isPush){
            work.push = "1";
        }
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.insert(work);
        firstDeleteShelf(work.wid);
        // 更新时间戳
        SharedPreferencesUtil.putInt(PlotRead.getConfig(), SHELF_TIME, ComYou.currentTimeSeconds());
        // 发送书架变更的通知
        Message message = Message.obtain();
        message.what = BUS_SHELF_CHANGE;
        EventBus.getDefault().post(message);
        //同步
        shelfUploadByWifi(mActivity);
        //加不加入书架都不需要打开push
//        if (!isPush){
//            setPush(work);
//        }
        // 加入书架上报、开启展示更新推送
        if (PlotRead.getAppUser().login()) {
            NetRequest.reportAddShelf(null);
//            NetRequest.reportAddShelfWid(work.wid, new OkHttpResult() {
//                @Override
//                public void onSuccess(JSONObject data) {
//
//                    String serverNo = JSONUtil.getString(data, "ServerNo");
//                    if (SN000.equals(serverNo)) {
//                        JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
//                        int status = JSONUtil.getInt(result, "status");
//                        if (status == ONE) {
//                            // 发送书架变更的通知
//                            Message message = Message.obtain();
//                            message.what = BUS_SHELF_CHANGE;
//                            EventBus.getDefault().post(message);
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onFailure(String error) {
//
//                }
//            });
            SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), SHOW_SHELF_UPDATE_PUSH + PlotRead.getAppUser().uid, TRUE);
        }
    }

    private static void setPush(final Work work) {
        int uid = PlotRead.getAppUser().uid;
        int wid = work.wid;
        work.push = "1";
        NetRequest.setWorkPushs(Constant.TWO, uid, wid, ONE,1, mToken, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {


                    }
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    private static String mToken;
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    mToken = task.getResult();
                });
    }
    /**
     * 获取书架上的所有首推的作品
     *
     * @return
     */
    public static List<Work> firstqueryShelfWorks() {
        FirstShelfSQLiteHelper helper = (FirstShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_FIRST_HELPER);
        return helper.firstqueryShelf();
    }

    /**
     * 获取书架上的所有未删除的作品
     *
     * @return
     */
    public static List<Work> queryShelfWorks() {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        return helper.queryShelf();
    }

    /**
     * 清空首推
     */
    private static void firstclearShelf() {
        FirstShelfSQLiteHelper helper = (FirstShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_FIRST_HELPER);
        helper.clearShelf();
    }

    /**
     * 删除已加入书架的首推书籍
     */
    public static void firstDeleteShelf(int wid) {
        FirstShelfSQLiteHelper helper = (FirstShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_FIRST_HELPER);
        helper.firstDeleteShelf(wid);
    }

    /**
     * 清空书架
     */
    private static void clearShelf() {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.clearShelf();
    }

    /**
     * 清理书架
     */
    private static void cleanShelf() {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.cleanShelf();
    }

    /**
     * 插入一条阅读记录
     *
     * @param work
     */
    public static void insertRecord(Work work) {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.insertRecord(work);
        // 发送书架变更的通知
        Message message = Message.obtain();
        message.what = BUS_READ_HISTORY_CHANGE;
        EventBus.getDefault().post(message);
    }

    /**
     * 获取书架上的所有未删除的作品
     *
     * @return
     */
    public static List<Work> queryRecord() {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        return helper.queryRecord();
    }

    /**
     * 查询指定作品是否存在阅读记录
     *
     * @param wid
     * @return
     */
    public static boolean existRecord(int wid) {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        return helper.existRecord(wid);
    }

    /**
     * 查询指定作品的阅读记录
     *
     * @param wid
     * @return
     */
    public static Work queryRecord(int wid) {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        return helper.queryRecord(wid);
    }

    /**
     * 清空阅读记录
     */
    public static void clearRecord() {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.clearRecord();
    }

    /**
     * 当前网络是否是wifi
     *
     * @param context
     * @return
     */
    private static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }


        return false;
    }

    /**
     * wifi下同步书架
     *
     * @param mActivity
     */
    private static void shelfUploadByWifi(Activity mActivity) {
//        if (isWifiConnected(mActivity)) {
            shelfUpload(mActivity);
//        }
    }

    /**
     * 任意网络下同步书架
     */
    public static void shelfUploads(final Context mContext) {
        if (!PlotRead.getAppUser().login()) {
            return;
        }
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        List<Work> works = helper.queryAllWithDeleted();
        if (works.size() == ZERO) {
            return;
        }
        JSONArray param = JSONUtil.newJSONArray();
        for (Work work : works) {
            JSONObject child = JSONUtil.newJSONObject();
            JSONUtil.put(child, "wid", work.wid);
            JSONUtil.put(child, "sort", work.lastChapterOrder);
            JSONUtil.put(child, "lastchapter", work.lastChapterId);
            JSONUtil.put(child, "lastchapterpos", work.lastChapterPosition);
            JSONUtil.put(child, "addtime", work.lasttime);
            JSONUtil.put(child, "readtime", work.lasttime);
            JSONUtil.put(child, "deleteflag", work.deleteflag);
            JSONUtil.put(param, child);
        }
        NetRequest.shelfUpload(param, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        SharedPreferencesUtil.putInt(PlotRead.getConfig(), Constant.SHELF_UPLOAD_TIME, ComYou.currentTimeSeconds());
                        cleanShelf();
                    } else {
                        LOG.i(getClass().getSimpleName(), "同步失败");
                    }
                } else if (!TextUtils.isEmpty(serverNo)) {
//                    NetRequest.error(mContext, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                LOG.i(getClass().getSimpleName(), "同步失败");
            }
        });
    }

    /**
     * 任意网络下同步书架
     */
    public static void shelfUpload(final Activity mActivity) {
        if (!PlotRead.getAppUser().login()) {
            return;
        }
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        List<Work> works = helper.queryAllWithDeleted();
        if (works.size() == ZERO) {
            return;
        }
        JSONArray param = JSONUtil.newJSONArray();
        for (Work work : works) {
            JSONObject child = JSONUtil.newJSONObject();
            JSONUtil.put(child, "wid", work.wid);
            JSONUtil.put(child, "sort", work.lastChapterOrder);
            JSONUtil.put(child, "lastchapter", work.lastChapterId);
            JSONUtil.put(child, "lastchapterpos", work.lastChapterPosition);
            JSONUtil.put(child, "addtime", work.lasttime);
            JSONUtil.put(child, "readtime", work.lasttime);
            JSONUtil.put(child, "deleteflag", work.deleteflag);
            JSONUtil.put(param, child);
        }
        NetRequest.shelfUpload(param, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        SharedPreferencesUtil.putInt(PlotRead.getConfig(), Constant.SHELF_UPLOAD_TIME, ComYou.currentTimeSeconds());
                        cleanShelf();
                    } else {
                        LOG.i(getClass().getSimpleName(), "同步失败");
                    }
                } else if (!TextUtils.isEmpty(serverNo)) {
                    NetRequest.error(mActivity, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                LOG.i(getClass().getSimpleName(), "同步失败");
            }
        });
    }

    /**
     * 书架更新
     *
     * @param mActivty
     * @param timestamp
     */
    public static void shelfDownload(final Activity mActivty, int timestamp,boolean isPush) {
//        if (!PlotRead.getAppUser().login()) {
//            return;
//        }
//        boolean firstPush = PlotRead.getConfig().getBoolean(FIRST_PUSH, TRUE);
        long time = System.currentTimeMillis()/1000;
        Log.e("shelfDownload", "shelfDownload long当前时间搓: " +  time );
        time = 0;
        NetRequest.shelfDownload(time, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    //-1书架无数据 1如果大于时间 则返回更新，说明服务器的新 2服务器不比客户端的新  3与上次拉取时间戳相同
                    if (status == ONE) {
                        Log.e("shelfDownload", "onSuccess: 保存数据" );
                        JSONArray bookshelf = JSONUtil.getJSONArray(result, "bookshelf");
                        List<Work> works = new ArrayList<>();
                        for (int i = ZERO; bookshelf != null && i < bookshelf.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(bookshelf, i);
                            Work work = new Work();
                            work.wid = JSONUtil.getInt(child, "wid");
                            work.push = JSONUtil.getString(child, "push");
                            work.wtype = JSONUtil.getInt(child, "wtype");
                            work.cover = JSONUtil.getString(child, "h_url");
                            work.title = JSONUtil.getString(child, "title");
                            work.author = JSONUtil.getString(child, "author");
                            work.isfinish = JSONUtil.getInt(child, "status");
                            work.updatetime = JSONUtil.getInt(child, "updatetime");
                            work.totalChapter = JSONUtil.getInt(child, "chapterCounts");
                            work.lasttime = JSONUtil.getInt(child, "readtime");
                            work.lastChapterId = JSONUtil.getInt(child, "lastchapter");
                            work.lastChapterOrder = JSONUtil.getInt(child, "sort");
                            work.lastChapterPosition = JSONUtil.getInt(child, "lastchapterpos");
                            works.add(work);
                            Log.e("shelfDownload", "onSuccess: 保存单条数据" );

                        }
                        if (works.size() > ZERO) {
                            clearShelf();
                            Log.e("shelfDownload", "onSuccess: 清空数据库" );

                            insert(mActivty, works,isPush);
                        }
                    } else if (status == TWO) {
                        shelfUpload(mActivty);
                    }
                    workUpdate(mActivty,isPush);
                } else {
                    workUpdate(mActivty,isPush);
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * 作品更新
     *
     * @param mActivity
     */
    public static void workUpdate(final Activity mActivity,boolean isPush) {
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        final List<Work> works = helper.queryShelf();
        LOG.i("ShelfUtil", "workUpdate size = " + works.size());
        if (works.size() == ZERO) {
            return;
        }
        String ids = "";
        for (Work work : works) {
            ids += work.wid + "##";
        }
        ids = ids.substring(ZERO, ids.lastIndexOf("##"));
        NetRequest.workUpdate(ids, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray booklist = JSONUtil.getJSONArray(result, "booklist");
                    List<Work> shelf = queryShelfWorks();
                    List<Work> update = new ArrayList<>();
                    for (int i = ZERO; booklist != null && i < booklist.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(booklist, i);
                        Work work = new Work();
                        work.wid = JSONUtil.getInt(child, "wid");
                        work.title = JSONUtil.getString(child, "title");
                        work.author = JSONUtil.getString(child, "author");
                        work.cover = JSONUtil.getString(child, "h_url");
                        work.isfinish = JSONUtil.getInt(child, "is_finish");
                        work.totalChapter = JSONUtil.getInt(child, "counts");
                        work.updatetime = JSONUtil.getInt(child, "update_time");


                        for (int j = ZERO; j < shelf.size(); j++) {
                            Work temp = shelf.get(j);
                            if (temp.equals(work)) {
                                temp.title = work.title;
                                temp.author = work.author;
                                temp.cover = work.cover;
                                temp.isfinish = work.isfinish;
                                temp.updatetime = work.updatetime;
                                if (temp.totalChapter != work.totalChapter) {
                                    temp.totalChapter = work.totalChapter;
                                    temp.updateflag = ONE;
                                    update.add(temp);
                                }
                                break;
                            }

                        }
                    }
                    if (update.size() > ZERO) {
                        insert(mActivity, update,isPush);
                    }
                }
//                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_PUSH, FALSE);
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * 首次推荐
     *
     * @param mActivity
     */
    public static void firstRecommend(final Activity mActivity) {
        NetRequest.firstRecommend(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");

                    JSONObject booklist = JSONUtil.getJSONObject(result, "data");
                    JSONArray rec_list = JSONUtil.getJSONArray(booklist, "rec_list");
                    List<Work> works = new ArrayList<>();
                    for (int i = ZERO; rec_list != null && i < rec_list.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(rec_list, i);
                        if (!TextUtils.isEmpty(BeanParser.getFirstRrcWork(child).cover)) {
                            works.add(BeanParser.getFirstRrcWork(child));
                        }
                    }

                    ShelfUtil.firstclearShelf();
                    // 插入书架
                    ShelfUtil.firstinsert(mActivity, works);
                } else {
                    LOG.e("ShelfUtil", "首推请求失败");
                }
            }

            @Override
            public void onFailure(String error) {
                LOG.e("ShelfUtil", "首推请求失败");
            }
        });
    }

    /**
     * 更新书架单个书籍信息
     * @param work
     */
    public static void updateWork(Work work){
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        helper.updateWorkByWid(work);
    }


    /**
     * 查询有更新的书架书籍
     */
    public static List<Work>  queryUpdateWork(String param){
        List<Work>  list = new ArrayList<>();
        try {
            ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
            List<Work>  queryList =   helper.query("update_flag",param);
            if ( queryList != null){
                list.addAll(queryList);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 启动页 初始化书架  下载最新书架数据
     */
    public static void initBookShelf(){
        int lastUploadTime =  SharedPreferencesUtil.getInt(PlotRead.getConfig(), Constant.SHELF_UPLOAD_TIME);
        long time = lastUploadTime;
        NetRequest.shelfDownload(time, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    //-1书架无数据 1如果大于时间 则返回更新，说明服务器的新 2服务器不比客户端的新  3与上次拉取时间戳相同
                    if (status == ONE) {
                        JSONArray bookshelf = JSONUtil.getJSONArray(result, "bookshelf");
                        List<Work> works = new ArrayList<>();
                        for (int i = ZERO; bookshelf != null && i < bookshelf.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(bookshelf, i);
                            Work work = new Work();
                            work.wid = JSONUtil.getInt(child, "wid");
                            work.push = JSONUtil.getString(child, "push");
                            work.wtype = JSONUtil.getInt(child, "wtype");
                            work.cover = JSONUtil.getString(child, "h_url");
                            work.title = JSONUtil.getString(child, "title");
                            work.author = JSONUtil.getString(child, "author");
                            work.isfinish = JSONUtil.getInt(child, "status");
                            work.updatetime = JSONUtil.getInt(child, "updatetime");
                            work.totalChapter = JSONUtil.getInt(child, "chapterCounts");
                            work.lasttime = JSONUtil.getInt(child, "readtime");
                            work.lastChapterId = JSONUtil.getInt(child, "lastchapter");
                            work.lastChapterOrder = JSONUtil.getInt(child, "sort");
                            work.lastChapterPosition = JSONUtil.getInt(child, "lastchapterpos");
                            works.add(work);
                        }
                        if (works.size() > ZERO) {
                          List<Work>  updateWorkList = queryUpdateWork("1");
                          if (updateWorkList.size() > 0) {
                              for (int i = 0; i < works.size(); i++) {
                                  for (int j = 0; j < updateWorkList.size(); j++) {
                                      if (works.get(i).wid == updateWorkList.get(j).wid) {
                                          works.get(i).updateflag = updateWorkList.get(j).updateflag;
                                      }
                                  }
                              }
                              //清除本地数据 保存合并的最新数据
                          }
                              clearShelf();
                              createBookShelf(works);
                        }


                    } else if (status == TWO) {
//                        shelfUpload(mActivty);
                        uploadBookShelfInfo();
                    }
//                    workUpdate(mActivty,isPush);
                } else {
//                    workUpdate(mActivty,isPush);
                }
            }


            @Override
            public void onFailure(String error) {

            }
        });

    }

    /**
     * 启动页 书架数据写入数据库
     * @param works
     */
    public static void createBookShelf(List<Work> works){
        try {
            //大量作品加入书架
            ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
            helper.insert(works);
            // 更新时间戳
            SharedPreferencesUtil.putInt(PlotRead.getConfig(), SHELF_TIME, ComYou.currentTimeSeconds());
            // 发送书架变更的通知
            Message message = Message.obtain();
            message.what = BUS_SHELF_CHANGE;
            EventBus.getDefault().post(message);
            //同步
//            shelfUploadByWifi(mActivity);
            // 加入书架上报、开启展示更新推送
            if (PlotRead.getAppUser().login()) {
                NetRequest.reportAddShelf(null);
                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), SHOW_SHELF_UPDATE_PUSH + PlotRead.getAppUser().uid, TRUE);
            }
        } catch (Exception e) {
        }
    }


    /**
     * 上传本地书架信息（同步数据）
     */
    public static void uploadBookShelfInfo(){
        if (!PlotRead.getAppUser().login()) {
            return;
        }
        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        List<Work> works = helper.queryAllWithDeleted();
        if (works.size() == ZERO) {
            return;
        }
        JSONArray param = JSONUtil.newJSONArray();
        for (Work work : works) {
            JSONObject child = JSONUtil.newJSONObject();
            JSONUtil.put(child, "wid", work.wid);
            JSONUtil.put(child, "sort", work.lastChapterOrder);
            JSONUtil.put(child, "lastchapter", work.lastChapterId);
            JSONUtil.put(child, "lastchapterpos", work.lastChapterPosition);
            JSONUtil.put(child, "addtime", work.lasttime);
            JSONUtil.put(child, "readtime", work.lasttime);
            JSONUtil.put(child, "deleteflag", work.deleteflag);
            JSONUtil.put(param, child);
        }

        NetRequest.shelfUpload(param, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        SharedPreferencesUtil.putInt(PlotRead.getConfig(), Constant.SHELF_UPLOAD_TIME, ComYou.currentTimeSeconds());
                        cleanShelf();
                    } else {
                        LOG.i(getClass().getSimpleName(), "同步失败");
                    }
                } else if (!TextUtils.isEmpty(serverNo)) {
//                    NetRequest.error(mActivity, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                LOG.i(getClass().getSimpleName(), "同步失败");
            }
        });
    }


    /**
     * 更新书架 更新的数据
     */
    public static void updateLocalShelf(){

        ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);
        final List<Work> works = helper.queryShelf();
        LOG.i("ShelfUtil", "workUpdate size = " + works.size());
        if (works.size() == ZERO) {
            return;
        }
        String ids = "";
        for (Work work : works) {
            ids += work.wid + "##";
        }
        ids = ids.substring(ZERO, ids.lastIndexOf("##"));
        NetRequest.workUpdate(ids, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray booklist = JSONUtil.getJSONArray(result, "booklist");
                    List<Work> shelf = queryShelfWorks();
                    List<Work> update = new ArrayList<>();
                    for (int i = ZERO; booklist != null && i < booklist.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(booklist, i);
                        Work work = new Work();
                        work.wid = JSONUtil.getInt(child, "wid");
                        work.title = JSONUtil.getString(child, "title");
                        work.author = JSONUtil.getString(child, "author");
                        work.cover = JSONUtil.getString(child, "h_url");
                        work.isfinish = JSONUtil.getInt(child, "is_finish");
                        work.totalChapter = JSONUtil.getInt(child, "counts");
                        work.updatetime = JSONUtil.getInt(child, "update_time");


                        for (int j = ZERO; j < shelf.size(); j++) {
                            Work temp = shelf.get(j);
                            if (temp.equals(work)) {
                                temp.title = work.title;
                                temp.author = work.author;
                                temp.cover = work.cover;
                                temp.isfinish = work.isfinish;
                                temp.updatetime = work.updatetime;
                                if (temp.totalChapter != work.totalChapter) {
                                    temp.totalChapter = work.totalChapter;
                                    temp.updateflag = ONE;
                                    update.add(temp);
                                }
                                break;
                            }

                        }
                    }
                    if (update.size() > ZERO) {
//                        insert(mActivity, update,isPush);
                        updateShelf(update);
                    }
                }
//                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_PUSH, FALSE);
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }


    /**
     *
     * @param workList
     */
    public static void updateShelf(List<Work>  workList){

        try {
            //大量作品加入书架
            Log.e("shelfDownload", "insert: 保存到数据库" );

            ShelfSQLiteHelper helper = (ShelfSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SHELF_HELPER);

            for (Work updateWork : workList){
                helper.updateWorkByWid(updateWork);
            }

//            helper.insert(works);
            // 更新时间戳
            SharedPreferencesUtil.putInt(PlotRead.getConfig(), SHELF_TIME, ComYou.currentTimeSeconds());
            // 发送书架变更的通知
            Message message = Message.obtain();
            message.what = BUS_SHELF_CHANGE;
            EventBus.getDefault().post(message);
            //同步
//            shelfUploadByWifi(mActivity);
            // 加入书架上报、开启展示更新推送
//            if (PlotRead.getAppUser().login()) {
//                NetRequest.reportAddShelf(null);
//                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), SHOW_SHELF_UPDATE_PUSH + PlotRead.getAppUser().uid, TRUE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
