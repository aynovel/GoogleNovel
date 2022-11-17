package life.forever.cf.activtiy;

import static android.view.View.GONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.MoreBuy;
import life.forever.cf.entry.RecList;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.AutoPayBookBean;
import life.forever.cf.entry.BookBean;
import life.forever.cf.entry.BookRecordBean;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.entry.BookCommentListResult;
import life.forever.cf.entry.BookMoreBuyInfoPackage;
import life.forever.cf.entry.BookRecommendListResult;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.interfaces.ReadContract;
import life.forever.cf.datautils.ReadPresenter;
import life.forever.cf.weight.MarqueeView;
import life.forever.cf.entry.DataPointBean;
import life.forever.cf.entry.DataPointType;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.adapter.person.personcenter.ReportActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.LOG;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.tool.TimeUtil;
import life.forever.cf.publics.weight.poputil.SharePopup;
import life.forever.cf.adapter.MoreBuyAdapter;
import life.forever.cf.adapter.NewReadCatalogAdapter;
import life.forever.cf.weight.ChapterPageStatusInfo;
import life.forever.cf.weight.PageLoader;
import life.forever.cf.weight.PageView;
import life.forever.cf.adapter.TxtChapter;
import life.forever.cf.interfaces.PageStyle;
import life.forever.cf.manage.ReadSettingManager;
import com.google.android.material.appbar.AppBarLayout;
import com.kc.openset.OSETBanner;
import com.kc.openset.OSETListener;
import com.kc.openset.OSETVideoListener;
import com.kc.openset.ad.OSETRewardVideoCache;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import life.forever.cf.interfaces.BusC;

public class ReadActivity extends ReaderBaseMvpActivity<ReadContract.Presenter> implements ReadContract.View {
    private static final String TAG = "ReaderReadActivity";

    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");


    @BindView(R.id.toolbar)
    Toolbar mReadToolbar;


    @BindView(R.id.read_dl_slide)
    DrawerLayout mDlSlide;

    @BindView(R.id.read_pv_page)
    PageView mPvPage;
    private PageLoader mPageLoader;

    @BindView(R.id.read_abl_top_menu)
    AppBarLayout mAblTopMenu;
    @BindView(R.id.ck_down)
    CheckBox ck_down;
    @BindView(R.id.ck_more)
    CheckBox ck_more;

    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout mLlBottomMenu;

    @BindView(R.id.rl_clickToDismiss)
    RelativeLayout rl_clickToDismiss;

    @BindView(R.id.iv_add_shelf_read)
    ImageView iv_add_shelf_read;


    @BindView(R.id.nightModeCheckBox)
    ImageView mNightModeCheckBox;
    @BindView(R.id.catalogOpen)
    ImageView catalogOpen;
    @BindView(R.id.readSettings)
    ImageView readSettings;
    @BindView(R.id.iv_comment)
    ImageView iv_comment;

    @BindView(R.id.read_tv_pre_chapter)
    TextView mTvPreChapter;

    @BindView(R.id.read_sb_chapter_progress)
    SeekBar mSbChapterProgress;
    @BindView(R.id.read_tv_next_chapter)
    TextView mTvNextChapter;
    @BindView(R.id.book_update_cb)
    CheckBox book_update_cb;//自动购买开关
    @BindView(R.id.is_add_shelf)
    LinearLayout is_add_shelf;
    @BindView(R.id.iv_small_cover)
    ImageView iv_small_cover;
    @BindView(R.id.tv_add_shelf)
    TextView tv_add_shelf;
    @BindView(R.id.iv_add_shelf)
    ImageView iv_add_shelf;
    @BindView(R.id.commentTag)
    TextView mCommentTag;


