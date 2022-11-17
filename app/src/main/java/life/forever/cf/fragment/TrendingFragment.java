package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.TrendingsAdapter;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.fresh.FeaturedRefreshHeaderView;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class TrendingFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    private static TrendingFragment instance;
    static FeaturedFragment.ICallBackListener mICallBackListener;
    static FeaturedFragment.ILoadingListener mILoadingListener;

    public static TrendingFragment get(FeaturedFragment.ICallBackListener iCallBackListener, FeaturedFragment.ILoadingListener iILoadingListener) {
        instance = new TrendingFragment();
        mICallBackListener = iCallBackListener;
        mILoadingListener = iILoadingListener;
        return instance;
    }


    static PullRefreshLayout mRefreshLayout;
    static RecyclerView mRecyclerView;

    @BindView(R.id.refreshHeader)
    FeaturedRefreshHeaderView mRefreshHeader;

    @BindView(R.id.noneView)
    View mNoneView;

    private TrendingsAdapter mTrendingAdapter;
    List<DiscoverBean.ResultData> mContactList;
    List<DiscoverBean.ResultData> contactList = new ArrayList<>();

    static boolean isShows;


    static int scrolly = 0;
    View getHeightView;

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_discover, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mRefreshLayout = root.findViewById(R.id.refreshLayout);
        mRefreshHeader.setOnRefreshListener(onRefreshListener);
        mRecyclerView = root.findViewById(R.id.rcv_content);
        ScreenUtil.setStatusBarDark(getActivity(), true);
        mWrongLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWrongLayout.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                discover(true);
            }
        });
        getHeightView = new View(context);
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
                    if (isShows) {
                        Message message = Message.obtain();
                        message.what = DISCOVERTABN;
                        EventBus.getDefault().post(message);
                        isShows = true;
                    }
                }

                getHeightView.scrollBy(0, dy);

                mICallBackListener.onScrollClick(getHeightView.getScrollY());
            }
        });

    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize(boolean isShow) {
        scrolly = 0;
        isShows = isShow;
        if (contactList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
            Message message = Message.obtain();
            message.what = DISCOVERTABC;
            EventBus.getDefault().post(message);
        } else {
            mNoneView.setVisibility(View.GONE);
            Message message = Message.obtain();
            message.what = DISCOVERTABN;
            EventBus.getDefault().post(message);
            isShows = true;
        }
    }

    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        linearLayoutManager.setInitialPrefetchItemCount(3);
        mRecyclerView.setItemAnimator(null);

        mTrendingAdapter = new TrendingsAdapter(getActivity(), mContactList);
        mRecyclerView.setAdapter(mTrendingAdapter);
        discover(true);
    }

    public static void Refresh(int discover) {
        if (discover == 1) {
            scrolly = 0;
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.startRefresh();
        }
        if (isShows) {
            isShows = false;
        }
    }

    /**
     * 请求发现数据
     */
    private void discover(boolean isShow) {
        NetRequest.trendingRequest(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        if (mRefreshLayout == null) {
                            return;
                        }
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                        }
                        mRefreshLayout.setHasHeader(TRUE);
                        mLoadingLayout.setVisibility(View.GONE);
                        mContentLayout.setVisibility(View.VISIBLE);
                        mWrongLayout.setVisibility(View.GONE);
                        if (mILoadingListener != null) {
                            mILoadingListener.dis();
                        }

                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONArray arr = new JSONArray(resultString);
                        if (mTrendingAdapter == null) {
                            return;
                        }
                        mTrendingAdapter.update();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = (JSONObject) arr.get(i);
                            String type = temp.getString("type");
                            mTrendingAdapter.add(type);
                        }
                        String strResult = jsonObject.getString("ResultData");
                        Gson gson = new Gson();
                        contactList = gson.fromJson(strResult, new TypeToken<List<DiscoverBean.ResultData>>() {
                        }.getType());
                        switchPageBySize(isShow);
                        mTrendingAdapter.data(contactList);
                    } catch (JSONException e) {
                        dismissLoading();
                        e.printStackTrace();
                    }

                } else {
                    if (mLoadingLayout == null) {
                        return;
                    }
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                    mILoadingListener.show();
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                switchPageBySize(isShow);
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.stopRefresh();
                }
                mLoadingLayout.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.GONE);
                mWrongLayout.setVisibility(View.VISIBLE);
                mILoadingListener.show();
//                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == TRENDINGTOPTAB) {
            if (scrolly != 0 || scrolly > 700 && scrolly < -700) {
                message.what = DISCOVERTABC;
            } else {
                message.what = DISCOVERTABN;
            }
            EventBus.getDefault().post(message);
        }
    }

    /**
     * 刷新监听
     */
    private final BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
            if (mContactList != null) {
                mContactList.clear();
            }
            discover(false);
            if (mICallBackListener != null) {

                FeaturedFragment.mTrendRecordHeight = 0;
                getHeightView.scrollBy(0, 0);
                getHeightView.setScrollY(0);
                mICallBackListener.onScrollClick(0);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        ScreenUtil.setStatusBarDark(getActivity(), true);
    }
}
