package life.forever.cf.publics;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import life.forever.cf.publics.weight.viewweb.Kill;
import life.forever.cf.publics.weight.viewweb.JsAndroid;

/**
 * 封装了一个{@link android.webkit.WebView}的{@link BaseActivity}
 *
 * @author Haojie.Dai
 */
public abstract class BaseWebViewActivity extends BaseActivity {

    protected Kill mWebView;

    @Override
    protected void initializeView() {
        mWebView = new Kill(context);
        mWebView.setJsAndroid(new JsAndroid(this, mWebView));
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mWebView);
    }

}
