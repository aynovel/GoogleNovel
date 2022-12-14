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

    // ?????????????????????
    public static final int STATUS_LOADING = 1;         // ????????????
    public static final int STATUS_FINISH = 2;          // ????????????
    public static final int STATUS_ERROR = 3;           // ???????????? (???????????????????????????)
    public static final int STATUS_EMPTY = 4;           // ?????????
    public static final int STATUS_PARING = 5;          // ???????????? (??????????????????)
    public static final int STATUS_PARSE_ERROR = 6;     // ????????????????????????(???????????????)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // ????????????????????????
    // ???????????????????????????
    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    public static final int DEFAULT_MARGIN_WIDTH = 15;
    private static final int DEFAULT_TIP_SIZE = 11;
    private static final int EXTRA_TITLE_SIZE = 4;

    //????????????????????????????????????
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
//    NSString    *_asciiCharTable;      //ascii?????????
//    NSString    *_specialChsCharTable;   //???????????????

    // ??????????????????
    protected List<TxtChapter> mChapterList;

    // ????????????
    protected BookBean mCollBook;
    // ?????????
    protected OnPageChangeListener mPageChangeListener;
    protected OnNativeAdListener mNativeAdListener;

    private Context mContext;
    // ???????????????
    private PageView mPageView;
    // ??????????????????
    public TxtPage mCurPage;  //??????????????????????????????????????????mCurPage??????
    // ??????????????????????????????
    private List<TxtPage> mPrePageList;
    // ???????????????????????????
    private List<TxtPage> mCurPageList;
    // ??????????????????????????????
    private List<TxtPage> mNextPageList;

    // ?????????????????????
    private Paint mBatteryPaint;
    // ?????????????????????
    private Paint mTipPaint;
    // ?????????????????????
    private Paint mTitlePaint;
    // ???????????????????????????(?????????????????????????????????)
    private Paint mBgPaint;
    // ???????????????????????????
    private TextPaint mTextPaint;
    //?????????????????????????????????????????????
    private Paint mSpeechBgPaint;
    // ??????


    // ??????frame
    private YYFrame mReloadBtnFrame;
    // ??????frame
    private boolean mReloadBtnIsShown = false;


    //??????
    private YYFrame mStatusBtnFrame;
    private YYFrame mStatusAutoPayBtnFrame;
    private boolean mStatusBtnIsShown = false;


    // ????????????????????????
    private ReadSettingManager mSettingManager;
    // ???????????????????????????????????????????????????
    private TxtPage mCancelPage;
    // ?????????????????????
    private BookRecordBean mBookRecord;

    private Disposable mPreLoadDisp;

    private TextPaint mAdTextPaint;

    /*****************params**************************/
    // ???????????????
    protected int mStatus = STATUS_LOADING;
    // ????????????????????????????????????
    protected boolean isChapterListPrepare;

    // ?????????????????????
    private boolean isChapterOpen;
    private boolean isFirstOpen = true;
    private boolean isClose;
    // ???????????????????????????
    private PageMode mPageMode;
    // ????????????????????????
    private PageStyle mPageStyle;
    //???????????????????????????
    private boolean isNightMode;
    //???????????????????????????
    private int mVisibleWidth;
    private int mVisibleHeight;
    //?????????????????????????????????????????????
    private int mDrawTopBottomMargin;
    private int mDrawLeftRightMargin;
    //???????????????
    private int mDisplayWidth;
    private int mDisplayHeight;
    //??????
    private int mMarginWidth;
    private int mMarginHeight;
    //???????????????
    private int mTextColor;
    //???????????????
    private int mTitleSize;
    //???????????????
    private int mTextSize;
    //?????????
    private int mTextMargin;
    //?????????
    private int mTextInterval;
    //??????????????????
    private int mTitleInterval;
    //????????????(??????????????????????????????)
    private int mTextPara;
    private int mTitlePara;
    //??????????????????
    private int mBatteryLevel;
    //?????????????????????
    private int mBgColor;
    //??????????????????????????????
    private int mSpeechBgColor;

    private int mBgResource;
    private int mScreenWidth;
    private int mScreenHeight;

    // ?????????
    protected int mCurChapterPos = 0;
    //??????????????????
    private int mLastChapterPos = 0;

    private boolean haveDisplayCutout = false;
    private int displaycutoutRight = 0;

    private String mCurFontPath;

    /**
     * ?????????????????????
     */
    private Bitmap autoOpenIcon;
    private Bitmap autoCloseIcon;
    private Bitmap recommentDes;
    private Bitmap autoPreferentialIcon;
    private Bitmap autoBuyIcon;
    private Bitmap rewardIcon;


    /**
     * ????????????
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

        // ???????????????
        initData();
        // ???????????????
        initPaint();

        //??????????????????
        initFontSizeTable();

        // ?????????PageView
        initPageView();
        // ???????????????
        prepareBook();
    }

    private void initData() {
        // ?????????????????????
        mSettingManager = ReadSettingManager.getInstance();
        // ??????????????????
        // TODO:1.8.1 2021/9/29 1.8.1??????????????????
        mPageMode = mSettingManager.getPageMode();
//        mPageMode = PageMode.COVER;
//        mPageMode = PageMode.VERTICAL_COVER;
//        mPageMode = PageMode.SCROLL;
        //????????????
//        if (mPageMode==PageMode.AUTO || (mPageMode == PageMode.SCROLL && !JuYueAppUserHelper.getInstance().isNoUserAdFlag())) {
//            mPageMode = PageMode.SIMULATION;
//            mSettingManager.setPageMode(PageMode.SIMULATION);
//        }
        mPageStyle = mSettingManager.getPageStyle();
        // ???????????????
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);
        // ???????????????????????????
        setUpTextParams(mSettingManager.getTextSize());
        initLayoutModeParm(mSettingManager.getLayoutMode());

        DisplayMetrics dm = PlotRead.getContext().getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = getRealHeight(mContext);

        //???????????????
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


    // TODO: 2021/10/5 1.8.1 ????????????
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
     * ???????????????????????????????????????
     *
     * @param textSize
     */
    private void setUpTextParams(int textSize) {
        // ????????????
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
//        // ?????????(????????????????????????)
//        mTextInterval = mTextSize / 2;
//        mTitleInterval = mTitleSize / 2;
//        // ????????????(????????????????????????)
//        mTextPara = mTextSize;
//        mTitlePara = mTitleSize;
//
//        //?????????
//        mTextMargin = 1;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param layoutMode
     */
    private void initLayoutModeParm(LayoutMode layoutMode) {
        switch (layoutMode) {
            case JincouMode: {
                //?????????
                mTextInterval = ScreenUtils.dpToPx(10);
                //?????????
                mTextPara = ScreenUtils.dpToPx(20);
                //???????????????
                mTitleInterval = ScreenUtils.dpToPx(20);
                //?????????
                mTextMargin = ScreenUtils.dpToPx(1);
                //????????????????????????
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(0);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_JinCou_LeftRightMargin);
            }
            break;
            case ShushiMode: {
                //?????????
                mTextInterval = ScreenUtils.dpToPx(15);
                //?????????
                mTextPara = ScreenUtils.dpToPx(30);
                //???????????????
                mTitleInterval = ScreenUtils.dpToPx(20);
                //?????????
                mTextMargin = ScreenUtils.dpToPx(1);
                //????????????????????????
                mTitlePara = ScreenUtils.dpToPx(40);
                //top & bottom margin
                mDrawTopBottomMargin = ScreenUtils.dpToPx(10);
                //left & right margin
                mDrawLeftRightMargin = ScreenUtils.dpToPx(YYReadCore_Shushi_LeftRightMargin);
            }
            break;
            case SongsanMode: {
                //?????????
                mTextInterval = ScreenUtils.dpToPx(20);
                //?????????
                mTextPara = ScreenUtils.dpToPx(40);
                //???????????????
                mTitleInterval = ScreenUtils.dpToPx(20);
                //?????????
                mTextMargin = ScreenUtils.dpToPx(1);
                //????????????????????????
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

//                //?????????
//                mTextInterval = ScreenUtils.dpToPx(10);
//                //???????????????
//                mTitleInterval = ScreenUtils.dpToPx(20);

                //?????????
                mTextPara = ScreenUtils.dpToPx(20);
                //?????????
                mTextMargin = ScreenUtils.dpToPx(1);
                //????????????????????????
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


        // ?????????????????????
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // ??????????????????
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip?????????????????????
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);
        mTipPaint.setTypeface(font);

        // ???????????????????????????
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(font);

        mSpeechBgPaint = new Paint();
        //??????????????????
        mSpeechBgPaint.setColor(mSpeechBgColor);
        //??????????????????????????????????????????FILL ??? STORKE
        mSpeechBgPaint.setStyle(Paint.Style.FILL);

        // ?????????????????????
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTypeface(font);

        //???????????????????????????
        mAdTextPaint = new TextPaint();
        mAdTextPaint.setColor(mTextColor);
        mAdTextPaint.setTextSize(ScreenUtils.spToPx(20));
        mAdTextPaint.setAntiAlias(true);

        // ?????????????????????
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        // ?????????????????????
        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

        // ?????????????????????
        setNightMode(mSettingManager.isNightMode());
    }

    private void initPageView() {
        //????????????
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }

    /****************************** public method***************************/
    /**
     * ??????????????????
     *
     * @return
     */
    public boolean skipPreChapter() {
        if (!hasPrevChapter()) {
            return false;
        }
        // ??????????????????
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public boolean skipNextChapter() {
        if (!hasNextChapter()) {
            return false;
        }

        //????????????????????????????????????
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * ?????????????????????
     *
     * @param pos:??? 0 ?????????
     */
    public void skipToChapter(int pos) {
        // ????????????
        mCurChapterPos = pos;

        // ??????????????????????????????null
        mPrePageList = null;
        // ???????????????????????????????????????????????????
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // ???????????????????????????null
        mNextPageList = null;

        // ??????????????????
        openChapter();
    }

    /**
     * ?????????????????????
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
     * ???????????????
     *
     * @return
     */
    public boolean skipToPrePage() {
        return mPageView.autoPrevPage();
    }

    /**
     * ???????????????
     *
     * @return
     */
    public boolean skipToNextPage() {
        return mPageView.autoNextPage();
    }

    /**
     * ????????????
     */
    public void updateTime() {
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * ????????????
     */
    public void forceUpdatePage() {
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    /**
     * ????????????
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
     * ???????????????????????????
     *
     * @param textSize:????????? px???
     */
    public void setTipTextSize(int textSize) {
        mTipPaint.setTextSize(textSize);

        // ??????????????????????????????
        mPageView.drawCurPage(false);
    }

    public void setReadFont() {
//        String curFontPath = ReadSettingManager.getInstance().getSelectedFontPath();
//        if (StringUtils.isNotBlank(curFontPath) && curFontPath.equals(mCurFontPath)) {
//            //?????????????????????????????????????????????
//            return;
//        }

        // ????????????????????????
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
        // ????????????
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // ??????????????????????????????
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // ????????????????????????
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //???????????????
                mStatus = STATUS_ERROR;
            } else {
                // ??????????????????????????????????????????????????????????????????????????????????????????
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // ????????????????????????
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

        mPageView.drawCurPage(false);
    }

    /*
     * ??????????????????
     */
    public void fullScreenChanged() {
        setTextSize(mTextSize);
    }

    /**
     * ????????????????????????
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        // ????????????????????????

        saveRecord();

        setUpTextParams(textSize);

        // ???????????????????????????
        mTextPaint.setTextSize(mTextSize);
        // ???????????????????????????
        mTitlePaint.setTextSize(mTitleSize);
        // ??????????????????
        mSettingManager.setTextSize(mTextSize);
        // ????????????
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // ??????????????????????????????
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // ????????????????????????
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //???????????????
                mStatus = STATUS_ERROR;
            } else {
                // ??????????????????????????????????????????????????????????????????????????????????????????
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // ????????????????????????
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

        mPageView.drawCurPage(false);
    }

    /**
     * ????????????????????????
     *
     * @param lineSize
     */
    public void setLineSize(int lineSize) {
        saveRecord();

        // ????????????????????????
        mSettingManager.setLineSize(lineSize);
        mTextInterval = mSettingManager.getLineSize();
        mTitleInterval = mSettingManager.getLineSize() + UIUtil.dip2px(mContext, 10);

        // ????????????
        mPrePageList = null;
        mNextPageList = null;

        // ??????????????????????????????
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // ????????????????????????
            dealLoadPageList(mCurChapterPos);

            // ??????????????????????????????????????????????????????????????????????????????????????????
            if (mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
            }

            // ????????????????????????
            mCurPage = mCurPageList.get(mCurPage.position);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * ??????????????????
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
     * ??????????????????
     *
     * @param pageStyle:????????????
     */
    public void setPageStyle(PageStyle pageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle;
            mSettingManager.setPageStyle(pageStyle);
        }

        if (isNightMode && pageStyle != PageStyle.NIGHT) {
            return;
        }


        // ????????????????????????
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
     * ????????????
     *
     * @param pageMode:????????????
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        Boolean needReDrow = false;
        if (mPageMode == PageMode.SCROLL || pageMode == PageMode.SCROLL) {
            //???scroll??????????????????,???scroll????????????????????????????????????????????????
            needReDrow = true;
        }

        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);
        mSettingManager.setPageMode(mPageMode);

        // ?????????????????????
        if (needReDrow) {
            mPageView.drawCurPage(false);
        }
    }

    public void setTemporaryPageMode(PageMode pageMode) {
        Boolean needReDrow = false;
        if (mPageMode == PageMode.SCROLL || pageMode == PageMode.SCROLL) {
            //???scroll??????????????????,???scroll????????????????????????????????????????????????
            needReDrow = true;
        }

        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);

        // ?????????????????????
        if (needReDrow) {
            mPageView.drawCurPage(false);
        }
    }

    /**
     * ????????????????????????
     */
    public boolean isAutoRead() {
        return mPageView.isAutoRead();
    }


    /**
     * ??????????????????
     */
    public void startAutoRead() {
        mPageView.setPageMode(PageMode.AUTO);
        mPageView.drawCurPage(false);
        mPageView.startAutoRead();
    }

    /**
     * ??????????????????
     */
    public void endAutoRead() {
        mPageView.setPageMode(mPageMode);
        mPageView.drawCurPage(false);
        mPageView.endAutoRead();
    }


    /**
     * ????????????
     *
     * @param layoutMode:????????????
     * @see LayoutMode
     */
    public void setLayoutMode(LayoutMode layoutMode) {
        saveRecord();
        initLayoutModeParm(layoutMode);
        mSettingManager.setLayoutMode(layoutMode);
        mPrePageList = null;
        mNextPageList = null;

        // ???????????? size
        if (mDisplayWidth > 0) {
            if (mNativeAdListener != null) {
                mNativeAdListener.updateNativeAdSize();
            }
        }

        // ??????????????????????????????
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // ????????????????????????
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                mStatus = STATUS_ERROR;
            } else {
                // ??????????????????????????????????????????????????????????????????????????????????????????
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // ????????????????????????
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }
        mPageView.drawCurPage(false);
    }


    /**
     * ??????????????????????????????
     *
     * @param marginWidth  :????????? px
     * @param marginHeight :????????? px
     */
    public void setMargin(int marginWidth, int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        // ????????????????????????????????????????????????
        if (mPageMode == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * ????????????????????????
     *
     * @param listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;

        // ?????????????????????????????????????????????????????????????????????
        if (isChapterListPrepare) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
    }

    public void setmNativeAdListener(@Nullable OnNativeAdListener mNativeAdListener) {
        this.mNativeAdListener = mNativeAdListener;
    }

    //???????????????????????????
    public void refreshPage(int speechingParaIndex) {

        //?????????????????????????????????
        if (mCurPage == null) {
            return;
        }

        saveRecord();

        // ????????????
        mPrePageList = null;
        mNextPageList = null;

        initFontSizeTable();

        // ??????????????????????????????
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // ????????????????????????
            dealLoadPageList(mCurChapterPos);

            if (mCurPageList == null) {
                //???????????????
                mStatus = STATUS_ERROR;
            } else {
                // ??????????????????????????????????????????????????????????????????????????????????????????
                mCurPage.position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());

                // ????????????????????????
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

//        mCurPage.setSpeechingPara(speechingParaIndex);
        mPageView.drawCurPage(false);
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    public int getPageStatus() {
        return mStatus;
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     *
     * @return
     */
    public List<TxtChapter> getChapterCategory() {
        return mChapterList;
    }

    /**
     * ????????????????????????
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
     * ???????????????????????????
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
     * ?????????????????????????????????
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }

    /**
     * ??????????????????
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

        //??????????????????
        DBUtils.getInstance().saveBookRecordWithAsync(mBookRecord);
    }

    /**
     * ???????????????
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
     *  ????????????
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
     *  ????????????
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
     * ??????????????????
     */
    public void openChapter() {
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // ?????????????????????????????????
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // ????????????????????????????????????
        if (mChapterList.isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        if (parseCurChapter()) {
            // ????????????????????????
            if (!isChapterOpen) {
                int position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());
//                int position = mBookRecord.getPagePos();

                // ???????????????????????????????????????????????????
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // ????????????
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
        //????????????
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    /**
     * ????????????
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
     * ??????????????????
     *
     * @param chapterPos:????????????
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos) throws Exception {
        // ????????????
        TxtChapter chapter = mChapterList.get(chapterPos);
        // ????????????????????????
        if (!hasChapterData(chapter)) {
            return null;
        }

        // TODO:1.8.1 2021/9/29 1.8.1????????????????????????


        // ????????????????????????
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
     * ??????????????????
     */
    public abstract void refreshChapterList();

    /**
     * ????????????????????????
     *
     * @param chapter
     * @return
     */
    protected abstract BufferedReader getChapterReader(TxtChapter chapter) throws Exception;

    /**
     * ????????????????????????
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
        //????????????
        mPageView.postInvalidate();
    }


    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        if (bitmap == null) {
            //???mPageAnim???null??????????????????????????????bitmap???null???????????????return
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        int tipMarginHeight = ScreenUtils.dpToPx(3);
        if (!isUpdate) {
            /****????????????****/
            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            } else {
                drawBackgroundImg(canvas, false);
            }
            if (!mChapterList.isEmpty()) {
                /*****????????????????????????********/
                //??????????????????:??????text???y???????????????text????????????????????????????????????text??????????????????
                float tipTop = tipMarginHeight + mTipPaint.getFontSpacing();
                //???????????????????????????????????????
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

                /******????????????********/
                // ???????????????????????????Y
                float y = mDisplayHeight - tipMarginHeight - ScreenUtils.dpToPx(2);
                // ??????finish?????????????????????
                if (mStatus == STATUS_FINISH) {
                    String percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
                    canvas.drawText(percent, mMarginWidth, y, mTipPaint);
                }
            }
        } else {
            //????????????
            if (mBgResource != 0) {
                drawBackgroundImg(canvas, true);
            } else {
                mBgPaint.setColor(mBgColor);
                // TODO: 1/14/21 ???????????????????????? 
//                canvas.drawRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
            }
        }

        /******????????????********/

        int visibleRight = mDisplayWidth - mMarginWidth;
        int visibleBottom = mDisplayHeight - tipMarginHeight;

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;

        //???????????????
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));

        mBatteryPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(polar, mBatteryPaint);

        //???????????????
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);

        //???????????????
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);

        /******??????????????????********/
        //???????????????????????????Y
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
            //???????????????, ???????????????
            canvas.drawColor(Color.WHITE);
        }
    }

    private void drawContent(Bitmap bitmap, boolean isUpdate) {
        if (bitmap == null) {
            //???mPageAnim???null??????????????????????????????bitmap???null???????????????return
            return;
        }

        Canvas canvas = new Canvas(bitmap);

        if (mPageMode == PageMode.SCROLL) {

            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            } else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//???????????????
//                drawBackgroundImg(canvas, false);
            }
        }
        /******????????????****/
        mReloadBtnFrame = null;
        mReloadBtnIsShown = false;

        mStatusBtnFrame = null;
        mStatusAutoPayBtnFrame = null;
        mStatusBtnIsShown = false;

        if (mStatus != STATUS_FINISH) {
            //????????????
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "Loading ...";
                    break;
                case STATUS_ERROR:
                    tip = "????????????(??????????????????)";
                    tip = PlotRead.getApplication().getString(R.string.reload_the);
                    mReloadBtnIsShown = true;
                    break;
                case STATUS_EMPTY:
                    tip = "??????????????????";
                    tip = PlotRead.getApplication().getString(R.string.reload_the) + "Content";
                    mReloadBtnIsShown = true;
                    break;
                case STATUS_PARING:
                    tip = "?????????????????????...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "??????????????????";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "??????????????????";
                    break;
            }

            //??????????????????????????????
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

                // ?????????????????????
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


            //???????????????
            //?????????
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            //?????????
            int para = mTextPara + (int) mTextPaint.getTextSize();
            //title???????????????
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            //title???????????????
            int titlePara = mTitlePara + (int) mTitlePaint.getTextSize();
            String str = null;

            //?????????????????????
            for (int i = 0; i < mCurPage.titleLines; ++i) {
                LineInfo aLine = mCurPage.lineInfos.get(i);
                str = aLine.getmLineText();

                //??????????????????
                // TODO: 2021/10/12 1.8.1 ???????????????
                if (i == 0) {
                    top += mTitlePara;
                }

                //??????????????????????????????
                float strWidth = getStrWidth(aLine);
//                float start = mMarginWidth + mDrawLeftRightMargin + (mVisibleWidth - strWidth - 2 * mDrawLeftRightMargin) / 2;

                float start = mMarginWidth + mDrawLeftRightMargin;
                //????????????
                for (int j = 0; j < str.length(); j++) {
                    String a = str.substring(j, j + 1);
                    canvas.drawText(a, start, top, mTitlePaint);
                    char aa = a.charAt(0);
                    start += aLine.getmAdjustOffset() + getCharWidth(aa, aLine.getmLineType());
                }

                //??????????????????
                if (i == mCurPage.titleLines - 1) {
                    top += mTitlePara + mTextPaint.getTextSize();
                } else {
                    //?????????
                    top += mTitleInterval + mTitlePaint.getTextSize();
                }
            }

