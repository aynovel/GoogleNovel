package life.forever.cf.activtiy;

import android.content.Context;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import life.forever.cf.R;
import life.forever.cf.fragment.BillExpensesFragment;
import life.forever.cf.fragment.BillRechargeFragment;
import life.forever.cf.tab.MagicIndicator;
import life.forever.cf.tab.ViewPagerHelper;
import life.forever.cf.tab.buildins.commonnavigator.CommonNavigator;
import life.forever.cf.tab.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerIndicator;
import life.forever.cf.tab.buildins.commonnavigator.abs.IPagerTitleView;
import life.forever.cf.tab.buildins.commonnavigator.indicators.LinePagerIndicator;
import life.forever.cf.tab.buildins.commonnavigator.titles.SimplePagerTitleView;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.tool.DisplayUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuanlong on 2020/12/10.
 * 充值记录
 */
public class BillDetailsActivity extends BaseActivity {

    @BindView(R.id.magicIndicator)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private final String[] pagerTitles = {BILL_RECEIVED, BILL_CONSUMED};
    private final List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText("Bill details");
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_bill_details);
        ButterKnife.bind(this);
        initIndicator();
    }

    private void initIndicator() {
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
                simplePagerTitleView.setNormalColor(BILL_COLOR_NO);
                simplePagerTitleView.setSelectedColor(BILL_COLOR);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index, FALSE);
                    }
                });
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
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        fragments.add(BillRechargeFragment.get());
        fragments.add(BillExpensesFragment.get());
        mViewPager.setOffscreenPageLimit(fragments.size() - 1);
        mViewPager.setAdapter(new RecommendPagerAdapter(getSupportFragmentManager()));
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    private class RecommendPagerAdapter extends FragmentPagerAdapter {

        RecommendPagerAdapter(FragmentManager fm) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
