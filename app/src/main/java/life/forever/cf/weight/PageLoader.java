package life.forever.cf.weight;

import static android.graphics.Color.WHITE;
import static life.forever.cf.publics.Constant.EIGHTY;
import static life.forever.cf.publics.Constant.ELEVEN;
import static life.forever.cf.publics.Constant.FALSE;
import static life.forever.cf.publics.Constant.FORTY;
import static life.forever.cf.publics.Constant.GREEN;
import static life.forever.cf.publics.Constant.ONE_HUNDRED_SIXTY;
import static life.forever.cf.publics.Constant.SEVENTY_THREE;
import static life.forever.cf.publics.Constant.THEME_COLOR;
import static life.forever.cf.publics.Constant.TWENTY_TWO;
import static life.forever.cf.activtiy.Cods.Reader_DrawBtn_Corner;
import static life.forever.cf.activtiy.Cods.YYNativeAd_TopBottom_Height;
import static life.forever.cf.activtiy.Cods.YYReadCore_Default_LeftRightMargin;
import static life.forever.cf.activtiy.Cods.YYReadCore_JinCou_LeftRightMargin;
import static life.forever.cf.activtiy.Cods.YYReadCore_Shushi_LeftRightMargin;
import static life.forever.cf.activtiy.Cods.YYReadCore_Songsan_LeftRightMargin;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Message;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.adapter.TxtChapter;
import life.forever.cf.entry.BookBean;
import life.forever.cf.entry.BookRecordBean;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.tab.buildins.UIUtil;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.entry.ParaInPageBean;
import life.forever.cf.interfaces.PageMode;
import life.forever.cf.interfaces.PageStyle;
import life.forever.cf.manage.ReadSettingManager;
import life.forever.cf.datautils.ReadSettings;
import life.forever.cf.activtiy.IOUtils;
import life.forever.cf.activtiy.LogUtils;
import life.forever.cf.activtiy.Cods;
import life.forever.cf.activtiy.RxUtils;
import life.forever.cf.activtiy.ScreenUtils;
import life.forever.cf.activtiy.TimeUtils;
import life.forever.cf.activtiy.ToastUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import life.forever.cf.interfaces.BusC;


/**
 * Created by newbiechen on 17-7-1.
 */

public abstract class PageLoader {
    private static final String TAG = "PageLoader";

    // 当前页面的状态
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_FINISH = 2;          // 加载完成
    public static final int STATUS_ERROR = 3;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;           // 空数据
    public static final int STATUS_PARING = 5;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 6;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // 获取到的目录为空
    // 默认的显示参数配置
    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    public static final int DEFAULT_MARGIN_WIDTH = 15;
    private static final int DEFAULT_TIP_SIZE = 11;
    private static final int EXTRA_TITLE_SIZE = 4;

    //智能断行需要的字符集标记
    private static int KAsciiCharCount = 256;
    private static final int KChsSpecialSymbol = 23;
    private static final char KSpecialChsBegin = 0x2010;
    private static final char KSpecialChsEnd = 0x2026;

    private static final char KFullWidthMarkBegin = 0xff00;
    private static final char KFullWidthMarkEnd = 0xffef;
    private static final char KCjkMarkBegin = 0x3001;
    private static final char KCjkMarkEnd = 0x303F;
    private static final char KAsciiMarkBegin = 0x21;
    private static final char KAsciiMarkEnd = 0x2F;

//    List<>  *_asciiFontWidth;
//    NSMutableArray  *_specialChsFontWidth;
//
//    NSMutableArray *_asciiTitleFontWidth;
//    NSMutableArray *_specialTitleChsFontWidth;
//
//    NSString    *_asciiCharTable;      //ascii字母表
//    NSString    *_specialChsCharTable;   //特殊字符表

    // 当前章节列表
    protected List<TxtChapter> mChapterList;

    // 书本对象
    protected BookBean mCollBook;
    // 监听器
    protected OnPageChangeListener mPageChangeListener;
    protected OnNativeAdListener mNativeAdListener;

    private Context mContext;
    // 页面显示类
    private PageView mPageView;
    // 当前显示的页
    public TxtPage mCurPage;  //当数据没加载出来时，有布局，mCurPage为空
    // 上一章的页面列表缓存
    private List<TxtPage> mPrePageList;
    // 当前章节的页面列表
    private List<TxtPage> mCurPageList;
    // 下一章的页面列表缓存
    private List<TxtPage> mNextPageList;

    // 绘制电池的画笔
    private Paint mBatteryPaint;
    // 绘制提示的画笔
    private Paint mTipPaint;
    // 绘制标题的画笔
    private Paint mTitlePaint;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 绘制小说内容的画笔
    private TextPaint mTextPaint;
    //绘制听书时播放内容的背景的画笔
    private Paint mSpeechBgPaint;
    // 绘制


    // 按钮frame
    private YYFrame mReloadBtnFrame;
    // 按钮frame
    private boolean mReloadBtnIsShown = false;


    //按钮
    private YYFrame mStatusBtnFrame;
    private YYFrame mStatusAutoPayBtnFrame;
    private boolean mStatusBtnIsShown = false;


    // 阅读器的配置选项
    private ReadSettingManager mSettingManager;
    // 被遮盖的页，或者认为被取消显示的页
    private TxtPage mCancelPage;
    // 存储阅读记录类
    private BookRecordBean mBookRecord;

    private Disposable mPreLoadDisp;

    private TextPaint mAdTextPaint;

    /*****************params**************************/
    // 当前的状态
    protected int mStatus = STATUS_LOADING;
    // 判断章节列表是否加载完成
    protected boolean isChapterListPrepare;

    // 是否打开过章节
    private boolean isChapterOpen;
    private boolean isFirstOpen = true;
    private boolean isClose;
    // 页面的翻页效果模式
    private PageMode mPageMode;
    // 加载器的颜色主题
    private PageStyle mPageStyle;
    //当前是否是夜间模式
    private boolean isNightMode;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //排版模式相关的绘制区域周边留白
    private int mDrawTopBottomMargin;
    private int mDrawLeftRightMargin;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //标题的大小
    private int mTitleSize;
    //字体的大小
    private int mTextSize;
    //字间距
    private int mTextMargin;
    //行间距
    private int mTextInterval;
    //标题的行间距
    private int mTitleInterval;
    //段落距离(基于行间距的额外距离)
    private int mTextPara;
    private int mTitlePara;
    //电池的百分比
    private int mBatteryLevel;
    //当前页面的背景
    private int mBgColor;
    //当前听书文字背景颜色
    private int mSpeechBgColor;

    private int mBgResource;
    private int mScreenWidth;
    private int mScreenHeight;

    // 当前章
    protected int mCurChapterPos = 0;
    //上一章的记录
    private int mLastChapterPos = 0;

    private boolean haveDisplayCutout = false;
    private int displaycutoutRight = 0;

    private String mCurFontPath;

    /**
     * 自定义绘制区域
     */
    private Bitmap autoOpenIcon;
    private Bitmap autoCloseIcon;
    private Bitmap recommentDes;
    private Bitmap autoPreferentialIcon;
    private Bitmap autoBuyIcon;
    private Bitmap rewardIcon;


    /**
     * 自动购买
     */
    private boolean isAutoBuySelected = true;

    public boolean noticeUnClockFlag = false;

    private PageTouch mPageStatusTouch;


    /*****************************init params*******************************/
    public PageLoader(PageView pageView, BookBean collBook) {
        mPageView = pageView;
        mContext = pageView.getContext();
        mCollBook = collBook;
        mChapterList = new ArrayList<>(1);

        mPageStatusTouch = new PageTouch();

        // 初始化数据
        initData();
        // 初始化画笔
        initPaint();

        //初始化字符集
        initFontSizeTable();

        // 初始化PageView
        initPageView();
        // 初始化书籍
        prepareBook();
    }

    private void initData() {
        // 获取配置管理器
        mSettingManager = ReadSettingManager.getInstance();
        // 获取配置参数
        // TODO:1.8.1 2021/9/29 1.8.1测试上下翻页
        mPageMode = mSettingManager.getPageMode();
//        mPageMode = PageMode.COVER;
//        mPageMode = PageMode.VERTICAL_COVER;
//        mPageMode = PageMode.SCROLL;
        //滑动模式
//        if (mPageMode==PageMode.AUTO || (mPageMode == PageMode.SCROLL && !JuYueAppUserHelper.getInstance().isNoUserAdFlag())) {
//            mPageMode = PageMode.SIMULATION;
//            mSettingManager.setPageMode(PageMode.SIMULATION);
//        }
        mPageStyle = mSettingManager.getPageStyle();
        // 初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);
        // 配置文字有关的参数
        setUpTextParams(mSettingManager.getTextSize());
        initLayoutModeParm(mSettingManager.getLayoutMode());

        DisplayMetrics dm = PlotRead.getContext().getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = getRealHeight(mContext);

        //挖孔屏处理
//        haveDisplayCutout =
////                SharedPreUtils.getInstance().getBoolean(Constant.HAVEDISPLAYCUTOUT, false);
//                DeviceDataManager.getInstance().getHaveDisplayCutout();
        haveDisplayCutout = false;
        if (haveDisplayCutout) {
//            displaycutoutRight =
////                    SharedPreUtils.getInstance().getInt(Constant.DISPLAYCUTOUT_RIGHT, 0);
//                DeviceDataManager.getInstance().getDisplayCutoutRight();
            displaycutoutRight = 0;
        }

        loadStatusBgBitmpData();
    }