    /*****  目录  ****/
    private LinearLayoutManager linearLayoutManager;
    @BindView(R.id.fl_bulk_buy)
    RelativeLayout fl_bulk_buy;
    @BindView(R.id.ll_all)
    LinearLayout ll_all;
    @BindView(R.id.ll_catalog)
    LinearLayout ll_catalog;
    @BindView(R.id.header)
    LinearLayout mHeader;
    @BindView(R.id.v_line)
    View v_line;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.counts)
    TextView mCounts;
    @BindView(R.id.order)
    ImageView mOrder;
    @BindView(R.id.book_cover_detail)
    ImageView book_cover_detail;
    @BindView(R.id.book_name_chapter)
    TextView book_name_chapter;
    @BindView(R.id.recyclerView)//目录cecyclervew
    RecyclerView mRecyclerView;


    /*****************view******************/
    private BaseDialog mSettingDialog;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    private NewReadCatalogAdapter mCatalogAdapter;
    private final boolean recordisShow = false;
    //控制屏幕常亮
    private PowerManager.WakeLock mWakeLock;


    /**
     * 设置反馈
     */
    @BindView(R.id.rl_menu)
    RelativeLayout rl_menu;
    @BindView(R.id.ll_menu)
    LinearLayout ll_menu;
    @BindView(R.id.book_title)
    MarqueeView book_title;

    @BindView(R.id.book_cover)
    ImageView book_cover;

    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.book_state)
    TextView book_state;
    @BindView(R.id.book_chapter)
    TextView book_chapter;


    /**************** 多章购买 ************/
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.rl_more)
    RelativeLayout rl_more;
    @BindView(R.id.ll_more_buy)
    LinearLayout ll_more_buy;
    @BindView(R.id.current_chapter)
    TextView current_chapter;
    @BindView(R.id.auto_lock_cb)
    CheckBox auto_lock_cb;  //购买多章缓存内容
    @BindView(R.id.buy_more_coins)
    TextView buy_more_coins;
    @BindView(R.id.tv_coupons)
    TextView tv_coupons;
    @BindView(R.id.recyclerView_more)
    RecyclerView recyclerView_more;
    @BindView(R.id.start_buy)
    TextView start_buy;

    @BindView(R.id.fl_banner)
    FrameLayout flBanner;
    private MoreBuyAdapter moreBuyAdapter;

    private final List<MoreBuy> HRMoreBuys = new ArrayList<>();


    /***************params*****************/

    private final List<ChapterItemBean> HRCatalogs = new ArrayList<>();

    /**
     * 热门章节id
     */
    private final List<Integer> hotIds = new ArrayList<>();

    /**
     * 是否是夜间模式
     */
    public boolean isNightMode = false;

    /**
     * 是否显示多章订阅按钮
     */
    private boolean isShowMore = false;

    /**
     * 是否显示打折标示
     */
    private boolean isShowPreferential = false;

    /**
     * isShowMorePreferential批量购买优惠引导语     topUpPreferential-充值优惠引导语
     */
    private String isShowMorePreferential = "", topUpPreferential = "";

    /**
     * 埋点统计mBid-小说id
     */
    private String mBid;


    private boolean isMultiBuyDownContentFlag = true;

    /**
     * 当前页数
     */
    private int page = 0;

    /**
     * 跳转到阅读后第一次不执行下一页
     */
    public static boolean isEndBack = FALSE;

    /**
     * 记录进入阅读页时间和离开时间
     */
    long startTime, endTime;
    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 是否注册广播监听器
     */
    private boolean isRegistered = false;


    /**
     * 书籍推荐列表
     */
    private final List<RecList> mHRRecLists = new ArrayList<>();


    private SharePopup sharePopup;

    //返回拦截推荐
    private String style = "1";


    Work mHRWork;
    private BookBean mReadBookBean;
    private BookRecordBean mRecoverBookRecord;



    @Override
    protected int getContentId() {
        return R.layout.activity_new_read;
    }

    @Override
    protected ReadContract.Presenter bindPresenter() {
        return new ReadPresenter();
    }

    private boolean showNewUserTips = PlotRead.getConfig().getBoolean(IS_NEW_USER_READ, TRUE);
    private boolean showNewUserUnclockTips = PlotRead.getConfig().getBoolean(IS_NEW_USER_UNCLOCK, TRUE);
    @BindView(R.id.tips_unclock)
    LinearLayout tipsUnclockLayout;
    @BindView(R.id.tips_read)
    View newUserTipsLayout;
    @BindView(R.id.tips_read_unclock)
    View newUserUnlockTipsLayout;

    @Override
    protected void initWidget() {
        super.initWidget();


        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        this.getWindow().setAttributes(lp);


        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPvPage.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        EventBus.getDefault().register(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        requestPermission();

        Cods.rechargeFlag = false;


        changeRootBackBG();

    }

    public void changeRootBackBG()
    {
        PageStyle pageStyle = ReadSettingManager.getInstance().getPageStyle();
        if(ReadSettingManager.getInstance().isNightMode())
        {
            pageStyle = PageStyle.NIGHT;
        }
        int mBgColor = ContextCompat.getColor(this, pageStyle.getBgColor());
        setBackGroudColor(mBgColor);
    }

    private void initToolbar() {
        if (mReadToolbar != null) {
            supportActionBar(mReadToolbar);
            setUpToolbar(mReadToolbar);
        }
        if (mTitleBar != null) {
            mTitleBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);

        //设置标题
        if (mHRWork != null && !TextUtils.isEmpty(mHRWork.title)) {
            toolbar.setTitle(mHRWork.title);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFinish();
            }
        });
    }

    @Override
    protected void processLogic() {
        super.processLogic();
    }

    @Override
    protected void initData() {
        super.initData();
        setShowMore();

        Work HRWork = getIntent().getParcelableExtra("work");

        if (mReadBookBean == null && HRWork != null) {
            mReadBookBean = new BookBean();
            mReadBookBean.wid = "" + HRWork.wid;
            mReadBookBean.chapterCount = HRWork.totalChapter;
            mHRWork = HRWork;
        }

        mRecoverBookRecord = DBUtils.getInstance().getBookRecord(mReadBookBean.wid);

        mPageLoader = mPvPage.getPageLoader(mReadBookBean);

        if(mHRWork != null)
        {
            switch (mHRWork.toReadType)
            {
                case 0:
                    break;
                case 1:
                    if(mRecoverBookRecord == null)
                    {
                        mRecoverBookRecord = new BookRecordBean();
                        mRecoverBookRecord.wid = mReadBookBean.wid;
                    }

                    mRecoverBookRecord.chapterIndex = mHRWork.lastChapterOrder;
                    mRecoverBookRecord.chapterCharIndex = 0;
                    break;
                case 2:
                    if(mRecoverBookRecord == null)
                    {
                        mRecoverBookRecord = new BookRecordBean();
                        mRecoverBookRecord.chapterIndex = mHRWork.lastChapterOrder;
                        mRecoverBookRecord.chapterCharIndex = mHRWork.lastChapterPosition;
                        mRecoverBookRecord.wid = mReadBookBean.wid;
                    }
                    break;
            }
        }


        if (mRecoverBookRecord != null) {
            mPageLoader.updateBookRecord(mRecoverBookRecord);
            mRecoverBookRecord = null;
        }


        if (mPageLoader != null) {
            freashCurrentPageViewStyle();

            AutoPayBookBean autoPayBookBean = DBUtils.getInstance().getAutoBuyBookRecord(mReadBookBean.wid);
            if (autoPayBookBean != null) {
                mPageLoader.setAutoBuySelected(autoPayBookBean.isAutoPay);
                if(mPresenter != null)
                {
                    mPresenter.setAutoPaySelected(autoPayBookBean.isAutoPay);
                }
                book_update_cb.setChecked(autoPayBookBean.isAutoPay);
            } else {
                boolean isstate = PlotRead.getConfig().getBoolean(IS_STATE, TRUE);
                mPageLoader.setAutoBuySelected(isstate);
                book_update_cb.setChecked(false);
            }
        }


        if (mPvPage != null) {
            mPvPage.setTouchListener(new PageView.TouchListener() {
                @Override
                public boolean onTouch() {
                    return !hideReadMenu();
                }

                @Override
                public void center() {

                    toggleMenu(true);

                    DataPointBean commentPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_activation");
                    commentPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                            ,0,0,0,null);
                }

                @Override
                public void prePage() {
                }

                @Override
                public void nextPage() {

                    if (mPageLoader.mCurPage != null && mPageLoader.mCurPage.getStringList() != null && mPageLoader.mCurPage.getmPageStatus() == null) {
                        showAddShelfNotice();
                    }

                }

                @Override
                public void cancel() {
                }

                @Override
                public void move(float x, float y) {

                    if (mPageLoader.mCurPage == null || mPageLoader.mCurPage.getStringList() == null || mPageLoader.mCurPage.getStringList().size() <= 0) {
                        return;
                    }
                }

                @Override
                public void up() {
                }

                @Override
                public void onReloadClick() {
                    LogUtils.d("点击重新加载 ======= ");

                    mPageLoader.skipToChapter(mPageLoader.getChapterPos());
                    if (mReadBookBean != null && mReadBookBean.getBookChapterList() == null) {
                        if (mPresenter != null) {
                            mPresenter.loadCategory(mReadBookBean);
                        }
                    }

                }


                @Override
                public void onStatusBtnClick(String chapter, int type, int chapterOrder) {
                    String typeName = "";

                    if (mPageLoader.getChapterPos() != chapterOrder) {
                        mPageLoader.skipToChapter(chapterOrder);
                    }

                    switch (type) ////1：登录 2：购买 3：购买更多 4：重新加载 5：自动购买
                    {
                        case 1: {
                            typeName = "登录";

                            gotoUserLogin();

                        }
                        break;
                        case 2: {
                            typeName = "购买";

                            if (mPageLoader.getChapterCategory().get(chapterOrder).getmChatperStatusInfo() != null) {
                                ChapterPageStatusInfo statusInfo = mPageLoader.getChapterCategory().get(chapterOrder).getmChatperStatusInfo();
                                if (statusInfo.getMode() == ChapterPageStatusInfo.PageStatusMode.LACK_BALANCE) {

                                    Intent mItent = new Intent(ReadActivity.this, TopUpActivity.class);
                                    startActivity(mItent);

                                } else {
                                    if (mPresenter != null) {
                                        mPresenter.buySingleChapter(mReadBookBean.wid, mPageLoader.getChapterCategory().get(chapterOrder));

                                        if (mPageLoader != null) {
                                            mPresenter.setAutoPaySelected(mPageLoader.isAutoBuySelected());
                                        }
                                    }

                                }
                            }


                        }
                        break;
                        case 3: {
                            typeName = "购买更多";

                            onBuyMoreClick(chapterOrder);
                        }
                        break;
                        case 4: {
                            typeName = "重新加载";
                            mPageLoader.clearPageStatusError();
                            if (mPresenter != null) {
                                List<TxtChapter> chapters = new ArrayList<>();
                                TxtChapter currentChapter = mPageLoader.getChapterCategory().get(chapterOrder);
                                if (currentChapter.getmChatperStatusInfo() != null) {
                                    currentChapter.setmChatperStatusInfo(null);
                                }
                                mPageLoader.skipToChapter(chapterOrder);
                                mPresenter.loadChapter(mReadBookBean.wid, chapters);
                            }
                        }
                        break;
                        case 5: {
                            typeName = "自动购买";
                        }
                        break;
                    }
                    ToastUtils.show("点击状态按钮 === " + chapter + " type === " + typeName);
                }

                @Override
                public void onAutoPayBtnClick(String chapter, int chapterOrder) {
                    ToastUtils.show("点击自动购买按钮 ===== " + chapter);
                    if (mPageLoader == null) {
                        return;
                    }

                    if (mPageLoader.getChapterPos() != chapterOrder) {
                        mPageLoader.skipToChapter(chapterOrder);
                    }

                    if (mReadBookBean != null) {


                        mPageLoader.setAutoBuySelected(!mPageLoader.isAutoBuySelected());

                        saveAutoPayRecord();


                        if (mPvPage != null) {
                            mPvPage.drawCurPage(false);
                        }
                    }

                }


                @Override
                public void onWatchVideoBtnClick() {
                }
            });
        }

        if (mPresenter != null) {
            mPresenter.loadCategory(mReadBookBean);
        }

        initToolbar();
    }

    private void saveAutoPayRecord()
    {
        AutoPayBookBean autoPayBookBean = DBUtils.getInstance().getAutoBuyBookRecord(mReadBookBean.wid);
        if (autoPayBookBean != null) {
            autoPayBookBean.isAutoPay = mPresenter.getAutoPaySelected();
            DBUtils.getInstance().saveBookAutoPayRecordWithAsync(autoPayBookBean);

        } else {
            autoPayBookBean = new AutoPayBookBean();
            autoPayBookBean.wid = mReadBookBean.wid;
            autoPayBookBean.isAutoPay = mPresenter.getAutoPaySelected();
            autoPayBookBean.addTime = TimeUtil.currentTimeSeconds();
            DBUtils.getInstance().saveBookAutoPayRecordWithAsync(autoPayBookBean);
        }
    }

    @Override
    protected void initClick() {
        super.initClick();


        //设置菜单事件
        //禁止滑动展示DrawerLayout
        mDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        mDlSlide.setFocusableInTouchMode(false);
        mSettingDialog = new BaseDialog(ReadActivity.this, mPageLoader, ll_catalog, mHeader, v_line, mAblTopMenu, mLlBottomMenu);
        mCatalogAdapter = new NewReadCatalogAdapter(this, HRCatalogs, hotIds);
        mCatalogAdapter.wid = mReadBookBean.wid;
        mRecyclerView.setAdapter(mCatalogAdapter);
        mCatalogAdapter.setOnItemClickListener(catalogItemClick);
//        if (ShelfUtil.existRecord(Integer.parseInt(collBook.get_id()))) {
//            Work record = ShelfUtil.queryRecord(Integer.parseInt(collBook.get_id()));
//            if (record != null) {
//                showContent();
//            }
//        }


        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);

        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }

        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ireader:keep bright");


        if (mPageLoader == null) {
            return;
        }

        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos, int wordcount) {

                LogUtils.e("切换章节 =======" + pos);

                DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_read");
                settingPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,null);


                freshUnlockTips();

            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {
//                mPresenter.loadChapter(mCollBook.getWid(), mCollBook.getCurrentSiteBean().site, requestChapters,mCollBook.getSiteFrom());
//                downChapterContents(re)

                if (mPresenter != null && mReadBookBean != null) {
                    mPresenter.loadChapter(mReadBookBean.getWid(), requestChapters);
                }
            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {

            }

            @Override
            public void onPageCountChange(int count) {
                if (mSbChapterProgress == null) {
                    return;
                }
                mSbChapterProgress.setMax(Math.max(0, count - 1));
                mSbChapterProgress.setProgress(0);
                // 如果处于错误状态，那么就冻结使用
                mSbChapterProgress.setEnabled(mPageLoader.getPageStatus() != PageLoader.STATUS_LOADING
                        && mPageLoader.getPageStatus() != PageLoader.STATUS_ERROR);
            }

            @Override
            public void onPageChange(int pos, int wordcount, int pagePos, int pageWordCount) {
                if (mSbChapterProgress == null) {
                    return;
                }

                mSbChapterProgress.post(
                        () -> mSbChapterProgress.setProgress(pos)
                );
            }

            @Override
            public void onPageChangeFinish(int pos, boolean success) {

                if (success == false)//最后一章
                {

                    LogUtils.d("翻到最后一页========");
                    if (mReadBookBean != null) {
                        Intent intent = new Intent(ReadActivity.this, NewReadEndActivity.class);
                        intent.putExtra("work", mHRWork);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onReadParaChanged(int paraIndex) {

            }
        });

        mPageLoader.setmNativeAdListener(new PageLoader.OnNativeAdListener() {
            @Override
            public boolean isHaveNativeAd() {
                return false;
            }

            @Override
            public boolean isHaveTailPageAd() {
                return false;
            }

            @Override
            public void updateNativeAdSize() {

            }

            @Override
            public void pauseAd() {

            }

            @Override
            public boolean isFroceClearStatusContent() {
                boolean forceClear = false;
                if(mPresenter != null)
                {
                    forceClear = mPresenter.getAutoPaySelected();
                }
                return forceClear;
            }
        });

        /**
         * 章节切换
         */
        bindChapterChange();

        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCatalogAdapter && null != mOrder) {

                    if (mCatalogAdapter.reverse) {
                        mCatalogAdapter.reverse = FALSE;
                        mOrder.setImageResource(R.drawable.positive_sequence_up);

                    } else {
                        mCatalogAdapter.reverse = TRUE;
                        mOrder.setImageResource(R.drawable.positive_sequence_down);
                    }
                    if (mPageLoader != null) {
                        if(mCatalogAdapter != null)
                        {
                            mCatalogAdapter.update(mPageLoader.getChapterPos());
                            mCatalogAdapter.smoothMoveToPosition(mRecyclerView,mCatalogAdapter.getCurrentPosition());
                        }
                    }
                }
            }
        });

        mPageLoader.noticeUnClockFlag = showNewUserUnclockTips;

        View newUserView = findViewById(R.id.tips_read);
        if(newUserView != null)
        {
            newUserView.setOnClickListener(view -> {
                if(newUserTipsLayout != null)
                {
                    newUserTipsLayout.setVisibility(GONE);
                    freshUnlockTips();
                }
            });

        }

        View newUnclockView = findViewById(R.id.tips_read_unclock);
        if(newUnclockView != null)
        {
            newUnclockView.setOnClickListener(view -> {
                if(newUserUnlockTipsLayout != null)
                {
                    newUserUnlockTipsLayout.setVisibility(GONE);
                }
            });
        }


    }
    private void freshUnlockTips()
    {
        if(mPageLoader != null)
        {
            if(mPageLoader.mCurPage != null&& mPageLoader.mCurPage.getmPageStatus() != null && showNewUserUnclockTips
                    && !showNewUserTips && newUserTipsLayout.getVisibility() == GONE)
            {

                if(showNewUserUnclockTips)
                {
                    if(newUserUnlockTipsLayout != null)
                    {

                        if(tipsUnclockLayout != null)
                        {
                            LinearLayout.LayoutParams mParams=(LinearLayout.LayoutParams)tipsUnclockLayout.getLayoutParams();
                            if(mParams != null && mPageLoader.mCurPage != null
                            && mPageLoader.mCurPage.mPayFrame != null)
                            {
                                mParams.topMargin = mPageLoader.mCurPage.mPayFrame.getY() - ScreenUtils.dpToPx(50);
                                tipsUnclockLayout.setLayoutParams(mParams);
                                newUserUnlockTipsLayout.setVisibility(VISIBLE);

                                showNewUserUnclockTips = false;

                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), Constant.IS_NEW_USER_UNCLOCK, false);


                                newUserUnlockTipsLayout.postDelayed(() -> {

                                    if(newUserUnlockTipsLayout != null)
                                    {
                                        newUserUnlockTipsLayout.setVisibility(View.GONE);
                                    }

                                }, 4000);
                            }
                        }
                    }

                }
            }
        }
    }

    //    /**
