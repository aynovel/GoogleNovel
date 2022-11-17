package life.forever.cf.publics.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class WrapListView extends ListView {

    public WrapListView(Context context) {
        super(context);
    }

    public WrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
