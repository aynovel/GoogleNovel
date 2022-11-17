package life.forever.cf.publics.scrollweight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerFrameLayout extends FrameLayout implements ScrollHelper.ScrollParent {

    private final RecyclerView recyclerView;


    public RecyclerFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        recyclerView = new RecyclerView(context);
        addView(recyclerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public View getScrollView() {
        return recyclerView;
    }
}
