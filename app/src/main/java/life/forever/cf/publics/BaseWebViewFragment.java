package life.forever.cf.publics;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import life.forever.cf.publics.weight.viewweb.Kill;
import life.forever.cf.publics.weight.viewweb.JsAndroid;

/**
 * 封装了一个垂直{@link android.webkit.WebView}的{@link BaseFragment}
 *
 * @author Haojie.Dai
 */
public abstract class BaseWebViewFragment extends BaseFragment {

    protected Kill mWebView;

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        mWebView = new Kill(context);
        mWebView.setJsAndroid(new JsAndroid(getActivity(), mWebView));
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContentLayout.addView(mWebView);
    }

}
