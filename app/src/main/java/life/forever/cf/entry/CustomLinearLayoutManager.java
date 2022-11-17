package life.forever.cf.entry;


import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;


public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, boolean isScrollEnabled) {
        super(context);
        this.isScrollEnabled = isScrollEnabled;
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        int orientation = getOrientation();
        if (orientation == LinearLayoutManager.VERTICAL) {
            return isScrollEnabled && super.canScrollVertically();
        }
        return super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        int orientation = getOrientation();
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            return isScrollEnabled && super.canScrollHorizontally();
        }
        return super.canScrollHorizontally();
    }
}
