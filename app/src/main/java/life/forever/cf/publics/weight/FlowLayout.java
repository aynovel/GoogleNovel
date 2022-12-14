package life.forever.cf.publics.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import life.forever.cf.R;
import life.forever.cf.adapter.Adapter;


public class FlowLayout extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    private int mGravity;

    private static final int LINES = 0;
    private static final int NUMBER = 1;
    private int mMaxMode = LINES;
    private int mMaximum = Integer.MAX_VALUE;


    private int[] mItemNumberInEachLine;

    private int[] mWidthSumInEachLine;

    private int measuredChildCount;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FlowLayout);
        mHorizontalSpacing = array.getDimensionPixelSize(
                R.styleable.FlowLayout_horizontalSpacing, 0);
        mVerticalSpacing = array.getDimensionPixelSize(
                R.styleable.FlowLayout_verticalSpacing, 0);
        mGravity = array.getInteger(R.styleable.FlowLayout_android_gravity, Gravity.LEFT);
        int maxLines = array.getInt(R.styleable.FlowLayout_android_maxLines, -1);
        if (maxLines >= 0) {
            setMaxLines(maxLines);
        }
        int maxNumber = array.getInt(R.styleable.FlowLayout_maxNumber, -1);
        if (maxNumber >= 0) {
            setMaxNumber(maxNumber);
        }
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int maxLineHeight = 0;

        int resultWidth;
        int resultHeight;

        final int count = getChildCount();

        mItemNumberInEachLine = new int[count];
        mWidthSumInEachLine = new int[count];
        int lineIndex = 0;

        // ???FloatLayout?????????MATCH_PARENT?????????????????????????????????View??????
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSpecSize;

            measuredChildCount = 0;

            // ????????????View???position
            int childPositionX = getPaddingLeft();
            int childPositionY = getPaddingTop();

            // ???View???Right??????????????????x??????
            int childMaxRight = widthSpecSize - getPaddingRight();

            for (int i = 0; i < count; i++) {
                if (mMaxMode == NUMBER && measuredChildCount >= mMaximum) {
                    // ????????????????????????????????????
                    break;
                } else if (mMaxMode == LINES && lineIndex >= mMaximum) {
                    // ????????????????????????????????????
                    break;
                }

                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }

                final LayoutParams childLayoutParams = child.getLayoutParams();
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingLeft() + getPaddingRight(), childLayoutParams.width);
                final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom(), childLayoutParams.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                final int childw = child.getMeasuredWidth();
                maxLineHeight = Math.max(maxLineHeight, child.getMeasuredHeight());
                // ????????????
                if (childPositionX + childw > childMaxRight) {
                    // ???????????????????????????????????????????????????
                    if (mMaxMode == LINES) {
                        if (lineIndex + 1 >= mMaximum) {
                            break;
                        }
                    }
                    mWidthSumInEachLine[lineIndex] -= mHorizontalSpacing; // ???????????????item??????????????????space??????????????????????????????????????????item????????????space???????????????????????????
                    lineIndex++; // ??????
                    childPositionX = getPaddingLeft(); // ??????????????????item???x
                    childPositionY += maxLineHeight + mVerticalSpacing; // ??????????????????item???y
                }
                mItemNumberInEachLine[lineIndex]++;
                mWidthSumInEachLine[lineIndex] += (childw + mHorizontalSpacing);
                childPositionX += (childw + mHorizontalSpacing);
                measuredChildCount++;
            }
            // ??????????????????item???????????????????????????lineCount????????????+1????????????mWidthSumInEachLine[lineCount]???0??????????????????????????????item???space
            if (mWidthSumInEachLine.length > 0 && mWidthSumInEachLine[lineIndex] > 0) {
                mWidthSumInEachLine[lineIndex] -= mHorizontalSpacing;
            }
            if (heightSpecMode == MeasureSpec.UNSPECIFIED) {
                resultHeight = childPositionY + maxLineHeight + getPaddingBottom();
            } else if (heightSpecMode == MeasureSpec.AT_MOST) {
                resultHeight = childPositionY + maxLineHeight + getPaddingBottom();
                resultHeight = Math.min(resultHeight, heightSpecSize);
            } else {
                resultHeight = heightSpecSize;
            }

        } else {
            // ????????????????????????????????????
            resultWidth = getPaddingLeft() + getPaddingRight();
            measuredChildCount = 0;

            for (int i = 0; i < count; i++) {
                if (mMaxMode == NUMBER) {
                    // ????????????????????????????????????
                    if (measuredChildCount > mMaximum) {
                        break;
                    }
                } else if (mMaxMode == LINES) {
                    // ????????????????????????????????????
                    if (1 > mMaximum) {
                        break;
                    }
                }
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                final LayoutParams childLayoutParams = child.getLayoutParams();
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingLeft() + getPaddingRight(), childLayoutParams.width);
                final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom(), childLayoutParams.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                resultWidth += child.getMeasuredWidth();
                maxLineHeight = Math.max(maxLineHeight, child.getMeasuredHeight());
                measuredChildCount++;
            }
            if (measuredChildCount > 0) {
                resultWidth += mHorizontalSpacing * (measuredChildCount - 1);
            }
            resultHeight = maxLineHeight + getPaddingTop() + getPaddingBottom();
            if (mItemNumberInEachLine.length > 0) {
                mItemNumberInEachLine[lineIndex] = count;
            }
            if (mWidthSumInEachLine.length > 0) {
                mWidthSumInEachLine[0] = resultWidth;
            }
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        // ????????????gravity?????????????????????????????????left
        switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                layoutWithGravityLeft(width);
                break;
            case Gravity.RIGHT:
                layoutWithGravityRight(width);
                break;
            case Gravity.CENTER_HORIZONTAL:
                layoutWithGravityCenterHorizontal(width);
                break;
            default:
                layoutWithGravityLeft(width);
                break;
        }
    }

    /**
     * ??????View????????????
     */
    private void layoutWithGravityCenterHorizontal(int parentWidth) {
        int nextChildIndex = 0;
        int nextChildPositionX;
        int nextChildPositionY = getPaddingTop();
        int lineHeight = 0;

        // ???????????????
        for (int i = 0; i < mItemNumberInEachLine.length; i++) {
            // ????????????????????????item?????????????????????
            if (mItemNumberInEachLine[i] == 0) {
                break;
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break;
            }

            // ?????????????????????????????????????????????
            nextChildPositionX = (parentWidth - getPaddingLeft() - getPaddingRight() - mWidthSumInEachLine[i]) / 2 + getPaddingLeft(); // ??? View ????????? x ???
            for (int j = nextChildIndex; j < nextChildIndex + mItemNumberInEachLine[i]; j++) {
                final View childView = getChildAt(j);
                if (childView.getVisibility() == GONE) {
                    continue;
                }
                final int childw = childView.getMeasuredWidth();
                final int childh = childView.getMeasuredHeight();
                childView.layout(nextChildPositionX, nextChildPositionY, nextChildPositionX + childw, nextChildPositionY + childh);
                lineHeight = Math.max(lineHeight, childh);
                nextChildPositionX += childw + mHorizontalSpacing;
            }

            // ????????????????????????????????????????????????
            nextChildPositionY += (lineHeight + mVerticalSpacing);
            nextChildIndex += mItemNumberInEachLine[i];
            lineHeight = 0;
        }

        int childCount = getChildCount();
        if (measuredChildCount < childCount) {
            for (int i = measuredChildCount; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() == GONE) {
                    continue;
                }
                childView.layout(0, 0, 0, 0);
            }
        }
    }

    /**
     * ??????View????????????
     */
    private void layoutWithGravityLeft(int parentWidth) {
        int childMaxRight = parentWidth - getPaddingRight();
        int childPositionX = getPaddingLeft();
        int childPositionY = getPaddingTop();
        int lineHeight = 0;
        final int childCount = getChildCount();
        final int childCountToLayout = Math.min(childCount, measuredChildCount);
        for (int i = 0; i < childCountToLayout; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            final int childw = child.getMeasuredWidth();
            final int childh = child.getMeasuredHeight();
            if (childPositionX + childw > childMaxRight) {
                // ??????
                childPositionX = getPaddingLeft();
                childPositionY += (lineHeight + mVerticalSpacing);
                lineHeight = 0;
            }
            child.layout(childPositionX, childPositionY, childPositionX + childw, childPositionY + childh);
            childPositionX += childw + mHorizontalSpacing;
            lineHeight = Math.max(lineHeight, childh);
        }

        // ??????????????????View??????childCount????????????????????????View???????????????
        if (measuredChildCount < childCount) {
            for (int i = measuredChildCount; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                child.layout(0, 0, 0, 0);
            }
        }
    }

    /**
     * ??????View????????????
     */
    private void layoutWithGravityRight(int parentWidth) {
        int nextChildIndex = 0;
        int nextChildPositionX;
        int nextChildPositionY = getPaddingTop();
        int lineHeight = 0;

        // ???????????????
        for (int i = 0; i < mItemNumberInEachLine.length; i++) {
            // ????????????????????????item?????????????????????
            if (mItemNumberInEachLine[i] == 0) {
                break;
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break;
            }

            // ?????????????????????????????????????????????
            nextChildPositionX = parentWidth - getPaddingRight() - mWidthSumInEachLine[i]; // ??????????????? View ????????? x ???
            for (int j = nextChildIndex; j < nextChildIndex + mItemNumberInEachLine[i]; j++) {
                final View childView = getChildAt(j);
                if (childView.getVisibility() == GONE) {
                    continue;
                }
                final int childw = childView.getMeasuredWidth();
                final int childh = childView.getMeasuredHeight();
                childView.layout(nextChildPositionX, nextChildPositionY, nextChildPositionX + childw, nextChildPositionY + childh);
                lineHeight = Math.max(lineHeight, childh);
                nextChildPositionX += childw + mHorizontalSpacing;
            }

            // ????????????????????????????????????????????????
            nextChildPositionY += (lineHeight + mVerticalSpacing);
            nextChildIndex += mItemNumberInEachLine[i];
            lineHeight = 0;
        }

        int childCount = getChildCount();
        if (measuredChildCount < childCount) {
            for (int i = measuredChildCount; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() == GONE) {
                    continue;
                }
                childView.layout(0, 0, 0, 0);
            }
        }
    }

    /**
     * ????????? View ?????????????????????????????? {@link Gravity#CENTER_HORIZONTAL}, {@link Gravity#LEFT} ??? {@link Gravity#RIGHT}
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return mGravity;
    }

    /**
     * ???????????????????????????View??????
     * ??????????????????????????????View??????????????????????????????????????????View??????
     *
     * @param maxNumber ?????????????????????View??????
     */
    public void setMaxNumber(int maxNumber) {
        mMaximum = maxNumber;
        mMaxMode = NUMBER;
        requestLayout();
    }

    /**
     * ???????????????????????????View??????
     */
    public int getMaxNumber() {
        return mMaxMode == NUMBER ? mMaximum : -1;
    }

    /**
     * ??????????????????????????????
     * ??????????????????????????????View??????????????????????????????????????????View??????
     *
     * @param maxLines ????????????????????????
     */
    public void setMaxLines(int maxLines) {
        mMaximum = maxLines;
        mMaxMode = LINES;
        requestLayout();
    }

    /**
     * ??????????????????????????????
     *
     * @return ?????????????????????-1
     */
    public int getMaxLines() {
        return mMaxMode == LINES ? mMaximum : -1;
    }

    /**
     * ????????? View ???????????????
     */
    public void setHorizontalSpacing(int spacing) {
        mHorizontalSpacing = spacing;
        invalidate();
    }

    /**
     * ????????? View ???????????????
     */
    public void setVerticalSpacing(int spacing) {
        mVerticalSpacing = spacing;
        invalidate();
    }

    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            addView(adapter.getItemView(this, i));
        }
    }

}
