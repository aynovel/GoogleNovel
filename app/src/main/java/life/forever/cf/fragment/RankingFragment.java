package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.adapter.RankTypeAdapter;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.RankType;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.OnItemClickListener;
import life.forever.cf.publics.cantview.CantSlideViewPager;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    private static RankingFragment instance;

    public static RankingFragment get() {
        instance = new RankingFragment();
        return instance;
    }

    private int pageId;
    private int rankId;
    private int cycleId;
    private final List<RankType> types = new ArrayList<>();
    private RankTypeAdapter mRankTypeAdapter;
    private final List<RankingChildFragment> fragments = new ArrayList<>();

    @BindView(R.id.layout_rank)
    LinearLayout mLayoutRank;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.viewPager)
    CantSlideViewPager mViewPager;

    @BindView(R.id.noneView)
    View mNoneView;

//    static int discovers = 0;

    @Override
    protected void bindView() {
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_ranking, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mTitleBar.setVisibility(View.GONE);
        mNoneView.setOnClickListener(onWrongClick);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ScreenUtil.setStatusBarDark(getActivity(), true);
    }

    @Override
    protected void fetchData() {
//        Bundle bundle = getArguments();
        pageId = TWO;
        rankId = ONE;
        cycleId = ONE;

        mRankTypeAdapter = new RankTypeAdapter(context, types, onItemClick);
        mRecyclerView.setAdapter(mRankTypeAdapter);
        getRankType();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getRankType() {
        NetRequest.rankType(pageId, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray typelist = JSONUtil.getJSONArray(result, "typelist");
                    for (int i = ZERO; typelist != null && i < typelist.length(); i++) {
                        RankType HRRankType = BeanParser.getRankType(JSONUtil.getJSONObject(typelist, i));
                        types.add(HRRankType);
                    }
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                    mLayoutRank.setVisibility(View.VISIBLE);
                    mNoneView.setVisibility(View.GONE);

                    for (RankType HRRankType : types) {
                        if (HRRankType.pageId == pageId && HRRankType.id == rankId) {
                            fragments.add(RankingChildFragment.get(HRRankType, cycleId));
                        } else {
                            fragments.add(RankingChildFragment.get(HRRankType, ZERO));
                        }
                    }
                    mViewPager.setOffscreenPageLimit(fragments.size() - ONE);
                    if (!isAdded()) return;
                    mViewPager.setAdapter(new RankDetailPagerAdapter(getChildFragmentManager()));

                    // 设置默认榜单
                    int current = ZERO;
                    for (int i = ZERO; i < types.size(); i++) {
                        if (types.get(i).id == rankId) {
                            current = i;
                            break;
                        }
                    }
                    mRankTypeAdapter.update(current);
                    mViewPager.setCurrentItem(current, FALSE);
                    if (types.size() > 0) {
                        DeepLinkUtil.addPermanent(context, "event_discover_rank", "首页/发现页推荐", "内页榜单页:listid=" + types.get(current).id, "", "", "", "", "", "");
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                mLoadingLayout.setVisibility(View.GONE);
                mLayoutRank.setVisibility(View.GONE);
                mNoneView.setVisibility(View.VISIBLE);
//                mWrongLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private final View.OnClickListener onWrongClick = view -> {
        mLoadingLayout.setVisibility(View.VISIBLE);
//        mContentLayout.setVisibility(View.GONE);
//        mWrongLayout.setVisibility(View.GONE);
        getRankType();
    };

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        getRankType();
    }

    private final OnItemClickListener onItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            mRankTypeAdapter.update(position);
            mViewPager.setCurrentItem(position, FALSE);
            DeepLinkUtil.addPermanent(context, "event_discover_rank", "首页/发现页推荐", "内页榜单页:listid=" + types.get(position).id, "", "", "", "", "", "");
            RankingChildFragment.Refresh();
            Message message = Message.obtain();
            message.what = RANKLEFTTAB;
            EventBus.getDefault().post(message);
        }
    };

    private class RankDetailPagerAdapter extends FragmentPagerAdapter {

        RankDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        ScreenUtil.setStatusBarDark(getActivity(), true);
    }
}
