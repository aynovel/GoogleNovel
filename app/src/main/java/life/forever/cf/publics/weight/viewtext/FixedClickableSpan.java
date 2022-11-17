package life.forever.cf.publics.weight.viewtext;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;


public abstract class FixedClickableSpan extends ClickableSpan implements ITouchableSpan {
    private boolean mIsPressed;
    @ColorInt
    private final int mNormalBackgroundColor;
    @ColorInt
    private final int mPressedBackgroundColor;
    @ColorInt
    private final int mNormalTextColor;
    @ColorInt
    private final int mPressedTextColor;

    private boolean mIsNeedUnderline = false;

    public abstract void onSpanClick(View widget);

    @Override
    public final void onClick(View widget) {
        if (ViewCompat.isAttachedToWindow(widget)) {
            onSpanClick(widget);
        }
    }

    public FixedClickableSpan(@ColorInt int normalTextColor,
                              @ColorInt int pressedTextColor,
                              @ColorInt int normalBackgroundColor,
                              @ColorInt int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mNormalBackgroundColor = normalBackgroundColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    public int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    public int getNormalTextColor() {
        return mNormalTextColor;
    }

    public int getPressedBackgroundColor() {
        return mPressedBackgroundColor;
    }

    public int getPressedTextColor() {
        return mPressedTextColor;
    }

    @Override
    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void setIsNeedUnderline(boolean isNeedUnderline) {
        mIsNeedUnderline = isNeedUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor
                : mNormalBackgroundColor;
        ds.setUnderlineText(mIsNeedUnderline);
    }
}
