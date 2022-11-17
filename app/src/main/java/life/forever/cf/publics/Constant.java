package life.forever.cf.publics;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;

import java.util.Locale;

public interface Constant {

    PlotRead application = PlotRead.getApplication();


    static long getAppVersionCode(Context context) {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return appVersionCode;
    }


    String APP = "app";
    // 用户资料sp文件名，需拼接用户Id
    String USER = "user";
    // 上次登录的用户id
    String LAST_ID = "lastId";
    // 专题存储列表信息
    String INBOXS = "inboxs";
    // 上次登录方式
    String LAST_LOGIN_WAY = "lastLoginWay";
    // 书架操作时间戳
    String SHELF_TIME = "shelfTime";
    //书架内容上传时间戳
    String SHELF_UPLOAD_TIME = "shelfUploadTime";
    // 书架周推荐时间戳
    String SHELF_WEEK_RECOMMEND_TIME = "shelfWeekTime";
    // 书架周推荐内容
    String SHELF_WEEK_RECOMMEND_JSON = "shelfWeekJson";
    // 书架显示
    String SHELF_SHOW = "shelfShow";
    // 系统亮度
    String SCREEN_BRIGHTNESS = "SCREEN_BRIGHTNESS";
    // 是否进入阅读页面
    String FIRST_READ = "isRead";
    // 是否进入阅读页面领取奖励
    String FIRST_READ_BOOK = "isRead_book_time";
    // 是否进入阅读页面第一次
    String FIRST_READ_RECOMMEND = "isRead_book_recommend";
    // 是否进入书架页面
    String FIRST_LIBRARY = "isLibrary";
    String SP_KEY_VISITOR = "isVisitor";
    String NSWELCOMIMGURL = "";
    String ADLENGTH = "5";
    // 是否签到
    String FIRST_SIGN = "isSign";
    // 是否开启全量自动购买
    String IS_STATE = "false";
    // 批量下载
    String IS_AUTO_BUY = "IS_AUTO_BUY";
    // 是否开启通知
    String IS_NOTIFY = "IS_NOTIFY";
    // 首次访问app
    String FIRST_ACCESS = "firstAccess";
    // 首次访问推送
    String FIRST_PUSH = "firstPush";
    // 首次订阅
    String FIRST_SUBSCRIBE = "firstSubscribe";
    // 记录每次google订单
    String DEVELOPERPAYLOAD = "DeveloperPayload";
    // 展示书架更新推送,需拼接用户Id
    String SHOW_SHELF_UPDATE_PUSH = "willShowShelfPush";
    // 书架更新推送已展示,需拼接用户Id
    String SHELF_UPDATE_PUSH_SHOWN = "shelfPushHasShown";
    // 广告数据
    String SPLASH_AD = "ad";
    // 用户性别
    String SEX = "sex";
    // 阅读时长
    String READ_TIME = "readTime";
    // 最后阅读日期
    String READ_DATE = "readDate";
    // 阅读页引导
    String SHOW_READ_GUIDE = "readGuide";
    // 用户需要绑定手机号的标示（当正常退出绑定页面，清除标示，由于杀死进程）
    String NEED_BIND_PHONE = "needBind";

    //新装用户阅读提示
    String IS_NEW_USER_READ = "IS_NEW_USER_READ";
    //新装用户订阅提示
    String IS_NEW_USER_UNCLOCK = "IS_NEW_USER_UNCLOCK";

    int APP_CODE = 1;


