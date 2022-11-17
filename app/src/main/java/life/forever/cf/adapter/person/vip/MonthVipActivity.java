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
import life.forever.cf.publics.BaseWebViewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MonthVipActivity extends BaseWebViewActivity {

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setMiddleText(MONTH_STRING_MY_MONTH_VIP);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setRefreshEnable(FALSE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_MONTH_PAY_SUCCESS || message.what == BUS_LOG_IN) {
            loadUrl();
        }
    }

    private void loadUrl() {
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_MONTH_VIP_INFO, BLANK);
        mWebView.loadUrl(url);
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
