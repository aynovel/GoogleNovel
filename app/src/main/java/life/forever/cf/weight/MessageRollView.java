package life.forever.cf.weight;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;


public class MessageRollView extends LinearLayout implements Constant {

    private final static int OUT = ZERO;
    private final static int NEXT = ONE;

    private final ImageView head;
    private final TextView content;

    private final TranslateAnimation inAnim;
    private final TranslateAnimation outAnim;

    private boolean isRolling;
    private List<Comment> comments;
    private int current;

    public MessageRollView(Context context) {
        this(context, null);
    }

    public MessageRollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setVisibility(GONE);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.shape_black_7_corner_20dp);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_roll_message, this, TRUE);
        head = findViewById(R.id.head);
        content = findViewById(R.id.content);

        inAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -ONE, Animation.RELATIVE_TO_SELF,
                ZERO, Animation.RELATIVE_TO_SELF, ZERO, Animation.RELATIVE_TO_SELF, ZERO);
        inAnim.setDuration(THREE_HUNDRED);
        inAnim.setFillAfter(TRUE);

        outAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, ZERO, Animation.RELATIVE_TO_SELF, ZERO,
                Animation.RELATIVE_TO_SELF, ZERO, Animation.RELATIVE_TO_SELF, -TWO);
        outAnim.setDuration(THREE_HUNDRED);
        outAnim.setFillAfter(TRUE);
    }

    public void gone() {
        clearAnimation();
        setVisibility(GONE);
    }

    public void stop() {
        if (isRolling) {
            isRolling = FALSE;
            handler.removeCallbacksAndMessages(null);
        }
        gone();
    }

    public void setMessages(List<Comment> comments) {
        if (isRolling) {
            isRolling = FALSE;
            handler.removeCallbacksAndMessages(null);
            gone();
        }
        if (comments != null && comments.size() > ZERO) {
            this.comments = comments;
            start();
        }
    }

    private void start() {
        isRolling = TRUE;
        current = ZERO;
        setChild();
        setVisibility(VISIBLE);
        startAnimation(inAnim);
        handler.sendEmptyMessageDelayed(OUT, THREE_THOUSAND);
    }

    private void setChild() {
        Comment comment = comments.get(current);
        GlideUtil.load(getContext(), comment.head, R.drawable.default_user_logo, head);
        content.setText(comment.content);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OUT) {
                startAnimation(outAnim);
                handler.sendEmptyMessageDelayed(NEXT, FIVE_HUNDRED);
                return;
            }
            if (msg.what == NEXT) {
                current++;
                if (current < comments.size() && current < TEN) {
                    setChild();
                    startAnimation(inAnim);
                    handler.sendEmptyMessageDelayed(OUT, THREE_THOUSAND);
                } else {
                    isRolling = FALSE;
                    gone();
                }
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
