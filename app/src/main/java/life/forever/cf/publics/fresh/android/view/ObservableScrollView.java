package life.forever.cf.publics.fresh.android.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

import life.forever.cf.publics.fresh.android.able.OnScrollListener;


public class ObservableScrollView extends NestedScrollView {

    private OnScrollListener onScrollListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
}