    boolean TRUE = true;
    boolean FALSE = false;
    int NEGATIVE_TEN = -10;
    float DOT_FIVE = 0.5F;
    int ZERO = 0;
    int ONE = 1;
    int TWO = 2;
    int THREE = 3;
    int FOUR = 4;
    int FIVE = 5;
    int SIX = 6;
    int SEVEN = 7;
    int EIGHT = 8;
    int NIGHT = 9;
    int TEN = 10;
    int ELEVEN = 11;
    int TWELVE = 12;
    int THIRTEEN = 13;
    int FOURTEEN = 14;
    int FIFTEEN = 15;
    int SIXTEEN = 16;
    int EIGHTTEEN = 18;
    int TWENTY = 20;
    int TWENTY_TWO = 22;
    int TWENTY_FOUR = 24;
    int TWENTY_FIVE = 25;
    int THIRTY = 30;
    int THIRTY_FIVE = 35;
    int THIRTY_SIX = 36;
    int THIRTY_EIGHT = 38;
    int FORTY = 40;
    int FORTY_SiX = 46;
    int FIFTY = 50;
    int FIFTY_THREE = 53;
    int SIXTY = 60;
    int SIXT_THREE = 63;
    int SIXT_SIX = 66;
    int SIXT_EIGHT = 68;
    int SEVENTY_THREE = 73;
    int SEVENTY_FIVE = 75;
    int SEVENTY_SIX = 76;
    int EIGHTY = 80;
    int EIGHTY_FIVE = 85;
    int NINETY = 90;
    int NINETY_SIX = 96;
    int ONE_HUNDRED = 100;
    int ONE_HUNDRED_SIX = 106;
    int ONE_HUNDRED_TWENTY_TWO = 122;
    int ONE_HUNDRED_SIXTY = 160;
    int ONE_HUNDRED_TEN = 110;
    int TWO_HUNDRED = 200;
    int THREE_HUNDRED = 300;
    int FOUR_HUNDRED = 400;

    int FOUR_HUNDRED_THREE = 430;
    int FOUR_HUNDRED_SIX = 455;
    int FIVE_HUNDRED = 500;
    int ONE_THOUSAND = 1000;
    int TWO_THOUSAND = 2000;
    int THREE_THOUSAND = 3000;
    int PAY_SUCCESS = 10000;
    int ONE_WEEK_SECONDS = 7 * 24 * 60 * 60;

    int GUIDE_BUTTON_WIDTH = 275;
    int GUIDE_BUTTON_HEIGHT = FORTY;
    int READ_CECHE = 1000000;

    String SUCCESS = "SUCCESS";
    String BLANK = "";
    String DATE_FORMATTER_1 = "yyyy-MM-dd";
    String DATE_FORMATTER_2 = "yyyyMMdd";
    String DATE_FORMATTER_3 = "yyyy年M月d日";
    String DATE_FORMATTER_4 = "yyyy-MM-dd HH:mm";
    String DATE_FORMATTER_5 = "MMM dd, yyyy";
    String DATE_FORMATTER_6 = "dd/MM HH:mm";
    String DATE_FORMATTER_7 = "MMM dd,yyyy HH:mm:ss";
    String DATE_FORMATTER_8 = "yyyy.MM.dd";
    String DATE_FORMATTER_9 = "HH:mm";
    String DATE_FORMATTER_10 = "MMM dd,yyyy HH:mm";


    String PERMISSION_PHONE_TITLE = application.getString(R.string.phone_permission);
    String PERMISSION_PHONE_HELP = application.getString(R.string.imei_permission);
    String PERMISSION_STORAGE_TITLE = application.getString(R.string.storage_space_permissions);
    String PERMISSION_STORAGE_HELP = application.getString(R.string.down_text);


