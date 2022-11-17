package life.forever.cf.internet;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.net.OkHttpUtil;
import life.forever.cf.publics.tool.AndroidManifestUtil;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeviceUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.LOG;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetRequest implements InterFace {

    private static final String TAG = "NetRequest";

    private static final Hashtable<Integer, int[]> hash;

    static {
        hash = new Hashtable<>();
        hash.put(0, new int[]{0, 5, 9, 15, 22, 28});
        hash.put(1, new int[]{2, 8, 19, 25, 30, 31});
        hash.put(2, new int[]{20, 25, 31, 3, 4, 8});
        hash.put(3, new int[]{25, 31, 0, 9, 13, 17});
        hash.put(4, new int[]{29, 2, 11, 17, 21, 26});
        hash.put(5, new int[]{10, 15, 18, 29, 2, 3});
        hash.put(6, new int[]{5, 10, 15, 17, 18, 22});
        hash.put(7, new int[]{8, 20, 22, 27, 19, 21});
    }

    public static void deviceLogin(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(DEVICE_LOGIN, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    public static void advert(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(ADVERT, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    public static void reportAddShelf(OkHttpResult okHttpResult) {
        String param = base64(Constant.BLANK);
        String url = PlotRead.getINDEX() + path(REPORT_ADD_SHELF, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }




    public static void reportAccessStore(OkHttpResult okHttpResult) {
        String param = base64(Constant.BLANK);
        String url = PlotRead.getINDEX() + path(REPORT_ACCESS_STORE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 发现页接口
     *
     * @param okHttpResult
     */
    public static void discoverRequest(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(DISCOVER, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * Trending页接口
     *
     * @param okHttpResult
     */
    public static void trendingRequest(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(TRENDING, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 发现页--More接口
     *
     * @param okHttpResult
     */
    public static void moreRequest(int rec_id, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "rec_id", rec_id);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(MORE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书库页接口
     *
     * @param okHttpResult
     */
    public static void libraryRequest(String tag_id, String sort_id, List<String> sorts_id, int page, String sex, String is_vip, String is_finish, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "tag_id", tag_id);
        if (sorts_id != null && sorts_id.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (String msort_id : sorts_id) {
                jsonArray.put(msort_id);
            }
            JSONUtil.put(object, "sort_id", jsonArray);
        } else {
            JSONUtil.put(object, "sort_id", sort_id);
        }
        JSONUtil.put(object, "page", page);
        JSONUtil.put(object, "size", Constant.TWENTY);
        JSONUtil.put(object, "sex", sex);
        JSONUtil.put(object, "is_vip", is_vip);
        JSONUtil.put(object, "is_finish", is_finish);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(LIBRARY, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 全部标签接口
     *
     * @param okHttpResult
     */
    public static void tagsRequest(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(TAGALL, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 专题列表接口
     *
     * @param okHttpResult
     */
    public static void projectRequest(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(PROJECT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 分类列表接口
     *
     * @param okHttpResult
     */
    public static void sortlistRequest(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SORTLIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 用户消息类型接口
     *
     * @param okHttpResult
     */
    public static void usermsgRequest(OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "uid", PlotRead.getAppUser().uid);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(USERMSG, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 用户消息列表接口
     *
     * @param okHttpResult
     */
    public static void msglistRequest(int type, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "uid", PlotRead.getAppUser().uid);
        JSONUtil.put(object, "type", type);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(MSGLIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 阅读时长上报
     *
     * @param date
     * @param minute
     * @param okHttpResult
     */
    public static void reportReadTime(String date, int minute, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "uid", PlotRead.getAppUser().uid);
        JSONUtil.put(object, "date", date);
        JSONUtil.put(object, "minute", minute);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(REPORT_READ_TIME, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 领取新手礼包
     *
     * @param okHttpResult
     */
    public static void receiveFreshGift(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(RECEIVE_FRESH_GIFT, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 查询新手礼包
     *
     * @param okHttpResult
     */
    public static void existFreshGift(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(EXIST_FRESH_GIFT, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品修改信息
     *
     * @param wid
     * @param okHttpResult
     */
    public static void workModifyInfo(int wid, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "wid", wid);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(WORK_MODIFY_INFO, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 充值记录
     *
     * @param page
     * @param okHttpResult
     */
    public static void userRechargeRecord(int page, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "page", page);
        JSONUtil.put(object, "size", Constant.TWENTY);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(RECHARGE_RECORD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 消费记录
     *
     * @param page
     * @param okHttpResult
     */
    public static void userConsumeRecord(int page, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "page", page);
        JSONUtil.put(object, "size", Constant.TWENTY);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(CONSUME_RECORD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 删除阅读记录
     *
     * @param okHttpResult
     */
    public static void deleteReadRecord(String bookId, OkHttpResult okHttpResult) {
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "booklist", bookId);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(DELETE_READ_RECORD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取阅读记录
     *
     * @param page
     * @param okHttpResult
     */
    public static void userReadRecord(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(READ_RECORD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 上报阅读记录
     *
     * @param wid
     * @param cid
     * @param timestamp
     * @param okHttpResult
     */
    public static void uploadReadRecord(int wid, int cid, int timestamp, OkHttpResult okHttpResult) {
        JSONObject child = JSONUtil.newJSONObject();
        JSONUtil.put(child, "wid", wid);
        JSONUtil.put(child, "cid", cid);
        JSONUtil.put(child, "newtime", timestamp);
        JSONArray array = JSONUtil.newJSONArray();
        JSONUtil.put(array, child);
        JSONObject object = JSONUtil.newJSONObject();
        JSONUtil.put(object, "booklist", array);
        String param = base64(object.toString());
        String url = PlotRead.getINDEX() + path(UPLOAD_READ_RECORD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }


    /**
     * 版本信息及系统参数
     *
     * @param okHttpResult
     */
    public static void versionParam(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(VERSION_PARAM, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 升级版本信息及系统参数
     *
     * @param okHttpResult
     */
    public static void versionUpdate(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(VERSION_UPDATE, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 系统消息
     *
     * @param page
     * @param okHttpResult
     */
    public static void systemMsgList(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SYSTEM_MSG_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 赞我的
     *
     * @param page
     * @param okHttpResult
     */
    public static void likeMeList(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(LIKE_ME_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 回复我的
     *
     * @param page
     * @param okHttpResult
     */
    public static void replyMeList(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(REPLY_ME_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 我的评论列表
     *
     * @param page
     * @param okHttpResult
     */
    public static void myCommentList(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(MY_COMMENT_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 排行榜列表
     *
     * @param pageType     1：男生 2：女生 3：出版
     * @param rankType
     * @param cycleType    1：日榜 2：周榜 3：月榜 4：总榜
     * @param page
     * @param okHttpResult
     */
    public static void rankList(int pageType, int rankType, int cycleType, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page_type", pageType);
        JSONUtil.put(jsonObject, "rank_type", rankType);
        JSONUtil.put(jsonObject, "cycle_type", 4);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "pagesize", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(RANK_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 排行榜类型
     *
     * @param type         1：男生 2：女生 3：出版
     * @param okHttpResult
     */
    public static void rankType(int type, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page_type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(RANK_TYPE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * @param wid          分享的作品Id
     * @param type         分享类型
     * @param channel      分享渠道：新浪(1)、微信(2)、朋友圈(3)、空间(4)、qq(5)
     * @param content      分享链接
     * @param okHttpResult
     */
    public static void shareUpload(int wid, int type, int channel, String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "themeid", wid);
        JSONUtil.put(jsonObject, "type", type);
        JSONUtil.put(jsonObject, "channel", channel);
        JSONUtil.put(jsonObject, "content", content);
        JSONUtil.put(jsonObject, "success", Constant.ONE);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SHARE_UPLOAD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 章节吐槽列表
     *
     * @param wid
     * @param cid
     * @param page
     * @param okHttpResult
     */
    public static void workChapterCommentList(int wid, int cid, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_CHAPTER_COMMENT_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 订阅单本
     *
     * @param wid
     * @param cid
     * @param okHttpResult
     */
    public static void buySingleWork(int wid, int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SUBCRIPTION_SINGLE_WORK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 批量购买页
     *
     * @param wid
     * @param cid
     * @param okHttpResult
     */
    public static void buyMultiPage(int wid, int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "rec_id", 0);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(PAY_MULTI_PAGE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 批量订阅
     *
     * @param wid
     * @param cid
     * @param count
     * @param okHttpResult
     */
    public static void buyMultiChapter(int wid, int cid, int count, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "count", count);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SUBCRIPTION_MULTI_CHAPTER, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 订阅单章
     *
     * @param wid
     * @param cid
     * @param okHttpResult
     */
    public static void buySingleChapter(int wid, int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SUBCRIPTION_SINGLE_CHAPTER, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 热门章节Id
     *
     * @param wid
     * @param okHttpResult
     */
    public static void workHotIds(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_HOT_IDS, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取可缓存的id
     *
     * @param wid
     * @param okHttpResult
     */
    public static void freeDownloadIds(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(FREE_DOWNLOAD_IDS, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品内容
     *
     * @param wid          作品Id
     * @param cid          章节Id
     * @param okHttpResult
     */
    public static void workContent(int wid, int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_CONTENT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }


    /**
     * 作品内容
     *
     * @param okHttpResult
     */
    public static void workReallyContent(String url, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
//        String url = PlotRead.getINDEX() + path(WORK_CONTENT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品目录
     *
     * @param wid
     * @param start
     * @param count
     * @param okHttpResult
     */
    public static void workCatalog(int wid, int start, int count, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "index", start);
        JSONUtil.put(jsonObject, "num", count);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_CATALOG, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品评论列表
     *
     * @param wid
     * @param page
     * @param order
     * @param okHttpResult
     */
    public static void workCommentList(int wid, int page,int order, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
//        JSONUtil.put(jsonObject, "type", type);
        JSONUtil.put(jsonObject, "order", order);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品热门评论列表
     *
     * @param wid
     * @param okHttpResult
     */
    public static void workHotCommentList(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_HOT_COMMENT_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 评论详细
     *
     * @param wid
     * @param cid
     * @param page
     * @param okHttpResult
     */
    public static void workCommentDetail(int wid, int cid, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_DETAIL, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 点赞
     *
     * @param wid
     * @param cid
     * @param type         1：点赞 2：取消
     * @param okHttpResult
     */
    public static void workCommentLike(int wid, int cid, int type, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_LIKE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 评论管理
     *
     * @param wid
     * @param cid
     * @param type         1:置顶 2:取消置顶 3:加精 4:取消加精 5:删除 6:取消删除
     * @param okHttpResult
     */
    public static void workCommentManage(int wid, int cid, int type, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_MANAGE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 评论管理
     *
     * @param wid
     * @param cid
     * @param type         1禁言   2取消禁言
     * @param okHttpResult
     */
    public static void workCommentBan(int wid, int cid, int type, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_BAN, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 评论举报
     *
     * @param wid
     * @param cid
     * @param reason
     * @param content
     * @param okHttpResult
     */
    public static void workCommentReport(int wid, int cid, String reason, String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "reason", reason);
        JSONUtil.put(jsonObject, "content", content);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_COMMENT_REPORT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 发表评论\回复
     *
     * @param wid
     * @param cid          吐槽章节id
     * @param type         1:评论  2:回复
     * @param pid
     * @param relate_id
     * @param uid
     * @param title
     * @param content
     * @param okHttpResult
     */
    public static void workAddComment(int wid, int cid, int type, int pid, int relate_id, int uid, String title, String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "type", type);
        JSONUtil.put(jsonObject, "pid", pid);
        JSONUtil.put(jsonObject, "relate_id", relate_id);
        JSONUtil.put(jsonObject, "uid", uid);
        JSONUtil.put(jsonObject, "title", title);
        JSONUtil.put(jsonObject, "content", content);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_ADD_WORK_COMMENT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 发表打分评论
     *
     * @param wid
     * @param score
     * @param content
     * @param okHttpResult
     */
    public static void workAddScoreComment(int wid, int score, String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "score", score);
        JSONUtil.put(jsonObject, "content", content);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_ADD_SCORE_COMMENT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 打赏
     *
     * @param wid
     * @param rule_id
     * @param money
     * @param cid
     * @param okHttpResult
     */
    public static void workReward(int wid, int rule_id, int money, int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "rule_id", rule_id);
        JSONUtil.put(jsonObject, "money", money);
        JSONUtil.put(jsonObject, "cid", cid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_REWARD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 打赏信息列表
     *
     * @param okHttpResult
     */
    public static void rewardList(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(WORK_REWARD_LIST, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品信息推荐
     *
     * @param wid          作品Id
     * @param okHttpResult
     */
    public static void workInfoRecommend(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_INFO_RECOMMEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品信息
     *
     * @param wid          作品Id
     * @param recid        推荐位id
     * @param okHttpResult
     */
    public static void workInfo(int wid, int recid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "recid", recid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_INFO, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 粉丝列表
     *
     * @param wid
     * @param page
     * @param okHttpResult
     */
    public static void fansList(int wid, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORK_FANS_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 阅读尾页推荐
     *
     * @param wid
     * @param okHttpResult
     */
    public static void readEndRecommend(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(READ_END_RECOMMEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书架周推荐
     *
     * @param okHttpResult
     */
    public static void shelfWeekRecommend(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(SHELF_WEEK_RECOMMEND, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取作品更新推送
     */
    public static void getWorkPush(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(GET_WORK_PUSH, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 设置作品更新推送
     *
     * @param type         0: 全部No  1:全部Yse 2:单个设置
     * @param wid
     * @param push
     * @param okHttpResult
     */
    public static void setWorkPush(int type, int wid, int push, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "all", type);
        JSONArray array = JSONUtil.newJSONArray();
        if (type == 2) { // 单个设置
            JSONObject child = JSONUtil.newJSONObject();
            JSONUtil.put(child, "wid", wid);
            JSONUtil.put(child, "push", push);
            JSONUtil.put(array, child);
        }
        JSONUtil.put(jsonObject, "set", array);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SET_WORK_PUSH, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 设置作品更新推送
     *
     * @param
     * @param wid
     * @param types 1 书籍推送 2 签到推送 3 推送任务
     * @param okHttpResult
     */
    public static void setWorkPushs(int type, int uid, int wid, int status,int types, String token, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
//        JSONObject child = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "uid", uid);
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "status", status);
//        if (status == 1) {
            JSONUtil.put(jsonObject, "token", token);
//        }
//        JSONUtil.put(array, child);
        JSONUtil.put(jsonObject, "type", types);
//        JSONUtil.put(jsonObject, "all", type);
//        JSONArray array = JSONUtil.newJSONArray();
//
//        if (type == 2) { // 单个设置
//            JSONObject child = JSONUtil.newJSONObject();
//            JSONUtil.put(jsonObject, "uid", uid);
//            JSONUtil.put(jsonObject, "wid", wid);
//            JSONUtil.put(jsonObject, "status", status);
//            if (status == 1) {
//                JSONUtil.put(jsonObject, "token", token);
//            }
//            JSONUtil.put(array, child);
//            JSONUtil.put(jsonObject, "type", types);
//        }
//        JSONUtil.put(jsonObject, "set", array);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SET_WORK_PUSHS, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 热搜词及推荐
     *
     * @param okHttpResult
     */
    public static void searchRecommend(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(SEARCH_RECOMMEND, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 作品更新
     *
     * @param books        作品id用##连接
     * @param okHttpResult
     */
    public static void workUpdate(String books, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "books", books);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SHELF_WORK_UPDATE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书架下载更新
     *
     * @param timestamp
     * @param okHttpResult
     */
    public static void shelfDownload(long timestamp, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "timestamp", timestamp);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SHELF_DOWNLOAD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书架上传同步
     *
     * @param bookshelf
     * @param okHttpResult
     */
    public static void shelfUpload(JSONArray bookshelf, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "bookshelf", bookshelf);
        JSONUtil.put(jsonObject, "timestamp", SharedPreferencesUtil.getInt(PlotRead.getConfig(), Constant.SHELF_TIME));
        String param = base64(jsonObject.toString());
        OkHttpUtil.post(PlotRead.getINDEX() + SHELF_UPLOAD, getParamsString(SHELF_UPLOAD, param), okHttpResult);
    }

    /**
     * 首推
     *
     * @param okHttpResult 回调
     */
    public static void firstRecommend(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(FIRST_RECOMMEND, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 领取任务奖励
     *
     * @param task_id      任务Id
     * @param okHttpResult
     */
    public static void receiveTaskAward(int task_id, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "task_id", task_id);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(RECEIVE_TASK_REWARD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 用户任务
     *
     * @param type         1：新手  2每日  3累计
     * @param okHttpResult
     */
    public static void userTask(int type, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(USER_TASK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 筛选结果
     *
     * @param map
     * @param page
     * @param okHttpResult
     */
    public static void libraryScreenResult(Map<String, Object> map, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "pagesize", Constant.TWENTY);
        for (String key : map.keySet()) {
            JSONUtil.put(jsonObject, key, map.get(key));
        }
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(LIBRARY_SCREEN_RESULT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书库筛选条件
     *
     * @param parent_sortid
     * @param okHttpResult
     */
    public static void libraryScreenCondition(int parent_sortid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "parent_sortid", parent_sortid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(LIBRARY_SCREEN_CONDITION, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 搜索
     *
     * @param keyword
     * @param sort         1：相关度  2：人气
     * @param page
     * @param okHttpResult
     */
    public static void search(String keyword, int sort, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "keyword", keyword);
        JSONUtil.put(jsonObject, "sort", sort);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SEARCH, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书券兑换
     *
     * @param code         兑换码
     * @param okHttpResult
     */
    public static void voucherExchange(String code, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "uid", PlotRead.getAppUser().uid);
        JSONUtil.put(jsonObject, "code", code);
        JSONUtil.put(jsonObject, "type", Constant.ONE);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(VOUCHER_EXCHANGE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书券列表
     *
     * @param type         1：有效  2：无效
     * @param page         分页
     * @param okHttpResult
     */
    public static void voucherList(int type, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "uid", PlotRead.getAppUser().uid);
        JSONUtil.put(jsonObject, "type", type);
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(VOUCHER_LIST, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 充值页列表
     */
    public static void topupList(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(TOPUP, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 充值记录列表
     */
    public static void topupExpend(int type, int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        JSONUtil.put(jsonObject, "type", type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(TOPUPEXPEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 章节解锁记录
     */
    public static void workexpend(int page, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "page", page);
        JSONUtil.put(jsonObject, "size", Constant.TWENTY);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(WORKEXPEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 意见反馈
     *
     * @param phone        手机号
     * @param content      反馈内容
     * @param okHttpResult 回调
     */
    public static void feedback(String phone, String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "phone", phone);
        JSONUtil.put(jsonObject, "content", content);
        JSONUtil.put(jsonObject, "img", Constant.BLANK);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(USER_FEEDBACK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 帮助和反馈 Q&A记录
     */
    public static void problemlist(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(PROBLEM_LIST, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 帮助和反馈 Q&A记录详情
     */
    public static void problemanswer(int id, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "id", id);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(PROBLEM_ANSWER, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 章节反馈
     *
     * @param wid          书籍id
     * @param cid          章节id
     * @param content      内容
     * @param okHttpResult
     */
    public static void chapterFeedback(String wid, String cid,String content, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "type", "2");
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "phone", Constant.BLANK);
        JSONUtil.put(jsonObject, "content", content);
        JSONUtil.put(jsonObject, "img", Constant.BLANK);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(USER_FEEDBACK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书币购买包月
     *
     * @param rmb
     * @param counts
     * @param ruleId
     * @param custom
     * @param okHttpResult
     */
    public static void monthPayByMoney(int rmb, int counts, int ruleId, JSONObject custom, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "money", rmb);
        JSONUtil.put(jsonObject, "counts", counts);
        JSONUtil.put(jsonObject, "rule_id", ruleId);
        JSONUtil.put(jsonObject, "custom", custom);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(MONTH_PAY_BY_MONEY, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 微信购买包月
     *
     * @param rmb
     * @param counts
     * @param ruleId
     * @param custom
     * @param okHttpResult
     */
    public static void monthPayByWx(double rmb, int counts, int ruleId, JSONObject custom, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "rmb", rmb);
        JSONUtil.put(jsonObject, "counts", counts);
        JSONUtil.put(jsonObject, "rule_id", ruleId);
        JSONUtil.put(jsonObject, "custom", custom);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(MONTH_PAY_BY_WX, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }


    /**
     * 支付前创建订单
     *
     * @param rmb
     * @param pay_type
     * @param price_type
     * @param price
     * @param okHttpResult
     */
    public static void doPay(String rmb, String rule_id
            , int pay_type
            , int pay_type_1
            , String price_type
            , String price
            , OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "rmb", rmb);
        JSONUtil.put(jsonObject, "rule_id", rule_id);
        JSONUtil.put(jsonObject, "pay_type", pay_type);
        JSONUtil.put(jsonObject, "pay_type_1", pay_type_1);
        JSONUtil.put(jsonObject, "price_type", price_type);
        JSONUtil.put(jsonObject, "price", price);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(DO_PAY, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 支付
     *
     * @param okHttpResult
     */
    public static void checkOrder(String order_id
            , OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "order_id", order_id);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(QP_CHECK_ORDER_PAY, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * google支付完成
     */
    public static void googlePay(String googleOrderInfo, String sign, String order_id, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "INAPP_PURCHASE_DATA", googleOrderInfo);
        JSONUtil.put(jsonObject, "INAPP_DATA_SIGNATURE", sign);
        JSONUtil.put(jsonObject, "order_id", order_id);
        String param = base64(jsonObject.toString());
        OkHttpUtil.post(PlotRead.getINDEX() + GOOGLE_PAY, getParamsString1("param", param), okHttpResult);
    }


    /**
     * 签到
     *
     * @param okHttpResult
     */
    public static void sign(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(USER_SIGN, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 签到
     *
     * @param okHttpResult
     */
    public static void sign(int week, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "week", week);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(USER_SIGN, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 签到信息
     *
     * @param okHttpResult
     */
    public static void signInfo(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(USER_SIGN_INFO, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 修改用户信息
     *
     * @param avatar       头像地址
     * @param nickname     昵称
     * @param sex          性别
     * @param birthday     生日
     * @param signature    签名
     * @param okHttpResult 结果回调
     */
    public static void modifyUserInfo(String avatar, String nickname, int sex, String birthday, String signature, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "avatar", avatar);
        JSONUtil.put(jsonObject, "nickname", nickname);
        JSONUtil.put(jsonObject, "sex", sex);
        JSONUtil.put(jsonObject, "birthday", birthday);
        JSONUtil.put(jsonObject, "signature", signature);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(MODIFY_USER_INFO, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取用户余额
     *
     * @param okHttpResult 回调
     */
    public static void getUserMoney(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(USER_MONEY, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取用户信息
     *
     * @param okHttpResult 回调
     */
    public static void getUserInfo(OkHttpResult okHttpResult) {
        String url = PlotRead.getINDEX() + path(USER_INFO, Constant.BLANK);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 绑定手机号
     *
     * @param mobile       手机号
     * @param sms_code     验证码
     * @param okHttpResult
     */
    public static void bindPhone(String mobile, String sms_code, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "mobile", mobile);
        JSONUtil.put(jsonObject, "sms_code", sms_code);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(BIND_PHONE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * google登录
     *
     * @param openid       用户id
     * @param nick         QQ获取用户信息的凭证
     * @param okHttpResult 回调
     */
    public static void GoogleLogin(String openid, String nick, String gender, String avatar, String email, String channel, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "openid", openid);
        JSONUtil.put(jsonObject, "nick", nick);
        JSONUtil.put(jsonObject, "gender", gender);
        JSONUtil.put(jsonObject, "avatar", avatar);
        JSONUtil.put(jsonObject, "email", email);
        JSONUtil.put(jsonObject, "channel", channel);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GOOGLE_LOGIN, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 处理服务器状态码
     *
     * @param serverNo
     */
    public static void error(Activity mActivity, String serverNo) {
        if (Constant.SN002.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
        } else if (Constant.SN003.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
        } else if (Constant.SN004.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
            cancelLogin();
            Intent intent = new Intent(mActivity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        } else if (Constant.SN005.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
        } else if (Constant.SN006.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
            cancelLogin();
            Intent intent = new Intent(mActivity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        } else if (Constant.SN009.equals(serverNo)) {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
            cancelLogin();
            Intent intent = new Intent(mActivity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        } else {
            PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet) + serverNo);
        }
    }

    /**
     * 取消登录
     */
    private static void cancelLogin() {
        SharedPreferencesUtil.clear(PlotRead.getAppUser().config);
        SharedPreferencesUtil.remove(PlotRead.getConfig(), Constant.LAST_ID);
        PlotRead.getAppUser().notifyWhenLogin();
        // 发送注销通知
        Message message = Message.obtain();
        message.what = Constant.BUS_LOG_OUT;
        EventBus.getDefault().post(message);
    }


    /**
     * 获取开启自动订阅奖励接口
     *
     * @param okHttpResult
     */
    public static void getAutoTask(int wid,OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(AUTO_TASk, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 阅读章末推荐接口
     *
     * @param okHttpResult
     */
    public static void getReadRecommend(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(READ_RECOMMEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 订阅领取奖励
     *
     * @param okHttpResult
     */
    public static void getDiscount(int task_id, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "task_id", task_id);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_DISCOUNT, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 阅读时长领取奖励
     * @param okHttpResult
     */
    public static void getRewardsReading(int special_id,int cid,int wid,OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "special_id", special_id);
        JSONUtil.put(jsonObject, "cid", cid);
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_REWARD_READING, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 书籍尾端推荐
     *
     * @param okHttpResult
     */
    public static void getBookReadRecommend(int wid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "wid", wid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(RET_READ_RECOMMEND, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取任务列表
     * @param okHttpResult
     */
    public static void getTask(OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_TASK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 通用领取任务奖励
     * @param okHttpResult
     */
    public static void getTaskReward(int taskId ,OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject,"task_id",taskId);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_TASK_REWARD, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 获取任务领取状态
     * @param okHttpResult
     */
    public static void getExistRewardStatus(OkHttpResult okHttpResult){
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_TASK_REWARD_STATE, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }


    /**
     * 设置推送开关接口
     * @param okHttpResult
     */
    public static void setSignSwitch(int uid,int status,String token,int type,OkHttpResult okHttpResult){
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject,"uid",uid);
        JSONUtil.put(jsonObject,"status",status);
        JSONUtil.put(jsonObject,"token",token);
        JSONUtil.put(jsonObject,"type",type);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(SET_WORK_PUSHS,param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url,okHttpResult);
    }


    /**
     * 举报类型
     * @param okHttpResult
     */
    public static void getReportType(OkHttpResult okHttpResult){
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(GET_REPORT_TYPE,param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url,okHttpResult);
    }


    /**
     * 用户反馈  上传举报信息
     * @param uid  用户id
     * @param phone 电话
     * @param content 内容
     * @param note  举报选项
     * @param img  图片数组
     * @param type 1 用户反馈 2 阅读反馈
     * @param type_source  类型 1 反馈 2 举报
     * @param wid         书籍id type=2 必传
     * @param cid          章节id type=2 必传
     * @param okHttpResult
     */
    public static void uploadReportInfo(int uid, String phone, String content, String note, ArrayList<String> img
            ,int type,int type_source,int wid,int cid, OkHttpResult okHttpResult) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "uid", uid);
        JSONUtil.put(jsonObject, "phone", phone);
        JSONUtil.put(jsonObject, "content", content);
        JSONUtil.put(jsonObject, "note", note);
        JSONArray imgarr = JSONUtil.newJSONArray(new Gson().toJson(img));
        JSONUtil.put(jsonObject, "img", imgarr);
        JSONUtil.put(jsonObject, "type", type);
        JSONUtil.put(jsonObject, "type_source", type_source);
        JSONUtil.put(jsonObject, "wid", wid);
        JSONUtil.put(jsonObject, "cid", cid);
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(USER_FEEDBACK, param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url, okHttpResult);
    }

    /**
     * 用户三日内即将过期书券
     * @param okHttpResult
     */
    public static void getOverTimeBook(OkHttpResult okHttpResult){
        JSONObject jsonObject = JSONUtil.newJSONObject();
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + path(OVER_TIME_LIMIT_BOOK,param);
        LOG.i(TAG, url);
        OkHttpUtil.get(url,okHttpResult);
    }











    

    /*-------------------------- 参数、链接处理 --------------------------*/

    /**
     * 拼接url和参数
     *
     * @param path
     * @param param
     * @return
     */
    public static String path(String path, String param) {
        return path + "?" + getParamsString(path, param);
    }

    private static String getParamsString(String path, String param) {
        Map<String, String> params = getParamsMap(path, param);
        String result = "";
        for (String key : params.keySet()) {
            String value = params.get(key);
            result += (key + "=" + value + "&");
        }
        return result.substring(0, result.lastIndexOf("&"));
    }

    public static String getParamsString1(String path, String param) {
        Map<String, String> params = getParamsMap(path, param);
        String result = "";
        for (String key : params.keySet()) {
            String value = params.get(key);
            result += (key + "=" + value + "&");
        }
        return result.substring(0, result.lastIndexOf("&"));
    }

    /**
     * token加密
     *
     * @param token 用户登录token
     * @return 加密结果
     */
    private static String mkToken(String token) {
        StringBuilder result = new StringBuilder();
        if (token.length() >= 9) {
            String mToken = token.charAt(2) + "" + token.charAt(5) + "" + token.charAt(8);
            int mInt = Integer.parseInt(mToken, 16);
            int index = mInt % 8;
            for (int i = 0; i < hash.get(index).length; i++) {
                result.append(token.charAt(hash.get(index)[i]));
            }
        } else {
            if (TextUtils.isEmpty(token)) {
                token = "bycw2018";
            }
            result.append(token);
        }
        return result.toString();
    }

    /**
     * base64加密
     *
     * @param source 字符串
     * @return 加密结果
     */
    public static String base64(String source) {
        byte[] decode = Base64.encode(source.getBytes(), Base64.DEFAULT);
        source = new String(decode);
        return source;
    }

    /**
     * 对加密后的参数进行UrlEncode编码
     *
     * @param source 字符串
     * @return 编码结果
     */
    private static String encode(String source) {
        try {
            source = URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * md5加密
     *
     * @param source 需要MD5加密的字符串
     * @return 加密结果
     */
    public static String MD5(String source) {
        String rst = source;
        try {
            byte[] result = MessageDigest.getInstance("MD5").digest(source.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : result) {
                hex.append(String.format("%02X", b));
            }
            rst = hex.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return rst;
    }

    /**
     * 获取连接参数
     *
     * @param path  请求地址
     * @param param 参数json字符串
     * @return a map
     */
    private static Map<String, String> getParamsMap(String path, String param) {
        Map<String, String> map = new LinkedHashMap<>();
        String apiVersion = AndroidManifestUtil.getApiVersion();
        String versionName = AndroidManifestUtil.getVersionName();
        String sdkVersion = Build.VERSION.RELEASE;
        String channel = AndroidManifestUtil.getChannel();


        String token = mkToken(PlotRead.getAppUser().token);
        int tokenTime = PlotRead.getAppUser().tokenTime;

        // 获取时间戳
        int time = ComYou.currentTimeSeconds();
        // 获取版本号
        String version = apiVersion + "_" + 3 + "_" + versionName + "_" + sdkVersion + "_"
                + tokenTime + "_" + channel + "_" + Constant.APP_CODE;
        // 获取用户id
        int uid = PlotRead.getAppUser().uid;
        // 获取设备号
        String deviceId = DeviceUtil.getAndroidID();
        // 获取签名
        String signature = path + time + uid + param + token;
        signature = MD5(signature);
        // 获取用户性别
        int sex = SharedPreferencesUtil.getInt(PlotRead.getConfig(), Constant.SEX);

        map.put("time", String.valueOf(time));
        map.put("version", version);
        map.put("uid", String.valueOf(uid));
        map.put("deviceid", deviceId);
        map.put("param", encode(param));
        map.put("signature", signature);
        map.put("sex", String.valueOf(sex));
        map.put("umid", MD5(deviceId));
        return map;
    }


}
