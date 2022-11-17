package life.forever.cf.publics.fresh.pullble;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import life.forever.cf.publics.fresh.android.able.VPullable;

public class PullableImageView extends AppCompatImageView implements VPullable {

    public PullableImageView(Context context) {
        super(context);
    }

    public PullableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canOverStart() {
        return true;
    }

    @Override
    public boolean canOverEnd() {
        return true;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void scrollAViewBy(int dp) {
    }
}