    String HOME_CLICK_ONCE_MORE_TO_EXIT = String.format(Locale.getDefault(), application.getString(R.string.aiye_exit_tip), application.getString(R.string.app_name));
    String aiye_STRING_SELECT_ALL = application.getString(R.string.aiye_select_all);
    String aiye_STRING_CANCEL_SELECT_ALL = application.getString(R.string.aiye_cancel_select_all);
    String aiye_STRING_SELECT_CLEAR = application.getString(R.string.aiye_clear);
    String aiye_STRING_SETTING = application.getString(R.string.aiye_setting);
    String aiye_STRING_COMPLETE = application.getString(R.string.aiye_complete);
    String aiye_STRING_CANCEL = application.getString(R.string.aiye_cancel);
    String aiye_STRING_SEARCH = application.getString(R.string.aiye_search);
    String aiye_STRING_SCREENING = application.getString(R.string.aiye_screening);
    String aiye_STRING_FANS_LIST = application.getString(R.string.fan_list);
    String aiye_STRING_FANS_RULE = application.getString(R.string.fans_rules);
    String aiye_STRING_LOGIN = application.getString(R.string.login_in);
    String aiye_STRING_MOMO_ORDER = application.getString(R.string.momo_title);
    String aiye_STRING_BIND_PHONE_NUM = application.getString(R.string.bind_phone_number);
    String aiye_STRING_READ_PREFERENCE_SETTING = application.getString(R.string.reading_preferences);
    String aiye_STRING_SUBMIT = application.getString(R.string.aiye_submit);
    String aiye_STRING_REPORT = application.getString(R.string.to_report);
    String aiye_STRING_PUBLISH = application.getString(R.string.published);
    String aiye_STRING_CATALOG = application.getString(R.string.directory);
    String aiye_STRING_RECHARGE = application.getString(R.string.purchase_coins_to_support);
    String aiye_STRING_ACTIVITY_RULE = application.getString(R.string.activity_rules);
    String aiye_STRING_FEEDBACK = application.getString(R.string.feedback);
    String aiye_HELP = application.getString(R.string.help);
    String aiye_STRING_HELP = application.getString(R.string.feed_back);
    String aiye_STRING_SEND_OUT = application.getString(R.string.support);
    String aiye_STRING_JUMP = application.getString(R.string.aiye_jump);

    String aiye_STRING_READ_CONTINUE_ASK = application.getString(R.string.aiye_read_continue_ask);

    String LOGIN_STRING_PRIVATE_POLICY = application.getString(R.string.privacy_policy);
    String LOGIN_STRING_SERVICE_TERMS = application.getString(R.string.terms_service);

    String COMMENT_STRING_ALL_COMMENT = application.getString(R.string.all_comments);
    String COMMENT_STRING_COMMENT_DETAIL = application.getString(R.string.comment_on_details);
    String COMMENT_STRING_PUBLISH_COMMENT = application.getString(R.string.poking_fun);

    String GUIDE_STRING_VISIT_NOW = application.getString(R.string.experience_immediately);

    /*------------------ 【书架】页常量 ------------------*/
    String SHELF_STRING_BOOK_SHELF = application.getString(R.string.my_bookshelf);
    String SHELF_STRING_SELECTED = application.getString(R.string.shelf_edit_selected);
    String SHELF_STRING_DELETES = application.getString(R.string.shelf_edit_pop_deletes);
    String SHELF_STRING_DELETE = application.getString(R.string.shelf_edit_pop_delete);
    String SHELF_STRING_LIST_SHOW = application.getString(R.string.shelf_manage_pop_list_show);
    String SHELF_STRING_GRID_SHOW = application.getString(R.string.shelf_manage_pop_grid_show);
    String SHELF_STRING_READ_HISTORY = application.getString(R.string.shelf_manage_pop_read_history);

    /*------------------ 【推荐】页常量 ------------------*/
    String RECOMMEND_STRING_CHOICE_GOODS = application.getString(R.string.home_activity_recommend);
    String RECOMMEND_STRING_BOY = application.getString(R.string.boy);
    String RECOMMEND_STRING_GIRL = application.getString(R.string.girl);
    String RECOMMEND_STRING_RANKING = application.getString(R.string.ranking);
    String RECOMMEND_STRING_FREE = application.getString(R.string.free);

    String BOOK_LIBRARY = application.getString(R.string.mine_activity_book_Library);
    String BOOK_RECORDING = application.getString(R.string.mine_activity_book_recording);
    String WRITE_ON_Reader = application.getString(R.string.mine_activity_write_on_navelstar);

    String BILL_RECEIVED = application.getString(R.string.bill_activity_received);
    String BILL_CONSUMED = application.getString(R.string.bill_activity_consumed);
    /*榜单*/

    String GENRES = application.getString(R.string.genres_title);
    /*发现、榜单*/
    String DISCOVER = application.getString(R.string.discover_title);
    String TRENDING = application.getString(R.string.trending_title);
    String RANK = application.getString(R.string.rank_title);


    String ANNOUNCEMENT = application.getString(R.string.announcement);
    String MESSAGE = application.getString(R.string.message);