    // TODO: 2021/10/5 1.8.1 状态绘制
    private void loadStatusBgBitmpData() {
        int size = DisplayUtil.dp2px(mContext, EIGHTY);
        int size1 = DisplayUtil.dp2px(mContext, TWENTY_TWO);
        int size2 = DisplayUtil.dp2px(mContext, SEVENTY_THREE);
        int size3 = DisplayUtil.dp2px(mContext, ONE_HUNDRED_SIXTY);
        int size4 = DisplayUtil.dp2px(mContext, FORTY);
        autoOpenIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.read_auto_buy_open_icon);
        autoOpenIcon = Bitmap.createScaledBitmap(autoOpenIcon, size1, size1, FALSE);
        autoPreferentialIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shape_preferential_hint);
        autoPreferentialIcon = Bitmap.createScaledBitmap(autoPreferentialIcon, size2, size1, FALSE);

        autoBuyIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shape_22dp_button);
        int btnW = mScreenWidth - 2 * mMarginWidth;
        autoBuyIcon = Bitmap.createScaledBitmap(autoBuyIcon, btnW, DisplayUtil.dp2px(mContext, 44), FALSE);

        autoCloseIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.read_auto_buy_close_icon);
        autoCloseIcon = Bitmap.createScaledBitmap(autoCloseIcon, size1, size1, FALSE);

        recommentDes = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.recomment_hint_bg);
        recommentDes = Bitmap.createScaledBitmap(recommentDes, size3, size4, FALSE);

        rewardIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.setting_recommend_icon);
        rewardIcon = Bitmap.createScaledBitmap(rewardIcon, size, size, FALSE);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getRealHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (display == null) {
            return 0;
        }
        display.getRealMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 作用：设置与文字相关的参数
     *
     * @param textSize
     */
    private void setUpTextParams(int textSize) {
        // 文字大小
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
//        // 行间距(大小为字体的一半)
//        mTextInterval = mTextSize / 2;
//        mTitleInterval = mTitleSize / 2;
//        // 段落间距(大小为字体的高度)
//        mTextPara = mTextSize;
//        mTitlePara = mTitleSize;
//
//        //字间距
//        mTextMargin = 1;
    }

    /**
     * 作用：设置与排版相关的参数
     *
     * @param layoutMode
     */
    private void initLayoutModeParm(LayoutMode layoutMode) {
        switch (layoutMode) {
            case JincouMode: {
                //行间距
                mTextInterval = ScreenUtils.dpToPx(10);
                //段间距
                mTextPara = ScreenUtils.dpToPx(20);
                //标题行间距
                mTitleInterval = ScreenUtils.dpToPx(20);
                //字间距
                mTextMargin = ScreenUtils.dpToPx(1);
                //标题到第一段距离
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(0);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_JinCou_LeftRightMargin);
            }
            break;
            case ShushiMode: {
                //行间距
                mTextInterval = ScreenUtils.dpToPx(15);
                //段间距
                mTextPara = ScreenUtils.dpToPx(30);
                //标题行间距
                mTitleInterval = ScreenUtils.dpToPx(20);
                //字间距
                mTextMargin = ScreenUtils.dpToPx(1);
                //标题到第一段距离
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(10);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_Shushi_LeftRightMargin);
            }
            break;
            case SongsanMode: {
                //行间距
                mTextInterval = ScreenUtils.dpToPx(20);
                //段间距
                mTextPara = ScreenUtils.dpToPx(40);
                //标题行间距
                mTitleInterval = ScreenUtils.dpToPx(20);
                //字间距
                mTextMargin = ScreenUtils.dpToPx(1);
                //标题到第一段距离
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(5);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_Songsan_LeftRightMargin);
            }
            break;
            case DefaultMode: {

                mTextInterval = mSettingManager.getLineSize();
                mTitleInterval = mSettingManager.getLineSize() + UIUtil.dip2px(mContext, 10);

//                //行间距
//                mTextInterval = ScreenUtils.dpToPx(10);
//                //标题行间距
//                mTitleInterval = ScreenUtils.dpToPx(20);

                //段间距
                mTextPara = ScreenUtils.dpToPx(20);
                //字间距
                mTextMargin = ScreenUtils.dpToPx(1);
                //标题到第一段距离
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(10);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_Default_LeftRightMargin);
            }
            break;
        }
    }

    private void initPaint() {

        mCurFontPath = ReadSettingManager.getInstance().getSelectedFontPath();
        Typeface font;
        if (StringUtils.isBlank(mCurFontPath) || mCurFontPath.equals(Cods.READ_DEFAULT_FONTNAME) || !new File(mCurFontPath).exists()) {
            font = null;
        } else {
            try {
                font = Typeface.createFromFile(mCurFontPath);
            } catch (Exception e) {
                font = null;
            }
        }

        if (ReadSettingManager.getInstance().getPageTypefaceMode() == 1) {
            font = Typeface.createFromAsset(mContext.getAssets(), "roboto.ttf");
        } else {
            font = Typeface.createFromAsset(mContext.getAssets(), "merriweatherr.otf");
        }


        // 绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);
        mTipPaint.setTypeface(font);

        // 绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(font);

        mSpeechBgPaint = new Paint();
        //设置画笔颜色
        mSpeechBgPaint.setColor(mSpeechBgColor);
        //设置它的填充方法，用的多的是FILL 和 STORKE
        mSpeechBgPaint.setStyle(Paint.Style.FILL);

        // 绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTypeface(font);

        //绘制广告内容的画笔
        mAdTextPaint = new TextPaint();
        mAdTextPaint.setColor(mTextColor);
        mAdTextPaint.setTextSize(ScreenUtils.spToPx(20));
        mAdTextPaint.setAntiAlias(true);

        // 绘制背景的画笔
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        // 绘制电池的画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

        // 初始化页面样式
        setNightMode(mSettingManager.isNightMode());
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }

    /****************************** public method***************************/
    /**
     * 跳转到上一章
     *
     * @return
     */
    public boolean skipPreChapter() {
        if (!hasPrevChapter()) {
            return false;
        }
        // 载入上一章。
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 跳转到下一章
     *
     * @return
     */
    public boolean skipNextChapter() {
        if (!hasNextChapter()) {
            return false;
        }

        //判断是否达到章节的终止点
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    public void skipToChapter(int pos) {
        // 设置参数
        mCurChapterPos = pos;

        // 将上一章的缓存设置为null
        mPrePageList = null;
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // 将下一章缓存设置为null
        mNextPageList = null;

        // 打开指定章节
        openChapter();
    }

    /**
     * 跳转到指定的页
     *
     * @param pos
     */
    public boolean skipToPage(int pos) {
        if (!isChapterListPrepare) {
            return false;
        }
        mCurPage = getCurPage(pos);
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 翻到上一页
     *
     * @return
     */
    public boolean skipToPrePage() {
        return mPageView.autoPrevPage();
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    public boolean skipToNextPage() {
        return mPageView.autoNextPage();
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 强制刷新
     */
    public void forceUpdatePage() {
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    /**
     * 更新电量
     *
     * @param level
     */
    public void updateBattery(int level) {
        mBatteryLevel = level;

        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 设置提示的文字大小
     *
     * @param textSize:单位为 px。
     */
    public void setTipTextSize(int textSize) {
        mTipPaint.setTextSize(textSize);

        // 如果屏幕大小加载完成
        mPageView.drawCurPage(false);
    }

    public void setReadFont() {
//        String curFontPath = ReadSettingManager.getInstance().getSelectedFontPath();
//        if (StringUtils.isNotBlank(curFontPath) && curFontPath.equals(mCurFontPath)) {
//            //目标字体文件路径相同则不做处理
//            return;
//        }

        // 设置文字相关参数
//
//        MobclickAgent.onEvent(App.getContext(), Constant.UMEVENT_READFONT);


        saveRecord();
        mCurFontPath = "";
        Typeface font;
        if (StringUtils.isBlank(mCurFontPath) || mCurFontPath.equals(Cods.READ_DEFAULT_FONTNAME) || !new File(mCurFontPath).exists()) {
            font = null;
        } else {
            try {
                font = Typeface.createFromFile(mCurFontPath);
            } catch (Exception e) {
                font = null;
            }
        }

        if (ReadSettingManager.getInstance().getPageTypefaceMode() == 1) {
            font = Typeface.createFromAsset(mContext.getAssets(), "roboto.ttf");
        } else {
            font = Typeface.createFromAsset(mContext.getAssets(), "merriweatherr.otf");
        }


        mTipPaint.setTypeface(font);

        mTextPaint.setTypeface(font);

        mTitlePaint.setTypeface(font);
        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //非正常情况
                mStatus = STATUS_ERROR;
            } else {
                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // 重新获取指定页面
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

        mPageView.drawCurPage(false);
    }

    /*
     * 全屏配置改变
     */
    public void fullScreenChanged() {
        setTextSize(mTextSize);
    }

    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        // 设置文字相关参数

        saveRecord();

        setUpTextParams(textSize);

        // 设置画笔的字体大小
        mTextPaint.setTextSize(mTextSize);
        // 设置标题的字体大小
        mTitlePaint.setTextSize(mTitleSize);
        // 存储文字大小
        mSettingManager.setTextSize(mTextSize);
        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //非正常情况
                mStatus = STATUS_ERROR;
            } else {
                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // 重新获取指定页面
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置文字相关参数
     *
     * @param lineSize
     */
    public void setLineSize(int lineSize) {
        saveRecord();

        // 设置文字相关参数
        mSettingManager.setLineSize(lineSize);
        mTextInterval = mSettingManager.getLineSize();
        mTitleInterval = mSettingManager.getLineSize() + UIUtil.dip2px(mContext, 10);

        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
            }

            // 重新获取指定页面
            mCurPage = mCurPageList.get(mCurPage.position);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置夜间模式
     *
     * @param nightMode
     */
    public void setNightMode(boolean nightMode) {
        mSettingManager.setNightMode(nightMode);
        isNightMode = nightMode;

        if (isNightMode) {
            mBatteryPaint.setColor(Color.WHITE);
            setPageStyle(PageStyle.NIGHT);
        } else {
            mBatteryPaint.setColor(Color.BLACK);
            setPageStyle(mPageStyle);
        }
    }

    /**
     * 设置页面样式
     *
     * @param pageStyle:页面样式
     */
    public void setPageStyle(PageStyle pageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle;
            mSettingManager.setPageStyle(pageStyle);
        }

        if (isNightMode && pageStyle != PageStyle.NIGHT) {
            return;
        }


        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(mContext, pageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(mContext, pageStyle.getBgColor());
//        mBgResource = pageStyle.getBgDrawable();
        mBgResource = 0;

//        if (pageStyle == PageStyle.NIGHT) {
//            mSpeechBgColor = mContext.getResources().getColor(R.color.jy_read_menu_bg_night);
//        }else {
//            mSpeechBgColor = mContext.getResources().getColor(R.color.jy_read_menu_bg);
//        }

        mTipPaint.setColor(mTextColor);
        mTitlePaint.setColor(mTextColor);
        mTextPaint.setColor(mTextColor);
        mSpeechBgPaint.setColor(mSpeechBgColor);

        mBgPaint.setColor(mBgColor);
        mPageView.drawCurPage(false);
    }

    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        Boolean needReDrow = false;
        if (mPageMode == PageMode.SCROLL || pageMode == PageMode.SCROLL) {
            //有scroll就要重新绘制,非scroll之间的动画切换，不重画，提高性能
            needReDrow = true;
        }

        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);
        mSettingManager.setPageMode(mPageMode);

        // 重新绘制当前页
        if (needReDrow) {
            mPageView.drawCurPage(false);
        }
    }

    public void setTemporaryPageMode(PageMode pageMode) {
        Boolean needReDrow = false;
        if (mPageMode == PageMode.SCROLL || pageMode == PageMode.SCROLL) {
            //有scroll就要重新绘制,非scroll之间的动画切换，不重画，提高性能
            needReDrow = true;
        }

        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);

        // 重新绘制当前页
        if (needReDrow) {
            mPageView.drawCurPage(false);
        }
    }

    /**
     * 是否正在自动阅读
     */
    public boolean isAutoRead() {
        return mPageView.isAutoRead();
    }


    /**
     * 开始自动阅读
     */
    public void startAutoRead() {
        mPageView.setPageMode(PageMode.AUTO);
        mPageView.drawCurPage(false);
        mPageView.startAutoRead();
    }

    /**
     * 关闭自动阅读
     */
    public void endAutoRead() {
        mPageView.setPageMode(mPageMode);
        mPageView.drawCurPage(false);
        mPageView.endAutoRead();
    }


    /**
     * 排版模式
     *
     * @param layoutMode:排版模式
     * @see LayoutMode
     */
    public void setLayoutMode(LayoutMode layoutMode) {
        saveRecord();
        initLayoutModeParm(layoutMode);
        mSettingManager.setLayoutMode(layoutMode);
        mPrePageList = null;
        mNextPageList = null;

        // 重设广告 size
        if (mDisplayWidth > 0) {
            if (mNativeAdListener != null) {
                mNativeAdListener.updateNativeAdSize();
            }
        }

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                mStatus = STATUS_ERROR;
            } else {
                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // 重新获取指定页面
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }
        mPageView.drawCurPage(false);
    }


    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    public void setMargin(int marginWidth, int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        // 如果是滑动动画，则需要重新创建了
        if (mPageMode == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
    }

    public void setmNativeAdListener(@Nullable OnNativeAdListener mNativeAdListener) {
        this.mNativeAdListener = mNativeAdListener;
    }

    //重新计算并绘制页面
    public void refreshPage(int speechingParaIndex) {

        //页面为准备好时不做处理
        if (mCurPage == null) {
            return;
        }

        saveRecord();

        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //非正常情况
                mStatus = STATUS_ERROR;
            } else {
                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // 重新获取指定页面
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

//        mCurPage.setSpeechingPara(speechingParaIndex);
        mPageView.drawCurPage(false);
    }


    /**
     * 获取当前页的状态
     *
     * @return
     */
    public int getPageStatus() {
        return mStatus;
    }

    /**
     * 获取书籍信息
     *
     * @return
     */
    public BookBean getCollBook() {
        return mCollBook;
    }

    public void setCollBook(BookBean collBook) {
        this.mCollBook = collBook;
    }

    /**
     * 获取章节目录。
     *
     * @return
     */
    public List<TxtChapter> getChapterCategory() {
        return mChapterList;
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        if (mCurPage == null) {
            return 0;
        }
        return mCurPage.position;
    }

    /**
     * 获取当前章节的页数
     *
     * @return
     */
    public int getCurChapterPageCount() {
        if (mCurPageList == null) {
            return 0;
        }
        return mCurPageList.size();
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }

    /**
     * 保存阅读记录
     */
    public void saveRecord() {

        if (mChapterList.isEmpty()) {
            return;
        }

        mBookRecord.setWid(mCollBook.wid);
        mBookRecord.setChapterIndex(mCurChapterPos);
        if (mCurPage == null || mCurPage.lineInfos == null || mCurPage.lineInfos.size() <= 0) {
            mBookRecord.setChapterCharIndex(0);
        } else {
            mBookRecord.setChapterCharIndex(mCurPage.lineInfos.get(0).getmStartPos());
        }

        if (mCurPage != null && mCurPage.lineInfos.size() > 0) {
            mBookRecord.setChapterCharIndex(mCurPage.lineInfos.get(0).getmStartPos());
        } else {
            mBookRecord.setChapterCharIndex(0);
        }

        //存储到数据库
        DBUtils.getInstance().saveBookRecordWithAsync(mBookRecord);
    }

    /**
     * 初始化书籍
     */
    private void prepareBook() {
        mBookRecord = DBUtils.getInstance()
                .getBookRecord(mCollBook.wid);

        if (mBookRecord == null) {
            mBookRecord = new BookRecordBean();
        }

        mCurChapterPos = mBookRecord.getChapterIndex();
        mLastChapterPos = mCurChapterPos;
    }

    private int getPagePosWithCharPos(int chapterPos) {
        if (chapterPos < 0 || mChapterList == null || mCurPageList == null) {
            return 0;
        }
        for (int i = 0; i < mCurPageList.size(); i++) {
            TxtPage page = mCurPageList.get(i);
            int lineCount = page.lineInfos.size();
            LineInfo lastLine = page.lineInfos.get(lineCount - 1);
            if (chapterPos < lastLine.getmStartPos() + lastLine.getmCharCount()) {
                return i;
            }
        }
        return mCurPageList.size() - 1;
    }

    /*
     *  获取书签
     */
    public BookRecordBean getBookRecord() {
        if (mChapterList.isEmpty()) {
            return null;
        }

        BookRecordBean recordBean = new BookRecordBean();
        recordBean.setWid(mCollBook.wid);
        recordBean.setChapterIndex(mCurChapterPos);
        if (mCurPage != null && mCurPage.title != null) {
            recordBean.setTitle(mCurPage.title);
        }
        if (mCurPage == null || mCurPage.lineInfos == null || mCurPage.lineInfos.size() <= 0) {
            recordBean.setChapterCharIndex(0);
        } else {
            recordBean.setChapterCharIndex(mCurPage.lineInfos.get(0).getmStartPos());
        }

        if (mCurPage != null) {
            recordBean.setChapterCharIndex(mCurPage.position);
        } else {
            recordBean.setChapterCharIndex(0);
        }

        return recordBean;
    }

    /*
     *  设置书签
     */
    public void updateBookRecord(BookRecordBean record) {
        mBookRecord = record;

        if (mBookRecord == null) {
            mBookRecord = new BookRecordBean();
        }

        mCurChapterPos = mBookRecord.getChapterIndex();
        mLastChapterPos = mCurChapterPos;
    }

    /**
     * 打开指定章节
     */
    public void openChapter() {
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mChapterList.isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                int position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());
//                int position = mBookRecord.getPagePos();

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // 切换状态
                isChapterOpen = true;
            } else {
                mCurPage = getCurPage(0);
            }
        } else {
            mCurPage = new TxtPage();
        }

        mPageView.drawCurPage(false);
    }

    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    /**
     * 关闭书本
     */
    public void closeBook() {
        isChapterListPrepare = false;
        isClose = true;

        if (bgBitmap != null && !bgBitmap.isRecycled()) {
            bgBitmap.recycle();
            bgBitmap = null;
        }

        if (autoBuyIcon != null && !autoBuyIcon.isRecycled()) {
            autoBuyIcon.recycle();
            autoBuyIcon = null;
        }

        if (autoCloseIcon != null && !autoCloseIcon.isRecycled()) {
            autoCloseIcon.recycle();
            autoCloseIcon = null;
        }

        if (autoOpenIcon != null && !autoOpenIcon.isRecycled()) {
            autoOpenIcon.recycle();
            autoOpenIcon = null;
        }

        if (recommentDes != null && !recommentDes.isRecycled()) {
            recommentDes.recycle();
            recommentDes = null;
        }

        if (autoPreferentialIcon != null && !autoPreferentialIcon.isRecycled()) {
            autoPreferentialIcon.recycle();
            autoPreferentialIcon = null;
        }

        if (rewardIcon != null && !rewardIcon.isRecycled()) {
            rewardIcon.recycle();
            rewardIcon = null;
        }


        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        clearList(mChapterList);
        clearList(mCurPageList);
        clearList(mNextPageList);

        mChapterList = null;
        mCurPageList = null;
        mNextPageList = null;
        mPageView = null;
        mCurPage = null;
    }

    public void cleanUp() {
        isChapterListPrepare = false;
        clearList(mChapterList);
        clearList(mCurPageList);
        clearList(mNextPageList);

        mChapterList = null;
        mCurPageList = null;
        mNextPageList = null;
        mCurPage = null;
        isChapterOpen = false;
//        mBookRecord.setPagePos(0);
        mBookRecord.setChapterIndex(0);
    }

    private void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    public boolean isClose() {
        return isClose;
    }

    public boolean isChapterOpen() {
        return isChapterOpen;
    }


    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos) throws Exception {
        // 获取章节
        TxtChapter chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }

        // TODO:1.8.1 2021/9/29 1.8.1测试页面状态绘制


        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);
        List<TxtPage> chapters = null;
        if (reader != null) {
            chapters = loadPages(chapter, reader);

            if (!realHasChapterData(chapter)) {
                for (TxtPage tempPage :
                        chapters) {
                    tempPage.setShowStatusFlag(true);
                    tempPage.setmPageStatus(chapter.getmChatperStatusInfo());
                }
            } else {
                if (chapter.getmChatperStatusInfo() != null) {
                    chapter.setmChatperStatusInfo(null);
                    chapters = loadPages(chapter, reader);
                }
            }

            if (chapters.size() <= 0) {

            }
        }
        return chapters;
    }

    /*******************************abstract method***************************************/

    /**
     * 刷新章节列表
     */
    public abstract void refreshChapterList();

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    protected abstract BufferedReader getChapterReader(TxtChapter chapter) throws Exception;

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData(TxtChapter chapter);


    protected abstract boolean realHasChapterData(TxtChapter chapter);

    /***********************************default method***********************************************/

    void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageView.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap, false);
        }
        //更新绘制
        mPageView.postInvalidate();
    }


    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        if (bitmap == null) {
            //当mPageAnim为null或者内存溢出时会导致bitmap为null，此时直接return
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        int tipMarginHeight = ScreenUtils.dpToPx(3);
        if (!isUpdate) {
            /****绘制背景****/
            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            } else {
                drawBackgroundImg(canvas, false);
            }
            if (!mChapterList.isEmpty()) {
                /*****初始化标题的参数********/
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                float tipTop = tipMarginHeight + mTipPaint.getFontSpacing();
                //根据状态不一样，数据不一样
                if (mStatus != STATUS_FINISH) {
                    if (isChapterListPrepare) {
                        if (mCurChapterPos >= mChapterList.size()) {
                            int pos = mChapterList.size() - 1;
                            String title = mChapterList.get(pos).getTitle();

                            int tiptitleStart = mMarginWidth;
                            if (haveDisplayCutout) {
                                tiptitleStart = displaycutoutRight + mMarginWidth;
                            }
                            canvas.drawText(title, tiptitleStart, tipTop, mTipPaint);
                        } else {

                            int tiptitleStart = mMarginWidth;
                            if (haveDisplayCutout) {
                                tiptitleStart = displaycutoutRight + mMarginWidth;
                            }

                            String title = "";
                            if (mCurChapterPos >= 0 && mCurChapterPos < mChapterList.size()) {
                                title = mChapterList.get(mCurChapterPos).getTitle();
                            }

                            canvas.drawText(title
                                    , tiptitleStart, tipTop, mTipPaint);
                        }
                    }
                } else {
                    int tiptitleStart = mMarginWidth;
                    if (haveDisplayCutout) {
                        tiptitleStart = displaycutoutRight + mMarginWidth;
                    }

                    if (mCurPage.title != null) {
                        canvas.drawText(mCurPage.title, tiptitleStart, tipTop, mTipPaint);
                    }
                }

                /******绘制页码********/
                // 底部的字显示的位置Y
                float y = mDisplayHeight - tipMarginHeight - ScreenUtils.dpToPx(2);
                // 只有finish的时候采用页码
                if (mStatus == STATUS_FINISH) {
                    String percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
                    canvas.drawText(percent, mMarginWidth, y, mTipPaint);
                }
            }
        } else {
            //擦除区域
            if (mBgResource != 0) {
                drawBackgroundImg(canvas, true);
            } else {
                mBgPaint.setColor(mBgColor);
                // TODO: 1/14/21 底部颜色覆盖问题 
//                canvas.drawRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
            }
        }

        /******绘制电池********/

        int visibleRight = mDisplayWidth - mMarginWidth;
        int visibleBottom = mDisplayHeight - tipMarginHeight;

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;

        //电极的制作
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));

        mBatteryPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(polar, mBatteryPaint);

        //外框的制作
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);

        //内框的制作
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);

        /******绘制当前时间********/
        //底部的字显示的位置Y
        float y = mDisplayHeight - tipMarginHeight - ScreenUtils.dpToPx(2);
        String time = TimeUtils.currentTimeFormat(System.currentTimeMillis(), Cods.FORMAT_TIME);
        float x = outFrameLeft - mTipPaint.measureText(time) - ScreenUtils.dpToPx(4);

        if (isUpdate) {
            canvas.drawRect(x,
                    mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(4),
                    outFrameLeft - ScreenUtils.dpToPx(2),
                    mDisplayHeight,
                    mBgPaint);
        }

        boolean showTimeFlag = false;
        if (showTimeFlag) {
            canvas.drawText(time, x, y, mTipPaint);
        }
    }

    private Bitmap bgBitmap;
    private int mBgBitmapHeight = 0;

    private void drawBackgroundImg(Canvas canvas, Boolean isUpdate) {
        if (canvas == null) {
            return;
        }
        try {

            if (bgBitmap == null || mBgBitmapHeight != mDisplayHeight) {

                if (bgBitmap != null && !bgBitmap.isRecycled()) {
                    bgBitmap.recycle();
                    bgBitmap = null;
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 4;

                Bitmap resourceBitmap = BitmapFactory.decodeResource(PlotRead.getContext().getResources(), mBgResource, options);
                bgBitmap = Bitmap.createScaledBitmap(resourceBitmap, mDisplayWidth, mDisplayHeight, true);
                mBgBitmapHeight = mDisplayHeight;
                if (resourceBitmap != null && !resourceBitmap.isRecycled()) {
                    resourceBitmap.recycle();
                    resourceBitmap = null;
                }
            }
            if (isUpdate) {
                canvas.save();
                canvas.clipRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight);
                canvas.drawBitmap(bgBitmap, 0, 0, null);
                canvas.restore();
            } else {
                canvas.drawBitmap(bgBitmap, 0, 0, null);
            }

//            if (bgBitmap != null && !bgBitmap.isRecycled()){
//                bgBitmap.recycle();
//                bgBitmap = null;
//            }
        } catch (Exception e) {
            //非正常情况, 画白色背景
            canvas.drawColor(Color.WHITE);
        }
    }

    private void drawContent(Bitmap bitmap, boolean isUpdate) {
        if (bitmap == null) {
            //当mPageAnim为null或者内存溢出时会导致bitmap为null，此时直接return
            return;
        }

        Canvas canvas = new Canvas(bitmap);

        if (mPageMode == PageMode.SCROLL) {

            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            } else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
//                drawBackgroundImg(canvas, false);
            }
        }
        /******绘制内容****/
        mReloadBtnFrame = null;
        mReloadBtnIsShown = false;

        mStatusBtnFrame = null;
        mStatusAutoPayBtnFrame = null;
        mStatusBtnIsShown = false;

        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "Loading ...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击按钮重试)";
                    tip = PlotRead.getApplication().getString(R.string.reload_the);
                    mReloadBtnIsShown = true;
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    tip = PlotRead.getApplication().getString(R.string.reload_the) + "Content";
                    mReloadBtnIsShown = true;
                    break;
                case STATUS_PARING:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }

            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = (mDisplayHeight - textHeight) / 2;

            if (mReloadBtnIsShown) {
                mPageView.cleanAdView();
                Paint btnTextPaint = new TextPaint();
                btnTextPaint.setColor(Color.WHITE);
                btnTextPaint.setTextSize(ScreenUtils.dpToPx(16));
                btnTextPaint.setAntiAlias(true);

                // 绘制按钮的画笔
                Paint mButtonPaint = new Paint();
                mButtonPaint.setColor(mContext.getResources().getColor(R.color.color_F97D20));
                mButtonPaint.setStyle(Paint.Style.FILL);

                int btnWidth = mScreenWidth - 2 * mMarginWidth;
                ;
                int btnHeight = ScreenUtils.dpToPx(44);
                int btnX = (mDisplayWidth - btnWidth) / 2;
                int btnY = (mDisplayHeight - btnHeight) / 2;
                RectF rectF = new RectF(btnX, btnY, btnX + btnWidth, btnY + btnHeight);

                canvas.drawRoundRect(rectF, ScreenUtils.dpToPx(6), ScreenUtils.dpToPx(6), mButtonPaint);

                int TipW = (int) btnTextPaint.measureText(tip);

                canvas.drawText(tip, btnX + (btnWidth - TipW) / 2, btnY + ScreenUtils.dpToPx(14) + ScreenUtils.dpToPx(14), btnTextPaint);

                mReloadBtnFrame = new YYFrame(btnX, btnY, btnWidth, btnHeight);

                ToastUtils.show(PlotRead.getApplication().getString(R.string.load_fail));
            } else {
                canvas.drawText(tip, pivotX, pivotY, mTextPaint);
            }
        } else {
            float top;
            int paraIndex = 0;

            if (mPageMode == PageMode.SCROLL) {
                top = -mTextPaint.getFontMetrics().top;
            } else {
                top = mMarginHeight + mTextPaint.getFontSpacing() + mDrawTopBottomMargin;
            }


            //设置总距离
            //行距离
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            //段距离
            int para = mTextPara + (int) mTextPaint.getTextSize();
            //title行之间距离
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            //title与正文距离
            int titlePara = mTitlePara + (int) mTitlePaint.getTextSize();
            String str = null;

            //对标题进行绘制
            for (int i = 0; i < mCurPage.titleLines; ++i) {
                LineInfo aLine = mCurPage.lineInfos.get(i);
                str = aLine.getmLineText();

                //设置顶部间距
                // TODO: 2021/10/12 1.8.1 标题上间距
                if (i == 0) {
                    top += mTitlePara;
                }

                //计算文字显示的起始点
                float strWidth = getStrWidth(aLine);
//                float start = mMarginWidth + mDrawLeftRightMargin + (mVisibleWidth - strWidth - 2 * mDrawLeftRightMargin) / 2;

                float start = mMarginWidth + mDrawLeftRightMargin;
                //进行绘制
                for (int j = 0; j < str.length(); j++) {
                    String a = str.substring(j, j + 1);
                    canvas.drawText(a, start, top, mTitlePaint);
                    char aa = a.charAt(0);
                    start += aLine.getmAdjustOffset() + getCharWidth(aa, aLine.getmLineType());
                }

                //设置尾部间距
                if (i == mCurPage.titleLines - 1) {
                    top += mTitlePara + mTextPaint.getTextSize();
                } else {
                    //行间距
                    top += mTitleInterval + mTitlePaint.getTextSize();
                }
            }

//            if (mCurPage.getAdType() == LineInfo.LineAdType.LineAdTypeNone){
            mPageView.cleanAdView();
//            }

            //对内容进行绘制
            for (int i = mCurPage.titleLines; i < mCurPage.lineInfos.size(); ++i) {
                LineInfo aLine = mCurPage.lineInfos.get(i);

                str = aLine.getmLineText();

                float startX = mMarginWidth + mDrawLeftRightMargin;
                float speechBackStartX = startX;
                if (aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
//                    startX += 2 * mTextSize;
//                    speechBackStartX += 2 * mTextSize;

                    startX += 1 * mTextSize;
                    speechBackStartX += 1 * mTextSize;
                }


                if (isEmoji(str)) {
                    canvas.drawText(str, startX, top, mTextPaint);
                } else {
                    // TODO: 2021/10/12  emoji 支持
                    for (int j = 0; j < str.length(); j++) {
//                    if (mPageView.getIsBookSpeeching() && paraIndex == mCurPage.getSpeechingPara()) {
//                        String a = str.substring(j, j + 1);
//                        char aa = a.charAt(0);
//                        speechBackStartX += aLine.getmAdjustOffset() + getCharWidth(aa, aLine.getmLineType());
//                        canvas.drawRect(speechBackStartX, top - mTextSize, startX, top + 5, mSpeechBgPaint);
//                    }


//                        String a;
//                        try {
//                            a = str.substring(str.offsetByCodePoints(0, j),
//                                    str.offsetByCodePoints(0, j + 1));
//                        } catch (Exception e) {
//                            a = "";
//                        }

                        String a = str.substring(j, j + 1);

                        canvas.drawText(a, startX, top, mTextPaint);
                        aLine.setmStartY(top - mTextSize);
                        char aa = a.charAt(0);
                        startX += aLine.getmAdjustOffset() + getCharWidth(aa, aLine.getmLineType());
                    }
                }


                aLine.setmParaIndex(paraIndex);

                if (i == mCurPage.titleLines || aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                    mCurPage.getPara(paraIndex).setStartY(top - mTextSize);
                }
                if (i + 1 < mCurPage.lineInfos.size()) {
                    LineInfo nextLine = mCurPage.lineInfos.get(i + 1);
                    if (/*aLine.getmLineType() == LineInfo.LineType.LineTypeMainText &&*/
                            nextLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                        top += para;
                        mCurPage.getPara(paraIndex).setEndY(top - mTextSize);
                        paraIndex++;
                    } else {
                        top += interval;
                        mCurPage.getPara(paraIndex).setEndY(top - mTextSize);
                    }
                } else {
                    top += interval;
                    mCurPage.getPara(paraIndex).setEndY(top - mTextSize);
                }
            }

            // TODO:1.8.1 2021/9/29 1.8.1测试绘制自定义视图
            if (mCurPage != null && mCurPage.getShowStatusFlag() && mCurPage.getmPageStatus() != null) {
                drawCustomStatus(canvas, top);
            }

        }
    }

    // TODO:1.8.1 2021/9/29 1.8.1测试绘制自定义视图
    private void drawCustomStatus(Canvas canvas, float topY) {
        //绘制字体
        String tip = "";
        String moretip = "";
        String autoTip = PlotRead.getApplication().getString(R.string.section_buy_automatically);
        String autoRecommdTip = PlotRead.getApplication().getString(R.string.section_buy_automatically);
        String payTip = "";
        String couponTip = "";


        ChapterPageStatusInfo pageStatusInfo = mCurPage.getmPageStatus();
        ChapterPageStatusInfo.PageStatusMode pageStatusMode = mCurPage.getmPageStatus().getMode();


//        mStatusBtnFrame = null;
//        mStatusAutoPayBtnFrame = null;


        boolean customBtnShow = true;
        if (customBtnShow) {
            mPageView.cleanAdView();


            switch (pageStatusMode) {
                case LOADING:
                    tip = "";
                    break;
                case ERROR:
                    tip = "加载失败(点击按钮重试)";
                    tip = PlotRead.getApplication().getString(R.string.reload_the);
                    break;
                case LOGIN:
                    tip = "登录(点击登录)";
                    tip = PlotRead.getApplication().getString(R.string.go_login);
                    break;
                case LACK_BALANCE:
                    tip = "购买钱不够";
                    tip = PlotRead.getApplication().getString(R.string.top_up_read);
                    break;
                case PAY:
                    tip = "VIP(点击购买)";
                    tip = pageStatusInfo.getChapterPrice() + " " + PlotRead.getApplication().getString(R.string.current_balance);
                    moretip = PlotRead.getApplication().getString(R.string.buy_more_chapter);
                    break;
            }

            Paint btnTextPaint = new TextPaint();
            btnTextPaint.setColor(Color.WHITE);
            btnTextPaint.setTextSize(ScreenUtils.dpToPx(16));
            btnTextPaint.setAntiAlias(true);


            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = btnTextPaint.getFontMetrics();
            float textWidth = btnTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = topY;


            // 绘制按钮的画笔
            Paint mButtonPaint = new Paint();
            mButtonPaint.setColor(THEME_COLOR);
            mButtonPaint.setStyle(Paint.Style.FILL);

            float btnTextWidth = btnTextPaint.measureText(tip);
            float moreTipWidth = btnTextPaint.measureText(moretip);


            int btnWidth = mScreenWidth - 2 * mMarginWidth;
            ;
            int btnHeight = ScreenUtils.dpToPx(44);
            int btnX = (mDisplayWidth - btnWidth) / 2;
            int btnY = (int) pivotY + ScreenUtils.dpToPx(50);

            switch (pageStatusMode) {
                case LOGIN: {
                    if (mCurPage.mLoginFrame != null) {
                        btnWidth = mCurPage.mLoginFrame.getWidth();
                        btnHeight = mCurPage.mLoginFrame.getHeight();
                        btnX = mCurPage.mLoginFrame.getX();
                        btnY = mCurPage.mLoginFrame.getY();

                        RectF rectF = new RectF(btnX, btnY, btnX + btnWidth, btnY + btnHeight);
                        canvas.drawRoundRect(rectF, ScreenUtils.dpToPx(6), ScreenUtils.dpToPx(6), mButtonPaint);


                        canvas.drawText(tip, btnX + (btnWidth - btnTextWidth) / 2, rectF.top + ScreenUtils.dpToPx(14) * 2, btnTextPaint);


                    }
                }
                break;
                case LACK_BALANCE: {
                    drawPayBtnstatus(canvas, btnTextPaint, tip, btnX, btnWidth, (int) btnTextWidth, pageStatusInfo);

//                    drawAutoBuyStatus(canvas, mButtonPaint, autoTip, pageStatusInfo);

                    drawCustomBottomStatus(canvas, pageStatusInfo);

                }
                break;

                case PAY: {
                    drawPayBtnstatus(canvas, btnTextPaint, tip, btnX, btnWidth, (int) btnTextWidth, pageStatusInfo);

                    drawMoreMultiStatus(canvas, btnTextPaint, moretip, btnX, btnWidth, (int) moreTipWidth, pageStatusInfo);

                    drawAutoBuyStatus(canvas, mButtonPaint, autoTip, pageStatusInfo);

                    drawCustomBottomStatus(canvas, pageStatusInfo);
                }
                break;
                case ERROR: {
                    if (mCurPage.mReloadFrame != null) {
                        Paint errorPaint = new Paint();
                        errorPaint.setColor(THEME_COLOR);
                        errorPaint.setStyle(Paint.Style.FILL);

                        btnWidth = mCurPage.mReloadFrame.getWidth();
                        btnHeight = mCurPage.mReloadFrame.getHeight();
                        btnX = mCurPage.mReloadFrame.getX();
                        btnY = mCurPage.mReloadFrame.getY();

                        RectF autoRectF = new RectF(btnX, btnY, btnX + btnWidth, btnY + btnHeight);
                        canvas.drawRoundRect(autoRectF, ScreenUtils.dpToPx(6), ScreenUtils.dpToPx(6), errorPaint);


                        canvas.drawText(tip, btnX + (btnWidth - btnTextWidth) / 2, btnY + ScreenUtils.dpToPx(14) * 2, btnTextPaint);
                    }
                }
                break;
                default: {
                    canvas.drawText(tip, pivotX, pivotY, mTextPaint);
                }
                break;
            }
        }
    }

    private void drawPayBtnstatus(Canvas canvas, Paint btnTextPaint, String tip, int btnX, int btnWidth, int btnTextWidth, ChapterPageStatusInfo pageStatusInfo) {
        if (mCurPage.mPayFrame != null) {
            Paint payPaint = new Paint();
            payPaint.setColor(THEME_COLOR);
            payPaint.setStyle(Paint.Style.FILL);

            RectF payRectF = new RectF(mCurPage.mPayFrame.getX(),
                    mCurPage.mPayFrame.getY(),
                    mCurPage.mPayFrame.getX() + mCurPage.mPayFrame.getWidth(),
                    mCurPage.mPayFrame.getY() + mCurPage.mPayFrame.getHeight());
            canvas.drawRoundRect(payRectF, ScreenUtils.dpToPx(Reader_DrawBtn_Corner), ScreenUtils.dpToPx(Reader_DrawBtn_Corner), payPaint);

            canvas.drawText(tip, btnX + (btnWidth - btnTextWidth) / 2, payRectF.top + ScreenUtils.dpToPx(14) * 2, btnTextPaint);

            boolean isShowCouponRecFlag = false;
            if (pageStatusInfo != null &&
                    pageStatusInfo.getFirstPayNoticeStr() != null &&
                    pageStatusInfo.getMode() == ChapterPageStatusInfo.PageStatusMode.LACK_BALANCE) {
                if (pageStatusInfo.getFirstPayNoticeStr().length() > 0) {
                    isShowCouponRecFlag = true;
                }
            }

            if (autoPreferentialIcon != null && !autoPreferentialIcon.isRecycled() && isShowCouponRecFlag) {

                int height = ScreenUtils.dpToPx(TWENTY_TWO);
                int width = ScreenUtils.dpToPx(SEVENTY_THREE);

                int iconX = (int) (payRectF.right - width / 2);
                if ((iconX + width) > mDisplayWidth) {
                    iconX = (int) (payRectF.right - width);
                }
                int iconY = (int) (payRectF.top - height / 2);

                RectF iconRect = new RectF(iconX, iconY, iconX + width, iconY + height);

                canvas.drawBitmap(autoPreferentialIcon, iconRect.left,
                        iconRect.top, null);

                if (pageStatusInfo != null && pageStatusInfo.getFirstPayNoticeStr() != null) {
                    Paint hitTextPaint = new TextPaint();
                    hitTextPaint.setColor(Color.WHITE);
                    hitTextPaint.setTextSize(ReadSettings.BUY_BTN_TEXT_SIZE_10);
                    hitTextPaint.setAntiAlias(true);

                    String hitStr = pageStatusInfo.getFirstPayNoticeStr();

                    int hitW = (int) hitTextPaint.measureText(hitStr);
                    int hitH = (int) hitTextPaint.getTextSize();

                    canvas.drawText(hitStr,
                            iconRect.left + (iconRect.width() - hitW) / 2,
                            iconRect.top + hitH + ScreenUtils.dpToPx(5),
                            hitTextPaint);
                }
            }

            if(noticeUnClockFlag)
            {
                noticeUnClockFlag = false;

                Message msg = Message.obtain();
                msg.what = BusC.BUS_NOTIFY_NOTICE_USER_UNCLOCK;
                EventBus.getDefault().post(msg);
            }
        }
    }

    private void drawMoreMultiStatus(Canvas canvas, Paint btnTextPaint11, String moretip, int btnX, int btnWidth, int moreTipWidth, ChapterPageStatusInfo pageStatusInfo) {
        if (mCurPage.mPayOneMoreFrame != null) {
            Paint morePaint = new Paint();
            morePaint.setStrokeWidth(DisplayUtil.dp2px(PlotRead.getApplication(), 1));
            morePaint.setStyle(Paint.Style.STROKE);
            morePaint.setColor(THEME_COLOR);

            Paint moreBtnTextPaint = new TextPaint();
            moreBtnTextPaint.setColor(THEME_COLOR);
            moreBtnTextPaint.setTextSize(ScreenUtils.dpToPx(16));
            moreBtnTextPaint.setAntiAlias(true);


            RectF moreRectF = new RectF(mCurPage.mPayOneMoreFrame.getX(),
                    mCurPage.mPayOneMoreFrame.getY(),
                    mCurPage.mPayOneMoreFrame.getX() + mCurPage.mPayOneMoreFrame.getWidth(),
                    mCurPage.mPayOneMoreFrame.getY() + mCurPage.mPayOneMoreFrame.getHeight());
            canvas.drawRoundRect(moreRectF,
                    ScreenUtils.dpToPx(Reader_DrawBtn_Corner),
                    ScreenUtils.dpToPx(Reader_DrawBtn_Corner),
                    morePaint);

            canvas.drawText(moretip, btnX + (btnWidth - moreTipWidth) / 2, moreRectF.top + ScreenUtils.dpToPx(14) * 2, moreBtnTextPaint);

            boolean isShowMoreRecFlag = false;
            if (pageStatusInfo != null && pageStatusInfo.getMultiPayNoticeStr() != null) {
                if (pageStatusInfo.getMultiPayNoticeStr().length() > 0) {
                    isShowMoreRecFlag = true;
                }
            }


            if (autoPreferentialIcon != null && !autoPreferentialIcon.isRecycled() && isShowMoreRecFlag) {

                int height = ScreenUtils.dpToPx(TWENTY_TWO);
                int width = ScreenUtils.dpToPx(SEVENTY_THREE);

                int iconX = (int) (moreRectF.right - width / 2);
                if ((iconX + width) > mDisplayWidth) {
                    iconX = (int) (moreRectF.right - width);
                }
                int iconY = (int) (moreRectF.top - height / 2);

                RectF iconRect = new RectF(iconX, iconY, iconX + width, iconY + height);

                canvas.drawBitmap(autoPreferentialIcon, iconRect.left,
                        iconRect.top, null);

                if (pageStatusInfo != null && pageStatusInfo.getMultiPayNoticeStr() != null) {
                    Paint hitTextPaint = new TextPaint();
                    hitTextPaint.setColor(Color.WHITE);
                    hitTextPaint.setTextSize(ReadSettings.BUY_BTN_TEXT_SIZE_10);
                    hitTextPaint.setAntiAlias(true);

                    String hitStr = pageStatusInfo.getMultiPayNoticeStr();

                    int hitW = (int) hitTextPaint.measureText(hitStr);
                    int hitH = (int) hitTextPaint.getTextSize();

                    canvas.drawText(hitStr,
                            iconRect.left + (iconRect.width() - hitW) / 2,
                            iconRect.top + hitH + ScreenUtils.dpToPx(5),
                            hitTextPaint);
                }
            }

        }
    }

    private void drawAutoBuyStatus(Canvas canvas, Paint mButtonPaint, String autoTip, ChapterPageStatusInfo pageStatusInfo) {
        if (mCurPage.mAutoPayFrame != null) {
            Paint autoPaint = new Paint();
            autoPaint.setColor(THEME_COLOR);
            autoPaint.setStyle(Paint.Style.FILL);

            RectF autoRectF = new RectF(mCurPage.mAutoPayFrame.getX(),
                    mCurPage.mAutoPayFrame.getY(),
                    mCurPage.mAutoPayFrame.getX() + mCurPage.mAutoPayFrame.getWidth(),
                    mCurPage.mAutoPayFrame.getY() + mCurPage.mAutoPayFrame.getHeight());

            if (mCollBook != null) {
                if (!isAutoBuySelected) {
                    mButtonPaint.setColor(GREEN);
                    if (autoCloseIcon != null && !autoCloseIcon.isRecycled()) {
                        canvas.drawBitmap(autoCloseIcon, autoRectF.left,
                                autoRectF.top, null);
                    }

                } else {
                    mButtonPaint.setColor(THEME_COLOR);
                    if (autoOpenIcon != null && !autoOpenIcon.isRecycled()) {
                        canvas.drawBitmap(autoOpenIcon, autoRectF.left,
                                autoRectF.top, null);
                    }
                }


                Paint autoTipTextPaint = new TextPaint();
                autoTipTextPaint.setColor(ReadSettings.BUY_MODE_TIP_TEXT_COLOR);
                autoTipTextPaint.setTextSize(ReadSettings.BUY_BTN_TEXT_SIZE);
                autoTipTextPaint.setAntiAlias(true);
                canvas.drawText(autoTip, autoRectF.right + ScreenUtils.dpToPx(10),
                        autoRectF.top + ScreenUtils.dpToPx(15),
                        autoTipTextPaint);


                boolean isShowAutoBuyRecFlag = false;
                if (pageStatusInfo.getAutoPayNoticeStr() != null
                        && pageStatusInfo.getAutoPayNoticeStr().length() > 0
                        && isAutoBuySelected == false) {
                    isShowAutoBuyRecFlag = true;
                }

                if (recommentDes != null && !recommentDes.isRecycled() && isShowAutoBuyRecFlag) {

//                                int size3 = DisplayUtil.dp2px(mContext, ONE_HUNDRED_SIXTY);
//                                int size4 = DisplayUtil.dp2px(mContext, FORTY);

                    int width = ScreenUtils.dpToPx(ONE_HUNDRED_SIXTY);
                    int height = ScreenUtils.dpToPx(FORTY);


                    int iconX = (int) (autoRectF.left + (autoRectF.width() - width) / 2);
                    int iconY = (int) (autoRectF.bottom + ScreenUtils.dpToPx(10));

                    RectF iconRect = new RectF(iconX, iconY, iconX + width, iconY + height);

                    canvas.drawBitmap(recommentDes, iconRect.left,
                            iconRect.top, null);


                    autoTipTextPaint.setColor(WHITE);

                    String hintStr = PlotRead.getApplication().getString(R.string.recomment_hint1)
                            + pageStatusInfo.getAutoPayNoticeStr() + PlotRead.getApplication().getString(R.string.recomment_hint2);

                    int hitW = (int) autoTipTextPaint.measureText(hintStr);
                    int hitH = (int) autoTipTextPaint.getTextSize();


                    canvas.drawText(hintStr, iconRect.left + (iconRect.width() - hitW) / 2,
                            iconRect.top + ScreenUtils.dpToPx(15) + (iconRect.height() - hitH) / 2,
                            autoTipTextPaint);

                }

//                            canvas.drawBitmap(autoCloseIcon, autoRectF.left,
//                                    autoRectF.top, null);
//                            canvas.drawRoundRect(autoRectF, ScreenUtils.dpToPx(6), ScreenUtils.dpToPx(6), mButtonPaint);
            }
        }
    }

    private void drawCustomBottomStatus(Canvas canvas, ChapterPageStatusInfo pageStatusInfo) {

        String bottomTips = PlotRead.getApplication().getString(R.string.coins_batance) + " " + PlotRead.getAppUser().money + " " + mContext.getString(R.string.topup_coins) + " + " +
                PlotRead.getAppUser().voucher + " " + mContext.getString(R.string.topup_bouns);

        if (pageStatusInfo != null) {
            if (pageStatusInfo.getMode() == ChapterPageStatusInfo.PageStatusMode.LACK_BALANCE) {
                bottomTips = pageStatusInfo.getChapterPrice() + " " + PlotRead.getApplication().getString(R.string.current_balance);
            }
        }

        Paint autoTipTextPaint = new TextPaint();
        autoTipTextPaint.setColor(ReadSettings.BUY_MODE_TIP_TEXT_COLOR);
        autoTipTextPaint.setTextSize(ScreenUtils.dpToPx(ELEVEN));
        autoTipTextPaint.setAntiAlias(true);


        int bottmTipsW = (int) autoTipTextPaint.measureText(bottomTips);

        int tipX = (mDisplayWidth - bottmTipsW) / 2;
        int tipY = mDisplayHeight - mDrawTopBottomMargin - ScreenUtils.dpToPx(15);


        canvas.drawText(bottomTips, tipX, tipY, autoTipTextPaint);

    }


    public boolean syncSpeechBack(float guideLineY) {
        boolean needSyncBack = false;

        int currentSpeechParaIndex = mCurPage.getSpeechingPara();

        if (currentSpeechParaIndex == -1) {
            return false;
        }

        float currentSpeechStartY = mCurPage.getPara(currentSpeechParaIndex).getStartY();
        float currentSpeechEndY = mCurPage.getPara(currentSpeechParaIndex).getEndY();

        if (guideLineY >= currentSpeechStartY && guideLineY <= currentSpeechEndY) {
            needSyncBack = false;
        } else if (guideLineY < currentSpeechStartY) {
            needSyncBack = true;
            currentSpeechParaIndex--;
            mPageChangeListener.onReadParaChanged(currentSpeechParaIndex);

            mCurPage.setSpeechingPara(currentSpeechParaIndex);
            mPageView.drawCurPage(false);
        } else if (guideLineY > currentSpeechEndY) {
            needSyncBack = true;
            currentSpeechParaIndex++;
            mPageChangeListener.onReadParaChanged(currentSpeechParaIndex);

            mCurPage.setSpeechingPara(currentSpeechParaIndex);
            mPageView.drawCurPage(false);
        }

        return needSyncBack;
    }

    public String drawSpeechBack(int currentSpeechPara) {

        String speechContent = "";
        if (currentSpeechPara != -1) {
            mCurPage.setSpeechingPara(currentSpeechPara);
            ParaInPageBean paraInPageBean = mCurPage.getPara(currentSpeechPara);
            if (paraInPageBean != null) {
                speechContent = paraInPageBean.getTextContent();
            }
        }
        if (StringUtils.isBlank(speechContent)) {
            List<LineInfo> paraLines = mCurPage.getParaLines(currentSpeechPara);
            for (int i = 0; i < paraLines.size(); i++) {
                speechContent = speechContent + paraLines.get(i).getmLineText();
            }
        }

        mCurPage.setSpeechingPara(currentSpeechPara);
        mPageView.drawCurPage(false);

        String finalContent = speechContent;
        finalContent = speechContent.replaceAll("\\*\\*", "");
        finalContent = finalContent.replaceAll("\\@\\@", "");
        finalContent = finalContent.replaceAll("\\#\\#", "");
        finalContent = finalContent.replaceAll("\\$\\$", "");
        finalContent = finalContent.replaceAll("\\%\\%", "");
        finalContent = finalContent.replaceAll("\\&\\&", "");
        finalContent = finalContent.replaceAll("\\+\\+", "");
        finalContent = finalContent.replaceAll("\\-\\-", "");

        return finalContent;
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    void prepareDisplay(int w, int h) {
        //回调排版文字宽度
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        if (mPageView == null) {
            return;
        }
        // 重置 PageMode
        mPageView.setPageMode(mPageMode);

        if (!isChapterOpen) {
            // 展示加载界面
            mPageView.drawCurPage(false);
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter();
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (mStatus == STATUS_FINISH) {
                mPrePageList = null;
                mNextPageList = null;
                dealLoadPageList(mCurChapterPos);
                int position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());
                // 重新设置文章指针的位置
                mCurPage = getCurPage(position);
            }
            mPageView.drawCurPage(false);
        }
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            TxtPage prevPage = getPrevPage();
            if (prevPage != null) {
                mCancelPage = mCurPage;
                mCurPage = prevPage;
                if (mPageView.getIsBookSpeeching()) {
                    mCurPage.setSpeechingParaToLastPara();
                    mPageChangeListener.onReadParaChanged(mCurPage.getSpeechingPara());
                }
                mPageView.drawNextPage();
                return true;
            }
        }

        if (!hasPrevChapter()) {
            return false;
        }

        mCancelPage = mCurPage;
        if (parsePrevChapter()) {
            mCurPage = getPrevLastPage();
        } else {
            mCurPage = new TxtPage();
        }

        if (mPageView.getIsBookSpeeching()) {
            mCurPage.setSpeechingParaToLastPara();
            mPageChangeListener.onReadParaChanged(mCurPage.getSpeechingPara());
        }

        mPageView.drawNextPage();
        return true;
    }

    /**
     * 解析上一章数据
     *
     * @return:数据是否解析成功
     */
    boolean parsePrevChapter() {
        // 加载上一章数据
        int prevChapter = mCurChapterPos - 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = prevChapter;

        countCurPage();
        // 当前章缓存为下一章
        mNextPageList = mCurPageList;

        TxtChapter chapter = mChapterList.get(prevChapter);
        if (chapter.getmChatperStatusInfo() != null) {
            if (mNextPageList != null && mNextPageList.size() > 0) {
                TxtPage page = mNextPageList.get(0);
                if (page.getmPageStatus() != null && page.chapterOrder == chapter.chapterOrder) {

                    if (page.getmPageStatus().getChapterContentStr() == null && chapter.getmChatperStatusInfo().getChapterContentStr() != null) {
                        mNextPageList = null;
                    }
                }
            }
        }


        // 判断是否具有上一章缓存
        if (mPrePageList != null) {
            mCurPageList = mPrePageList;
            mPrePageList = null;

            // 回调
            chapterChangeCallback();
        } else {
            dealLoadPageList(prevChapter);
        }
        return mCurPageList != null ? true : false;
    }

    private boolean hasPrevChapter() {
        //判断是否上一章节为空
        if (mCurChapterPos - 1 < 0) {
            return false;
        }
        return true;
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            TxtPage nextPage = getNextPage();
            if (nextPage != null) {
                mCancelPage = mCurPage;
                mCurPage = nextPage;
                if (mPageView.getIsBookSpeeching()) {
                    mCurPage.setSpeechingParaToFirstPara();
                    mPageChangeListener.onReadParaChanged(0);
                }
                mPageView.drawNextPage();
                mPageChangeListener.onPageChangeFinish(getPagePos(), true);
                return true;
            }
        }

        if (!hasNextChapter()) {
            mPageChangeListener.onPageChangeFinish(getPagePos(), false);
            return false;
        }

        mCancelPage = mCurPage;
        // 解析下一章数据
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
//            mCurPage = mCurPageList.get(0);
        } else {
            mCurPage = new TxtPage();
        }

        if (mPageView.getIsBookSpeeching()) {
            mCurPage.setSpeechingParaToFirstPara();
            mPageChangeListener.onReadParaChanged(0);
        }
        mPageView.drawNextPage();
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChangeFinish(getPagePos(), true);
        }
        return true;
    }

    private boolean hasNextChapter() {
        // 判断是否到达目录最后一章
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return false;
        }
        return true;
    }

    boolean parseCurChapter() {
        //换源，重新进入阅读界面时判断
        if (mCurChapterPos >= mChapterList.size()) {
            mCurChapterPos = mChapterList.size() - 1;
        }
        // 解析数据
        dealLoadPageList(mCurChapterPos);
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    /**
     * 解析下一章数据
     *
     * @return:返回解析成功还是失败
     */
    boolean parseNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = nextChapter;

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList;

        countCurPage();

        TxtChapter chapter = mChapterList.get(nextChapter);
        if (chapter.getmChatperStatusInfo() != null) {
            if (mNextPageList != null && mNextPageList.size() > 0) {
                TxtPage page = mNextPageList.get(0);
                if (page.getmPageStatus() != null && page.chapterOrder == chapter.chapterOrder) {

                    if (page.getmPageStatus().getChapterContentStr() == null) {
                        mNextPageList = null;
                    }
                }
            }
        }


        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {


            mCurPageList = mNextPageList;
            mNextPageList = null;
            // 回调
            chapterChangeCallback();
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter);
        }
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    private void dealLoadPageList(int chapterPos) {
        try {
            mCurPageList = loadPageList(chapterPos);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = STATUS_FINISH;
                }
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }

    private void chapterChangeCallback() {
        if (mPageChangeListener != null && mChapterList != null && mCurChapterPos < mChapterList.size() && mLastChapterPos < mChapterList.size() && mLastChapterPos >= 0 && mCurChapterPos >= 0) {
            try {
                int wordcount = mChapterList.get(mLastChapterPos).getTextCount();
                mPageChangeListener.onChapterChange(mCurChapterPos, wordcount);
                mPageChangeListener.onPageCountChange(mCurPageList != null ? mCurPageList.size() : 0);
            } catch (Exception e) {

            }
        }
    }

    // 预加载下一章
    private void preLoadNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        // 如果不存在下一章，且下一章没有数据，则不进行加载。
        if (!hasNextChapter()
                || !hasChapterData(mChapterList.get(nextChapter))) {
            return;
        }

