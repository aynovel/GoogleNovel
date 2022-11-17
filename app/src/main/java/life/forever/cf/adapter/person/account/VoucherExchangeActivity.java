package life.forever.cf.adapter.person.account;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VoucherExchangeActivity extends BaseActivity {

    @BindView(R.id.code)
    EditText mCode;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_voucher_exchange);
        ButterKnife.bind(this);
        mTitleBar.showDivider(FALSE);
        mTitleBar.setMiddleText(ACCOUNT_STRING_VOUCHER_EXCHANGE);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @OnClick(R.id.exchange)
    void onExchangeClick() {
        String code = mCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            PlotRead.toast(PlotRead.INFO, "请输入兑换码");
            return;
        }

        showLoading("正在兑换");
        NetRequest.voucherExchange(code, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String severNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(severNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        // 刷新余额
                        PlotRead.getAppUser().fetchUserMoney();
                        // 发出通知，刷新书券列表
                        Message message = Message.obtain();
                        message.what = BUS_VOUCHER_EXCHANGE_SUCCESS;
                        EventBus.getDefault().post(message);

                        PlotRead.toast(PlotRead.SUCCESS, "兑换成功");
                        onBackPressed();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(VoucherExchangeActivity.this, severNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, "兑换失败");
            }
        });
    }

    @Override
    protected void initializeData() {

    }
}