    String RANKING_STRING_RANK = application.getString(R.string.ranking_activity_title);
    String RANKING_STRING_DAY_LIST = application.getString(R.string.ranking_activity_day);
    String RANKING_STRING_WEEK_LIST = application.getString(R.string.ranking_activity_week);
    String RANKING_STRING_MONTH_LIST = application.getString(R.string.ranking_activity_month);
    String RANKING_STRING_TOTAL_LIST = application.getString(R.string.ranking_activity_total);

    /*------------------ 【我的】页常量 ------------------*/

    String MINE_STRING_CLICK_TO_LOGIN = application.getString(R.string.aiye_login);
    String MINE_STRING_USER_ID = application.getString(R.string.mine_user_id);

    String MINE_STRING_SIGN_RULE = application.getString(R.string.sign_in_rules);
    String MINE_STRING_USER_SIGN_WELFARE = application.getString(R.string.mine_sign_welfare);
    String MINE_STRING_CONTINUE_SIGN = application.getString(R.string.mine_sign_continuous);
    String MINE_STRING_SIGN = application.getString(R.string.mine_sign);
    String MINE_STRING_SIGNED = application.getString(R.string.mine_signed);

    String MINE_STRING_DO_TASK = application.getString(R.string.mine_do_task);
    String MINE_STRING_USER_MONEY = application.getString(R.string.mine_user_money);

    String MINE_STRING_RECHARGE = application.getString(R.string.mine_recharge);
    String MINE_STRING_MONTH_VIP_DATE = application.getString(R.string.mine_user_month_date);
    String MINE_STRING_MONTH_OPEN = application.getString(R.string.mine_user_month_open);
    String MINE_STRING_MONTH_CONTINUE = application.getString(R.string.mine_user_month_continue);

    String MINE_STRING_DAY_TASK = application.getString(R.string.date_task);
    String MINE_STRING_GUIDE_TASK = application.getString(R.string.new_task);
    String MINE_STRING_SUM_TASK = application.getString(R.string.the_cumulative_task);
    String TASK_STRING_RECEIVE = application.getString(R.string.task_receive_award);
    String TASK_STRING_COMPLETED = application.getString(R.string.task_completed);
    String TASK_STRING_WAIT_COMPLETED = application.getString(R.string.task_wait_complete);
    String TASK_STRING_AWARD_DESCRIPTION = application.getString(R.string.task_award_description);
    String TASK_STRING_AWARD_VOUCHER = application.getString(R.string.task_award_voucher);
//    String TASK_STRING_AWARD_EXPERIENCE = application.getString(R.string.task_award_experience);

    String MINE_STRING_MY_MESSAGE = application.getString(R.string.my_news);
    String MINE_STRING_MESSAGE_REPLY_ME = application.getString(R.string.reply_me);
    String MINE_STRING_MESSAGE_LIKE_ME = application.getString(R.string.received_praise);
    String MINE_STRING_MESSAGE_SYSTEM = application.getString(R.string.system_message);
    String MINE_STRING_MY_COMMENT = application.getString(R.string.my_comments);

    String MINE_STRING_NGANLUONG = application.getString(R.string.nganluong);
    String MINE_STRING_CARD = application.getString(R.string.card);
    String MINE_STRING_SETTING_AUTO_BUY_MANAGE = application.getString(R.string.book_purchase);
    String MINE_STRING_SETTING_SHELF_PUSH_MANAGE = application.getString(R.string.push_settings);
    String MINE_STRING_SETTING_ABOUT_US = application.getString(R.string.about_us);
    String BOOK_NOTIFICATIONS = application.getString(R.string.book_notifications_updates);
    String MINE_STRING_SETTING_JOIN_US = application.getString(R.string.author_joined);
    String MINE_STRING_PERSONAL_INFO = application.getString(R.string.personal_data);

