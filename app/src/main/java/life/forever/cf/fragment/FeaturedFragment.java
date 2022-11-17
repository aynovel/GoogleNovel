package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.SignBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.BookRecordBean;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.HomeActivity;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.activtiy.SearchActivity;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.tab.MagicIndicator;
import life.forever.cf.tab.ViewPagerHelper;
import life.forever.cf.tab.buildins.commonnavigator.CommonNavigator;
import life.forever.cf.tab.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerIndicator;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerTitleView;
import life.forever.cf.tab.buildins.commonnavigator.indicators.LinePagerIndicator;
import life.forever.cf.tab.buildins.commonnavigator.titles.SimplePagerTitleView;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.CommonUtil;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.tool.SignDialog;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.activtiy.TaskCenterActivity;
import life.forever.cf.activtiy.ReadActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import life.forever.cf.activtiy.Cods;


public class FeaturedFragment extends BaseFragment {


    @BindView(R.id.tv_search)
    TextView mTvSearch;
    @BindView(R.id.head)
    RadiusImageView mRadiusImageView;
    @BindView(R.id.img_search)
    ImageView mImgSearch;
    @BindView(R.id.img_sign)
    ImageView mImgSign;

    private SignDialog mSignDialog;
    private SignBean.ResultData mSignBean;

    @BindView(R.id.magicIndicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.rl_cqs_bg)
    RelativeLayout rl_cqs_bg;


    @BindView(R.id.layout_read_history)
    RelativeLayout mLayoutReadHistory;
    @BindView(R.id.layout_history)
    LinearLayout mLayoutHistory;
    @BindView(R.id.img_cover)
    ImageView mImgCover;
    @BindView(R.id.tv_book_name)
    TextView mTvBookName;
    @BindView(R.id.tv_book_info)
    TextView mTvBookInfo;

    @BindView(R.id.imgBr1)
    ImageView mImgBr1;
    @BindView(R.id.imgBr2)
    ImageView mImgBr2;
    @BindView(R.id.imgBr3)
    ImageView mImgBr3;
    @BindView(R.id.imgBr4)
    ImageView mImgBr4;

    static int mPosition;
    boolean isshowhistory = true;
    private int mCount = 0;

    public static int mDiscoverRecordHeight, mTrendRecordHeight = 0;
    private float mDensity;
    private boolean mNeedShake = false;
    private boolean mHasGift = false;
    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 94;
    private static final float DEGREE_0 = 2.0f;
    private static final float DEGREE_1 = -2.0f;
    private static final int ANIMATION_DURATION = 500;
    private final List<Work> mWork = new ArrayList<>();
    private final String[] pagerTitles = {DISCOVER};
    private final List<BaseFragment> fragments = new ArrayList<>();
    boolean isFirst = false;

