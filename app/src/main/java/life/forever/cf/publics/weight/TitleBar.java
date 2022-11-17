package life.forever.cf.publics.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;


public class TitleBar extends FrameLayout implements Constant {

    private final FrameLayout mParentLayout;
    private final ImageView mLeftImageView;
    private final TextView mLeftTextView;
    private final TextView mMiddleTextView;
    private final ImageView mRightImageView;
    private final ImageView mRightImageViewTwo;
    private final TextView mRightTextView;
    private final View mDivider;

    public TitleBar(@NonNull Context context) {
        this(context, null);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, TRUE);
        mParentLayout = findViewById(R.id.parent_layout);
        mLeftImageView = findViewById(R.id.left_image_view);
        mLeftTextView = findViewById(R.id.left_text_view);
        mMiddleTextView = findViewById(R.id.middle_text_view);

        mRightImageViewTwo = findViewById(R.id.right_image_view_two);
        mRightImageView = findViewById(R.id.right_image_view);
        mRightTextView = findViewById(R.id.right_text_view);
        mDivider = findViewById(R.id.divider);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

        final Drawable leftDrawable = array.getDrawable(R.styleable.TitleBar_left_src);
        if (leftDrawable != null) {
            mLeftImageView.setImageDrawable(leftDrawable);
        }

        final int leftTextColor = array.getColor(R.styleable.TitleBar_left_color, ZERO);
        if (leftTextColor != ZERO) {
            mLeftTextView.setTextColor(leftTextColor);
        }

        final int leftTextSize = array.getDimensionPixelOffset(R.styleable.TitleBar_left_size, ZERO);
        if (leftTextSize != ZERO) {
            mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        }

        final String leftText = array.getString(R.styleable.TitleBar_left_text);
        mLeftTextView.setText(leftText);

        final int middleTextColor = array.getColor(R.styleable.TitleBar_middle_color, ZERO);
        if (middleTextColor != ZERO) {
            mMiddleTextView.setTextColor(middleTextColor);
        }

        final int middleTextSize = array.getDimensionPixelOffset(R.styleable.TitleBar_middle_size, ZERO);
        if (middleTextSize != ZERO) {
            mMiddleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, middleTextSize);
        }

        final String middleText = array.getString(R.styleable.TitleBar_middle_text);
        mMiddleTextView.setText(middleText);

        final Drawable rightDrawable = array.getDrawable(R.styleable.TitleBar_right_src);
        if (rightDrawable != null) {
            mRightImageView.setImageDrawable(rightDrawable);
        }

        final int rightTextColor = array.getColor(R.styleable.TitleBar_right_color, ZERO);
        if (rightTextColor != ZERO) {
            mRightTextView.setTextColor(rightTextColor);
        }

        final int rightTextSize = array.getDimensionPixelOffset(R.styleable.TitleBar_right_size, ZERO);
        if (rightTextSize != ZERO) {
            mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        }

        final String rightText = array.getString(R.styleable.TitleBar_right_text);
        setRightText(rightText);

        final boolean leftShowImage = array.getBoolean(R.styleable.TitleBar_left_show_image, TRUE);
        showLeftImageView(leftShowImage);

        final boolean rightShowImage = array.getBoolean(R.styleable.TitleBar_right_show_image, TRUE);
        showRightImageView(rightShowImage);

        final boolean showDivider = array.getBoolean(R.styleable.TitleBar_show_divider, TRUE);
        showDivider(showDivider);

        array.recycle();
    }

    public void setLeftImageResource(int resId) {
        mLeftImageView.setImageResource(resId);
    }

    public void setLeftText(String text) {
        mLeftTextView.setText(text);
    }

    public void setRightImageResource(int resId) {
        mRightImageView.setImageResource(resId);
    }

    public void setRightText(String text) {
        mRightTextView.setText(text);
    }

    public void setRightImageResourceTwo(int resId) {
        mRightImageViewTwo.setImageResource(resId);
        mRightImageViewTwo.setVisibility(VISIBLE);
    }


    public void showRightImageView(boolean showImageView) {
        if (showImageView) {
            mRightImageView.setVisibility(VISIBLE);
            mRightTextView.setVisibility(GONE);
        } else {
            mRightTextView.setVisibility(VISIBLE);
            mRightImageView.setVisibility(GONE);
        }
    }

    public void showLeftImageView(boolean showImageView) {
        if (showImageView) {
            mLeftImageView.setVisibility(VISIBLE);
            mLeftTextView.setVisibility(GONE);
        } else {
            mLeftTextView.setVisibility(VISIBLE);
            mLeftImageView.setVisibility(GONE);
        }
    }

    public void showDivider(boolean showDivider) {
        if (showDivider) {
            mDivider.setVisibility(VISIBLE);
        } else {
            mDivider.setVisibility(GONE);
        }
    }

    public void setMiddleText(String text) {
        mMiddleTextView.setText(text);
    }

    public void setLeftImageViewOnClickListener(OnClickListener onClickListener) {
        mLeftImageView.setOnClickListener(onClickListener);
    }

    public void setRightImageViewOnClickListener(OnClickListener onClickListener) {
        mRightImageView.setOnClickListener(onClickListener);
    }

    public void setRightImageViewTwoOnClickListener(OnClickListener onClickListener) {
        mRightImageViewTwo.setOnClickListener(onClickListener);
    }

    public void setRightImageViewTwoIsOnClick(boolean isOnClick) {
        mRightImageViewTwo.setEnabled(isOnClick);
    }

    public void setRightTextViewOnClickListener(OnClickListener onClickListener) {
        mRightTextView.setOnClickListener(onClickListener);
    }

    public ImageView getLeftImageView() {
        return mLeftImageView;
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }

    public ImageView getRightImageView() {
        return mRightImageView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public TextView getMiddleTextView() {
        return mMiddleTextView;
    }

    /**
     * 设置自定义标题View
     *
     * @return
     */
    public void setCustomTitleView(View view) {
        mParentLayout.removeAllViews();
        mParentLayout.addView(view);
    }
}