//     * 展示loading弹窗
//     *
//     * @param tip
//     */
//    public void showLoading(String tip) {
//        dismissLoading();
//        loadingDialog = LoadingAlertDialog.show(ReaderReadActivity.this, tip);
//    }
//
//    /**
//     * 隐藏loading弹窗
//     */
//    public void dismissLoading() {
//        LoadingAlertDialog.dismiss(loadingDialog);
//    }


    @Override
    public void showLoading() { //加载状态

        ReadActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mContentLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showError() {//报错状态

    }

    @Override
    public Activity getActivityContext() {
        return ReadActivity.this;
    }

    @Override
    public void complete() {
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }


    private void freashCurrentPageViewStyle() {
        ReadSettingManager manager = ReadSettingManager.getInstance();
        if (mPageLoader != null) {
            if (manager.isNightMode()) {
                mPageLoader.setNightMode(true);
                mPageLoader.setPageStyle(PageStyle.NIGHT);
            } else {
                mPageLoader.setNightMode(false);
                mPageLoader.setPageStyle(manager.getPageStyle());
            }
        }
    }


    /*******************      数据 present回调   ******************/
    @Override
    public void showCategory(List<ChapterItemBean> bookChapterList) {//获取章节目录返回

        if (bookChapterList != null && bookChapterList.size() > 0) {
            mReadBookBean.setBookChapterList(bookChapterList);
            if (mPageLoader != null) {
                mPageLoader.setCollBook(mReadBookBean);
                mPageLoader.refreshChapterList();
            }

            HRCatalogs.clear();
            HRCatalogs.addAll(bookChapterList);
            if (mCatalogAdapter != null) {
                mCatalogAdapter.notifyDataSetChanged();
            }
        } else {
            mPageLoader.chapterError();

        }

    }





    @Override
    public void finishChapter(TxtChapter chapter) {
        if (mPageLoader == null) {
            return;
        }
        if(showNewUserTips)
        {

            toggleMenu(true);

            if(newUserTipsLayout != null)
            {
                newUserTipsLayout.setVisibility(VISIBLE);

                showNewUserTips = false;
                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), Constant.IS_NEW_USER_READ, false);

                newUserTipsLayout.postDelayed(() -> {

                    if(newUserTipsLayout != null)
                    {
                        newUserTipsLayout.setVisibility(View.GONE);
                    }
                    freshUnlockTips();

                }, 4000);
            }
        }

        LogUtils.e("current chapter = " + mPageLoader.getCurChapterTitle() + " * chapter = " + chapter.getTitle());

        if (mPageLoader.getCurChapterID().equals(chapter.getChapterId()))//当前页面刷新
        {

            mPageLoader.openChapter();

            if(showNewUserTips == false)
            {
                freshUnlockTips();
            }
        } else {

            int currentChaterPos = mPageLoader.getChapterPos();
            int length = Math.abs(currentChaterPos - chapter.getChapterOrder());
            if (length <= 2) {
                mPageLoader.clearPageStatusError();
            }
        }
    }

    @Override
    public void errorChapter(TxtChapter chapter) {//章节错误状态
        if (mPageLoader == null) {
            return;
        }


        if (mPageLoader.getCurChapterID().equals(chapter.getChapterId()))//当前页面刷新
        {
            mPageLoader.clearPageStatusError();
            mPageLoader.openChapter();
        } else {
            int currentChaterPos = mPageLoader.getChapterPos();
            int length = Math.abs(currentChaterPos - chapter.getChapterOrder());
            if (length <= 2) {
                mPageLoader.clearPageStatusError();
            }
        }

    }

    @Override
    public boolean getIsCurrentChapter(TxtChapter chapter) {
        if (chapter != null && mPageLoader != null) {
            if (mPageLoader.getCurChapterID().equals(chapter.getChapterId()))//当前页面刷新
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void buySuccessChapter(TxtChapter chapter, int payType, boolean isMulti) {
        String wid = chapter.getBookId();
        String cid = chapter.getChapterId();
        int currentChapterOrder = chapter.getChapterOrder();

        if (isMulti) {
            onDirectoryDownClick(null, ll_more_buy, rl_more);
            DeepLinkUtil.addPermanent(PlotRead.getApplication(), "event_content_subscribe", "阅读页", "点击订阅", "", wid + "", cid + "", "订阅成功_0", "", "");
            String type = "币";
            DeepLinkUtil.subscribe(PlotRead.getApplication(), type, cid, 0, wid + "", currentChapterOrder + "");
        } else {
            DeepLinkUtil.addPermanent(PlotRead.getApplication(), "event_content_subscribe", "阅读页", "点击订阅", "", wid + "", cid + "", "订阅成功_0", "", "");

            String type = "";
            if (payType == 1) {
                type = "币";
            } else {
                type = "豆";
            }
            DeepLinkUtil.subscribe(PlotRead.getApplication(), type, cid + "", 0, wid + "", currentChapterOrder + "");
        }


        boolean showToast = false;
        if (chapter.getmChatperStatusInfo() != null) {
            chapter.setmChatperStatusInfo(null);
            if (mPageLoader.getCurChapterID().equals(chapter.getChapterId()))//当前页面刷新
            {
                mPageLoader.clearPageStatusError();

                if (mPageLoader.isAutoBuySelected()) {
                    int currentPos = mPageLoader.getChapterPos();
                    int startPos = currentPos - 2;
                    int endPos = currentPos + 2;

                    if (startPos <= 0) {
                        startPos = 0;
                    }
                    if (endPos >= mPageLoader.getChapterCategory().size()) {
                        endPos = mPageLoader.getChapterCategory().size() - 1;
                    }
                    List<String> freshChapters = new ArrayList<>();
                    for (int i = startPos; i < endPos; i++) {
                        TxtChapter tempChapter = mPageLoader.getChapterCategory().get(i);
                        if (tempChapter.getmChatperStatusInfo() != null) {
                            tempChapter.setmChatperStatusInfo(null);
                        }
                    }
                }

                mPageLoader.openChapter();
                showToast = true;
            }
        }

        if (isMulti) {
            showToast = true;
        }

        if (showToast) {
            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.Successfully_unlocked));
        }

        if (!ShelfUtil.exist(mHRWork.wid)) {
            addLocalShelf();
            if (showToast) {
                PlotRead.toast(PlotRead.SUCCESS, getString(R.string.added_shelf_subcribe));
            }
        }
    }

    @Override
    public void buyErrorChapter(TxtChapter chapter) {

        String wid = chapter.getBookId();
        String cid = chapter.getChapterId();


        if (mPageLoader != null && mPageLoader.getCurChapterID().equals(chapter.getChapterId())) {
            PlotRead.toast(PlotRead.FAIL, getString(R.string.purchase_failed));
        }

        DeepLinkUtil.addPermanent(ReadActivity.this, "event_content_subscribe", "阅读页", "点击订阅", "", wid + "", cid + "", "订阅失败_0", "", "");
    }

    /**
     * 获取批量购买信息
     * @param infoPackage
     */
    @Override
    public void getSuccessMoreBuyInfo(BookMoreBuyInfoPackage infoPackage) {
        if (infoPackage != null) {
            HRMoreBuys.clear();
            if (infoPackage.result != null && infoPackage.result.info != null && infoPackage.result.info.list != null) {
                List<MoreBuy> tempMoreBuys = new ArrayList<>();
                int chapter_money = 0;
                for (MoreBuy item :
                        infoPackage.result.info.list) {
                    if (item != null) {
                        tempMoreBuys.add(item);
                    }
                }
                if (tempMoreBuys.size() > 0) {
                    MoreBuy tempBuy = tempMoreBuys.get(0);
                    tempBuy.isclick = true;
                    chapter_money = tempBuy.origin;
                }

                HRMoreBuys.addAll(tempMoreBuys);
                moreBuyAdapter.update();

                if (infoPackage.result.info.finance != null) {
                    buy_more_coins.setText(""+infoPackage.result.info.finance.money);
                    tv_coupons.setText(""+infoPackage.result.info.finance.voucher);
                }

                boolean isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, FALSE);
                if(auto_lock_cb != null)
                {
                    auto_lock_cb.setChecked(isstate);
                }
                if (PlotRead.getAppUser().money > chapter_money) {
                    if (!isstate) {
                        start_buy.setText(getString(R.string.unlock_and_download));
                    } else {
                        start_buy.setText(getString(R.string.unlock));
                    }
                } else {
                    start_buy.setText(getString(R.string.purchase_coins_read));
                }

            }
        }
    }

    @Override
    public void getBookInfo(BookBean bookBean) {
        if (bookBean != null) {
            mReadBookBean = bookBean;

            book_title.setText(mReadBookBean.title);
            book_name.setText(mReadBookBean.title);

            mHRWork.cover = bookBean.h_url;
            mHRWork.totalChapter = bookBean.chapterCount;
            mHRWork.title = bookBean.title;
            mHRWork.author = bookBean.author;
            mHRWork.isfinish = bookBean.is_finish;

            GlideUtil.load(ReadActivity.this, mReadBookBean.h_url, R.drawable.default_work_cover, iv_small_cover);
            GlideUtil.load(ReadActivity.this, mReadBookBean.h_url, R.drawable.default_work_cover, book_cover);
            GlideUtil.load(ReadActivity.this, mReadBookBean.h_url, R.drawable.default_work_cover, book_cover_detail);
            book_name_chapter.setText(mReadBookBean.title);
        }
    }

    @Override
    public void getBookRecommendList(BookRecommendListResult recommendListResult) {
        if(recommendListResult != null && recommendListResult.rec_list != null)
        {
            mHRRecLists.clear();
            mHRRecLists.addAll(recommendListResult.rec_list);
        }
    }

    @Override
    public void getBookCommentList(BookCommentListResult commentListResult) {
        if (commentListResult != null) {
            if (commentListResult.status == ONE) {

                if (commentListResult.count > ZERO) {
                    mCommentTag.setVisibility(View.VISIBLE);
                    if (commentListResult.count >= ONE_HUNDRED) {
                        mCommentTag.setText("99+");
                    } else {
                        mCommentTag.setText(String.valueOf(commentListResult.count));
                    }
                } else {
                    mCommentTag.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void freashHasBuyChapters(List<String> chapterIDs) {
        if (chapterIDs != null && chapterIDs.size() > 0) {
            for (TxtChapter chapter :
                    mPageLoader.getChapterCategory()) {
                if (chapterIDs.contains(chapter.getChapterId())) {
                    if (chapter.getmChatperStatusInfo() != null) {
                        chapter.setmChatperStatusInfo(null);
                    }
                }
            }
        }
    }

    /*************************************/
    @Override
    protected void onResume() {
        super.onResume();

        if (mWakeLock != null) {
            mWakeLock.acquire();
        }


        if (mPageLoader != null) {
            mPageLoader.forceUpdatePage();
        }

        timer();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null) {
            mWakeLock.release();
        }

        mPageLoader.saveRecord();

        endTime = System.currentTimeMillis();
        SharedPreferencesUtil.putLong(PlotRead.getConfig(), TimeUtil.currentDate(), endTime - startTime +   SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate()));
        if (null != timer){
            timer.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBrightObserver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SystemBarUtils.hideStableStatusBar(this);

    }


    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.e(TAG, "register mBrightObserver error! " + throwable);
        }
    }

    //解注册
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            LogUtils.e(TAG, "unregister BrightnessObserver error! " + throwable);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        try {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
        }

        EventBus.getDefault().unregister(this);

        if (null != timer){
            timer.cancel();
        }


        try {
            if (mPageLoader != null) {
                mPageLoader.closeBook();
            }
        } catch (Exception e) {
        }

        mPageLoader = null;

        Glide.get(this).clearMemory();
        System.gc();

        if (mPresenter != null) {
            mPresenter.clearDownDisposable();
        }
    }


    /**
     * 多章订阅
     */
    public void onBuyMoreClick(int startOrder) {
        if (moreBuyAdapter == null) {
            moreBuyAdapter = new MoreBuyAdapter(this, HRMoreBuys);
            recyclerView_more.setAdapter(moreBuyAdapter);
            moreBuyAdapter.setOnItemClickListener(moreBuyItemClick);
            recyclerView_more.setLayoutManager(new LinearLayoutManager(this));
        }
//        buyMultiPage(HRWork.wid, HRCatalogs.get(mPageLoader.getChapterPos()).id);

        TxtChapter chapter = mPageLoader.getChapterCategory().get(startOrder);

        if (mPresenter != null) {
            mPresenter.getMoreBuyMultiInfo(mReadBookBean.wid, mPageLoader.getChapterCategory().get(startOrder));
        }

        mAblTopMenu.setVisibility(GONE);
        mLlBottomMenu.setVisibility(GONE);
        rl_clickToDismiss.setVisibility(GONE);

        boolean isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, false);
        auto_lock_cb.setChecked(!isstate);


        if (chapter != null) {
            current_chapter.setText(getString(R.string.current_chapter) + chapter.getTitle());
        }

        //设置动画，从自身位置的最下端向上滑动了自身的高度，持续时间为500ms
        onDirectoryUpClick(null, ll_more_buy, rl_more);
    }


    /***     activity 配置相关     **/

    /**
     * 接收电池信息和时间更新的广播
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };

    /**
     * 亮度调节监听
     * 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
     */
    private final ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;
            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                BrightnessUtils.setBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                BrightnessUtils.setDefaultBrightness(ReadActivity.this);
            }
        }
    };


    private void requestPermission() {//需要SD卡去写权限


        final String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        //获取读取和写入SD卡的权限
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //请求权限
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1111);

        }



    }


    /**
     * 刷新列表
     *
     * @param currentChapterOrder 当前阅读章节的位置
     */
    public void updateCatalogAdapter(int currentChapterOrder) {
        if (mCatalogAdapter != null) {
            setCount();
            mCatalogAdapter.update(currentChapterOrder);
        }
    }


    private void setCount() {
        if (HRCatalogs != null) {
            mStatus.setText(HRCatalogs.size() + " " + getString(R.string.chapters));
            if (mHRWork.isfinish == 0) {
                mStatus.setText(getString(R.string.update));
            } else {
                mStatus.setText(getString(R.string.completed));
            }

        }
    }


    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        if (mAblTopMenu != null && mAblTopMenu.getVisibility() == VISIBLE) {
            toggleMenu(true);
            return true;
        } else if (mSettingDialog != null && mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu(boolean hideStatusBar) {
        initMenuAnim();

        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            //关闭
            mAblTopMenu.startAnimation(mTopOutAnim);
            mLlBottomMenu.startAnimation(mBottomOutAnim);
            mAblTopMenu.setVisibility(GONE);
            mLlBottomMenu.setVisibility(GONE);
            rl_clickToDismiss.setVisibility(GONE);
        } else {
            mAblTopMenu.setVisibility(View.VISIBLE);
            mLlBottomMenu.setVisibility(View.VISIBLE);
            addShelfisShow();
            mAblTopMenu.startAnimation(mTopInAnim);
            mLlBottomMenu.startAnimation(mBottomInAnim);
        }
    }


    private void initMenuAnim() {
        if (mTopInAnim != null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }



    @SuppressLint("NewApi")
    public void setNightMode(boolean isSetBG) {
        if (isNightMode) {
            mReadToolbar.setNavigationIcon(R.drawable.back_white);
            ck_down.setBackgroundResource(R.drawable.content_down_white);
            ck_more.setBackgroundResource(R.drawable.ic_menu_overflow);
            book_name_chapter.setTextColor(ReadActivity.this.getColor(R.color.color_656667));
            mTvPreChapter.setTextColor(getResources().getColor(R.color.color_656667));
            mTvNextChapter.setTextColor(getResources().getColor(R.color.color_656667));
            iv_comment.setImageResource(R.drawable.selector_read_nav_comment_black);
            readSettings.setImageResource(R.drawable.selector_read_nav_setting_black);
            catalogOpen.setImageResource(R.drawable.selector_read_nav_catalog_black);
            mNightModeCheckBox.setImageResource(R.drawable.selector_read_nav_night_black);
            mSettingDialog.setBGColor(4);
        } else {
            mReadToolbar.setNavigationIcon(R.drawable.back);
            ck_down.setBackgroundResource(R.drawable.content_down);
            ck_more.setBackgroundResource(R.drawable.read_menu);
            book_name_chapter.setTextColor(ReadActivity.this.getColor(R.color.color_000001));
            mTvPreChapter.setTextColor(getResources().getColor(R.color.color_555758));
            mTvNextChapter.setTextColor(getResources().getColor(R.color.color_555758));
            if (isSetBG) {
                mSettingDialog.setBGColor(ReadSettingManager.getInstance().getPageStyle().ordinal());
            }
            iv_comment.setImageResource(R.drawable.selector_read_nav_comment);
            readSettings.setImageResource(R.drawable.selector_read_nav_setting);
            catalogOpen.setImageResource(R.drawable.selector_read_nav_catalog);
            mNightModeCheckBox.setImageResource(R.drawable.selector_read_nav_night);
        }
    }


    private void onDirectoryUpClick(LinearLayout a, LinearLayout b, RelativeLayout c) {
        if (a == null) {
            c.setVisibility(View.VISIBLE);
        } else {
            a.setVisibility(View.VISIBLE);
        }

        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
        b.setVisibility(View.VISIBLE);
        b.startAnimation(ctrlAnimation);
        updateCatalogAdapter(mPageLoader.getChapterPos());
        mRecyclerView.scrollToPosition(mPageLoader.getChapterPos());
    }

    private void onDirectoryDownClick(LinearLayout a, LinearLayout b, RelativeLayout c) {
        if (b.getVisibility() == View.VISIBLE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
            b.setVisibility(View.GONE);
            b.startAnimation(ctrlAnimation);
            ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (a == null) {
                        c.setVisibility(View.GONE);
                    } else {
                        a.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(b.getWindowToken(), 0);

        }
    }


    private void gotoUserLogin() {
        Intent intent = new Intent(ReadActivity.this, LoginActivity.class);
        startActivity(intent);
    }



    private final NewReadCatalogAdapter.OnItemClickListener catalogItemClick =
            new NewReadCatalogAdapter.OnItemClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, int chapterPos) {
                    onDirectoryDownClick(ll_all, ll_catalog, null);
                    int position = viewHolder.getAdapterPosition();
                    updateCatalogAdapter(position);
                    if (mCatalogAdapter.reverse) { // 取数据，注意正序倒序
                        position = HRCatalogs.size() - 1 - position;
                    }
                    mPageLoader.skipToChapter(position);
//            mPageLoader.skipToChapter(position, STATUS_LOADING, true, 0,true,false);
                }
            };


    private void setShowMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OSETRewardVideoCache.getInstance(context).setOSETVideoListener(new OSETVideoListener() {
                    @Override
                    public void onShow() {
                        Log.d("addddddd", "onShow: ");
                    }

                    @Override
                    public void onError(String s, String s1) {
                        Log.d("addddddd", "error: ");
                    }

                    @Override
                    public void onClick() {
                        Log.d("addddddd", "onClick: ");
                    }

                    @Override
                    public void onClose(String s) {
                        Log.d("addddddd", "onClose: ");
                    }

                    @Override
                    public void onVideoEnd(String s) {
                        Log.d("addddddd", "onVideoEnd: ");
                    }

                    @Override
                    public void onLoad() {
                        Log.d("addddddd", "onLoad: ");
                    }

                    @Override
                    public void onVideoStart() {
                        Log.d("addddddd", "onVideoStart: ");
                    }

                    @Override
                    public void onReward(String s) {
                        Toast.makeText(ReadActivity.this, "onReward", Toast.LENGTH_SHORT).show();
                    }
                }).showAd((BaseActivity)context);
            }
        },30000);

        OSETBanner.getInstance().setWHScale(0.15625); //只对穿山甲起作用
        OSETBanner.getInstance()
                .show(this,  "11B4B247E3F243E56D46269A149F3CDC", flBanner, new OSETListener() {
                    @Override
                    public void onShow() {

                        Log.e("openseterror", "code:$s----message:$s1");
                    }

                    @Override
                    public void onError(String s, String s1) {
                        Log.e("openseterror", "code:$s----message:$s1");
                    }

                    @Override
                    public void onClick() {
                        Log.e("onClick", "code:$s----message:$s1");
                    }

                    @Override
                    public void onClose() {
                        Log.e("onClose", "code:$s----message:$s1");
                    }
                });

    }

    /**
     * 下载弹窗点击事件
     */
    private final MoreBuyAdapter.OnItemClickListener moreBuyItemClick = new MoreBuyAdapter.OnItemClickListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onItemClick(RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();

            int buy_chapter_count = 0;
            int chapter_money = 0;
            for (int i = 0; i < HRMoreBuys.size(); i++) {
                if (position == i) {
                    HRMoreBuys.get(i).isclick = true;
                    buy_chapter_count = HRMoreBuys.get(i).count;
                    chapter_money = HRMoreBuys.get(i).origin;
                    boolean isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, FALSE);
                    if (PlotRead.getAppUser().money > chapter_money) {
                        if (isstate) {
                            start_buy.setText(getString(R.string.unlock_and_download));
                        } else {
                            start_buy.setText(getString(R.string.unlock));
                        }
                    } else {
                        start_buy.setText(getString(R.string.purchase_coins_read));
                    }
                } else {
                    HRMoreBuys.get(i).isclick = false;
                }

            }
            moreBuyAdapter.update();
            if (PlotRead.getAppUser().money > chapter_money) {
                boolean isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, FALSE);
                if (!isstate) {
                    start_buy.setText(getString(R.string.unlock_and_download));
                } else {
                    start_buy.setText(getString(R.string.unlock));
                }
            } else {
                start_buy.setText(getString(R.string.purchase_coins_read));
            }
        }
    };


    @OnClick({R.id.book_this_detail, R.id.book_detail, R.id.fl_bulk_buy, R.id.rl_more, R.id.img_back, R.id.start_buy
            , R.id.fl_feedback, R.id.cancel_setting, R.id.rl_menu, R.id.book_update_cb, R.id.ll_book_report, R.id.facebook_share
            , R.id.ll_catalogOpen, R.id.ll_all, R.id.rl_close_chapter, R.id.ll_readSettings, R.id.ll_comment, R.id.ll_nightModeCheckBox
            , R.id.auto_lock_cb, R.id.iv_add_shelf_read, R.id.iv_add_shelf, R.id.iv_add_shelf_cancel})
    public void setOnClick(View id) {
        Intent intent = new Intent();
        switch (id.getId()) {
            //跳转详情
            case R.id.book_detail:
            case R.id.book_this_detail:
                if (mReadBookBean != null) {
                    intent.setClass(ReadActivity.this, WorkDetailActivity.class);
                    intent.putExtra("wid", mReadBookBean.wid);
                    startActivity(intent);
                }
                break;
            //下载
            case R.id.fl_bulk_buy:
                if (PlotRead.getAppUser().login() && mPageLoader != null) {
                    if (mPageLoader.getChapterPos() != -1) {
                        onBuyMoreClick(mPageLoader.getChapterPos());
                    }
                } else {
                    intent.setClass(ReadActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //关闭多章节购买弹窗
            case R.id.rl_more:
            case R.id.img_back:
                onDirectoryDownClick(null, ll_more_buy, rl_more);
                break;
            //章节购买
            case R.id.start_buy:

                int chapter_money = 0;
                int buy_chapter_count = 0;
                if (moreBuyAdapter.getSelectedMoreBuy() != null) {
                    chapter_money = moreBuyAdapter.getSelectedMoreBuy().origin;
                    buy_chapter_count = moreBuyAdapter.getSelectedMoreBuy().count;
                }
                boolean isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, FALSE);
                if(auto_lock_cb != null)
                {
                    auto_lock_cb.setChecked(isstate);
                }
                if (PlotRead.getAppUser().money >= chapter_money) {
                    if (mPresenter != null) {
                        mPresenter.buyMultiChapters(mReadBookBean.wid,
                                mPageLoader.getChapterCategory().get(mPageLoader.getChapterPos()),
                                buy_chapter_count);
                    }
                } else {
                    onDirectoryDownClick(null, ll_more_buy, rl_more);
                    Intent mItent = new Intent(ReadActivity.this, TopUpActivity.class);
                    startActivity(mItent);
                }

                break;
            //右上角三个点
            case R.id.fl_feedback:
                toggleMenu(false);

                AutoPayBookBean autoPayBookBean = DBUtils.getInstance().getAutoBuyBookRecord(mReadBookBean.wid);
                if (autoPayBookBean != null) {
                    book_update_cb.setChecked(autoPayBookBean.isAutoPay);
                } else {
                    book_update_cb.setChecked(false);
                }

                onDirectoryUpClick(null, ll_menu, rl_menu);
                break;
            //关闭弹窗
            case R.id.cancel_setting:
            case R.id.rl_menu:
                onDirectoryDownClick(null, ll_menu, rl_menu);
                break;
            //开启自动订阅
            case R.id.book_update_cb:
                refreshAutoBuy(book_update_cb.isChecked());
                break;
            //章节反馈
            case R.id.ll_book_report:
                if (PlotRead.getAppUser().login() && mPageLoader != null) {
                    intent.setClass(this, ReportActivity.class);
                    intent.putExtra("wid", mReadBookBean.wid);
                    intent.putExtra("type", 2);
                    if (HRCatalogs != null) {
                        intent.putExtra("cid", mPageLoader.getCurChapterID());
                    }
                    startActivity(intent);
                } else {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            //弹出分享弹窗
            case R.id.facebook_share:
                if (sharePopup == null) {
                    sharePopup = new SharePopup(this);
                }
                sharePopup.show(mPvPage);
                break;
            //目录
            case R.id.ll_catalogOpen:
                //设置动画，从自身位置的最下端向上滑动了自身的高度，持续时间为500ms
                toggleMenu(false);
                onDirectoryUpClick(ll_all, ll_catalog, null);
//                hotIds();
                updateCatalogAdapter(mPageLoader.getChapterPos());

                DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_catalog");
                readPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,null);

                break;
            //关闭目录弹窗
            case R.id.ll_all:
            case R.id.rl_close_chapter:
                onDirectoryDownClick(ll_all, ll_catalog, null);
                break;
            //设置
            case R.id.ll_readSettings:
                toggleMenu(false);
                mSettingDialog.show();

                DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_setting");
                settingPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,null);

                break;
            //评论
            case R.id.ll_comment:
                if (mReadBookBean != null) {

                    DeepLinkUtil.addPermanent(ReadActivity.this, "event_content_comment", "阅读页", "点击评论", "", "", "", "", "", "");

                    DataPointBean commentPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_comment");
                    commentPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                            ,0,0,0,null);

                    toggleMenu(false);
                    intent.setClass(this, WorkCommentListActivity.class);
                    intent.putExtra("wid", Integer.parseInt(mReadBookBean.wid.trim()));
                    startActivity(intent);
                }
                break;
            //日夜模式切换
            case R.id.ll_nightModeCheckBox:
                isNightMode = !isNightMode;
                mPageLoader.setNightMode(isNightMode);
                setNightMode(true);
                break;
            //多章购买是否缓存按钮
            case R.id.auto_lock_cb:
                isstate = PlotRead.getConfig().getBoolean(IS_AUTO_BUY, FALSE);
                chapter_money = 0;
                if (moreBuyAdapter.getSelectedMoreBuy() != null) {
                    chapter_money = moreBuyAdapter.getSelectedMoreBuy().origin;
                }


                if (PlotRead.getAppUser().money > chapter_money) {
                    if (isstate) {
                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_AUTO_BUY, FALSE);
                        auto_lock_cb.setChecked(false);
                        start_buy.setText(getString(R.string.unlock_and_download));
                    } else {
                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_AUTO_BUY, TRUE);
                        auto_lock_cb.setChecked(true);
                        start_buy.setText(getString(R.string.unlock));
                    }
                } else {
                    if (isstate) {
                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_AUTO_BUY, FALSE);
                    } else {
                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_AUTO_BUY, TRUE);
                    }
                    start_buy.setText(getString(R.string.purchase_coins_read));
                }
                break;
            //加入书架
            case R.id.iv_add_shelf_read:
                mAblTopMenu.setVisibility(GONE);
                mLlBottomMenu.setVisibility(GONE);
                rl_clickToDismiss.setVisibility(GONE);