//            if (mCurPage.getAdType() == LineInfo.LineAdType.LineAdTypeNone){
            mPageView.cleanAdView();
//            }

            //?????????????????????
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
                    // TODO: 2021/10/12  emoji ??????
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

            // TODO:1.8.1 2021/9/29 1.8.1???????????????????????????
            if (mCurPage != null && mCurPage.getShowStatusFlag() && mCurPage.getmPageStatus() != null) {
                drawCustomStatus(canvas, top);
            }

        }
    }

    // TODO:1.8.1 2021/9/29 1.8.1???????????????????????????
    private void drawCustomStatus(Canvas canvas, float topY) {
        //????????????
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
                    tip = "????????????(??????????????????)";
                    tip = PlotRead.getApplication().getString(R.string.reload_the);
                    break;
                case LOGIN:
                    tip = "??????(????????????)";
                    tip = PlotRead.getApplication().getString(R.string.go_login);
                    break;
                case LACK_BALANCE:
                    tip = "???????????????";
                    tip = PlotRead.getApplication().getString(R.string.top_up_read);
                    break;
                case PAY:
                    tip = "VIP(????????????)";
                    tip = pageStatusInfo.getChapterPrice() + " " + PlotRead.getApplication().getString(R.string.current_balance);
                    moretip = PlotRead.getApplication().getString(R.string.buy_more_chapter);
                    break;
            }

            Paint btnTextPaint = new TextPaint();
            btnTextPaint.setColor(Color.WHITE);
            btnTextPaint.setTextSize(ScreenUtils.dpToPx(16));
            btnTextPaint.setAntiAlias(true);


            //??????????????????????????????
            Paint.FontMetrics fontMetrics = btnTextPaint.getFontMetrics();
            float textWidth = btnTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = topY;


            // ?????????????????????
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
        /** ???????????????canvas????????????????????????????????? */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    void prepareDisplay(int w, int h) {
        //????????????????????????
        // ??????PageView?????????
        mDisplayWidth = w;
        mDisplayHeight = h;

        // ?????????????????????????????????
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        if (mPageView == null) {
            return;
        }
        // ?????? PageMode
        mPageView.setPageMode(mPageMode);

        if (!isChapterOpen) {
            // ??????????????????
            mPageView.drawCurPage(false);
            // ????????? display ??????????????? openChapter ???????????????????????????
            // ?????????????????? display ????????????????????????
            if (!isFirstOpen) {
                // ????????????
                openChapter();
            }
        } else {
            // ???????????????????????????????????????????????????
            if (mStatus == STATUS_FINISH) {
                mPrePageList = null;
                mNextPageList = null;
                dealLoadPageList(mCurChapterPos);
                int position = getPagePosWithCharPos(mBookRecord.getChapterCharIndex());
                // ?????????????????????????????????
                mCurPage = getCurPage(position);
            }
            mPageView.drawCurPage(false);
        }
    }

    /**
     * ???????????????
     *
     * @return
     */
    boolean prev() {
        // ????????????????????????
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // ??????????????????????????????
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
     * ?????????????????????
     *
     * @return:????????????????????????
     */
    boolean parsePrevChapter() {
        // ?????????????????????
        int prevChapter = mCurChapterPos - 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = prevChapter;

        countCurPage();
        // ???????????????????????????
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


        // ?????????????????????????????????
        if (mPrePageList != null) {
            mCurPageList = mPrePageList;
            mPrePageList = null;

            // ??????
            chapterChangeCallback();
        } else {
            dealLoadPageList(prevChapter);
        }
        return mCurPageList != null ? true : false;
    }

    private boolean hasPrevChapter() {
        //??????????????????????????????
        if (mCurChapterPos - 1 < 0) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????
     *
     * @return:??????????????????
     */
    boolean next() {
        // ????????????????????????
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // ??????????????????????????????
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
        // ?????????????????????
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
        // ????????????????????????????????????
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return false;
        }
        return true;
    }

    boolean parseCurChapter() {
        //??????????????????????????????????????????
        if (mCurChapterPos >= mChapterList.size()) {
            mCurChapterPos = mChapterList.size() - 1;
        }
        // ????????????
        dealLoadPageList(mCurChapterPos);
        // ?????????????????????
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    /**
     * ?????????????????????
     *
     * @return:??????????????????????????????
     */
    boolean parseNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = nextChapter;

        // ???????????????????????????????????????????????????
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


        // ???????????????????????????????????????
        if (mNextPageList != null) {


            mCurPageList = mNextPageList;
            mNextPageList = null;
            // ??????
            chapterChangeCallback();
        } else {
            // ??????????????????
            dealLoadPageList(nextChapter);
        }
        // ?????????????????????
        preLoadNextChapter();
        return mCurPageList != null ? true : false;
    }

    private void dealLoadPageList(int chapterPos) {
        try {
            mCurPageList = loadPageList(chapterPos);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // ?????????????????????
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

        // ??????
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

    // ??????????????????
    private void preLoadNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        // ???????????????????????????????????????????????????????????????????????????
        if (!hasNextChapter()
                || !hasChapterData(mChapterList.get(nextChapter))) {
            return;
        }

//        if (!hasNextChapter()
//                || !realHasChapterData(mChapterList.get(nextChapter))) {
//            return;
//        }


        //?????????????????????????????????
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        //?????????????????????????????????
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
                        //????????????
                    }
                });
    }

    // ????????????
    void pageCancel() {
        if (mCurPage.position == 0 && mCurChapterPos > mLastChapterPos) { // ???????????????????????????
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
                && mCurChapterPos < mLastChapterPos)) {  // ????????????????????????

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
            // ?????????????????????????????????????????????????????????????????????
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
        // ???????????????
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;
        // ??????????????????
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

    private float mChsCharWidth;  //??????????????????
    private float mTitleChsCharWidth;  //??????????????????

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
        mChsCharWidth = mTextPaint.measureText("???");
        mTitleChsCharWidth = mTitlePaint.measureText("???");
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

    //?????????????????????
    private ArrayList<LineInfo> newLayoutWithText(String vTextPtr) {
        ArrayList<LineInfo> mlines = new ArrayList<>();
        int baseCharPos = 0;  //??????????????????char pos????????????
        char vBeginChar;
        int vBreakPos = 0, vLength;
        String vParaTextPtr = null;

        StringBuilder vModParaTextSb = new StringBuilder();
        //????????????????????????
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

            //?????????????????????????????????????????????\n???
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

        //???????????????string
        if (vTextPtr.length() > 0) {
            mlines.addAll(SmartBreakText(vTextPtr, vModParaTextSb, baseCharPos, bTitle));
        }
        return mlines;
    }

    //????????????
    private ArrayList<LineInfo> SmartBreakText(String aParaText, StringBuilder aModSb, int baseCharPos, boolean bTitle) {
        //???????????????????????????
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

        //??????????????????????????????
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
        //???????????????????????????
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
        //???????????????????????????
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
     * Emoji????????????
     *
     * @param string
     * @return
     */
    private boolean isEmoji(String string) {
        //??????Emoji??????
        Pattern p = Pattern.compile("[^\\u0000-\\uFFFF]");
        //??????Emoji??????????????????
        //Pattern p = Pattern.compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]|[\\ud83e\\udd00-\\ud83e\\uddff]|[\\u2300-\\u23ff]|[\\u2500-\\u25ff]|[\\u2100-\\u21ff]|[\\u0000-\\u00ff]|[\\u2b00-\\u2bff]|[\\u2d06]|[\\u3030]");
        Matcher m = p.matcher(string);
        return m.find();
    }

//    private ArrayList<LineInfo> mLines = new ArrayList<LineInfo>();

    // aLineWidth ??????
    // TODO: 2021/10/4 ???????????????
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
        //??????????????????(????????????)?????????[????????????????????????????????????+???????????????????????????????????????????????????????????????]
        int lineWidth = aLineWidth - 2 * mMarginWidth;
        //?????????????????????????????????????????????????????????0???
        int vLineCharCount = 0;
        //????????????????????????????????????????????????????????????????????????????????????0???
        int totalCharWidth = 0;
        char c;

//        if (lineType != LineInfo.LineType.LineTypeTitle) {
//            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
//            lineWidth -= 2 * mChsCharWidth;
//        }

        if (lineType != LineInfo.LineType.LineTypeTitle) {
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
            lineWidth -= 1 * mChsCharWidth;
        }


        int i = 0;
        int spaceNum = 0;//????????????????????????????????????:(?????????????????????????????????????????????)
        int wordNum = 0; //???????????????(welcome. welcome wel- ),??????????????????????????????

        int lineCount = mLines.size();  //????????????????????????????????????????????????FistLine????????????MainLine

        for (; i < vTextCount; i++) {
            c = aText.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                //????????????
                vWordSize = GetWord(aText, i, vWordWidth, lineType);
                wordNum++;
                //?????????????????????????????????
                if (vWordWidth[0] < lineWidth) {
                    totalCharWidth += vWordWidth[0];

                    //????????????????????????????????????????????????????????????
                    if (totalCharWidth > lineWidth) {
                        String word = aText.substring(i, i + vWordSize);
                        //???????????????????????????
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

                        //?????????????????????
                        //??????????????????????????????
                        int[] vHeadSpCount = new int[1];
                        int[] vTailSpCount = new int[1];

                        //?????????????????????
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
                    //???????????????????????? TODO ...
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
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                //charWidth:???????????????????????????+?????????
                float charWidth = getCharWidth(c, lineType);
                wordNum++;
                totalCharWidth += charWidth;

                //?????????????????????????????????????????????
                if (totalCharWidth > lineWidth) {
                    if (c == 0x0020) {//???????????????????????????????????????????????????' ',??????????????????????????????????????????
                        spaceNum++;
                        vLineCharCount++;
                        continue;
                    }
                    totalCharWidth -= charWidth;
                    //??????????????????????????????????????????????????????
                    char lastChar = aText.charAt(i - 1);
                    if (lastChar == 0x201c) {
                        i--;
                        vLineCharCount--;
                        totalCharWidth -= getCharWidth(lastChar, lineType);
                    } else {
                        //??????????????????
                        if (c == 0x201d) {
                            //???????????????,??????????????????
                            i++;
                            vLineCharCount++;
                            totalCharWidth += charWidth;
                        } else if (c == 0x201c) {
                            //??????????????????donothing
                            //                        continue;
                        } else if ((c >= KFullWidthMarkBegin && c <= KFullWidthMarkEnd)
                                || (c >= KCjkMarkBegin && c <= KCjkMarkEnd)
                                || (c >= KSpecialChsBegin && c <= KSpecialChsEnd)
                                || (c >= KAsciiMarkBegin && c <= KAsciiMarkEnd)) {
                            //????????? ??? ????????????????????? ????????????????????????
                            if (c == 0x3002 && aText.charAt(i - 1) == 0x201d) {
                                i++;
                                vLineCharCount++;
                                totalCharWidth += charWidth;
                            } else {
                                //?????????????????????????????????????????????????????????
                                //????????????????????????????????????
                                i--;
                                vLineCharCount--;
                                totalCharWidth -= getCharWidth(aText.charAt(i), lineType);
                            }
                        }
                    }

                    KRepeatCount++;
                    //????????????????????????????????????
                    //i:????????????????????????????????????
                    //vLineCharCount:????????????????????????????????????????????????????????????????????????
                    //baseCharPos:??????????????????????????????????????????????????????
                    String text = aText.substring(i - vLineCharCount, i);
                    LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);

                    //??????????????????????????????
                    int[] vHeadSpCount = new int[1];
                    int[] vTailSpCount = new int[1];

                    //?????????????????????
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
                            //????????????????????????
                            info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                        } else {
                            info.setmLineType(LineInfo.LineType.LineTypeMainText);
                        }
                    }
                    //??????????????????????????????
                    //adjustOffset:?????????????????????????????????????????????????????????????????????????????????????????????,????????????????????????????????????????????????
                    info.setmAdjustOffset((float) (lineWidth - totalCharWidth) / (float) (vLineCharCount - 1));
//                    if (info.getmLineType() == LineInfo.LineType.LineTypeFirstLine){
//                        info.setmAdjustOffset((float) (lineWidth - totalCharWidth-2*mTextSize) / (float) (wordNum - 1));
//                    }
                    mLines.add(info);
                    info = null;

                    //????????????????????????
                    totalCharWidth = 0;
                    vLineCharCount = 0;
                    spaceNum = 0;
                    wordNum = 0;
                    //????????????????????????????????????
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

            //??????????????????????????????
            int[] vHeadSpCount = new int[1];
            int[] vTailSpCount = new int[1];

            //?????????????????????
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
            //??????Pos
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
            //??????Pos
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
            //????????????????????????????????????
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
     * ???????????????????????????????????????
     *
     * @param chapter???????????????
     * @param br?????????????????????
     * @return
     */
    private List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br) {
        //???????????????
        List<TxtPage> pages = new ArrayList<>();
        //????????????????????????
//        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // ??????????????????
        String paragraph = chapter.getTitle();//??????????????????

        StringBuilder vModParaTextSb = new StringBuilder();


        try {
            vModParaTextSb.append(paragraph + "\n");
            while ((paragraph = br.readLine()) != null) {
                vModParaTextSb.append(paragraph + "\n");
            }

            if (vModParaTextSb.length() <= 0) {
                return pages;
            }
            //????????????
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

            //???????????? ????????????
            // TODO: 2021/10/12 1.8.1 ???????????????
            startY += mTitlePara;
            //                if (i == 0) {
//                    top += mTitlePara;
//                }

            int displayHeight = mVisibleHeight - mDrawTopBottomMargin;
            float offSetY = 0;
            float lineHeight = 0;

            // TODO: 1/13/21 ??????????????????
            YYFrame frame = getNativeAdSize();

            int adStartX = mDrawLeftRightMargin + mMarginWidth;
            int adMesureStartY = generateAdLineStartYFormWidth(frame.getHeight(), displayHeight);
            boolean bPageHaveLoadAd = false;
//            int pageCountPerAd = YYOLParmManage.getInstance().getPageCountPerAd();
            // TODO: 1/8/21 ??????
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
                    //????????????
                    lineHeight = mTextPaint.getTextSize();
                    offSetY = mTextPaint.getTextSize();
                }

                if (startY + lineHeight > displayHeight) {
                    //??????Page
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
                    // ??????Lines
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
                // ??????Lines
                lines.clear();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }

        //???????????????
        List<TxtPage> tempPages = new ArrayList<>();

        if (chapter.getmChatperStatusInfo() != null) {
            // TODO:1.8.1 2021/9/30 1.8.1 ???????????????????????????
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


            // TODO: 2021/10/18 1.8.1?????? ?????? ??????????????????
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

            // TODO:1.8.1 2021/9/30 1.8.1 ???????????????????????????
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
     * @return:???????????????????????????
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
     * ?????????????????????????????????????????????read
     */
    private void countCurPage() {
        if (mPageChangeListener != null && mCurPage != null) {
            mPageChangeListener.onPageChange(mCurPage.position, 0, mCurChapterPos, mCurPage.pageCharCount());
//            startPagePos = pos;
        }
    }

    /**
     * @return:?????????????????????
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
     * @return:?????????????????????
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
     * @return:????????????????????????????????????
     */
    private TxtPage getPrevLastPage() {
        int pos = mCurPageList.size() - 1;

        if (mPageChangeListener != null && mCurPageList.get(pos) != null) {
            mPageChangeListener.onPageChange(pos, mChapterList.get(mCurChapterPos).getTextCount(), mCurChapterPos, 0);
        }

        return mCurPageList.get(pos);
    }

    /**
     * ?????????????????????????????????????????????
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
         * ??????????????????????????????????????????
         *
         * @param pos:?????????????????????
         */
        void onChapterChange(int pos, int wordcount);

        /**
         * ?????????????????????????????????
         *
         * @param requestChapters:???????????????????????????
         */
        void requestChapters(List<TxtChapter> requestChapters);

        /**
         * ?????????????????????????????????????????????
         *
         * @param chapters?????????????????????
         */
        void onCategoryFinish(List<TxtChapter> chapters);

        /**
         * ???????????????????????????????????????????????????==> ??????????????????????????????????????????????????????????????????????????????????????????
         *
         * @param count:???????????????
         */
        void onPageCountChange(int count);

        /**
         * ???????????????????????????????????????
         *
         * @param pos:????????????????????????
         */
        void onPageChange(int pos, int wordcount, int pagePos, int pageWordCount);

        void onPageChangeFinish(int pos, boolean success);

        void onReadParaChanged(int paraIndex);
    }

    /*******  ????????????  *********/
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
            //????????????????????????????????????
            int margin = ScreenUtils.dpToPx(8);
            int closeWidth = ScreenUtils.dpToPx(51 - 4);
            int closeHeight = ScreenUtils.dpToPx(17 - 2);
            int closeX = adFrame.getX() + adFrame.getWidth() - closeWidth - margin;
            int closeY = adFrame.getY() + margin;
            return new YYFrame(closeX, closeY, closeWidth, closeHeight);
        } else if (type == LineInfo.LineAdType.LineAdTypePage) {
            //????????????????????????
            int closeHeight = ScreenUtils.dpToPx(25);
            return new YYFrame(adFrame.getX(), adFrame.getY() + adFrame.getHeight() - closeHeight, adFrame.getWidth(), closeHeight);
        }
        return YYFrame.YYFrameZero();
    }

    public interface OnNativeAdListener {
        //        YYAdView requestNativeAd(YYFrame frame);  //????????????
        boolean isHaveNativeAd();  //?????????????????????

        boolean isHaveTailPageAd();  //???????????????????????????????????????

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
        //??????????????????????????????????????????mCurPage??????
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
