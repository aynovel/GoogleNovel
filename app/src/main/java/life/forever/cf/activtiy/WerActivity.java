package life.forever.cf.activtiy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.R;
import life.forever.cf.entry.ShareType;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseWebViewActivity;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.tool.LOG;
import life.forever.cf.publics.tool.BDeepLinkUtil;
import life.forever.cf.publics.weight.poputil.SharePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.media.UMWeb;

/**
 * 通用Web二级页
 *
 * @author haojie
 *         Created on 2018/3/12.
 */
public class WerActivity extends BaseWebViewActivity {

    private String index;
    private String path;
    private String shareUrl;
    private int shareType;
    private boolean sharefresh;
    private String shareTitle;
    private String shareDesc;
    private String shareImg;

    private String promotionUrl;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setRightImageResource(R.drawable.re_icon_gray);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setRefreshListener(onRefreshListener);
        mTitleBar.setRightImageViewOnClickListener(onShareClick);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        Intent data = getIntent();
        index = data.getStringExtra("index");
        path = data.getStringExtra("path");
        promotionUrl = data.getStringExtra("promotionUrl");
        boolean pagefresh = data.getBooleanExtra("pagefresh", FALSE);
        boolean share = data.getBooleanExtra("share", FALSE);
        if (share) {
            shareUrl = data.getStringExtra("shareUrl");
            shareType = data.getIntExtra("shareType", ShareType.DEFAULT_SHARE);
            sharefresh = data.getBooleanExtra("sharefresh", FALSE);
            shareTitle = data.getStringExtra("shareTitle");
            shareDesc = data.getStringExtra("shareDesc");
            shareImg = data.getStringExtra("shareImg");
        }

        mWebView.setRefreshEnable(pagefresh);
        mTitleBar.getRightImageView().setVisibility(share ? View.VISIBLE : View.GONE);
        loadUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN
                || message.what == BUS_LOG_OUT
                || message.what == BUS_MONEY_CHANGE) {
            loadUrl();
        }
    }

    private void loadUrl() {
        String url = index + NetRequest.path(path, BLANK);
        if(index.length() <=0 && path.length()<=0)
        {
            url = promotionUrl + NetRequest.path("", BLANK);;

            if(url != null && (url.startsWith("http://")||url.startsWith("https://")))
            {
            }else{
                PlotRead.toast(PlotRead.FAIL,"URL error:" + url);
            }
        }
        mWebView.loadUrl(url);
    }

    private BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
            loadUrl();
        }
    };

    private View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            int activities = am.getRunningTasks(ONE).get(ZERO).numRunning;
            LOG.i(getClass().getSimpleName(), "numActivities = " + activities);
            if (getIntent().getBooleanExtra("push", FALSE) && activities == ONE) {
                startActivity(new Intent(context, HomeActivity.class));
            }
        }
        super.onBackPressed();
    }

    private View.OnClickListener onShareClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            UMWeb umWeb = new UMWeb(shareUrl);
//            umWeb.setTitle(TextUtils.isEmpty(shareTitle) ? getString(R.string.app_name) : shareTitle);
//            umWeb.setDescription(TextUtils.isEmpty(shareDesc) ? DOWNLOAD_SHARE : shareDesc);
//            if (TextUtils.isEmpty(shareImg)) {
//                umWeb.setThumb(new UMImage(context, R.mipmap.ic_launcher));
//            } else {
//                umWeb.setThumb(new UMImage(context, shareImg));
//            }
//            SharePopup sharePopup = new SharePopup(WerActivity.this, shareType, ZERO, umWeb);
//            sharePopup.setOnSuccessShareListener(onSuccessShareListener);
//            sharePopup.show(mTitleBar);
        }
    };

    private SharePopup.OnSuccessShareListener onSuccessShareListener = new SharePopup.OnSuccessShareListener() {

        @Override
        public void onSuccess() {
            if (sharefresh) {
                loadUrl();
            }
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (!title.contains("https://")) {
                mTitleBar.setMiddleText(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mWebView.setProgress(newProgress);
        }
    };

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mWebView.isRefreshing()) {
                mWebView.stopRefresh();
            } else {
                mLoadingLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWrongLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            getData(url);
//            if (!url.startsWith("https")) {
//                return FALSE;
//            }

            if(BDeepLinkUtil.getDeepLinkData(WerActivity.this,url))
            {
                return true;
            }


            return true;
        }
    };


    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        loadUrl();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