    String ACCOUNT_STRING_MY_ACCOUNT = application.getString(R.string.my_accounts);
    String ACCOUNT_STRING_RECHARGE_RECORD = application.getString(R.string.prepaid_records);
    String ACCOUNT_STRING_CONSUME_RECORD = application.getString(R.string.consumption_records);
    String ACCOUNT_STRING_MY_VOUCHER = application.getString(R.string.my_book_bean);
    String ACCOUNT_STRING_VOUCHER_HELP = application.getString(R.string.book_bean_show);
    String ACCOUNT_STRING_OVERDUE_VOUCHER = application.getString(R.string.expired_book_bean);
    String ACCOUNT_STRING_VOUCHER_EXCHANGE = application.getString(R.string.change_the_book_bean);
    String ACCOUNT_STRING_VOUCHER_DATE = application.getString(R.string.voucher_enable_date);

    String MONTH_STRING_MY_MONTH_VIP = application.getString(R.string.my_monthly);
    String MONTH_STRING_OPEN_MONTH_VIP = application.getString(R.string.open_monthly);

    /*--------------------- share -----------------------*/

    String WORK_DETAIL_SHARE = application.getString(R.string.watch_it_with_me);
    String DOWNLOAD_SHARE = application.getString(R.string.have_a_book_shortage) + application.getString(R.string.app_name) + "！";

    /*------------------ EventBus Code ------------------*/

    /**
     * 登录
     */
    int BUS_LOG_IN = 10000;
    /**
     * 退出
     */
    int BUS_LOG_OUT = 10001;
    /**
     * 用户信息请求成功
     */
    int BUS_USER_INFO_SUCCESS = 10002;
    /**
     * 用户信息请求失败
     */
    int BUS_USER_INFO_FAILURE = 10003;
    /**
     * 充值成功
     */
    int BUS_RECHARGE_SUCCESS = 10004;
    /**
     * 充值失败
     */
    int BUS_RECHARGE_FAILURE = 10005;
    /**
     * 用户余额发生变化
     */
    int BUS_MONEY_CHANGE = 10006;
    /**
     * 支付宝支付结果
     */
    int BUS_ALI_PAY_RESULT = 10007;
    /**
     * 用户消息数发生变化
     */
    int BUS_MSG_NUM_CHANGE = 10008;
    /**
     * 消息数发生变化
     */
    int MSG_NUM = 100008;
    /**
     * 用户信息修改成功
     */
    int BUS_MODIFY_INFO_SUCCESS = 10009;
    /**
     * 微信支付结果
     */
    int BUS_WX_PAY_RESULT = 10010;
    /**
     * 书券兑换成功
     */
    int BUS_VOUCHER_EXCHANGE_SUCCESS = 10011;
    /**
     * 用户签到状态发生变化
     */
    int BUS_USER_SIGN_STATE_CHANGE = 10013;
    /**
     * 书架变化
     */
    int BUS_SHELF_CHANGE = 10014;
    /**
     * 搜索历史变化
     */
    int BUS_SEARCH_CHANGE = 10015;
    /**
     * 任务状态发生变化
     */
    int BUS_TASK_STATUS_CHANGE = 10016;
    /**
     * 评论发表成功
     */
    int BUS_WORK_COMMENT_ADD_SUCCESS = 10017;
    /**
     * 章节购买成功（用于阅读页取消弹窗）
     */
    int BUS_CHAPTER_BUY_SUCCESS = 10018;
    /**
     * 章节购买失败（用于阅读页取消弹窗）
     */
    int BUS_CHAPTER_BUY_FAIL = 10019;
    /**
     * 批量下载完成
     */
    int BUS_MULTI_DOWNLOAD_COMPLETE = 10020;
    /**
     * 开始请求批量购买
     */
    int BUS_START_MULTI_BUY = 10021;
    /**
     * 批量购买成功
     */
    int BUS_MULTI_BUY_SUCCESS = 10022;
    /**
     * 发送要回复的评论对象
     */
    int BUS_SEND_COMMENT_TO_REPLY = 10023;
    /**
     * 章节吐槽发表成功
     */
    int BUS_SEND_CHAPTER_COMMENT_SUCCESS = 10024;
    /**
     * 评论点赞
     */
    int BUS_COMMENT_ADD_LIKE = 10025;
    /**
     * 评论被删除
     */
    int BUS_COMMENT_DELETE = 10026;
    /**
     * 评论回复
     */
    int BUS_COMMENT_ADD_REPLY = 10027;
    /**
     * 包月成功
     */
    int BUS_MONTH_PAY_SUCCESS = 10028;
    /**
     * 新手礼包领取成功
     */
    int BUS_FRESH_GIFT_RECEIVED = 10029;
    /**
     * 打赏作品成功
     */
    int BUS_REWARD_SUCCESS = 10030;