//        if (!hasNextChapter()
//                || !realHasChapterData(mChapterList.get(nextChapter))) {
//            return;
//        }


        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        //调用异步进行预加载加载
        Single.create(new SingleOnSubscribe<List<TxtPage>>() {
            @Override
            public void subscribe(SingleEmitter<List<TxtPage>> e) throws Exception {
                e.onSuccess(loadPageList(nextChapter));
            }
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mPreLoadDisp = d;
                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        mNextPageList = pages;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
    }

    // 取消翻页
    void pageCancel() {
        if (mCurPage.position == 0 && mCurChapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter();
            } else {
                if (parsePrevChapter()) {
                    mCurPage = getPrevLastPage();
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else if (mCurPageList == null
                || (mCurPage.position == mCurPageList.size() - 1
                && mCurChapterPos < mLastChapterPos)) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter();
            } else {
                if (parseNextChapter()) {
                    mCurPage = getCurPage(0);
//                    mCurPage = mCurPageList.get(0);
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage;
        }
    }

    private void cancelNextChapter() {
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;

        mNextPageList = mCurPageList;
        mCurPageList = mPrePageList;
        mPrePageList = null;

        chapterChangeCallback();

        mCurPage = getPrevLastPage();
        mCancelPage = null;
    }

    private void cancelPreChapter() {
        // 重置位置点
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;
        // 重置页面列表
        mPrePageList = mCurPageList;
        mCurPageList = mNextPageList;
        mNextPageList = null;

        chapterChangeCallback();

        mCurPage = getCurPage(0);
        mCancelPage = null;
    }

    private float[] iAsciiFontWidth;
    private float[] iSpecialChsFontWidth;
    private float[] iHAsciiFontWidth;
    private float[] iHSpecialChsFontWidth;
    private String iAsciiCharTable;
    private String iSpecialChsCharTable;

    private float mChsCharWidth;  //普通汉字大小
    private float mTitleChsCharWidth;  //标题汉字大小

    private void initFontSizeTable() {
        assert (mTextSize > 0);
        int i;
        if (iAsciiCharTable == null) {
            byte[] vAsciiBytes = new byte[KAsciiCharCount];
            for (i = 0; i < KAsciiCharCount; i++) {
                vAsciiBytes[i] = (byte) i;
            }
            iAsciiCharTable = new String(vAsciiBytes);
            KAsciiCharCount = iAsciiCharTable.length();
        }
        if (iSpecialChsCharTable == null) {
            byte[] vSpecialChsBytes = new byte[KChsSpecialSymbol * 2];
            char vSpecialChsBegin = KSpecialChsBegin;
            for (i = 0; i < KChsSpecialSymbol; i++) {
                vSpecialChsBytes[2 * i] = (byte) vSpecialChsBegin;
                vSpecialChsBytes[2 * i + 1] = (byte) (vSpecialChsBegin >> 8);
                vSpecialChsBegin++;
            }
            try {
                iSpecialChsCharTable = new String(vSpecialChsBytes, "UTF-16LE");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (iAsciiFontWidth == null) {
            iAsciiFontWidth = new float[KAsciiCharCount];
        }
        if (iHAsciiFontWidth == null) {
            iHAsciiFontWidth = new float[KAsciiCharCount];
        }
        for (i = 0; i < KAsciiCharCount; i++) {
            iAsciiFontWidth[i] = mTextPaint.measureText(iAsciiCharTable, i, i + 1);
            iHAsciiFontWidth[i] = mTitlePaint.measureText(iAsciiCharTable, i, i + 1);
        }

        if (iSpecialChsFontWidth == null) {
            iSpecialChsFontWidth = new float[KChsSpecialSymbol];
        }
        if (iHSpecialChsFontWidth == null) {
            iHSpecialChsFontWidth = new float[KChsSpecialSymbol];
        }
        for (i = 0; i < KChsSpecialSymbol; i++) {
            iSpecialChsFontWidth[i] = mTextPaint.measureText(iSpecialChsCharTable, i, i + 1);
            iHSpecialChsFontWidth[i] = mTitlePaint.measureText(iSpecialChsCharTable, i, i + 1);
        }
        mChsCharWidth = mTextPaint.measureText("国");
        mTitleChsCharWidth = mTitlePaint.measureText("国");
    }

    private float GetStringWidth(String aString) {
        float retWidth = 0;
        int count = aString.length();
        char vChar;
        for (int i = 0; i < count; i++) {
            vChar = aString.charAt(i);
            if (vChar >= 0 && vChar < KAsciiCharCount) {
                retWidth += iAsciiFontWidth[vChar];
            } else if (vChar >= KSpecialChsBegin && vChar <= KSpecialChsEnd) {
                retWidth += iSpecialChsFontWidth[vChar - KSpecialChsBegin];
            } else {
                retWidth += mChsCharWidth;
            }
        }
        return retWidth;
    }

    //章节断行总入口
    private ArrayList<LineInfo> newLayoutWithText(String vTextPtr) {
        ArrayList<LineInfo> mlines = new ArrayList<>();
        int baseCharPos = 0;  //线程中用于对char pos进行计数
        char vBeginChar;
        int vBreakPos = 0, vLength;
        String vParaTextPtr = null;

        StringBuilder vModParaTextSb = new StringBuilder();
        //去掉段首非法字符
        while (vBreakPos < vTextPtr.length()) {
            vBeginChar = vTextPtr.charAt(vBreakPos);
            if (vBeginChar == '\n') {
                vBreakPos++;
                continue;
            } else if (vBeginChar > 0x20) {
                break;
            }
            vBreakPos++;
        }
        baseCharPos += vBreakPos;
        vTextPtr = vTextPtr.substring(vBreakPos);

        int location = vTextPtr.indexOf("\n");
        boolean bTitle = true;
        while (location > 0 && location < vTextPtr.length()) {
            vBreakPos = location;
            vTextPtr.charAt(vBreakPos - 1);
            if (vBreakPos > 0 && vTextPtr.charAt(vBreakPos - 1) == '\r') {
                vParaTextPtr = vTextPtr.substring(0, vBreakPos - 1);
            } else {
                vParaTextPtr = vTextPtr.substring(0, vBreakPos);
            }
            mlines.addAll(SmartBreakText(vParaTextPtr, vModParaTextSb, baseCharPos, bTitle));
            if (bTitle) {
                bTitle = false;
            }

            //去除段落前非法控制字符及多个‘\n’
            vLength = vTextPtr.length();
            while (++vBreakPos < vLength) {
                vBeginChar = vTextPtr.charAt(vBreakPos);
                if (vBeginChar == '\n') {
                    continue;
                } else if (vBeginChar > 0x20) {
                    break;
                }
            }

            vTextPtr = vTextPtr.substring(vBreakPos);
            baseCharPos += vBreakPos;

            vModParaTextSb.setLength(0);

            location = vTextPtr.indexOf("\n");
        }

        //处理剩下的string
        if (vTextPtr.length() > 0) {
            mlines.addAll(SmartBreakText(vTextPtr, vModParaTextSb, baseCharPos, bTitle));
        }
        return mlines;
    }

    //智能断行
    private ArrayList<LineInfo> SmartBreakText(String aParaText, StringBuilder aModSb, int baseCharPos, boolean bTitle) {
        //检测段首是否有空格
        ArrayList<LineInfo> mlines = new ArrayList<>();
        char vBeginChar;
        int vHeadSpCount = 0, vEndSpCount = 0;
        int vLength = aParaText.length();
        while (vHeadSpCount < vLength) {
            vBeginChar = aParaText.charAt(vHeadSpCount);
            if (vBeginChar == 0x3000 || vBeginChar <= 0x20 || (vBeginChar <= 0xff && vBeginChar >= 0x81)) {
                vHeadSpCount++;
                continue;
            }
            break;
        }

        if (vHeadSpCount >= vLength) {
            return mlines;
        }

        //计算段尾空白字符数量
        for (int i = vLength - 1; i >= 0; i--) {
            vBeginChar = aParaText.charAt(i);
            if (vBeginChar == 0x3000 || vBeginChar <= 0x20 || (vBeginChar <= 0xff && vBeginChar >= 0x81)) {
                vEndSpCount++;
                continue;
            }
            break;
        }

        aModSb.setLength(0);
//        aModSb.append(aParaText, vHeadSpCount, aParaText.length()-vHeadSpCount-vEndSpCount);
        aModSb.append(aParaText, vHeadSpCount, aParaText.length() - vEndSpCount);
        mlines.addAll(newBreakText(aModSb.toString(), mDisplayWidth - 2 * mDrawLeftRightMargin, baseCharPos, bTitle));
        return mlines;
    }

    private float getStrWidth(LineInfo info) {
        String str = info.getmLineText();
        float width = 0;
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            width += getCharWidth(a, info.getmLineType()) + info.getmAdjustOffset();
        }
        return width;
    }

    private float getCharWidth(char aChar, LineInfo.LineType lineType) {
        if (lineType == LineInfo.LineType.LineTypeTitle) {
            if (aChar >= 0 && aChar < KAsciiCharCount) {
                return iHAsciiFontWidth[aChar];
            } else if (aChar >= KSpecialChsBegin && aChar <= KSpecialChsEnd) {
                return iHSpecialChsFontWidth[aChar - KSpecialChsBegin];
            } else {
                return mTitleChsCharWidth + mTextMargin;
            }
        } else {
            if (aChar >= 0 && aChar < KAsciiCharCount) {
                return iAsciiFontWidth[aChar];
            } else if (aChar >= KSpecialChsBegin && aChar <= KSpecialChsEnd) {
//                return iSpecialChsFontWidth[aChar];
                return iSpecialChsFontWidth[aChar - KSpecialChsBegin];
            } else {
                return mChsCharWidth + mTextMargin;
            }
        }
    }

    private void getSpaceNumWithStr(String text, int[] vHeadSpCount, int[] vTailSpCount) {
        vHeadSpCount[0] = 0;
        vTailSpCount[0] = 0;
        //检测行首是否有空格
        char vBeginChar;
        int vLength = text.length();

        while (vHeadSpCount[0] < vLength) {
            vBeginChar = text.charAt(vHeadSpCount[0]);
            if (vBeginChar == 0x0020) {
                vHeadSpCount[0]++;
                continue;
            }
            break;
        }
        //检测行尾是否有空格
        int tailSPCount = text.length() - 1;
        char tailChar;
        while (tailSPCount >= 0) {
            tailChar = text.charAt(tailSPCount);
            if (tailChar == 0x3000 || tailChar == 0x0020) {
                tailSPCount--;
                vTailSpCount[0]++;
                continue;
            }
            break;
        }
        return;
    }


    private int GetWord(String str, int offset, float[] aWidth, LineInfo.LineType lineType) {
        aWidth[0] = 0;
        int len = 0;
        float width = 0;
        int strLen = str.length();
        char c;
        while (offset + len <= strLen - 1) {
            c = str.charAt(offset + len);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')) {
                len++;
                if (lineType == LineInfo.LineType.LineTypeTitle) {
                    if (c >= 0 && c < KAsciiCharCount) {
                        width += iHAsciiFontWidth[c];
                    } else if (c >= KSpecialChsBegin && c <= KSpecialChsEnd) {
                        width += iHSpecialChsFontWidth[c - KSpecialChsBegin];
                    } else {
                        width += mTitleChsCharWidth;
                    }
                } else {
                    if (c >= 0 && c < KAsciiCharCount) {
                        width += iAsciiFontWidth[c];
                    } else if (c >= KSpecialChsBegin && c <= KSpecialChsEnd) {
                        width += iSpecialChsFontWidth[c - KSpecialChsBegin];
                    } else {
                        width += mChsCharWidth;
                    }
                }
            } else {
                break;
            }
        }
        if (aWidth.length > 0) {
            aWidth[0] = width;
        }
        return len;
    }

    /**
     * Emoji表情校验
     *
     * @param string
     * @return
     */
    private boolean isEmoji(String string) {
        //过滤Emoji表情
        Pattern p = Pattern.compile("[^\\u0000-\\uFFFF]");
        //过滤Emoji表情和颜文字
        //Pattern p = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");
        Matcher m = p.matcher(string);
        return m.find();
    }

//    private ArrayList<LineInfo> mLines = new ArrayList<LineInfo>();

    // aLineWidth 行宽
    // TODO: 2021/10/4 加载分割线
    private ArrayList<LineInfo> newBreakText(String aText, int aLineWidth, int baseCharPos, boolean bTitle) {
        ArrayList<LineInfo> mLines = new ArrayList<LineInfo>();
        int vTextCount = aText.length();
        if (vTextCount <= 0) {
            return mLines;
        }

        LineInfo.LineType lineType = LineInfo.LineType.LineTypeFirstLine;
        if (bTitle) {
            lineType = LineInfo.LineType.LineTypeTitle;
        }

        int KRepeatCount = 0;
        String tmpTitleStr = null;


        float[] vWordWidth = new float[1];
        int vWordSize;
        //普通的文本行(非第一行)的宽度[因为每次累加的是文字宽度+字间距，为了计算方便行宽需要加上一个字间距]
        int lineWidth = aLineWidth - 2 * mMarginWidth;
        //每一行的字符数。每次计算新行时，重置为0。
        int vLineCharCount = 0;
        //遍历字符时，累加每一行字符的宽度。每次计算新行时，重置为0。
        int totalCharWidth = 0;
        char c;

//        if (lineType != LineInfo.LineType.LineTypeTitle) {
//            //段落的第一行缩进两个中文字符，所以还要再减去两个字符及两个字间距的宽度
//            lineWidth -= 2 * mChsCharWidth;
//        }

        if (lineType != LineInfo.LineType.LineTypeTitle) {
            //段落的第一行缩进两个中文字符，所以还要再减去两个字符及两个字间距的宽度
            lineWidth -= 1 * mChsCharWidth;
        }


        int i = 0;
        int spaceNum = 0;//统计用于断行的空格的数量:(加上该空格使字符总宽度大于行宽)
        int wordNum = 0; //一行中单词(welcome. welcome wel- ),以及其他字符的总数量

        int lineCount = mLines.size();  //断行前已有的行数，新增的第一行为FistLine，其他为MainLine

        for (; i < vTextCount; i++) {
            c = aText.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                //英文字母
                vWordSize = GetWord(aText, i, vWordWidth, lineType);
                wordNum++;
                //单词宽度小于行宽的情况
                if (vWordWidth[0] < lineWidth) {
                    totalCharWidth += vWordWidth[0];

                    //目前行中字符加上英文单词后的宽度超过行宽
                    if (totalCharWidth > lineWidth) {
                        String word = aText.substring(i, i + vWordSize);
                        //先减去最后一个单词
                        totalCharWidth -= vWordWidth[0];

                        String text = aText.substring(i - vLineCharCount, i);
                        LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);
                        info.setmLineType(lineType);

                        if (lineType != LineInfo.LineType.LineTypeTitle) {
                            if (lineCount == mLines.size() && aText.startsWith(info.getmLineText())) {
                                info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                            } else {
                                info.setmLineType(LineInfo.LineType.LineTypeMainText);
                            }
                        }

                        //检测行首尾空格
                        //检测行首尾空格的数量
                        int[] vHeadSpCount = new int[1];
                        int[] vTailSpCount = new int[1];

                        //去除行首尾空格
                        getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
                        text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
                        totalCharWidth -= getCharWidth(' ', lineType) * (vHeadSpCount[0] + vTailSpCount[0]);
                        wordNum -= (vHeadSpCount[0] + vTailSpCount[0] + 1);

                        info.setmLineText(text);
                        info.setmStartPos(info.getmStartPos() + vHeadSpCount[0]);
                        info.setmCharCount(text.length());

                        mLines.add(info);
                        info = null;

                        totalCharWidth = 0;
                        vLineCharCount = 0;
                        wordNum = 0;
                        spaceNum = 0;

                        lineWidth = aLineWidth - 2 * mMarginWidth;
                        i--;
                    } else {
                        i += (vWordSize - 1);
                        vLineCharCount += (vWordSize);
                    }
                } else {
                    //单词宽度大于行宽 TODO ...
                    totalCharWidth += getCharWidth(c, lineType);
                    if (totalCharWidth > lineWidth) {
                        int startIndex = i - vLineCharCount;
                        if (startIndex < aText.length()
                                && startIndex <= vLineCharCount
                                && vLineCharCount < aText.length()) {
                            String text = aText.substring(startIndex, vLineCharCount);
                            LineInfo info = new LineInfo(text, baseCharPos + startIndex, vLineCharCount);
                            info.setmLineType(lineType);
                            if (lineType != LineInfo.LineType.LineTypeTitle) {
                                if (lineCount == mLines.size() && aText.startsWith(text)) {
                                    info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                                } else {
                                    info.setmLineType(LineInfo.LineType.LineTypeMainText);

                                }
                            }

                            float adjust = (getCharWidth(c, lineType) - (float) (lineWidth - totalCharWidth)) / (float) (info.getmLineText().length());
                            info.setmAdjustOffset(adjust);
                            mLines.add(info);
                            info = null;

                        }


                        totalCharWidth = 0;
                        vLineCharCount = 0;

                        lineWidth = aLineWidth - 2 * mMarginWidth;
                        i--;
                    } else {
                        vLineCharCount++;
                    }
                }
            } else {
                //非英文字母的文字断行，一个个累加，判断字符累计长度是否超过行长，如果超过，则将前面没超过的字符生成行
                //charWidth:即为字符的实际宽度+字间距
                float charWidth = getCharWidth(c, lineType);
                wordNum++;
                totalCharWidth += charWidth;

                //此时的总宽度大于行的宽度的时候
                if (totalCharWidth > lineWidth) {
                    if (c == 0x0020) {//总宽度大于行宽度时，如果此时字符为' ',那么跳过这个字符，不进行断行
                        spaceNum++;
                        vLineCharCount++;
                        continue;
                    }
                    totalCharWidth -= charWidth;
                    //检查上一行的最后一个字符是否为上引号
                    char lastChar = aText.charAt(i - 1);
                    if (lastChar == 0x201c) {
                        i--;
                        vLineCharCount--;
                        totalCharWidth -= getCharWidth(lastChar, lineType);
                    } else {
                        //检查当前字符
                        if (c == 0x201d) {
                            //全角下引号,与上一行合并
                            i++;
                            vLineCharCount++;
                            totalCharWidth += charWidth;
                        } else if (c == 0x201c) {
                            //全角上引号，donothing
                            //                        continue;
                        } else if ((c >= KFullWidthMarkBegin && c <= KFullWidthMarkEnd)
                                || (c >= KCjkMarkBegin && c <= KCjkMarkEnd)
                                || (c >= KSpecialChsBegin && c <= KSpecialChsEnd)
                                || (c >= KAsciiMarkBegin && c <= KAsciiMarkEnd)) {
                            //如果是 。 并且前面一个是 ”，都并到上一行
                            if (c == 0x3002 && aText.charAt(i - 1) == 0x201d) {
                                i++;
                                vLineCharCount++;
                                totalCharWidth += charWidth;
                            } else {
                                //独立标点符号，取上行末尾字符到下行行首
                                //去除对最后一个字符的累加
                                i--;
                                vLineCharCount--;
                                totalCharWidth -= getCharWidth(aText.charAt(i), lineType);
                            }
                        }
                    }

                    KRepeatCount++;
                    //去除对最后一个字符的累加
                    //i:即为此时遍历的字符总数量
                    //vLineCharCount:此时字符累加的宽度不大于一行时，该行字符的总数量
                    //baseCharPos:该段落的第一个字符在整个章节中的位置
                    String text = aText.substring(i - vLineCharCount, i);
                    LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);

                    //检测行首尾空格的数量
                    int[] vHeadSpCount = new int[1];
                    int[] vTailSpCount = new int[1];

                    //去除行首尾空格
                    getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
                    text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
                    totalCharWidth -= getCharWidth(' ', lineType) * (vHeadSpCount[0] + vTailSpCount[0]);
                    wordNum -= (vHeadSpCount[0] + vTailSpCount[0] + 1);

                    info.setmLineText(text);
                    info.setmStartPos(info.getmStartPos() + vHeadSpCount[0]);
                    info.setmCharCount(text.length());


                    if (KRepeatCount == 1) {
                        tmpTitleStr = new String(info.getmLineText());
                    } else if (KRepeatCount == 2) {
                        tmpTitleStr = null;
                    }
                    info.setmLineType(lineType);
                    if (lineType != LineInfo.LineType.LineTypeTitle) {
                        if (lineCount == mLines.size() && aText.startsWith(info.getmLineText())) {
                            //判断是否为第一行
                            info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                        } else {
                            info.setmLineType(LineInfo.LineType.LineTypeMainText);
                        }
                    }
                    //判断字符间距的偏移量
                    //adjustOffset:整个行减去字符以及间距后的剩余宽度，平均分配到每个字符间距中去,从而使每个完整的行都被完全填充。
                    info.setmAdjustOffset((float) (lineWidth - totalCharWidth) / (float) (vLineCharCount - 1));
//                    if (info.getmLineType() == LineInfo.LineType.LineTypeFirstLine){
//                        info.setmAdjustOffset((float) (lineWidth - totalCharWidth-2*mTextSize) / (float) (wordNum - 1));
//                    }
                    mLines.add(info);
                    info = null;

                    //断行后初始化参数
                    totalCharWidth = 0;
                    vLineCharCount = 0;
                    spaceNum = 0;
                    wordNum = 0;
                    //非首行时，用于计算的行宽
                    lineWidth = aLineWidth - 2 * mMarginWidth;
                    i--;
                } else {
                    vLineCharCount++;
                }
            }
        }

        if (vLineCharCount > 0) {
            String text = aText.substring(i - vLineCharCount, i);
            LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);
            info.setmLineType(lineType);

            //检测行首尾空格的数量
            int[] vHeadSpCount = new int[1];
            int[] vTailSpCount = new int[1];

            //去除行首尾空格
            getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
            text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
            if (text.length() > 0) {
                info.setmLineText(text);
                info.setmCharCount(text.length());
                if (mLines.size() > 0 && bTitle == false) {
                    info.setmLineType(LineInfo.LineType.LineTypeMainText);
                }
                mLines.add(info);
            }
        }
        return mLines;
    }

    private int generateAdLineStartYFormWidth(int adHeight, int drawHeight) {
        Boolean isRandomPos = true;
//        isRandomPos = YYOLParmManage.getInstance().isRandomPos();
        if (isRandomPos) {
            //随机Pos
            float maxStartY = drawHeight - adHeight - 2 * (mTextPaint.getTextSize() + mTextInterval);
            float minStartY = 4 * (mTextPaint.getTextSize() + mTextInterval);
            if (minStartY + adHeight > drawHeight) {
                minStartY = drawHeight - adHeight;
                return (int) minStartY;
            }
            if (maxStartY <= 0) {
                return (int) minStartY;
            }

            float randomDxHeight = (float) Math.random() * (maxStartY - minStartY);
            return (int) (randomDxHeight + minStartY);
        } else {
            //固定Pos
            return (drawHeight - adHeight) / 2;
        }
    }

    private YYFrame getNativeAdSize() {
//        int adWidth = mDisplayWidth-2*mDrawLeftRightMargin;
        int adWidth = mDisplayWidth;
//            int adHeight = adWidth*9/16+ScreenUtils.dpToPx(10+12);
        int adHeight = (adWidth - 2 * mDrawLeftRightMargin) * 9 / 16 + 2 * ScreenUtils.dpToPx(YYNativeAd_TopBottom_Height);
//        int adHeight = adWidth*9/16;
        YYFrame frame = new YYFrame(0, 0, adWidth, adHeight);
        return frame;
    }

    private YYFrame getTailPageAdSize() {
        float ratio = mDisplayHeight / mDisplayWidth;
        if (ratio <= 1.7) {
            //小屏幕尺寸，广告缩小面积
            int adHeight = mDisplayHeight * 2 / 3;
            int adWidth = adHeight * 2 / 3;
            int startX = (mDisplayWidth - adWidth) / 2;
            int startY = mDrawTopBottomMargin + mMarginHeight;
            YYFrame frame = new YYFrame(startX, startY, adWidth, adHeight + ScreenUtils.dpToPx(30));
            return frame;
        } else {
            int adWidth = mDisplayWidth - 2 * mDrawLeftRightMargin - 2 * mMarginWidth;
            int adHeight = adWidth * 3 / 2 + ScreenUtils.dpToPx(30);
            int startX = mDrawLeftRightMargin + mMarginWidth;
            int startY = (mDisplayHeight - adHeight) / 2;
            YYFrame frame = new YYFrame(startX, startY, adWidth, adHeight);
            return frame;
        }
    }


    /**************************************private method********************************************/
    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br) {
        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
//        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = chapter.getTitle();//默认展示标题

        StringBuilder vModParaTextSb = new StringBuilder();


        try {
            vModParaTextSb.append(paragraph + "\n");
            while ((paragraph = br.readLine()) != null) {
                vModParaTextSb.append(paragraph + "\n");
            }

            if (vModParaTextSb.length() <= 0) {
                return pages;
            }
            //智能断行
            ArrayList<LineInfo> mLines = newLayoutWithText(vModParaTextSb.toString());
            if (mLines.size() <= 0) {
                return pages;
            }
            int wordcount = 0;
            for (LineInfo lineInfo : mLines) {
                wordcount += lineInfo.getmCharCount();
            }
            chapter.setTextCount(wordcount);
            int startY = 0;

            //设置标题 顶部间距
            // TODO: 2021/10/12 1.8.1 标题上间距
            startY += mTitlePara;
            //                if (i == 0) {
//                    top += mTitlePara;
//                }

            int displayHeight = mVisibleHeight - mDrawTopBottomMargin;
            float offSetY = 0;
            float lineHeight = 0;

            // TODO: 1/13/21 广告范围显示
            YYFrame frame = getNativeAdSize();

            int adStartX = mDrawLeftRightMargin + mMarginWidth;
            int adMesureStartY = generateAdLineStartYFormWidth(frame.getHeight(), displayHeight);
            boolean bPageHaveLoadAd = false;
//            int pageCountPerAd = YYOLParmManage.getInstance().getPageCountPerAd();
            // TODO: 1/8/21 广告
            int pageCountPerAd = 6;

            ArrayList<LineInfo> lines = new ArrayList<>();

            for (int i = 0; i < mLines.size(); i++) {
                LineInfo line = mLines.get(i);
                if (i + 1 < mLines.size()) {
                    LineInfo nextInfo = mLines.get(i + 1);
                    if (line.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                        lineHeight = mTitlePaint.getTextSize();
                        if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                            offSetY = mTitlePaint.getTextSize() + mTitleInterval;
                        } else {
                            offSetY = mTitlePaint.getTextSize() + mTitlePara;
                        }
                    } else if (line.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                        lineHeight = mTextPaint.getTextSize();
                        if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            offSetY = mTextPaint.getTextSize() + mTextPara;
                        } else {
                            offSetY = mTextPaint.getTextSize() + mTextInterval;
                        }
                    } else {
                        lineHeight = mTextPaint.getTextSize();
                        if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            offSetY = mTextPaint.getTextSize() + mTextPara;
                        } else {
                            offSetY = mTextPaint.getTextSize() + mTextInterval;
                        }
                    }
                } else {
                    //最后一行
                    lineHeight = mTextPaint.getTextSize();
                    offSetY = mTextPaint.getTextSize();
                }

                if (startY + lineHeight > displayHeight) {
                    //创建Page
                    TxtPage page = new TxtPage();
                    page.position = pages.size();
                    page.title = chapter.getTitle();
                    page.chapterOrder = chapter.chapterOrder;
//                    page.title = StringUtils.convertCC(chapter.getTitle(), mContext);

//                    page.lines = new ArrayList<>(lines);
                    boolean haveSetTitleLine = false;
                    for (int j = 0; j < lines.size(); j++) {
                        LineInfo aLine = lines.get(j);
                        if (aLine.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                            continue;
                        } else {
                            if (!haveSetTitleLine) {
                                page.titleLines = j;
                                haveSetTitleLine = true;
                            }
//                            break;
                            if (j == page.titleLines) {
                                page.addLineToLastPara(aLine, true);
                            } else {
                                page.addLineToLastPara(aLine, false);
                            }
                        }
                    }
                    page.lineInfos = new ArrayList<>(lines);
                    pages.add(page);
                    // 重置Lines
                    lines.clear();
                    startY = 0;
                    i--;
                    bPageHaveLoadAd = false;
                    adMesureStartY = generateAdLineStartYFormWidth(frame.getHeight(), displayHeight);
                } else {
                    lines.add(line);
                    startY += offSetY;
                }
            }
            if (lines.size() > 0) {
                int iStartY = 0;
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = chapter.getTitle();
                page.chapterOrder = chapter.chapterOrder;
//                page.title = NStringUtils.convertCC(chapter.getTitle(), mContext);
//                    page.lines = new ArrayList<>(lines);

                boolean bHaveSetTitleLines = false;
                boolean bHaveAdLine = false;
                for (int j = 0; j < lines.size(); j++) {
                    LineInfo aLine = lines.get(j);
                    if (aLine.getmLineType() == LineInfo.LineType.LineTypeAdView) {
                        bHaveAdLine = true;
                    }
                    if (aLine.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                        iStartY += mTitlePaint.getTextSize() + mTitleInterval;
                        continue;
                    } else {
                        if (aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            iStartY += mTextPaint.getTextSize() + mTitlePara;
                        } else {
                            iStartY += mTextPaint.getTextSize() + mTextInterval;
                        }
                        if (bHaveSetTitleLines == false) {
                            page.titleLines = j;
                            bHaveSetTitleLines = true;
                        }
                    }

                    if (j == page.titleLines) {
                        page.addLineToLastPara(aLine, true);
                    } else {
                        page.addLineToLastPara(aLine, false);
                    }
                }

                page.lineInfos = new ArrayList<>(lines);
                pages.add(page);
                // 重置Lines
                lines.clear();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }

        //生成的页面
        List<TxtPage> tempPages = new ArrayList<>();

        if (chapter.getmChatperStatusInfo() != null) {
            // TODO:1.8.1 2021/9/30 1.8.1 提前计算好按钮位置
            switch (chapter.getmChatperStatusInfo().getMode()) {
                case LOADING:
                case LOGIN:
                case LACK_BALANCE:
                case PAY:
                case ERROR: {
                    pages = addStatusFrameLayout(pages, chapter);
                }
                break;
            }
        }

        return pages;
    }

    private List<TxtPage> addStatusFrameLayout(List<TxtPage> pages, TxtChapter chapter) {
        List<TxtPage> tempPages = new ArrayList<>();

        if (pages.size() > 0) {
            TxtPage firstPage = pages.get(0);
            int iStartY = 0;

            iStartY += mTitlePara;


            int minLine = 2;
            if (firstPage.lineInfos != null && firstPage.lineInfos.size() > 0) {
                List<LineInfo> lines = firstPage.lineInfos;
                int textContentLines = 0;

                List<LineInfo> newLines = new ArrayList<>();
                for (int j = 0; j < lines.size(); j++) {
                    LineInfo aLine = lines.get(j);
                    if (aLine.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                        iStartY += mTitlePaint.getTextSize() + mTitleInterval;
                        newLines.add(aLine);
                        continue;
                    } else {
                        if (aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            iStartY += mTextPaint.getTextSize() + mTitlePara;
                        } else {
                            iStartY += mTextPaint.getTextSize() + mTextInterval;
                        }
                        newLines.add(aLine);
                        textContentLines++;

                        if (textContentLines >= minLine) {
                            break;
                        }
                    }
                }
                firstPage.lineInfos = newLines;
            }
            addChapterStatus(firstPage, chapter, iStartY);
//            firstPage.chapterOrder =
            tempPages.add(firstPage);
        }

        return tempPages;
    }

    private void addChapterStatus(TxtPage statusPage, TxtChapter chapter, int startY) {
        if (statusPage != null && chapter != null) {
            int dy = (int) (ScreenUtils.dpToPx(20) + mTextPaint.getTextSize());

            int btndy = ScreenUtils.dpToPx(20);

//            int btnWidth = mScreenWidth - 2 * mMarginWidth;


            // TODO: 2021/10/18 1.8.1版本 修改 按钮宽度间距
            int btnWidth = mScreenWidth - 2 * ScreenUtils.dpToPx(42);
            ;
            int btnHeight = ScreenUtils.dpToPx(44);

            int autoBtnWidth = ScreenUtils.dpToPx(TWENTY_TWO);
            int autoBtnHeight = ScreenUtils.dpToPx(TWENTY_TWO);
            int btnX = (mDisplayWidth - btnWidth) / 2;

            String autoTip = PlotRead.getApplication().getString(R.string.section_buy_automatically);
            Paint autoTipTextPaint = new TextPaint();
            autoTipTextPaint.setColor(ReadSettings.BUY_MODE_TIP_TEXT_COLOR);
            autoTipTextPaint.setTextSize(ReadSettings.BUY_BTN_TEXT_SIZE);
            autoTipTextPaint.setAntiAlias(true);

            int autoTipW = (int) autoTipTextPaint.measureText(autoTip);
            autoTipW = autoBtnWidth + autoTipW + ScreenUtils.dpToPx(10);


            int lastY = startY + dy;

            // TODO:1.8.1 2021/9/30 1.8.1 提前计算好按钮位置
            switch (chapter.getmChatperStatusInfo().getMode()) {
                case LOADING: {

                }
                break;
                case LOGIN: {
                    YYFrame loginFrame = new YYFrame(btnX, lastY, btnWidth, btnHeight);
                    statusPage.mLoginFrame = loginFrame;
                }
                break;
                case LACK_BALANCE: {
                    YYFrame payFrame = new YYFrame(btnX, lastY, btnWidth, btnHeight);
                    statusPage.mPayFrame = payFrame;

                    lastY += btnHeight;

                    lastY += btndy;

                    YYFrame autoBuyFrame = new YYFrame(btnX + (payFrame.getWidth() - autoTipW) / 2, lastY, autoBtnWidth, autoBtnHeight);
                    statusPage.mAutoPayFrame = autoBuyFrame;


                }
                break;
                case PAY: {
                    YYFrame payFrame = new YYFrame(btnX, lastY, btnWidth, btnHeight);
                    statusPage.mPayFrame = payFrame;

                    lastY += btnHeight;

                    lastY += btndy;

                    YYFrame payMoreFrame = new YYFrame(btnX, lastY, btnWidth, btnHeight);
                    statusPage.mPayOneMoreFrame = payMoreFrame;

                    lastY += btnHeight;

                    lastY += btndy;

                    YYFrame autoBuyFrame = new YYFrame(btnX + (payFrame.getWidth() - autoTipW) / 2, lastY, autoBtnWidth, autoBtnHeight);
                    statusPage.mAutoPayFrame = autoBuyFrame;

                }
                break;
                case ERROR: {
                    YYFrame reloadFrame = new YYFrame(btnX, lastY, btnWidth, btnHeight);
                    statusPage.mReloadFrame = reloadFrame;
                }
                break;
            }
        }
    }