//                addLocalShelf();
                PlotRead.toast(PlotRead.SUCCESS, getString(R.string.bookshelf_added_successfully));
                //第一种动画方式
                iv_add_shelf_read.animate()
                        .translationX(300)
                        .setDuration(1500)
                        .start();
                iv_add_shelf_read.postDelayed(() -> {
                    if(iv_add_shelf_read != null)
                    {
                        iv_add_shelf_read.setVisibility(View.GONE);
                    }

                    if(rl_clickToDismiss != null)
                    {
                        rl_clickToDismiss.setVisibility(View.GONE);
                    }

                }, 1500);
                break;
            //加入书架
            case R.id.iv_add_shelf:
                addLocalShelf();
                tv_add_shelf.setText(getString(R.string.added_shelf));
                iv_add_shelf.setImageResource(R.drawable.add_shelf_success_button);
                iv_add_shelf_read.setVisibility(View.GONE);
                rl_clickToDismiss.setVisibility(View.GONE);
                is_add_shelf.postDelayed(() -> {
                    if(is_add_shelf != null)
                    {
                        is_add_shelf.setVisibility(View.GONE);
                    }
                }, 1500);

                DataPointBean commentPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_note");
                commentPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,"addshelf");


                break;
            case R.id.iv_add_shelf_cancel:
                is_add_shelf.setVisibility(View.GONE);

                DataPointBean cancelPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_note");
                cancelPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,"close");
                break;
            default:
                break;
        }
    }

    /**
     * 刷新自动购买状态
     */
    public void refreshAutoBuy(boolean check) {

        if(mReadBookBean != null)
        {
            if(mPresenter != null)
            {
                mPresenter.setAutoPaySelected(check);
            }

            saveAutoPayRecord();

            mPageLoader.setAutoBuySelected(check);

//            mPageLoader.refreshPage();
        }
    }


    /**
     * 阅读结束时，更新数据到本地
     */
    private void store() {
        // 更新阅读历史
        mHRWork.lastChapterOrder = mPageLoader.getChapterPos();
        mHRWork.lastChapterPosition = mPageLoader.getPagePos();
        mHRWork.lasttime = TimeUtil.currentTimeSeconds();
        // 更新书架
        if (ShelfUtil.exist(mHRWork.wid)) {
            mHRWork.deleteflag = 0;
            ShelfUtil.insert(ReadActivity.this, mHRWork,false);
        }
        ShelfUtil.insertRecord(mHRWork);
        // 上报阅读记录
        if (PlotRead.getAppUser().login()) {
            NetRequest.uploadReadRecord(mHRWork.wid, mHRWork.lastChapterId, mHRWork.lasttime, null);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if (mAblTopMenu.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
//            if (!ReadSettingManager.getInstance().isFullScreen()) {
//            }
            toggleMenu(true);
            return;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mDlSlide.isDrawerOpen(Gravity.START)) {
            mDlSlide.closeDrawer(Gravity.START);
            return;
        } else if (ll_all.getVisibility() == View.VISIBLE) {
            onDirectoryDownClick(ll_all, ll_catalog, null);
            return;
        } else if (rl_more.getVisibility() == View.VISIBLE) {
            onDirectoryDownClick(null, ll_more_buy, rl_more);
            return;
        } else if (rl_menu.getVisibility() == View.VISIBLE) {
            onDirectoryDownClick(null, ll_menu, rl_menu);
            return;
        } else {
            doFinish();
        }
    }

    /**
     * 点击返回键
     */
    private void doFinish() {
        if (mHRWork == null) {
            return;
        }

        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }

        // 保存记录
        store();
        //是否显示推荐
        boolean isGetReadReword = PlotRead.getConfig().getBoolean(FIRST_READ_RECOMMEND
                + getString(R.string.app_name)
                + mHRWork.wid, FALSE);
        if (!isGetReadReword && mHRRecLists != null && mHRRecLists.size() > 0) {
            SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_READ_RECOMMEND
                    + getString(R.string.app_name)
                    + mHRWork.wid, TRUE);
            RecommendationAlertDialog.show(this, mHRRecLists, style, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doBackPressed();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        } else {
            // 加入书架提示
            if (!ShelfUtil.exist(mHRWork.wid)) {





                TipAddShelfAlertDialog.show(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doBackPressed();

                        DataPointBean cancelPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_quite_addshelf");
                        cancelPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                                ,0,0,0,"cancel");
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addLocalShelf();
                        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.bookshelf_added_successfully));
                        doBackPressed();

                        DataPointBean cancelPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_quite_addshelf");
                        cancelPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                                ,0,0,0,"addshelf");
                    }
                });
            } else {
                doBackPressed();
            }
        }
    }

    /**
     * 设置加入书架按钮是否显示
     */
    protected void addShelfisShow() {
        if (ShelfUtil.exist(mHRWork.wid)) {
            rl_clickToDismiss.setVisibility(View.GONE);
        } else {
            if (mLlBottomMenu.getVisibility() == VISIBLE) {
                rl_clickToDismiss.setVisibility(View.VISIBLE);
            } else {
                rl_clickToDismiss.setVisibility(GONE);
            }
        }
    }

    /**
     * 加书架
     */
    private void addLocalShelf() {
        if (mHRWork == null)
            return;
        mHRWork.deleteflag = 0;
        ShelfUtil.insert(ReadActivity.this, mHRWork, false);

        DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_addshelf");
        readPagePoint.setReadDataPoint(mReadBookBean.wid,null
                ,0,0,0,null);
    }

    /**
     * 返回健
     */
    private void doBackPressed() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            int activities = am.getRunningTasks(ONE).get(ZERO).numRunning;
            LOG.i(getClass().getSimpleName(), "numActivities = " + activities);
            if (getIntent().getBooleanExtra("push", FALSE) && activities == ONE) {
                startActivity(new Intent(this, HomeActivity.class));
            }
        }
        super.onBackPressed();
    }

    /**
     * 章节切换
     */
    private void bindChapterChange() {


        mSbChapterProgress.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //进行切换
                        if (mPageLoader != null) {
                            int pagePos = mSbChapterProgress.getProgress();
                            if (pagePos != mPageLoader.getPagePos()) {
                                mPageLoader.skipToPage(pagePos);
                            }
                        }
                    }
                }
        );


        mTvPreChapter.setOnClickListener(
                (v) -> {
                    if (mPageLoader != null) {
                        if (mPageLoader.skipPreChapter()) {
                        }
                    }

                }
        );

        mTvNextChapter.setOnClickListener(
                (v) -> {
                    if (mPageLoader != null) {
                        if (mPageLoader.skipNextChapter()) {
                        } else {
                            Intent intent = new Intent(ReadActivity.this, NewReadEndActivity.class);
                            intent.putExtra("work", mHRWork);
                            startActivity(intent);
                        }
                    }
                }
        );
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message msg) {

        if (msg != null) {
            switch (msg.what) {
                case BUS_LOG_IN: {
                    if (mPageLoader != null) {
                        mPageLoader.refreshChapterList();
                        mPageLoader.openChapter();
                    }
                }
                break;
                case BUS_RECHARGE_SUCCESS: {
                    if (mPageLoader != null) {
                        if (mPageLoader != null) {
                            mPresenter.setAutoPaySelected(mPageLoader.isAutoBuySelected());
                        }

                        mPageLoader.clearPageStatusError();
                        int chapterOrder = mPageLoader.getChapterPos();
                        if (mPresenter != null) {
                            List<TxtChapter> chapters = new ArrayList<>();
                            TxtChapter currentChapter = mPageLoader.getChapterCategory().get(chapterOrder);
                            if (currentChapter.getmChatperStatusInfo() != null) {
                                currentChapter.setmChatperStatusInfo(null);
                            }
                            mPageLoader.skipToChapter(chapterOrder);
                            mPresenter.loadChapter(mReadBookBean.wid, chapters);
                        }
                    }
                }
                break;
                case BUS_MULTI_DOWNLOAD_COMPLETE: {
                    if (msg.obj.equals(mReadBookBean.wid)) {
                        PlotRead.toast(PlotRead.NORMAL, getString(R.string.cache_success));

                        if(progressDialog != null)
                        {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    }
                }
                break;
                case BusC.BUS_NOTIFY_USER_READ_NEXT_CHAPTER: {

                }
                break;
                case BusC.BUS_NOTIFY_REQUEST_STROGE: {
                    requestPermission();
                }
                break;
                case BusC.BUS_NOTIFY_UPLOAD_READTIME:{
                    long allReadMillonTime = endTime - startTime +   SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
                    int readTime = (int)allReadMillonTime/60000;
                    UserTaskReceiveManager.getInstance().updateUserReadTimeAndGetTaskRecevice(getActivityContext(), readTime);
                }
                break;
                case BusC.BUS_NOTIFY_NOTICE_USER_UNCLOCK:{
                    freshUnlockTips();
                }
                break;
                case BusC.BUS_NOTIFY_MULIT_DOWN_PROGRESS:{

                    String progressStr = (String) msg.obj;
                    if(progressStr != null)
                    {
                        showDownProgress(progressStr);
                    }
                }
                break;

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1111 && grantResults.length >= 2) {
            if (grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults[ONE] == PackageManager.PERMISSION_GRANTED) {
                    if (mPageLoader != null && mPageLoader.getChapterCategory().size()>0) {
                        mPageLoader.clearPageStatusError();
                        mPageLoader.openChapter();
                    }
                }
            }
        }
    }


    /**
     * 显示加入书架提示
     */
    private void showAddShelfNotice()
    {
        if (!ShelfUtil.exist(mHRWork.wid) && !PlotRead.getConfig().getBoolean(getString(R.string.app_name) + mHRWork.wid + "isShowAddShelf", false)) {

            page++;
            if (page == 5) {
                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), getString(R.string.app_name) + mHRWork.wid + "isShowAddShelf", true);
                is_add_shelf.setVisibility(View.VISIBLE);
                is_add_shelf.postDelayed(() -> {

                    if(mPageLoader != null && mReadBookBean != null)
                    {
                        DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_note");
                        readPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                                ,0,0,0,"null");
                    }


                    if(is_add_shelf != null)
                    {

                        is_add_shelf.setVisibility(View.GONE);
                    }
                }, 5000);

                DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_note");
                readPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                        ,0,0,0,"null");
            }

        }


        DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_click");
        readPagePoint.setReadDataPoint(mReadBookBean.wid,mPageLoader.getCurChapterID()
                ,0,0,0,null);


        DeepLinkUtil.addPermanent(ReadActivity.this, "event_chapter_click", "阅读页", "翻页点击", "", mHRWork.wid + "", mPageLoader.getChapterPos() + "", "", "", "");