    /**
     *
     */
    int AD_SUCCESS = 10040;

    /**
     *
     */
    int AD_FAIL = 10041;

    /**
     * 单击书架tab
     */
    int BOOKSHELFTAB = 10051;

    /**
     * 单击发现tab
     */
    int DISCOVERTAB = 10052;

    /**
     *
     */
    int DISCOVERTOPTAB = 10053;
    int TRENDINGTOPTAB = 10054;
    int RANKTOPTAB = 10055;

    /**
     * 单击榜单左侧分类tab
     */
    int RANKLEFTTAB = 10056;

    /**
     * 切换发现tab图标状态
     */
    int DISCOVERTABC = 10057;
    int DISCOVERTABN = 10058;
    int SHOWHISTORY = 10059;
    int HINTHISTORY = 10060;

    /**
     * 阅读历史变化
     */
    int BUS_READ_HISTORY_CHANGE = 10061;
    /**
     * 首页状态栏变化
     */
    int TITLECOLOR = 10062;

    /**
     * 加币成功
     */
    int ADD_BOUNS_SUCCESS = 100058;
    /**
     * 过期书卷提醒
     */
    int BONUS_OVER_LIMIT = 100059;

    /*------------------ PERMISSION REQUEST CODE ------------------*/

    int PERMISSION_STORAGE = 1001;
    int PERMISSION_PHONE = 1002;
    int PERMISSION_CAMERA = 1003;
    int PERMISSION_LOCATION = 1004;
    int PERMISSION_SMS_SEND = 1005;
    int PERMISSION_SETTINGS = 101;
    int PERMISSIONS_REQUEST = 102;

    int CLICK_PAYPAL = 11221;
    int PAYPAL_STARE = 11222;
    int PAYPAL_RESULT = 11223;
    int GOOGLE_PAY = 11224;
    int ADD_SHELF = 11225;
    /*------------------ Color ------------------*/

    int THEME_COLOR = application.getResources().getColor(R.color.theme_color);
    int BILL_COLOR = application.getResources().getColor(R.color.discover_name);
    int BILL_COLOR_NO = application.getResources().getColor(R.color.color_999999);
    int ORANGE = application.getResources().getColor(R.color.orange_color);
    int WHITE = application.getResources().getColor(R.color.colorWhite);
    int GREEN = application.getResources().getColor(R.color.green_color);
    int WHITE69 = application.getResources().getColor(R.color.b0_white_color);
    int WECHAT_COLOR = application.getResources().getColor(R.color.wechat_color);
    int QQ_COLOR = application.getResources().getColor(R.color.qq_color);
    int COLOR_F2F6F7 = application.getResources().getColor(R.color.color_F2F6F7);

    int DARK_1 = application.getResources().getColor(R.color.dark_1_color);
    int DARK_2 = application.getResources().getColor(R.color.dark_2_color);
    int DARK_3 = application.getResources().getColor(R.color.color_999999);
    int DARK_4 = application.getResources().getColor(R.color.discover_name);

    int GRAY_4 = application.getResources().getColor(R.color.gray_4_color);
    int GRAY_3 = application.getResources().getColor(R.color.color_F0F4F4);
    int GRAY_2 = application.getResources().getColor(R.color.gray_2_color);
    int GRAY_1 = application.getResources().getColor(R.color.gray_1_color);

    int BLACK = application.getResources().getColor(R.color.color_000000);
    int color_656667 = application.getResources().getColor(R.color.color_656667);
    int color_2F3031 = application.getResources().getColor(R.color.color_2F3031);
    int color_000001 = application.getResources().getColor(R.color.color_000001);
    /*------------------ User信息 ------------------*/

