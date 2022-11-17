package life.forever.cf.adapter.person.readinglevel;

import android.view.View;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import life.forever.cf.R;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.BaseWebViewFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnPageChange;


public class LevelRuleActivity extends BaseActivity {

    @BindView(R.id.normalLevel)
    RadioButton mNormalLevel;
    @BindView(R.id.vipLevel)
    RadioButton mVipLevel;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private final BaseWebViewFragment[] fragments = new BaseWebViewFragment[TWO];

    @Override
    protected void initializeView() {
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_level_rule);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        fragments[ZERO] = new NormalLevelRuleFragment();
        fragments[ONE] = new VipLevelRuleFragment();
        mViewPager.setAdapter(new LevelRulePagerAdapter(getSupportFragmentManager()));
        mNormalLevel.setChecked(TRUE);
    }

    @OnClick(R.id.back)
    void onBackClick() {
        onBackPressed();
    }

    @OnCheckedChanged(R.id.normalLevel)
    void onNormalLevelChange(boolean check) {
        if (check) {
            mViewPager.setCurrentItem(ZERO, FALSE);
        }
    }

    @OnCheckedChanged(R.id.vipLevel)
    void onVipLevelChange(boolean check) {
        if (check) {
            mViewPager.setCurrentItem(ONE, FALSE);
        }
    }

    @OnPageChange(R.id.viewPager)
    void onPageSelected(int position) {
        if (position == ZERO) {
            mNormalLevel.setChecked(TRUE);
        } else {
            mVipLevel.setChecked(TRUE);
        }
    }

    /**
     * 等级规则页面适配器
     *
     * @author haojie
     */
    private class LevelRulePagerAdapter extends FragmentPagerAdapter {

        public LevelRulePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
