package life.forever.cf.publics.fresh.android.utils;


import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.publics.fresh.android.able.VPullable;

public class VCanPullUtil {

    public static VPullable getPullAble(View view) {
        if (view == null) {
            return null;
        }
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (view instanceof VPullable) {
            return (VPullable) view;
        } else if (view instanceof AbsListView) {
            return new AbsListViewCanPull((AbsListView) view);
        } else if (view instanceof ScrollView || view instanceof NestedScrollView) {
            return new ScrollViewCanPull((ViewGroup) view);
        } else if (view instanceof WebView) {
            return new WebViewCanPull((WebView) view);
        } else if (view instanceof RecyclerView) {
            return new RecyclerViewCanPull((RecyclerView) view);
        }
        return null;
    }

    private static class AbsListViewCanPull implements VPullable {
        public AbsListViewCanPull(AbsListView absListView) {
            this.absListView = absListView;
        }

        AbsListView absListView;

        @Override
        public boolean canOverStart() {
            if (absListView.getCount() == 0) {
                return true;
            } else return absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop();
        }

        @Override
        public boolean canOverEnd() {
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            int count = absListView.getCount();
            if (count == 0) {
                return true;
            } else if (lastVisiblePosition == (count - 1)) {
                View view = absListView.getChildAt(lastVisiblePosition - firstVisiblePosition);
                return view != null && view.getBottom() <= absListView.getMeasuredHeight() - absListView.getPaddingBottom();
            }
            return false;
        }

        @Override
        public View getView() {
            return absListView;
        }

        @Override
        public void scrollAViewBy(int dp) {
            absListView.smoothScrollBy(dp, 0);
        }
    }

    private static class ScrollViewCanPull implements VPullable {
        public ScrollViewCanPull(ViewGroup scrollView) {
            this.scrollView = scrollView;
        }

        ViewGroup scrollView;

        @Override
        public boolean canOverStart() {
            return scrollView.getScrollY() <= 0;
        }

        @Override
        public boolean canOverEnd() {
            if (scrollView.getChildCount() == 0) {
                return true;
            }
            return scrollView.getScrollY() >= (scrollView.getChildAt(0).getHeight() - scrollView.getMeasuredHeight());
        }

        @Override
        public View getView() {
            return scrollView;
        }

        @Override
        public void scrollAViewBy(int dp) {
            if (scrollView.getChildCount() != 0) {
                float maxScrollY = scrollView.getChildAt(0).getHeight() - scrollView.getMeasuredHeight();
                if (scrollView.getScrollY() + dp >= maxScrollY) {
                    scrollView.scrollTo(0, (int) maxScrollY);
                } else {
                    scrollView.scrollBy(0, dp);
                }
            }
        }
    }


    private static class RecyclerViewCanPull implements VPullable {
        public RecyclerViewCanPull(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;

        private void initLayoutManager() {
            if (layoutManager == null) {
                RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
                if (layout != null && layout instanceof LinearLayoutManager) {
                    layoutManager = (LinearLayoutManager) layout;
                }
            }
        }

        @Override
        public boolean canOverStart() {
            initLayoutManager();
            if (layoutManager != null) {
                if (layoutManager.getItemCount() == 0) {
                    return true;
                } else return layoutManager.findFirstVisibleItemPosition() == 0 && recyclerView.getChildAt(0).getTop() >= recyclerView.getPaddingTop();
            }
            return false;
        }


        @Override
        public boolean canOverEnd() {
            initLayoutManager();
            if (layoutManager != null) {
                int count = layoutManager.getItemCount();
                if (count == 0) {
                    return true;
                } else return layoutManager.findLastCompletelyVisibleItemPosition() == count - 1;
            }
            return false;
        }

        @Override
        public View getView() {
            return recyclerView;
        }

        @Override
        public void scrollAViewBy(int dp) {
            recyclerView.scrollBy(0, dp);
        }
    }

    private static class WebViewCanPull implements VPullable {
        public WebViewCanPull(WebView webView) {
            this.webView = webView;
        }

        WebView webView;

        @Override
        public boolean canOverStart() {
            return webView.getScrollY() <= 0;
        }

        @Override
        public boolean canOverEnd() {
            return webView.getScrollY() >= webView.getContentHeight() * webView.getScale() - webView.getMeasuredHeight();
        }

        @Override
        public View getView() {
            return webView;
        }

        @Override
        public void scrollAViewBy(int dp) {

            float maxScrollY = webView.getContentHeight() * webView.getScale() - webView.getMeasuredHeight();
            if (webView.getScrollY() + dp >= maxScrollY) {
                webView.scrollTo(0, (int) maxScrollY);
            } else {
                webView.scrollBy(0, dp);
            }
        }
    }

}
