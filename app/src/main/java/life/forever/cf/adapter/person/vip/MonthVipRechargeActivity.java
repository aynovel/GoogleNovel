package life.forever.cf.adapter.person.vip;

import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.person.pay.RechargeCallback;
import life.forever.cf.publics.BaseWebViewActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;


public class MonthVipRechargeActivity extends BaseWebViewActivity {

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MONTH_STRING_OPEN_MONTH_VIP);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setRefreshEnable(FALSE);
        mWebView.getJsAndroid().setRechargeCallback(rechargeCallback);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        loadUrl();
    }

    private void loadUrl() {
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_RECHARGE_MONTH_VIP, BLANK);
        mWebView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_WX_PAY_RESULT) {
            int code = (int) message.obj;
            if (code == ZERO) {
                // 请求用户信息
                PlotRead.getAppUser().fetchUserInfo(MonthVipRechargeActivity.this);
                // 发送包月成功的通知
                Message msg = Message.obtain();
                msg.what = BUS_MONTH_PAY_SUCCESS;
                EventBus.getDefault().post(msg);
                PlotRead.toast(PlotRead.SUCCESS, getString(R.string.monthly_success));
                onBackPressed();
            } else {
                PlotRead.toast(PlotRead.INFO, getString(R.string.buy_fail));
            }
            return;
        }
        if (message.what == BUS_ALI_PAY_RESULT) {
            int code = (int) message.obj;
            if (code == ONE) {
                // 请求用户信息
                PlotRead.getAppUser().fetchUserInfo(MonthVipRechargeActivity.this);
                // 发送包月成功的通知
                Message msg = Message.obtain();
                msg.what = BUS_MONTH_PAY_SUCCESS;
                EventBus.getDefault().post(msg);
                PlotRead.toast(PlotRead.SUCCESS, getString(R.string.monthly_success));
                onBackPressed();
            } else {
                PlotRead.toast(PlotRead.INFO, getString(R.string.buy_fail));
            }
            return;
        }
        if (message.what == BUS_LOG_IN) {
            loadUrl();
        }
    }

    /**
     * 充值回调
     */
    private final RechargeCallback rechargeCallback = new RechargeCallback() {

        @Override
        public void onResult(int channel, int channel_child, int ruleId,String iapid, double cash, int counts, JSONObject custom) {
            if (channel == ONE) { // 微信
//                wxPay(cash, counts, ruleId, custom);
            } else if (channel == THREE) { // 余额
                moneyPay((int) cash, counts, ruleId, custom);
            }
        }
    };



    /**
     * 余额充值
     */
    private void moneyPay(int cash, int counts, int ruleId, JSONObject custom) {
        showLoading(getString(R.string.buging_to));
        NetRequest.monthPayByMoney(cash, counts, ruleId, custom, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        // 请求用户信息
                        PlotRead.getAppUser().fetchUserInfo(MonthVipRechargeActivity.this);
                        // 发送包月成功的通知
                        Message msg = Message.obtain();
                        msg.what = BUS_MONTH_PAY_SUCCESS;
                        EventBus.getDefault().post(msg);
                        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.monthly_success));
                        onBackPressed();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(MonthVipRechargeActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mWebView.setProgress(newProgress);
        }
    };

    private final WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            mLoadingLayout.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWrongLayout.setVisibility(View.VISIBLE);
        }

    };

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        loadUrl();
    }
}
