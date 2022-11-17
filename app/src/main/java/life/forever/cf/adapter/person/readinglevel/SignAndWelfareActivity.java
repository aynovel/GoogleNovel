package life.forever.cf.adapter.person.readinglevel;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AD;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.tab.MagicIndicator;
import life.forever.cf.tab.ViewPagerHelper;
import life.forever.cf.tab.buildins.commonnavigator.CommonNavigator;
import life.forever.cf.tab.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerIndicator;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerTitleView;
import life.forever.cf.tab.buildins.commonnavigator.titles.SimplePagerTitleView;
import life.forever.cf.adapter.person.readingtask.TaskFragment;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.scrollweight.ScrollLayout;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.LOG;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.weight.AutoRollBanner;
import life.forever.cf.publics.weight.LevelView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;


public class SignAndWelfareActivity extends BaseActivity {

    @BindView(R.id.normalLevel)
    LevelView mNormalLevel;
    @BindView(R.id.vipLevel)
    LevelView mVipLevel;
    @BindView(R.id.sign)
    TextView mSign;
    @BindView(R.id.signDays)
    TextView mSignDays;
    @BindView(R.id.scrollLayout)
    ScrollLayout mScrollLayout;
    @BindView(R.id.banner)
    AutoRollBanner mBanner;
    @BindView(R.id.magicIndicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private final String[] pagerTitles = {MINE_STRING_DAY_TASK,
            MINE_STRING_GUIDE_TASK};

    private final List<TaskFragment> fragments = new ArrayList<>();
    private final List<AD> ads = new ArrayList<>();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setMiddleText(MINE_STRING_USER_SIGN_WELFARE);
        setContentView(R.layout.activity_sign_welfare);
        ButterKnife.bind(this);
        initIndicator();
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        mNormalLevel.setLevel(PlotRead.getAppUser().level);
        mVipLevel.setVipLevel(PlotRead.getAppUser().vip);
        fillSignState();

        fragments.add(TaskFragment.get(TaskFragment.TASK_TYPE_DAY));
        fragments.add(TaskFragment.get(TaskFragment.TASK_TYPE_GUIDE));
//        fragments.add(TaskFragment.get(TaskFragment.TASK_TYPE_SUM));
        mViewPager.setOffscreenPageLimit(pagerTitles.length - 1);
        mViewPager.setAdapter(new TaskPagerAdapter(getSupportFragmentManager()));
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
        mScrollLayout.getHelper().setCurrentScrollParent(fragments.get(ZERO));
        // 获取签到信息
        fetchSignInfo();
        // 自动签到
        if (!PlotRead.getAppUser().signDate.equals(ComYou.currentTimeFormat(DATE_FORMATTER_1))) {
            fetchSign();
        }
    }

    /**
     * 填充签到状态
     */
    private void fillSignState() {
        mSignDays.setText(String.format(Locale.getDefault(), MINE_STRING_CONTINUE_SIGN, PlotRead.getAppUser().signDays));
        mSign.setText(PlotRead.getAppUser().signDate.equals(ComYou.currentTimeFormat(DATE_FORMATTER_1)) ? MINE_STRING_SIGNED : MINE_STRING_SIGN);
        mSign.setEnabled(!PlotRead.getAppUser().signDate.equals(ComYou.currentTimeFormat(DATE_FORMATTER_1)));
    }

