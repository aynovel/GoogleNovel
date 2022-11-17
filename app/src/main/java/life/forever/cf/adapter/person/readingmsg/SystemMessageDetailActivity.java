package life.forever.cf.adapter.person.readingmsg;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseWebViewActivity;


public class SystemMessageDetailActivity extends BaseWebViewActivity {

    private String index;
    private String path;

    @Override
    protected void initializeView() {
        super.initializeView();
        mWebView.setRefreshEnable(FALSE);
        mTitleBar.setMiddleText(MINE_STRING_MESSAGE_SYSTEM);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
        index = getIntent().getStringExtra("index");
        path = getIntent().getStringExtra("path");
        loadUrl();
    }

    private void loadUrl() {
        String url = index + NetRequest.path(path, BLANK);
        mWebView.loadUrl(url);
    }

    private final WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            mLoadingLayout.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            mWrongLayout.setVisibility(View.VISIBLE);
        }
    };

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mWebView.setProgress(newProgress);
        }
    };

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        loadUrl();
    }

}