    String KEY_TOKEN = "token";
    String KEY_TOKEN_TIME = "tokenTime";
    String KEY_HEAD = "head";
    String KEY_NICKNAME = "nickName";
    String KEY_SEX = "sex";
    String KEY_LEVEL = "level";
    String KEY_VIP = "vip";
    String KEY_SIGN_DATE = "signDate";
    String KEY_SIGN_DAYS = "signDays";
    String KEY_MONEY = "money";
    String KEY_VOUCHER = "voucher";
    String KEY_MONTH_VIP = "monthVip";
    String KEY_MONTH_DATE = "monthDate";
    String KEY_MESSAGE_TAG = "messageTag";
    String KEY_MESSAGE_TOTAL = "messageTotal";
    String KEY_BIRTHDAY = "birthday";
    String KEY_SIGNATURE = "signature";
    String KEY_DISCOUNT = "order_discount";
    String KEY_AUTHOR = "author";
    String KEY_AUTHOR_MESSAGE = "author_message";

    String KEY_IS_VISITOR = "isVisitor";

    /*------------------ 阅读设置 ------------------*/

    String KEY_NIGHT_MODE = "nightMode";
    String KEY_MESSAGE_ROLL = "roll";
    String KEY_TEXT_SIZE = "textSize";
    String KEY_FLIP_MODE = "flipMode";
    String KEY_LINE_SPACE = "lineSpace";
    String KEY_BACKGROUND = "background";


    /*------------------ ServerNo ------------------*/

    /**
     * 成功
     */
    String SN000 = "SN000";

    /**
     * 未购买
     */
    String SN031 = "SN031";

    /**
     * 未登录
     */
    String SN006 = "SN006";

    /**
     * token过期
     */
    String SN009 = "SN009";

    /**
     * 全局uid为空
     */
    String SN004 = "SN004";

    /**
     * 签名错误
     */
    String SN005 = "SN005";

    /**
     * 版本号异常
     */
    String SN003 = "SN003";

    /**
     * 请求超时
     */
    String SN002 = "SN002";

    /*------------------ UMeng Event ------------------*/

    /**
     * 书架
     */
    String SJ = "sj";

    /**
     * 书架浏览历史
     */
    String SJ_1 = "sj_1";

    /**
     * 书架更新通知
     */
    String SJ_2 = "sj_2";

    /**
     * 书架签到
     */
    String SJ_3 = "sj_3";

    /**
     * 书架周推荐收入囊中
     */
    String SJ_4 = "sj_4";

    /**
     * 自动购买
     */
    String SP_SET_AUTO = "set_auto";

    /**
     * 精选
     */
    String JX = "jx";

    /**
     * 精选男生
     */
    String JX_1 = "jx_1";

    /**
     * 精选女生
     */
    String JX_2 = "jx_2";

    /**
     * 精选排行
     */
    String JX_3 = "jx_3";

    /**
     * 精选免费
     */
    String JX_4 = "jx_4";

    /**
     * 精选新手礼包
     */
    String JX_5 = "jx_5";

    /**
     * 搜索
     */
    String SS = "ss";

    /**
     * 搜索_精选入口
     */
    String SS_1 = "ss_1";

    /**
     * 搜索_书库入口
     */
    String SS_2 = "ss_2";

    /**
     * 搜索_书架入口
     */
    String SS_3 = "ss_3";

    /**
     * 书库
     */
    String SK = "sk";

    /**
     * 个人中心
     */
    String WO = "wo";

    /**
     * 个人中心-我的消息
     */
    String WO_1 = "wo_1";

    /**
     * 个人中心-签到
     */
    String WO_2 = "wo_2";

    /**
     * 个人中心-充值
     */
    String WO_3 = "wo_3";

    /**
     * 个人中心-开通包月
     */
    String WO_4 = "wo_4";

    /**
     * 个人中心-邀请好友
     */
    String WO_5 = "wo_5";

    /**
     * 个人中心-阅读历史
     */
    String WO_6 = "wo_6";

    /**
     * 个人中心-我的书评
     */
    String WO_7 = "wo_7";

    /**
     * 个人中心-帮助&反馈
     */
    String WO_8 = "wo_8";

    /**
     * 个人中心-设置
     */
    String WO_9 = "wo_9";

