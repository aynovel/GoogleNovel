package life.forever.cf.publics.fresh.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.view.ViewCompat;

public class MiFliingLayout extends FlingLayout {
    public MiFliingLayout(Context context) {
        super(context);
    }

    public MiFliingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiFliingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onScroll(float y) {
        View view = getPullable().getView();
        int heigth = view.getMeasuredHeight();
        if (y >= 0) {
            ViewCompat.setPivotY(view, 0);
            ViewCompat.setScaleY(view, (heigth + y) / heigth);
        } else {
            ViewCompat.setPivotY(view, heigth);
            ViewCompat.setScaleY(view, (heigth - y) / heigth);
        }
        return true;
    }
}
