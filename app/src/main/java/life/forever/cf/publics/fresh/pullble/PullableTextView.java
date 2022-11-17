package life.forever.cf.publics.fresh.pullble;


import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import life.forever.cf.publics.fresh.android.able.OnScrollListener;
import life.forever.cf.publics.fresh.android.able.VPullable;

public class PullableTextView extends AppCompatTextView implements VPullable {

    private OnScrollListener onScrollListener = null;

    public PullableTextView(Context context) {
        super(context);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public PullableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public PullableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean canOverStart() {
        return getScrollY() == 0;
    }

    @Override
    public boolean canOverEnd() {
        return getScrollY() >= (getLayout().getHeight() - getMeasuredHeight() + getCompoundPaddingBottom() + getCompoundPaddingTop());
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void scrollAViewBy(int dp) {
        int maxScrollY = (getLayout().getHeight() - getMeasuredHeight() + getCompoundPaddingBottom() + getCompoundPaddingTop());
        if (getScrollY() + dp >= maxScrollY) {
            scrollTo(0, maxScrollY);
        } else {
            scrollBy(0, dp);
        }
    }
}
