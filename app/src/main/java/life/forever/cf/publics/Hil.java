package life.forever.cf.publics;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.R;

import butterknife.ButterKnife;



public class Hil extends BaseActivity {

       WebView webView;

    @SuppressLint("JavascriptInterface")

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();
    @Override
    protected void initializeData() {
        Intent data = getIntent();
        String path = data.getStringExtra("path");
        webView = findViewById(R.id.webview);
//        webView.loadUrl("file:///android_asset/test.html");//加载asset文件夹下html
        webView.loadUrl(path);//加载url

        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);

        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
        private final WebViewClient webViewClient=new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {//页面加载完成
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

        };

        //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
        private final WebChromeClient webChromeClient=new WebChromeClient(){
            //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
            @Override
            public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
                return true;
            }

            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            //加载进度回调
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        };

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
                webView.goBack(); // goBack()表示返回webView的上一页面
                return true;
            }
            return super.onKeyDown(keyCode,event);
        }

        /**
         * JS调用android的方法
         * @param str
         * @return
         */
        @JavascriptInterface //仍然必不可少
        public void  getClient(String str){
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            //释放资源
            webView.destroy();
            webView=null;
        }


}