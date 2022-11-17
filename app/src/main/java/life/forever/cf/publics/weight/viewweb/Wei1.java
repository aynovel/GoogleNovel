package life.forever.cf.publics.weight.viewweb;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.tool.DisplayUtil;


public class Wei1 extends FrameLayout implements Constant {

    LinearLayout mRefreshLayout;
    Ayes mWebView;
    ProgressBar mProgressBar;

    boolean refreshEnable;

    public Wei1(@NonNull Context context) {
        this(context, null);
    }

    public Wei1(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRefreshLayout = new LinearLayout(getContext());
        mRefreshLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mWebView = new Ayes(getContext());
        mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mRefreshLayout.addView(mWebView);
        this.addView(mRefreshLayout);

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.ress_drawable));
        mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(context, ONE)));
        mProgressBar.setMax(ONE_HUNDRED);
        this.addView(mProgressBar);
        mProgressBar.setVisibility(GONE);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public void setWebViewClient(WebViewClient client) {
        mWebView.setWebViewClient(client);
    }

    public void setWebChromeClient(WebChromeClient client) {
        mWebView.setWebChromeClient(client);
    }

    public void setProgress(int newProgress) {
        mProgressBar.setProgress(newProgress);
        if (newProgress == ONE_HUNDRED) {
            if (mProgressBar.getVisibility() == VISIBLE) {
                mProgressBar.setVisibility(GONE);
            }
            if (refreshEnable) {
            }
        } else {
            if (mProgressBar.getVisibility() == GONE) {
                mProgressBar.setVisibility(VISIBLE);
            }
        }
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    public void setRefreshListener(BaseHeaderView.OnRefreshListener onRefreshListener) {
    }

//    public boolean isRefreshing() {
//        return mRefreshLayout.isRefreshing();
//    }
//
//    public void stopRefresh() {
//        mRefreshLayout.stopRefresh();
//    }

    public JsAndroid1 getJsAndroid() {
        return mWebView.getJsAndroid();
    }

    public void setJsAndroid(JsAndroid1 jsAndroid) {
        mWebView.setJsAndroid(jsAndroid);
    }

    public void reload() {
        mWebView.reload();
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }
    public Ayes getWebview() {
        if (mWebView == null){
            mWebView = new Ayes(getContext());
            mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        return mWebView;
    }
}