//    int startPagePos = -1;

    /**
     * @return:获取初始显示的页面
     */
    private TxtPage getCurPage(int pos) {

        if (mCurPageList == null) {
            return new TxtPage();
        }

        if (mCurPageList != null && mCurPageList.size() <= 0) {
            return new TxtPage();
        }


        if (mCurPageList != null && pos >= mCurPageList.size()) {
            return mCurPageList.get(mCurPageList.size() - 1);
        }

        if (mPageChangeListener != null && mCurChapterPos < mChapterList.size() && mCurPageList.get(pos) != null) {
            mPageChangeListener.onPageChange(pos, mChapterList.get(mCurChapterPos).getTextCount(), mCurChapterPos, 0);
//            startPagePos = pos;
        }
        return mCurPageList.get(pos);
    }

    /**
     * 跳章时获取当前页面字数并回调给read
     */
    private void countCurPage() {
        if (mPageChangeListener != null && mCurPage != null) {
            mPageChangeListener.onPageChange(mCurPage.position, 0, mCurChapterPos, mCurPage.pageCharCount());
//            startPagePos = pos;
        }
    }

    /**
     * @return:获取上一个页面
     */
    private TxtPage getPrevPage() {
        int pos = mCurPage.position - 1;
        if (pos < 0) {
            return null;
        }
        if (mPageChangeListener != null && mCurPageList.get(pos) != null) {
            mPageChangeListener.onPageChange(pos, mChapterList.get(mCurChapterPos).getTextCount(), mCurChapterPos, mCurPageList.get(pos + 1).pageCharCount());
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取下一的页面
     */
    private TxtPage getNextPage() {
        int pos = mCurPage.position + 1;
        if (pos >= mCurPageList.size()) {
            return null;
        }
        if (mPageChangeListener != null && mCurPageList.get(pos) != null) {
            mPageChangeListener.onPageChange(pos, mChapterList.get(mCurChapterPos).getTextCount(), mCurChapterPos, mCurPageList.get(pos - 1).pageCharCount());
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取上一个章节的最后一页
     */
    private TxtPage getPrevLastPage() {
        int pos = mCurPageList.size() - 1;

        if (mPageChangeListener != null && mCurPageList.get(pos) != null) {
            mPageChangeListener.onPageChange(pos, mChapterList.get(mCurChapterPos).getTextCount(), mCurChapterPos, 0);
        }

        return mCurPageList.get(pos);
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private boolean canTurnPage() {

        if (!isChapterListPrepare) {
            return false;
        }


        if (mPageMode == PageMode.SCROLL) {
            if (mCurPage.getmPageStatus() != null && mCurPage.getmPageStatus().getMode() != ChapterPageStatusInfo.PageStatusMode.LOADING) {
                return false;
            }
        }


        if (mStatus == STATUS_PARSE_ERROR
                || mStatus == STATUS_PARING) {
            return false;
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_LOADING;
        }
        return true;
    }

    /*****************************************interface*****************************************/

    public interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        void onChapterChange(int pos, int wordcount);

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        void requestChapters(List<TxtChapter> requestChapters);

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        void onCategoryFinish(List<TxtChapter> chapters);

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        void onPageCountChange(int count);

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        void onPageChange(int pos, int wordcount, int pagePos, int pageWordCount);

        void onPageChangeFinish(int pos, boolean success);

        void onReadParaChanged(int paraIndex);
    }

    /*******  广告相关  *********/
    public boolean bCurPageHaveAd() {
        if (mCurPage == null) {
            return false;
        }
        return mCurPage.bHaveAd();
    }

    public LineInfo.LineAdType curPageAdType() {
        if (mCurPage == null) {
            return LineInfo.LineAdType.LineAdTypeNone;
        }
        return mCurPage.getAdType();
    }

    public YYFrame getCurPageAdFrame() {
        return mCurPage.getAdFrame();
    }

    public YYFrame getCurPageAdCloseFrameFromAdFrame(YYFrame adFrame, LineInfo.LineAdType type) {
        if (adFrame.isZeroFrame()) {
            return adFrame;
        }

        if (type == LineInfo.LineAdType.LineAdTypeInterNative) {
            //缩小点击范围，提高点击率
            int margin = ScreenUtils.dpToPx(8);
            int closeWidth = ScreenUtils.dpToPx(51 - 4);
            int closeHeight = ScreenUtils.dpToPx(17 - 2);
            int closeX = adFrame.getX() + adFrame.getWidth() - closeWidth - margin;
            int closeY = adFrame.getY() + margin;
            return new YYFrame(closeX, closeY, closeWidth, closeHeight);
        } else if (type == LineInfo.LineAdType.LineAdTypePage) {
            //大页广告关闭区域
            int closeHeight = ScreenUtils.dpToPx(25);
            return new YYFrame(adFrame.getX(), adFrame.getY() + adFrame.getHeight() - closeHeight, adFrame.getWidth(), closeHeight);
        }
        return YYFrame.YYFrameZero();
    }

    public interface OnNativeAdListener {
        //        YYAdView requestNativeAd(YYFrame frame);  //获取广告
        boolean isHaveNativeAd();  //查询是否有广告

        boolean isHaveTailPageAd();  //查询是否有章节尾竖屏大广告

        //        YYInterAdView needShowAd(YYAdView adView, YYFrame frame);
//        YYPageAdView needShowPageAd(YYAdView adView, YYFrame frame);
        void updateNativeAdSize();

        void pauseAd();

        boolean isFroceClearStatusContent();
    }

    public boolean curPageShowReloadBtn() {
//        if (mCurPage == null){
//            return false;
//        }
        return mReloadBtnIsShown;
    }


    public boolean curPageShowStatusBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect) {
        if (mCurPage == null) {
            return false;
        }

        LogUtils.e("scollerRect ======= " + scollerRect.top);


        boolean touchInStatusBtn = false;

        if(mPageStatusTouch != null)
        {
            touchInStatusBtn = mPageStatusTouch.touchInStatusBtns(event,isScrollFlag,scollerRect,preRect,
                    mPrePageList,
                    mCurPageList,
                    mNextPageList,
                    mCurPage,
                    mDrawTopBottomMargin);
        }

        if(touchInStatusBtn)
        {
            return touchInStatusBtn;
        }


        return mStatusBtnIsShown;
    }


    public boolean curPageAutoBuyShowStatusBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect) {
        if (mCurPage == null) {
            return false;
        }

        boolean touchInStatusBtn = false;

        if(mPageStatusTouch != null)
        {
            touchInStatusBtn = mPageStatusTouch.touchInAutoBuyBtn(event,isScrollFlag,scollerRect,preRect,
                    mPrePageList,
                    mCurPageList,
                    mNextPageList,
                    mCurPage,
                    mDrawTopBottomMargin);
        }

        if(touchInStatusBtn)
        {
            return touchInStatusBtn;
        }


        return mStatusBtnIsShown;
    }


    public TxtPage curScollPageShowStatusBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect) {

        TxtPage touchStatusPage = null;
        if(mPageStatusTouch != null)
        {
            touchStatusPage = mPageStatusTouch.getPageTouchInStatusBtns(event,isScrollFlag,scollerRect,preRect,
                    mPrePageList,
                    mCurPageList,
                    mNextPageList,
                    mCurPage,
                    mDrawTopBottomMargin);
        }

        return touchStatusPage;

    }

    public TxtPage curScollPageAutoBuyShowStatusBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect) {

        TxtPage touchStatusPage = null;
        if(mPageStatusTouch != null)
        {
            touchStatusPage = mPageStatusTouch.getPageTouchInAutoBuyBtn(event,isScrollFlag,scollerRect,preRect,
                    mPrePageList,
                    mCurPageList,
                    mNextPageList,
                    mCurPage,
                    mDrawTopBottomMargin);
        }

        return touchStatusPage;
    }


    public YYFrame getReloadBtnFram() {
        if (mReloadBtnIsShown && mReloadBtnFrame != null) {
            return mReloadBtnFrame;
        }
        return new YYFrame();
    }

    public YYFrame getStatusBtnFram() {
        if (mStatusBtnIsShown && mStatusBtnFrame != null) {
            return mStatusBtnFrame;
        }
        return new YYFrame();
    }

    public YYFrame getStatusAutoPayBtnFram() {
        if (mStatusBtnIsShown && mStatusAutoPayBtnFrame != null) {
            return mStatusAutoPayBtnFrame;
        }
        return new YYFrame();
    }


    public boolean isPageAdPage() {
        //当数据没加载出来时，有布局，mCurPage为空
        if (mCurPage != null && mCurPage.bHaveAd() && mCurPage.getAdType() == LineInfo.LineAdType.LineAdTypePage) {
            return true;
        }
        return false;
    }


    public int getCurPageStartCharPos() {
        if (mCollBook == null || mCurPage == null) {
            return -1;
        }
        if (mCurPage.lineInfos.size() <= 0) {
            return -1;
        }
        return mCurPage.lineInfos.get(0).getmStartPos();
    }

    public int getCurPageCharCount() {
        if (mCollBook == null || mCurPageList == null) {
            return -1;
        }
        return mCurPage.pageCharCount();
    }

    public int getPageStartPos(int pagePos) {
        if (mCollBook == null || mCurPageList == null) {
            return -1;
        }
        return mCurPageList.get(pagePos).lineInfos.get(0).getmStartPos();
    }

    public int getPageCharCount(int pagePos) {
        if (mCollBook == null || mCurPageList == null) {
            return -1;
        }
        return mCurPageList.get(pagePos).pageCharCount();
    }

    public float getTextHeight() {
        return mTextPaint.getTextSize();
    }


    public String getCurChapterLink() {

        if (mChapterList == null || mChapterList.size() <= mCurChapterPos||mCurChapterPos<0) {
            return "";
        }

        return mChapterList.get(mCurChapterPos).getLink();
    }

    public String getCurChapterTitle() {

        if (mChapterList == null || mChapterList.size() <= mCurChapterPos||mCurChapterPos<0) {
            return "";
        }

        return mChapterList.get(mCurChapterPos).getTitle();
    }

    public String getCurChapterID() {

        if (mChapterList == null || mChapterList.size() <= mCurChapterPos||mCurChapterPos<0) {
            return "";
        }

        return mChapterList.get(mCurChapterPos).getChapterId();
    }

    public void clearPageStatusError() {
        if (mPrePageList != null) {
            mPrePageList.clear();
            mPrePageList = null;
        }

        if (mNextPageList != null) {
            mNextPageList.clear();
            mNextPageList = null;
        }
    }


    public boolean isAutoBuySelected() {
        return isAutoBuySelected;
    }

    public void setAutoBuySelected(boolean autoBuySelected) {
        isAutoBuySelected = autoBuySelected;

        if (mCurPage != null && mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }
}