    private void initIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(getBaseContext());
        commonNavigator.setScrollPivotX(DOT_FIVE);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return pagerTitles == null ? ZERO : pagerTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(pagerTitles[index]);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIFTEEN);
                simplePagerTitleView.setNormalColor(DARK_2);
                simplePagerTitleView.setSelectedColor(THEME_COLOR);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_USER_SIGN_STATE_CHANGE || message.what == BUS_USER_INFO_SUCCESS) {
            fillSignState();
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
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONObject info = JSONUtil.getJSONObject(result, "info");
                        JSONObject recommend = JSONUtil.getJSONObject(info, "recommend");
                        JSONObject rec_info = JSONUtil.getJSONObject(recommend, "rec_info");
                        JSONArray rec_list = JSONUtil.getJSONArray(recommend, "rec_list");
                        for (int i = ZERO; rec_list != null && i < rec_list.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(rec_list, i);
                            AD ad = new AD();
                            ad.recId = JSONUtil.getInt(rec_info, "rec_id");
                            ad.during = JSONUtil.getInt(rec_info, "length");
                            ad.image = JSONUtil.getString(child, "h_url");
                            ad.type = JSONUtil.getInt(child, "advertise_type");

                            JSONObject advertise = JSONUtil.getJSONObject(child, "advertise_data");
                            if (ad.type == ONE) {
                                ad.wid = JSONUtil.getInt(advertise, "wid");
                                ad.readflag = JSONUtil.getInt(advertise, "readflag");
                                ad.cid = JSONUtil.getInt(advertise, "cid");
                            } else if (ad.type == TWO) {
                                ad.index = JSONUtil.getString(advertise, "ht");
                                ad.path = JSONUtil.getString(advertise, "path");
                                ad.pagefresh = JSONUtil.getInt(advertise, "ps") == ZERO;
                                ad.share = JSONUtil.getInt(advertise, "is") == ONE;
                                ad.shareUrl = JSONUtil.getString(advertise, "su");
                                ad.shareType = JSONUtil.getInt(advertise, "st");
                                ad.sharefresh = JSONUtil.getInt(advertise, "ifreash") == ZERO;
                                ad.shareTitle = JSONUtil.getString(advertise, "title");
                                ad.shareDesc = JSONUtil.getString(advertise, "desc");
                                ad.shareImg = JSONUtil.getString(advertise, "image");
                            } else if (ad.type == THREE) {
                                ad.url = JSONUtil.getString(advertise, "url");
                            }
                            ads.add(ad);
                        }
                        if (ads.size() == ONE) {
                            mBanner.setBanner(ads.get(ZERO), TWO);
                            mBanner.setVisibility(View.VISIBLE);
                        } else if (ads.size() > ONE) {
                            mBanner.setBanners(ads, TWO);
                            mBanner.setVisibility(View.VISIBLE);
                        } else {
                            mBanner.setVisibility(View.GONE);
                        }
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        LOG.i(getClass().getSimpleName(), msg);
                        mBanner.setVisibility(View.GONE);
                    }
                } else {
                    NetRequest.error(SignAndWelfareActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                mBanner.setVisibility(View.GONE);
                LOG.i(getClass().getSimpleName(), getString(R.string.request_failed));
            }
        });
    }

    /**
     * 签到
     */
    private void fetchSign() {
        showLoading(getString(R.string.signed_in_label));
        NetRequest.sign(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (ComYou.isDestroy(SignAndWelfareActivity.this)){
                    return;
                }
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONObject info = JSONUtil.getJSONObject(result, "info");
                        int signDays = JSONUtil.getInt(info, "continue");
                        int next = JSONUtil.getInt(info, "next");
                        int voucher = JSONUtil.getInt(info, "voucher");
                        int experience = JSONUtil.getInt(info, "experience");
                        String signDate = ComYou.currentTimeFormat(DATE_FORMATTER_1);
                        // 签到成功弹窗
                        SignSuccessPopup successPopup = new SignSuccessPopup(SignAndWelfareActivity.this, voucher, experience, signDays, next);
                        successPopup.show(mTitleBar);
                        // 更新用户信息
                        PlotRead.getAppUser().voucher += voucher;
                        PlotRead.getAppUser().signDays = signDays;
                        PlotRead.getAppUser().signDate = signDate;
                        // 保存信息
                        SharedPreferencesUtil.putInt(PlotRead.getAppUser().config, KEY_VOUCHER, PlotRead.getAppUser().voucher);
                        SharedPreferencesUtil.putInt(PlotRead.getAppUser().config, KEY_SIGN_DAYS, PlotRead.getAppUser().signDays);
                        SharedPreferencesUtil.putString(PlotRead.getAppUser().config, KEY_SIGN_DATE, PlotRead.getAppUser().signDate);
                        // 发送签到通知
                        Message message = Message.obtain();
                        message.what = BUS_USER_SIGN_STATE_CHANGE;
                        EventBus.getDefault().post(message);
                        // 发送余额变化通知
                        Message msg = Message.obtain();
                        msg.what = BUS_MONEY_CHANGE;
                        EventBus.getDefault().post(msg);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(SignAndWelfareActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @OnPageChange(R.id.viewPager)
    void onPagerSelect(int position) {
        mScrollLayout.getHelper().setCurrentScrollParent(fragments.get(position));
    }

    @OnClick(R.id.sign)
    void onSignClick() {
        fetchSign();
    }

    @OnClick(R.id.signRule)
    void onSignRuleClick() {
        Intent intent = new Intent(getBaseContext(), SignRuleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.levelInfo)
    void onLevelInfoClick() {
        Intent intent = new Intent(getBaseContext(), LevelRuleActivity.class);
        startActivity(intent);
    }

    /**
     * 任务ViewPager适配器
     *
     * @author haojie
     */
    private class TaskPagerAdapter extends FragmentPagerAdapter {

        TaskPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return pagerTitles.length;
        }
    }
}
