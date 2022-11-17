package life.forever.cf.adapter.person.account;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Voucher;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;

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


public class VoucherActivity extends BaseActivity {

    @BindView(R.id.voucherValue)
    TextView mVoucherValue;
    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    List<Voucher> vouchers = new ArrayList<>();
    private VoucherAdapter voucherAdapter;
    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);
        mTitleBar.showDivider(FALSE);
        mTitleBar.setMiddleText(ACCOUNT_STRING_MY_VOUCHER);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        mVoucherValue.setText(String.valueOf(PlotRead.getAppUser().voucher));
        voucherAdapter = new VoucherAdapter(context, vouchers);
        mRecyclerView.setAdapter(voucherAdapter);
        fetchVoucher();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_VOUCHER_EXCHANGE_SUCCESS) {
            pageIndex = ONE;
            totalPage = ZERO;
            fetchVoucher();
            return;
        }
        if (message.what == BUS_MONEY_CHANGE) {
            mVoucherValue.setText(String.valueOf(PlotRead.getAppUser().voucher));
            return;
        }
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
        pageIndex = ONE;
        totalPage = ZERO;
        fetchVoucher();
    }

    /**
     * 请求书券列表
     */
    private void fetchVoucher() {
        NetRequest.voucherList(ONE, pageIndex, new OkHttpResult() {

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
                            // 刷新书豆余额
                            int voucher = JSONUtil.getInt(result, "voucher");
                            PlotRead.getAppUser().voucher = voucher;
                            // 保存信息
                            SharedPreferencesUtil.putInt(PlotRead.getAppUser().config, KEY_VOUCHER, voucher);
                            // 发送余额变化通知
                            Message message = Message.obtain();
                            message.what = BUS_MONEY_CHANGE;
                            EventBus.getDefault().post(message);
                            PlotRead.getAppUser().voucher = voucher;
                            // 加载第一页时，清空数据
                            vouchers.clear();
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            vouchers.add(BeanParser.getVoucher(JSONUtil.getJSONObject(lists, i)));
                        }
                        voucherAdapter.notifyDataSetChanged();
                        mContentLayout.setVisibility(View.VISIBLE);
                        mLoadingLayout.setVisibility(View.GONE);
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                            PlotRead.toast(PlotRead.FAIL, msg);
                        } else {
                            PlotRead.toast(PlotRead.FAIL, msg);
                            onBackPressed();
                        }
                    }
                } else {
                    NetRequest.error(VoucherActivity.this, serverNo);
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                    PlotRead.toast(PlotRead.FAIL, "加载失败");
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetchVoucher();
        }
    };

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @OnClick(R.id.exchange)
    void onExchangeClick() {
        Intent intent = new Intent(context, VoucherExchangeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.voucherHelp)
    void onVoucherHelpClick() {
        Intent intent = new Intent(context, VoucherHelpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.overdueVoucher)
    void onOverdueVoucherClick() {
        Intent intent = new Intent(context, OverdueVoucherActivity.class);
        startActivity(intent);
    }
}
