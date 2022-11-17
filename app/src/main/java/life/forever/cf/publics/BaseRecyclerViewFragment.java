package life.forever.cf.publics;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.RefreshHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;


public abstract class BaseRecyclerViewFragment extends BaseFragment {

    protected PullRefreshLayout mRefreshLayout;
    protected RefreshHeaderView mRefreshHeader;
    protected LoadFooterView mLoadFooter;
    protected RecyclerView mRecyclerView;

    @Override
    protected void bindView() {
        mRefreshLayout = new PullRefreshLayout(context);
        mRefreshLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRefreshHeader = new RefreshHeaderView(context);
        mRefreshHeader.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLoadFooter = new LoadFooterView(context);
        mLoadFooter.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mRefreshLayout.addView(mRefreshHeader);
        mRefreshLayout.addView(mLoadFooter);
        mRefreshLayout.addView(mRecyclerView);
        mContentLayout.addView(mRefreshLayout);
    }

}
