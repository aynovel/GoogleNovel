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

        // 若FloatLayout指定了MATCH_PARENT或固定宽度，则需要使子View换行
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSpecSize;

            measuredChildCount = 0;

            // 下一个子View的position
            int childPositionX = getPaddingLeft();
            int childPositionY = getPaddingTop();

            // 子View的Right最大可达到的x坐标
            int childMaxRight = widthSpecSize - getPaddingRight();

            for (int i = 0; i < count; i++) {
                if (mMaxMode == NUMBER && measuredChildCount >= mMaximum) {
                    // 超出最多数量，则不再继续
                    break;
                } else if (mMaxMode == LINES && lineIndex >= mMaximum) {
                    // 超出最多行数，则不再继续
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
                // 需要换行
                if (childPositionX + childw > childMaxRight) {
                    // 如果换行后超出最大行数，则不再继续
                    if (mMaxMode == LINES) {
                        if (lineIndex + 1 >= mMaximum) {
                            break;
                        }
                    }
                    mWidthSumInEachLine[lineIndex] -= mHorizontalSpacing; // 后面每次加item都会加上一个space，这样的话每行都会为最后一个item多加一次space，所以在这里减一次
                    lineIndex++; // 换行
                    childPositionX = getPaddingLeft(); // 下一行第一个item的x
                    childPositionY += maxLineHeight + mVerticalSpacing; // 下一行第一个item的y
                }
                mItemNumberInEachLine[lineIndex]++;
                mWidthSumInEachLine[lineIndex] += (childw + mHorizontalSpacing);
                childPositionX += (childw + mHorizontalSpacing);
                measuredChildCount++;
            }
            // 如果最后一个item不是刚好在行末（即lineCount最后没有+1，也就是mWidthSumInEachLine[lineCount]非0），则要减去最后一个item的space
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
            // 不计算换行，直接一行铺开
            resultWidth = getPaddingLeft() + getPaddingRight();
            measuredChildCount = 0;

            for (int i = 0; i < count; i++) {
                if (mMaxMode == NUMBER) {
                    // 超出最多数量，则不再继续
                    if (measuredChildCount > mMaximum) {
                        break;
                    }
                } else if (mMaxMode == LINES) {
                    // 超出最大行数，则不再继续
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
        // 按照不同gravity使用不同的布局，默认是left
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
     * 将子View居中布局
     */
    private void layoutWithGravityCenterHorizontal(int parentWidth) {
        int nextChildIndex = 0;
        int nextChildPositionX;
        int nextChildPositionY = getPaddingTop();
        int lineHeight = 0;

        // 遍历每一行
        for (int i = 0; i < mItemNumberInEachLine.length; i++) {
            // 如果这一行已经没item了，则退出循环
            if (mItemNumberInEachLine[i] == 0) {
                break;
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break;
            }

            // 遍历该行内的元素，布局每个元素
            nextChildPositionX = (parentWidth - getPaddingLeft() - getPaddingRight() - mWidthSumInEachLine[i]) / 2 + getPaddingLeft(); // 子 View 的最小 x 值
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

            // 一行结束了，整理一下，准备下一行
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
     * 将子View靠左布局
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
                // 换行
                childPositionX = getPaddingLeft();
                childPositionY += (lineHeight + mVerticalSpacing);
                lineHeight = 0;
            }
            child.layout(childPositionX, childPositionY, childPositionX + childw, childPositionY + childh);
            childPositionX += childw + mHorizontalSpacing;
            lineHeight = Math.max(lineHeight, childh);
        }

        // 如果布局的子View少于childCount，则表示有一些子View不需要布局
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
     * 将子View靠右布局
     */
    private void layoutWithGravityRight(int parentWidth) {
        int nextChildIndex = 0;
        int nextChildPositionX;
        int nextChildPositionY = getPaddingTop();
        int lineHeight = 0;

        // 遍历每一行
        for (int i = 0; i < mItemNumberInEachLine.length; i++) {
            // 如果这一行已经没item了，则退出循环
            if (mItemNumberInEachLine[i] == 0) {
                break;
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break;
            }

            // 遍历该行内的元素，布局每个元素
            nextChildPositionX = parentWidth - getPaddingRight() - mWidthSumInEachLine[i]; // 初始值为子 View 的最小 x 值
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

            // 一行结束了，整理一下，准备下一行
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
     * 设置子 View 的对齐方式，目前支持 {@link Gravity#CENTER_HORIZONTAL}, {@link Gravity#LEFT} 和 {@link Gravity#RIGHT}
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
     * 设置最多可显示的子View个数
     * 注意该方法不会改变子View的个数，只会影响显示出来的子View个数
     *
     * @param maxNumber 最多可显示的子View个数
     */
    public void setMaxNumber(int maxNumber) {
        mMaximum = maxNumber;
        mMaxMode = NUMBER;
        requestLayout();
    }

    /**
     * 获取最多可显示的子View个数
     */
    public int getMaxNumber() {
        return mMaxMode == NUMBER ? mMaximum : -1;
    }

    /**
     * 设置最多可显示的行数
     * 注意该方法不会改变子View的个数，只会影响显示出来的子View个数
     *
     * @param maxLines 最多可显示的行数
     */
    public void setMaxLines(int maxLines) {
        mMaximum = maxLines;
        mMaxMode = LINES;
        requestLayout();
    }

    /**
     * 获取最多可显示的行数
     *
     * @return 没有限制时返回-1
     */
    public int getMaxLines() {
        return mMaxMode == LINES ? mMaximum : -1;
    }

    /**
     * 设置子 View 的水平间距
     */
    public void setHorizontalSpacing(int spacing) {
        mHorizontalSpacing = spacing;
        invalidate();
    }

    /**
     * 设置子 View 的垂直间距
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
