package life.forever.cf.adapter.person.readinglevel;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseWebViewFragment;


public class NormalLevelRuleFragment extends BaseWebViewFragment {

    @Override
    protected void bindView() {
        super.bindView();
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mWebView.setRefreshEnable(FALSE);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
    }

    @Override
    protected void fetchData() {
        loadUrl();
    }

    private void loadUrl() {
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_NORMAL_LEVEL, BLANK);
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