    /**
     * 设置-更新通知
     */
    String SZ_1 = "sz_1";

    /**
     * 设置-书籍自动购买
     */
    String SZ_2 = "sz_2";

    /**
     * 设置-清除缓存
     */
    String SZ_3 = "sz_3";

    /**
     * 设置-修改个人资料
     */
    String SZ_4 = "sz_4";

    /**
     * 设置-关于我们
     */
    String SZ_5 = "sz_5";

    /**
     * 设置-推荐给好友
     */
    String SZ_6 = "sz_6";

    /**
     * 作品详情
     */
    String XQ = "xq";

    /**
     * 作品详情-目录
     */
    String XQ_1 = "xq_1";

    /**
     * 作品详情-分享
     */
    String XQ_2 = "xq_2";

    /**
     * 作品详情-打赏
     */
    String XQ_3 = "xq_3";

    /**
     * 作品详情-加书架
     */
    String XQ_4 = "xq_4";

    /**
     * 作品详情-查看全部评论点击
     */
    String XQ_5 = "xq_5";

    /**
     * 作品详情-写评论
     */
    String XQ_6 = "xq_6";

    /**
     * 作品详情-横幅
     */
    String XQ_7 = "xq_7";

    /**
     * 作品详情-同类热门好书
     */
    String XQ_8 = "xq_8";

    /**
     * 作品详情-开始阅读
     */
    String XQ_9 = "xq_9";

    /**
     * 作品详情-书友还喜欢
     */
    String XQ_10 = "xq_10";

    /**
     * 阅读
     */
    String YD = "yd";

    /**
     * 阅读-加标签
     */
    String YD_1 = "yd_1";

    /**
     * 阅读-评论入口
     */
    String YD_2 = "yd_2";

    /**
     * 阅读-目录
     */
    String YD_3 = "yd_3";

    /**
     * 阅读-打赏悬浮按钮
     */
    String YD_4 = "yd_4";

    /**
     * 阅读-阅读设置
     */
    String YD_5 = "yd_5";

    /**
     * 阅读-夜间模式
     */
    String YD_6 = "yd_6";

    /**
     * 阅读-缓存
     */
    String YD_7 = "yd_7";

    /**
     * 阅读-菜单
     */
    String YD_8 = "yd_8";

    /**
     * 阅读-菜单-打赏
     */
    String YD_9_1 = "yd_9_1";

    /**
     * 阅读-菜单-去评论
     */
    String YD_9_2 = "yd_9_2";

    /**
     * 阅读-菜单-分享
     */
    String YD_9_3 = "yd_9_3";

    /**
     * 阅读-菜单-作品详情入口
     */
    String YD_9_4 = "yd_9_4";

    /**
     * 阅读-缓存全部可读章节
     */
    String YD_10 = "yd_10";

    /**
     * 阅读-底部吐槽按钮
     */
    String YD_11 = "yd_11";

    /**
     * 阅读-章节尾页打赏
     */
    String YD_12 = "yd_12";

    /**
     * 阅读-章节尾页分享
     */
    String YD_13 = "yd_13";

    /**
     * 阅读-购买多章
     */
    String YD_14 = "yd_14";

    /**
     * 阅读-去充值
     */
    String YD_15 = "yd_15";

    /**
     * 阅读尾页
     */
    String YDWY = "ydwy";

    /**
     * 阅读尾页-打赏
     */
    String YDWY_1 = "ydwy_1";

    /**
     * 阅读尾页-评论
     */
    String YDWY_2 = "ydwy_2";

    /**
     * 阅读尾页-分享
     */
    String YDWY_3 = "ydwy_3";

    /**
     * 阅读尾页-好书推荐
     */
    String YDWY_4 = "ydwy_4";

    /**
     * 阅读尾页-同类热门好书
     */
    String YDWY_5 = "ydwy_5";

    /**
     * 阅读尾页-免费好书
     */
    String YDWY_6 = "ydwy_6";

    /**
     * 签到-横幅
     */
    String QD = "qd";

    /**
     * 快捷登陆
     */
    String LOGIN_1 = "login_1";

}
