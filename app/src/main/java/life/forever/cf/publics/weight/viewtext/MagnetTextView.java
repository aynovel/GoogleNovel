package life.forever.cf.publics.weight.viewtext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.widget.AppCompatTextView;


public class MagnetTextView extends AppCompatTextView {

    private boolean isPressed;
    private boolean performClick;
    private ScaleAnimation zoomInAnimation;
    private ScaleAnimation zoomOutAnimation;

    public MagnetTextView(Context context) {
        this(context, null);
    }

    public MagnetTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        zoomInAnimation = new ScaleAnimation(1f, 0.95f, 1f, 0.95f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        zoomInAnimation.setFillAfter(true);
        zoomInAnimation.setDuration(200);

        zoomOutAnimation = new ScaleAnimation(0.95f, 1f, 0.95f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOutAnimation.setFillAfter(true);
        zoomOutAnimation.setDuration(200);
    }

    private void toNormalState() {
        if (isPressed) {
            invalidate();
            isPressed = false;
            startAnimation(zoomOutAnimation);
        }
    }

    private boolean pointInView(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < getWidth() + slop && localY < getHeight() + slop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                invalidate();
                startAnimation(zoomInAnimation);
                break;
            case MotionEvent.ACTION_MOVE:
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                if (!pointInView(x, y, 20)) {
                    toNormalState();
                }
                break;
            case MotionEvent.ACTION_UP:
                performClick = isPressed;
                toNormalState();
                if (performClick) {
                    performClick();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                toNormalState();
                break;
        }

        return true;
    }
}