//
    }


    /**
     * 阅读时长统计
     */

    public void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
                endTime = System.currentTimeMillis();
                Long timeAll = endTime - startTime +  SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
                Log.e(TAG, "阅读时间计时 : "+ timeAll );
                Message message = Message.obtain();
                message.what = BusC.BUS_NOTIFY_UPLOAD_READTIME;

                if ( 660000> timeAll && timeAll >= 600000){
                    message.obj = TEN;
                }
                if ( 960000> timeAll && timeAll >= 900000){
                    message.obj = FIFTEEN;
                }
                if (1860000> timeAll && timeAll >= 1800000){
                    message.obj = THIRTY;
                }
                if (3660000> timeAll && timeAll > 3600000){
                    message.obj = SIXTY;
                }
//                message.obj = "发送消息的线程名称：" + Thread.currentThread().getName();

                EventBus.getDefault().post(message);
            }
        }, 0,60000);// 设定指定的时间time
    }


    private TipsDownProgressDialog progressDialog;
    private void showDownProgress(String progressStr)
    {

        if(progressDialog == null)
        {
            progressDialog = new TipsDownProgressDialog(ReadActivity.this);
            progressDialog.show();
        }else {
            progressDialog.freashProgressText(progressStr);
            if(!progressDialog.isShowing())
            {
                progressDialog.show();
            }
        }



//        if(progressTimer == null)
//        {
//            progressTimer = new Timer();
//            progressTimer.schedule(new TimerTask() {
//                public void run() {
//                    System.out.println("-------设定要指定任务--------");
//                    progress++;
//                    Message message = Message.obtain();
//                    message.what = BUS_NOTIFY_MULIT_DOWN_PROGRESS;
//                    EventBus.getDefault().post(message);
//                }
//            }, 0,2000);// 设定指定的时间time
//        }

    }


}
