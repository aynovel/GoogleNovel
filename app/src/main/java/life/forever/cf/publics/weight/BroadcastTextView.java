package life.forever.cf.publics.weight;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.widget.AppCompatTextView;

import life.forever.cf.publics.Constant;

import java.util.ArrayList;
import java.util.List;


public class BroadcastTextView extends AppCompatTextView implements Constant {

    private final int IN = ZERO;
    private final int OUT = ONE;

    private int index;
    private final List<CharSequence> texts = new ArrayList<>();
    private final TranslateAnimation mInAnim;
    private final TranslateAnimation mOutAnim;

    public BroadcastTextView(Context context) {
        this(context, null);
    }

    public BroadcastTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BroadcastTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER_VERTICAL);

        // 跑马灯
        setMaxLines(ONE);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(TRUE);
        setFocusableInTouchMode(TRUE);
        setHorizontallyScrolling(TRUE);
        setMarqueeRepeatLimit(ONE);

        mInAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, ONE,
                Animation.RELATIVE_TO_SELF, ZERO);
        mInAnim.setFillAfter(TRUE);
        mInAnim.setDuration(THREE_HUNDRED);

        mOutAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, -ONE);
        mOutAnim.setFillAfter(TRUE);
        mOutAnim.setDuration(THREE_HUNDRED);
    }

    public void setTexts(List<CharSequence> texts) {
        if (texts == null || texts.isEmpty()) {
            return;
        }
        removeMessages();
        this.texts.clear();
        this.texts.addAll(texts);
        index = ZERO;
        handler.sendEmptyMessage(IN);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeMessages();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == IN) {
                setText(texts.get(index % texts.size()));
                startAnimation(mInAnim);
                handler.sendEmptyMessageDelayed(OUT, THREE_THOUSAND);
                return;
            }
            startAnimation(mOutAnim);
            index++;
            handler.sendEmptyMessageDelayed(IN, THREE_HUNDRED);
        }
    };

    private void removeMessages() {
        clearAnimation();
        handler.removeMessages(IN);
        handler.removeMessages(OUT);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }

}
