package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BillBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.BillAdapter;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuanlong on 2020/12/10.
 * 充值记录之
 */
public class BillRechargeFragment extends BaseFragment {

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.noneView)
    View mNoneView;

    private int pageIndex = ONE;
    private int totalPage = ZERO;
    private BillAdapter mBillAdapter;
    private List<BillBean.ResultData.Lists> mBillList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private static BillRechargeFragment instance;

    public static BillRechargeFragment get() {
        instance = new BillRechargeFragment();
        return instance;
    }

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_bill_recharge, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void fetchData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // 初始化适配器
        mBillAdapter = new BillAdapter(getActivity(), mBillList, ZERO);
        mRecyclerView.setAdapter(mBillAdapter);
        // 初始化数据
        topupExpend();
    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (mBillList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载更多监听
     */
    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
//            pageIndex = ONE;
//            totalPage = ZERO;
            topupExpend();
        }
    };

    /*
     * 请求收入列表数据
     */
    private void topupExpend() {
        NetRequest.topupExpend(ZERO, pageIndex, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                    }
                    try {
                        if (pageIndex == ONE) {
                            int count = JSONUtil.getInt(result, "count");
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + 1;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }

                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String strOrders = jsonOders.getString("lists");

                        Type listType = new TypeToken<List<BillBean.ResultData.Lists>>() {
                        }.getType();
                        Gson gson = new Gson();
                        List<BillBean.ResultData.Lists> List = gson.fromJson(strOrders, listType);

                        if (mBillAdapter == null) {
                            mBillList = List;
                            mBillAdapter.data(mBillList, ZERO);
                        } else {
                            mBillList.addAll(List);
                            mBillAdapter.data(mBillList, ZERO);
                        }
                        switchPageBySize();
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
