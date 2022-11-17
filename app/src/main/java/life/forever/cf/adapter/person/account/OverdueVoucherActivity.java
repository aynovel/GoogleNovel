package life.forever.cf.adapter.person.account;

import android.os.Message;
import android.view.View;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Voucher;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OverdueVoucherActivity extends BaseRecyclerViewActivity {

    private final List<Voucher> vouchers = new ArrayList<>();
    private VoucherAdapter voucherAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.showDivider(FALSE);
        mTitleBar.setMiddleText(ACCOUNT_STRING_OVERDUE_VOUCHER);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mContentLayout.setBackgroundColor(GRAY_4);
        mRecyclerView.setPadding(ZERO, DisplayUtil.dp2px(context, TWENTY), ZERO, ZERO);
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetchVoucher();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        voucherAdapter = new VoucherAdapter(context, vouchers);
        mRecyclerView.setAdapter(voucherAdapter);
        fetchVoucher();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            vouchers.clear();
            voucherAdapter.notifyDataSetChanged();
            reload();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        // 初始化数据
        pageIndex = ONE;
        totalPage = ZERO;
        vouchers.clear();
        voucherAdapter.notifyDataSetChanged();
        // 请求数据
        fetchVoucher();
    }

    /**
     * 请求书券列表
     */
    private void fetchVoucher() {
        NetRequest.voucherList(TWO, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (pageIndex == ONE && totalPage == ZERO) {
                            int count = JSONUtil.getInt(result, "count");
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + ONE;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            vouchers.add(BeanParser.getVoucher(JSONUtil.getJSONObject(lists, i)));
                        }
                        voucherAdapter.notifyDataSetChanged();
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        } else {
                            mContentLayout.setVisibility(View.VISIBLE);
                            mLoadingLayout.setVisibility(View.GONE);
                        }
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.FAIL, msg);
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        } else {
                            onBackPressed();
                        }
                    }
                } else {
                    NetRequest.error(OverdueVoucherActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
