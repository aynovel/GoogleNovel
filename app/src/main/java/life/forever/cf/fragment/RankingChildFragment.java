package life.forever.cf.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.adapter.RankWorkAdapter;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.CycleType;
import life.forever.cf.entry.RankType;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.RefreshHeaderView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.RankHintDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RankingChildFragment extends BaseFragment {

    private static final CycleType[] CYCLE_TYPE = {new CycleType(ONE, RANKING_STRING_DAY_LIST),
            new CycleType(TWO, RANKING_STRING_WEEK_LIST),
            new CycleType(THREE, RANKING_STRING_MONTH_LIST),
            new CycleType(FOUR, RANKING_STRING_TOTAL_LIST)};

    private RankType HRRankType;
    private int cycleId;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @BindView(R.id.textView)
    TextView mCycleText;

    @BindView(R.id.tv_type)
    TextView tv_type;

    @BindView(R.id.iv_hint)
    ImageView iv_hint;

    @BindView(R.id.cycleSelector)
    RelativeLayout mCycleSelector;

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.refreshHeader)
    RefreshHeaderView mRefreshHeader;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    static boolean isShows = true;

    private final List<CycleType> HRCycleTypes = new ArrayList<>();
    private final List<Work> HRWorks = new ArrayList<>();
    private RankWorkAdapter HRRankWorkAdapter;

    static int scrolly = 0;

    /**
     * 获取fragment实例
     *
     * @param HRRankType 榜单类型
     * @param cycleId    默认周期
     */
    public static RankingChildFragment get(RankType HRRankType, int cycleId) {
        RankingChildFragment instance = new RankingChildFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("rankType", HRRankType);
        bundle.putInt("cycleId", cycleId);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    protected void bindView() {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_rank_child, mContentLayout, TRUE);
        ButterKnife.bind(this, view);
        mTitleBar.setVisibility(View.GONE);
//        mRefreshLayout.setHasHeader(FALSE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRefreshHeader.setOnRefreshListener(onRefreshListener);
        mLoadFooter.setOnLoadListener(onLoadListener);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolly = scrolly + dy;
                if (scrolly != 0 || scrolly > 700 && scrolly < -700) {
                    //下滑监听
                    Message message = Message.obtain();
                    message.what = DISCOVERTABC;
                    EventBus.getDefault().post(message);
                } else {
                    Message message = Message.obtain();
                    message.what = DISCOVERTABN;
                    EventBus.getDefault().post(message);
                }
            }
        });
    }

    private final BaseFooterView.OnLoadListener onLoadListener = baseFooterView -> rankList();

    @OnClick(R.id.iv_hint)
    void onTvhintClick() {
        RankHintDialog mRankHintDialog = new RankHintDialog(getActivity(), HRRankType.desc);
        mRankHintDialog.show();
        Window dialogWindow = mRankHintDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.TOP);
        }
    }

    public static void Refresh() {
        scrolly = 0;
//        if (isShows) {
//            isShows = false;
//        }
    }

//    @OnClick(R.id.cycleSelector)
//    void onCycleSelectorClick() {
//        if (cycleSelectPop == null) {
//            String[] titles = new String[HRCycleTypes.size()];
//            for (int i = ZERO; i < HRCycleTypes.size(); i++) {
//                titles[i] = HRCycleTypes.get(i).title;
//            }
//            cycleSelectPop = new aiyeBottomSheet(getActivity(), titles, new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    cycleSelectPop.dismiss();
//                    HR_CycleType type = HRCycleTypes.get(position);
//                    mCycleText.setText(type.title);
//                    cycleId = type.id;
//
//                    pageIndex = ONE;
//                    totalPage = ZERO;
//                    rankList();
//                }
//            });
//            cycleSelectPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//                @Override
//                public void onDismiss() {
//                    mCycleText.setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, R.drawable.aiye_black_arrow_icon_down, ZERO);
//                }
//            });
//        }
//        cycleSelectPop.show(mTitleBar);
//        mCycleText.setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, R.drawable.aiye_black_arrow_icon_up, ZERO);
//    }

    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            HRRankType = bundle.getParcelable("rankType");
            cycleId = bundle.getInt("cycleId", ZERO);
            tv_type.setText(HRRankType.title);
        }
        HRRankWorkAdapter = new RankWorkAdapter(context, HRWorks, HRRankType.icon_type);
        rankList();
        mCycleSelector.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(HRRankWorkAdapter);
    }

    /**
     * 刷新监听
     */
    private final BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
            pageIndex = ONE;
            totalPage = ZERO;
            rankList();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case BUS_LOG_IN:
                HRWorks.clear();
                HRRankWorkAdapter.notifyDataSetChanged();
                reload();
                break;
            case DISCOVERTAB:
            case RANKLEFTTAB:
                scrolly = 0;
                mRecyclerView.scrollToPosition(0);
                message.what = DISCOVERTABN;
                EventBus.getDefault().post(message);
                break;
            case RANKTOPTAB:
                if (scrolly != 0 || scrolly > 700 && scrolly < -700) {
                    message.what = DISCOVERTABC;
                } else {
                    message.what = DISCOVERTABN;
                }
                EventBus.getDefault().post(message);
                break;

        }
    }

    private void rankList() {
        NetRequest.rankList(HRRankType.pageId, HRRankType.id, cycleId, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.stopRefresh();
                    }
                    mRefreshLayout.setHasHeader(TRUE);
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int total = JSONUtil.getInt(result, "total");
                    if (pageIndex == ONE && totalPage == ZERO) {
                        totalPage = total % TWENTY == ZERO ? total / TWENTY : total / TWENTY + ONE;
                        mRefreshLayout.setHasFooter(totalPage > ONE);
                        HRWorks.clear();
                        HRCycleTypes.clear();
                    }
                    JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                    for (int i = ZERO; lists != null && i < lists.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(lists, i);
                        HRWorks.add(BeanParser.getWork(child));
                    }
                    HRRankWorkAdapter.notifyDataSetChanged();
                    if (HRCycleTypes.isEmpty()) {
                        JSONArray cyclelists = JSONUtil.getJSONArray(result, "cycle_type");
                        for (int i = ZERO; cyclelists != null && i < cyclelists.length(); i++) {
                            for (CycleType type : CYCLE_TYPE) {
                                if (JSONUtil.getInt(cyclelists, i) == type.id) {
                                    HRCycleTypes.add(type);
                                    break;
                                }
                            }
                        }
                        // 设置默认周期类型
                        for (int i = ZERO; i < HRCycleTypes.size(); i++) {
                            if (cycleId == HRCycleTypes.get(i).id) {
                                mCycleText.setText(HRCycleTypes.get(i).title);
                                break;
                            }
                        }
                    }
                    pageIndex++;
                    mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.stopRefresh();
                    PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        pageIndex = ONE;
        totalPage = ZERO;
        rankList();
    }
}
