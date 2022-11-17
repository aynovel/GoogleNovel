package life.forever.cf.publics.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import life.forever.cf.activtiy.PlotRead;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.math.BigDecimal;
import java.util.Currency;

import life.forever.cf.publics.Constant;

public class DeepLinkUtil {


    public static FirebaseAnalytics mFirebaseAnalytics;
    public static AppEventsLogger logger;

    @SuppressLint("MissingPermission")
    public static void addPermanent(Context context, String event, String content, String content_type, String deep_link_url,
                                    String novelID, String chapterID, String money, String searchKey, String mWebView_refresh) {

        if (null == mFirebaseAnalytics) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        if (null == logger) {
            logger = AppEventsLogger.newLogger(context);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, content);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, content_type);

        if (!TextUtils.isEmpty(deep_link_url)) {
            bundle.putString("deep_link_url", deep_link_url);
        }
        if (!TextUtils.isEmpty(novelID)) {
            bundle.putString("novelID", novelID);
        }
        if (!TextUtils.isEmpty(chapterID)) {
            bundle.putString("chapterID", chapterID);
        }
        if (!TextUtils.isEmpty(money)) {
            bundle.putString("money", money);
        }

        if (!TextUtils.isEmpty(money)) {
            bundle.putString("money", money);
        }
        if (!TextUtils.isEmpty(searchKey)) {
            bundle.putString("searchKey", searchKey);
        }
        if (!TextUtils.isEmpty(mWebView_refresh)) {
            bundle.putString("mWebView_refresh", mWebView_refresh);
        }
        if ("event_pay_restore".equals(event)) {
            bundle.putString("restore_num", deep_link_url);
        }
        mFirebaseAnalytics.logEvent(event, bundle);
        logger.logEvent(event, bundle);

    }

    /**
     * 暂时修正发现页菜单埋点上报
     * @param context
     * @param event            埋点事件ID
     * @param content          页面
     * @param content_type     事件
     * @param novelid          小说ID
     * @param chapterid        章节ID
     * @param chpternum        解锁章节数
     * @param menuid           菜单
     * @param listid           对应列表排序的榜单
     * @param item_location_id 楼层id
     * @param type             VIP页面类型
     * @param id               ID
     * @param money            充值金额
     * @param restore_num      补单数量
     * @param shareway         分享
     * @param actid            详情页评论操作
     * @param modeid           阅读页点击设置类型
     * @param mode             推荐位样式
     * @param source           点击搜索按钮入口
     * @param searchKey        搜索关键字
     * @param signid           签到
     * @param mWebView_refresh webview页面刷新时长
     * @param
     * @return
     */

    @SuppressLint("MissingPermission")
    public static void addEventPermanent(Context context, String event, String content, String content_type,
                                         String novelid, String chapterid, String chpternum, String menuid,
                                         String listid, String item_location_id, String type, String id,
                                         String money, String restore_num, String shareway, String actid,
                                         String modeid, String mode, String source, String searchKey,
                                         String signid, String mWebView_refresh) {

        if (null == mFirebaseAnalytics) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        if (null == logger) {
            logger = AppEventsLogger.newLogger(context);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, content);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, content_type);

        if (!TextUtils.isEmpty(novelid)) {
            bundle.putString("novelid", novelid);
        }
        if (!TextUtils.isEmpty(chapterid)) {
            bundle.putString("chapterid", chapterid);
        }

        if (!TextUtils.isEmpty(chpternum)) {
            bundle.putString("chpternum", chpternum);
        }

        if (!TextUtils.isEmpty(menuid)) {
            bundle.putString("menuid", menuid);
        }

        if (!TextUtils.isEmpty(listid)) {
            bundle.putString("listid", listid);
        }

        if (!TextUtils.isEmpty(item_location_id)) {
            bundle.putString("item_location_id", item_location_id);
        }

        if (!TextUtils.isEmpty(type)) {
            bundle.putString("type", type);
        }

        if (!TextUtils.isEmpty(id)) {
            bundle.putString("id", id);
        }

        if (!TextUtils.isEmpty(money)) {
            bundle.putString("money", money);
        }

        if (!TextUtils.isEmpty(restore_num)) {
            bundle.putString("restore_num", restore_num);
        }

        if (!TextUtils.isEmpty(shareway)) {
            bundle.putString("shareway", shareway);
        }

        if (!TextUtils.isEmpty(actid)) {
            bundle.putString("actid", actid);
        }

        if (!TextUtils.isEmpty(modeid)) {
            bundle.putString("modeid", modeid);
        }

        if (!TextUtils.isEmpty(mode)) {
            bundle.putString("mode", mode);
        }

        if (!TextUtils.isEmpty(source)) {
            bundle.putString("source", source);
        }

        if (!TextUtils.isEmpty(signid)) {
            bundle.putString("signid", signid);
        }

        if (!TextUtils.isEmpty(searchKey)) {
            bundle.putString("searchKey", searchKey);
        }
        if (!TextUtils.isEmpty(mWebView_refresh)) {
            bundle.putString("mWebView_refresh", mWebView_refresh);
        }
        mFirebaseAnalytics.logEvent(event, bundle);
        logger.logEvent(event, bundle);

    }

    /**
     * facebook 充值埋点
     *
     * @param contentData
     * @param contentId
     * @param contentType
     * @param numItems
     * @param currency
     * @param totalPrice
     */
    public static void logInitiateCheckoutEvent(Context context, String contentData, String contentId, String contentType, int numItems, String currency, double totalPrice) {
        if (null == logger) {
            logger = AppEventsLogger.newLogger(context);
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, 1);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, totalPrice, params);


        BigDecimal decimal = BigDecimal.valueOf(totalPrice);
        Currency currencyParam = Currency.getInstance(currency.toUpperCase());
        logger.logPurchase(decimal, currencyParam, params);
    }

    /**
     * facebook 订阅埋点
     *
     * @param type      币、豆
     * @param chapterId
     * @param account   价格
     */
    public static void subscribe(Context context, String type, String chapterId, double account, String wid, String order) {
        if (null == logger) {
            logger = AppEventsLogger.newLogger(context);
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_ORDER_ID, type);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, chapterId);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SUBSCRIBE, account, params);
        int firstAccess = PlotRead.getConfig().getInt(Constant.FIRST_SUBSCRIBE, 1);
        if (firstAccess == 1) {
            SharedPreferencesUtil.putInt(PlotRead.getConfig(), Constant.FIRST_SUBSCRIBE, 0);
        }

    }


}
