package life.forever.cf.adapter.person.readingmsg;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import life.forever.cf.R;
import life.forever.cf.publics.BaseFragmentActivity;
import life.forever.cf.publics.BaseRecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageActivity extends BaseFragmentActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;


    final List<BaseRecyclerViewFragment> fragments = new ArrayList<>();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MINE_STRING_MY_MESSAGE);
        mTitleBar.showDivider(FALSE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
//        fragments.add(new MessageReplyMeFragment());
//        fragments.add(new MessageLikeMeFragment());
        fragments.add(new MessageSystemFragment());
//        mViewPager.setOffscreenPageLimit(pagerTitles.length - ONE);
        mViewPager.setAdapter(new MessagePagerAdapter(getSupportFragmentManager()));
//        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

//    private void initIndicator() {
//        CommonNavigator commonNavigator = new CommonNavigator(getBaseContext());
//        commonNavigator.setScrollPivotX(DOT_FIVE);
//        commonNavigator.setAdjustMode(true);
//        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
//
//            @Override
//            public int getCount() {
//                return pagerTitles == null ? ZERO : pagerTitles.length;
//            }
//
//            @Override
//            public IPagerTitleView getTitleView(Context context, final int index) {
//                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
//                simplePagerTitleView.setText(pagerTitles[index]);
//                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FIFTEEN);
//                simplePagerTitleView.setNormalColor(DARK_2);
//                simplePagerTitleView.setSelectedColor(THEME_COLOR);
//                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mViewPager.setCurrentItem(index);
//                    }
//                });
//                return simplePagerTitleView;
//            }
//
//            @Override
//            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                indicator.setLineHeight(DisplayUtil.dp2px(context, TWO));
//                indicator.setLineWidth(DisplayUtil.dp2px(context, FOURTEEN));
//                indicator.setRoundRadius(DisplayUtil.dp2px(context, ONE));
//                indicator.setStartInterpolator(new AccelerateInterpolator());
//                indicator.setEndInterpolator(new DecelerateInterpolator(TWO));
//                indicator.setColors(THEME_COLOR);
//                return indicator;
//            }
//        });
//        mMagicIndicator.setNavigator(commonNavigator);
//    }

    class MessagePagerAdapter extends FragmentPagerAdapter {

        MessagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
