package life.forever.cf.activtiy;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.R;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseWebViewActivity;

public class FansRuleActivity extends BaseWebViewActivity {

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(aiye_STRING_FANS_RULE);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
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
        loadUrl();
    }

    private void loadUrl() {
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_FANS_RULE, BLANK);
        mWebView.loadUrl(url);
    }

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

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mWebView.setProgress(newProgress);
        }
    };

}