    private Animation animation1;
    private Animation animation2;
    private Animation animation3;
    private Animation animation4;

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_featured, mContentLayout);
        ButterKnife.bind(this, root);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mMagicIndicator.setVisibility(View.GONE);
        rl_cqs_bg.setVisibility(View.GONE);
        DisplayMetrics dm = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mNeedShake = true;
        shakeAnimation(mImgSign);
        mImgSign.setOnClickListener(onSignClick);
        initIndicator(WHITE69, WHITE, mPosition);
        ScreenUtil.setStatusBarDark(getActivity(), true);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!isFirst && position == 1) {
                    mMagicIndicator.setVisibility(View.GONE);
                    rl_cqs_bg.setVisibility(View.GONE);
                    isFirst = true;
                }
                mPosition = position;
                if (position == 0) {
                      Message message = Message.obtain();
                    message.what = DISCOVERTOPTAB;
                    EventBus.getDefault().post(message);
                    if (mDiscoverRecordHeight > 180) {
                        mScrollView();
                    } else {
                        mRefreshView();
                    }
                    alphaTitle(mDiscoverRecordHeight);
                } else if (position == 1) {
                    Message message = Message.obtain();
                    message.what = TRENDINGTOPTAB;
                    EventBus.getDefault().post(message);
                    if (mTrendRecordHeight > 180) {
                        mScrollView();
                    } else {
                        mRefreshView();
                    }
                } else if (position == 2) {
                    Message message = Message.obtain();
                    message.what = RANKTOPTAB;
                    EventBus.getDefault().post(message);
                    mScrollView();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void mRefreshView() {
        rl_cqs_bg.setBackgroundResource(R.color.color_00FFFFFF);
        mMagicIndicator.setBackgroundResource(R.color.color_00FFFFFF);
        mTvSearch.setBackgroundResource(R.drawable.shape_f2_corner_15dp);
        mTvSearch.setTextColor(getResources().getColor(R.color.color_B3FFFFFF));
        initIndicator(WHITE69, WHITE, mPosition);
        mImgSearch.setImageResource(R.drawable.home_search);
        ScreenUtil.setStatusBarDark(getActivity(), true);
    }


    public void mScrollView() {
        rl_cqs_bg.setBackgroundResource(R.color.colorWhite);
        mImgSearch.setImageResource(R.drawable.home_search_gray);
        mImgSearch.bringToFront();
        mMagicIndicator.setBackgroundResource(R.color.colorWhite);
        initIndicator(DARK_3, color_000001, mPosition);
        mTvSearch.setBackgroundResource(R.drawable.shape_f1f2f3_corner_15dp);
        mTvSearch.setTextColor(getResources().getColor(R.color.color_B3AAAAAA));
        ScreenUtil.setStatusBarDark(getActivity(), false);
    }


    public static void Refresh(int discover) {
        if (mPosition == 0) {
            DiscoverFragment.Refresh(discover);
        } else if (mPosition == 1) {
            TrendingFragment.Refresh(discover);
        } else if (mPosition == 2) {
            RankingChildFragment.Refresh();
            Message message = Message.obtain();
            message.what = DISCOVERTAB;
            EventBus.getDefault().post(message);
        }
    }

    private void initIndicator(int normal, int select, int mPosition) {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdjustMode(TRUE);
        commonNavigator.setScrollPivotX(DOT_FIVE);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return pagerTitles == null ? ZERO : pagerTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(final Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(pagerTitles[index]);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SIXTEEN);
                simplePagerTitleView.setNormalColor(normal);
                simplePagerTitleView.setSelectedColor(select);
                simplePagerTitleView.setOnClickListener(v -> mViewPager.setCurrentItem(index, FALSE));

                return simplePagerTitleView;

            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(DisplayUtil.dp2px(context, TWO));
                indicator.setLineWidth(DisplayUtil.dp2px(context, FORTY));
                indicator.setRoundRadius(DisplayUtil.dp2px(context, ONE));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(TWO));
                indicator.setColors(THEME_COLOR);
                indicator.setYOffset(DisplayUtil.dp2px(context, SIX));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        mMagicIndicator.onPageSelected(mPosition);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        fetchRecommend();
        fragments.add(DiscoverFragment.get(alp -> {
            mDiscoverRecordHeight = alp;
            alphaTitle(alp);

        }, new ILoadingListener() {
            @Override
            public void show() {
                if (mMagicIndicator != null && rl_cqs_bg != null) {
                    mMagicIndicator.setVisibility(View.GONE);
                    rl_cqs_bg.setVisibility(View.GONE);
                }
            }

            @Override
            public void dis() {
                if (mMagicIndicator != null && rl_cqs_bg != null){
                    rl_cqs_bg.setVisibility(View.VISIBLE);
                }

            }
        }));

        mViewPager.setOffscreenPageLimit(fragments.size() - 1);
        mViewPager.setAdapter(new RecommendPagerAdapter(getChildFragmentManager()));
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);


        if (PlotRead.getAppUser().login()) {
            fetchSignInfo();
            fetchReadRecord();
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.logo_default_user, mRadiusImageView);
        } else {
            mLayoutReadHistory.setVisibility(View.GONE);
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.default_user_logo, mRadiusImageView);
        }

    }

    private final View.OnClickListener onSignClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                Intent intent = new Intent();
                intent.setClass(context, TaskCenterActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
            }
        }
    };


    private void alphaTitle(int alpha) {
        if (alpha <= 0) {
            rl_cqs_bg.setBackgroundResource(R.color.color_00FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_00FFFFFF);
            mRefreshView();
        } else if (0 < alpha && alpha < 10) {
            rl_cqs_bg.setBackgroundResource(R.color.color_1AFFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_1AFFFFFF);
        } else if (10 <= alpha && alpha < 20) {
            rl_cqs_bg.setBackgroundResource(R.color.color_26FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_26FFFFFF);
        } else if (20 <= alpha && alpha < 30) {
            rl_cqs_bg.setBackgroundResource(R.color.color_33FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_33FFFFFF);
        } else if (30 <= alpha && alpha < 40) {
            rl_cqs_bg.setBackgroundResource(R.color.color_40FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_33FFFFFF);
        } else if (40 <= alpha && alpha < 50) {
            rl_cqs_bg.setBackgroundResource(R.color.color_4DFFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_4DFFFFFF);
        } else if (50 <= alpha && alpha < 60) {
            rl_cqs_bg.setBackgroundResource(R.color.color_59FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_59FFFFFF);
        } else if (60 <= alpha && alpha < 70) {
            rl_cqs_bg.setBackgroundResource(R.color.color_66FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_66FFFFFF);
        } else if (70 <= alpha && alpha < 80) {
            rl_cqs_bg.setBackgroundResource(R.color.color_73FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_73FFFFFF);
        } else if (80 <= alpha && alpha < 90) {
            rl_cqs_bg.setBackgroundResource(R.color.color_80FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_80FFFFFF);
        } else if (90 <= alpha && alpha < 100) {
            rl_cqs_bg.setBackgroundResource(R.color.color_8CFFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_8CFFFFFF);
        } else if (100 <= alpha && alpha < 110) {
            rl_cqs_bg.setBackgroundResource(R.color.color_99FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_99FFFFFF);
        } else if (110 <= alpha && alpha < 120) {
            rl_cqs_bg.setBackgroundResource(R.color.color_A6FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_A6FFFFFF);
        } else if (120 <= alpha && alpha < 130) {
            rl_cqs_bg.setBackgroundResource(R.color.color_B3FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_B3FFFFFF);
        } else if (130 <= alpha && alpha < 140) {
            rl_cqs_bg.setBackgroundResource(R.color.color_BFFFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_BFFFFFFF);
        } else if (140 <= alpha && alpha < 150) {
            rl_cqs_bg.setBackgroundResource(R.color.color_CCFFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_CCFFFFFF);
        } else if (150 <= alpha && alpha < 160) {
            rl_cqs_bg.setBackgroundResource(R.color.color_D9FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_D9FFFFFF);
        } else if (160 <= alpha && alpha < 170) {
            rl_cqs_bg.setBackgroundResource(R.color.color_E6FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_E6FFFFFF);
        } else if (170 <= alpha && alpha < 180) {
            rl_cqs_bg.setBackgroundResource(R.color.color_F2FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_F2FFFFFF);
        } else if (180 <= alpha) {
            rl_cqs_bg.setBackgroundResource(R.color.color_FFFFFF);
            mMagicIndicator.setBackgroundResource(R.color.color_FFFFFF);
            mScrollView();
        }
    }

    private class RecommendPagerAdapter extends FragmentPagerAdapter {

        RecommendPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return pagerTitles.length;
        }
    }

    @OnClick(R.id.tv_search)
    public void setmTvSearch() {
        Intent intent = new Intent(context, SearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.head)
    public void setmLeftSide() {
        ((HomeActivity) requireActivity()).openDrawer();
    }


    @OnClick(R.id.layout_read_history)
    public void Reading() {
        if (mWork == null || mWork.size() == ZERO) {
            return;
        }
          updateRecord();




        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("work", mWork.get(0));
        CollBookBean mCollBook  = new CollBookBean();
        mCollBook.setTitle(mWork.get(0).title);
        mCollBook.set_id(mWork.get(0).wid+"");
        intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
        startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    private void updateRecord() {
        if (null != mWork && ShelfUtil.existRecord(mWork.get(0).wid)) {
            Work record = ShelfUtil.queryRecord(this.mWork.get(0).wid);
            if (record != null) {
                mWork.get(0).lasttime = record.lasttime;
                mWork.get(0).lastChapterId = record.lastChapterId;
                mWork.get(0).lastChapterOrder = record.lastChapterOrder;
                mWork.get(0).lastChapterPosition = record.lastChapterPosition;

                freashReadHistroyInfo();
            }

            if(mWork.get(0) != null)
            {
                BookRecordBean recordBean = DBUtils.getInstance().getBookRecord(""+mWork.get(0).wid);

                if(recordBean == null)
                {
                    recordBean = new BookRecordBean();
                    recordBean.wid = ""+mWork.get(0).wid;
                    recordBean.chapterIndex = mWork.get(0).lastChapterOrder;
                    recordBean.chapterCharIndex = mWork.get(0).lastChapterOrder;
                    DBUtils.getInstance().saveBookRecordWithAsync(recordBean);
                }
            }
        }
    }

    @OnClick(R.id.layout_close)
    public void CloseHistory() {
        isshowhistory = false;
        onDirectoryDownClick(mLayoutReadHistory);
    }


    private void fetchReadRecord() {
        NetRequest.userReadRecord(ONE, new OkHttpResult() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray booklist = JSONUtil.getJSONArray(result, "booklist");
                    for (int i = ZERO; booklist != null && i < booklist.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(booklist, i);
                        Work mWorks = BeanParser.getWork(child);
                        mWorks.lasttime = JSONUtil.getInt(child, "readtime");

                        BookRecordBean recordBean = DBUtils.getInstance().getBookRecord("" + mWorks.wid);
                        if(recordBean != null)
                        {
                            mWorks.lastChapterOrder = recordBean.chapterIndex;
                            mWorks.lastChapterPosition = recordBean.chapterCharIndex;
                        }
                        mWork.add(mWorks);
                    }
                    if (mWork != null && mWork.size() > 0) {
                        mLayoutReadHistory.setVisibility(View.VISIBLE);
                        GlideUtil.shelfPic(context, mWork.get(0).cover, R.drawable.default_work_cover, mImgCover);
                        mTvBookName.setText(mWork.get(0).title);



                        freashReadHistroyInfo();
                    } else {
                        mLayoutReadHistory.setVisibility(View.GONE);
                    }
                } else {
                    mLayoutReadHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                mLayoutReadHistory.setVisibility(View.GONE);
            }
        });
    }

    private void freashReadHistroyInfo()
    {
        if (CommonUtil.isDestroy((Activity) context)) {
            mTvBookInfo.setText("Chapter  " + (mWork.get(0).lastChapterOrder + 1));
        } else {
            mTvBookInfo.setText(application.getString(R.string.discover_history) + (mWork.get(0).lastChapterOrder + 1));
        }
    }


    /**
     * 请求签到信息
     */
    private void fetchSignInfo() {
        NetRequest.signInfo(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        Gson gson = new Gson();
                        mSignBean = gson.fromJson(resultString, SignBean.ResultData.class);
                        if (mSignBean != null && mSignBean.info != null) {
                            if (1 == mSignBean.info.today_is_sign) {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, TRUE);
                                if (mNeedShake) {
                                    mNeedShake = false;
                                    mCount = 0;
                                }
                            } else {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, FALSE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }



    /**
     * 获取热搜词及推荐
     */
    private void fetchRecommend() {
        NetRequest.searchRecommend(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    String recWords = JSONUtil.getString(result, "rec_words");
                    if (!TextUtils.isEmpty(recWords)) {
                        mTvSearch.setText(recWords);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }



    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {

            case SHOWHISTORY:
                if (PlotRead.getAppUser().login() && isshowhistory) {
                    if (mWork != null && mWork.size() > 0) {
                        onDirectoryUpClick(mLayoutReadHistory);
                    } else {
                        onDirectoryDownClick(mLayoutReadHistory);
                    }
                }
                break;
            case HINTHISTORY:
                onDirectoryDownClick(mLayoutReadHistory);
                break;

            case BUS_USER_SIGN_STATE_CHANGE:
                PlotRead.getAppUser().fetchUserInfo(getActivity());
                break;

            case BUS_USER_INFO_SUCCESS:
                fetchSignInfo();
                AppUser user = PlotRead.getAppUser();
                GlideUtil.load(context, user.head, R.drawable.logo_default_user, mRadiusImageView);
                if (mWork != null) {
                    mWork.clear();
                }
                fetchReadRecord();
                break;

            case BUS_READ_HISTORY_CHANGE:
                if (PlotRead.getAppUser().login()) {
                    if (mWork != null) {
                        mWork.clear();
                    }
                    mWork.addAll(ShelfUtil.queryRecord());
                    if (mWork != null && mWork.size() > 0) {
                        onDirectoryUpClick(mLayoutReadHistory);
//                        mLayoutReadHistory.setVisibility(View.VISIBLE);
                        GlideUtil.shelfPic(context, mWork.get(0).cover, R.drawable.default_work_cover, mImgCover);
                        mTvBookName.setText(mWork.get(0).title);

                        freashReadHistroyInfo();
                    } else {
                        mLayoutReadHistory.setVisibility(View.GONE);
                    }
                } else {
                    mLayoutReadHistory.setVisibility(View.GONE);
                }
                break;
            case BUS_MODIFY_INFO_SUCCESS:
                AppUser mUser = PlotRead.getAppUser();
                GlideUtil.load(context, mUser.head, R.drawable.logo_default_user, mRadiusImageView);
                break;
            case BUS_LOG_OUT:
                AppUser users = PlotRead.getAppUser();
                GlideUtil.load(context, users.head, R.drawable.default_user_logo, mRadiusImageView);
                break;

            case BONUS_OVER_LIMIT:
                    //过期书卷提醒，开启动画
                    doBreathinglamp();
                break;
        }
    }

    private void shakeAnimation(final View v) {
        float rotate;
        int c = mCount++ % 5;
        switch (c) {
            case 1:
            case 3:
                rotate = DEGREE_1;
                break;
            default:
                rotate = DEGREE_0;
                break;
        }
        final RotateAnimation mra = new RotateAnimation(rotate, -rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);
        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);

        mra.setDuration(ANIMATION_DURATION);
        mrb.setDuration(ANIMATION_DURATION);

        mra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake | mHasGift) {
                    mra.reset();
                    v.startAnimation(mrb);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        mrb.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake | mHasGift) {
                    mrb.reset();
                    v.startAnimation(mra);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        v.startAnimation(mra);
    }

    /**
     * 监听滑动距离
     */
    public interface ICallBackListener {
        void onScrollClick(int alp);
    }

    /**
     * 监听loading是否结束
     */
    public interface ILoadingListener {
        void show();
        void dis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void onDirectoryUpClick(RelativeLayout a) {
        if (a.getVisibility() == View.GONE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
            ctrlAnimation.setDuration(200l);     //设置动画的过渡时间
            a.setVisibility(View.VISIBLE);
            a.startAnimation(ctrlAnimation);
        }
    }

    void onDirectoryDownClick(RelativeLayout a) {
        if (a.getVisibility() == View.VISIBLE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            ctrlAnimation.setDuration(200l);     //设置动画的过渡时间
            a.setVisibility(View.GONE);
            a.startAnimation(ctrlAnimation);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        ScreenUtil.setStatusBarDark(getActivity(), true);

    }


    public void doBreathinglamp(){
        animation1 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation2 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation3 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation4 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation2.setStartOffset(600);
        animation3.setStartOffset(1200);
        animation4.setStartOffset(1800);
        mImgBr1.startAnimation(animation1);
        mImgBr2.startAnimation(animation2);
        mImgBr3.startAnimation(animation3);
        mImgBr4.startAnimation(animation4);

    }


    public void clearBreathingAnimation(){
        if (animation1 != null){
            mImgBr1.clearAnimation();
            animation1.cancel();
            mImgBr1.setVisibility(View.GONE);
        }
        if (animation2 != null){
            mImgBr2.clearAnimation();
            animation2.cancel();
            mImgBr2.setVisibility(View.GONE);
        }
        if (animation3 != null){
            mImgBr3.clearAnimation();
            animation3.cancel();
            mImgBr3.setVisibility(View.GONE);
        }
        if (animation4 != null){
            mImgBr4.clearAnimation();
            animation4.cancel();
            mImgBr4.setVisibility(View.GONE);
        }
    }



}
