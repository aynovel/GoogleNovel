package life.forever.cf.publics.weight;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


public class TaskScrollView extends NestedScrollView {

    private onScrollChanged onScrollChanged;
    public TaskScrollView(@NonNull Context context) {
        super(context);
    }

    public TaskScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChanged != null){
            onScrollChanged.onScroll(l,t,oldl,oldt);
        }
    }

    public void setOnScrollChanged(onScrollChanged onScrollChanged){
        this.onScrollChanged = onScrollChanged;
    }

    public  interface  onScrollChanged{
        void onScroll(int l,int t,int oldl,int oldt);
    }

}
