package life.forever.cf.publics.textviewfold;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.lang.reflect.Field;


public class ExpandableTextView extends AppCompatTextView {

    public static final String ELLIPSIS_STRING = new String(new char[]{'\u2026'});
    private static final int DEFAULT_MAX_LINE = 3;
    private static final String DEFAULT_OPEN_SUFFIX = " More";

    private static final String DEFAULT_CLOSE_SUFFIX = " ";
    volatile boolean animating = false;
    boolean isClosed = false;
    private int mMaxLines = DEFAULT_MAX_LINE;
    private int initWidth = 0;
    private CharSequence originalText;

    private SpannableStringBuilder mOpenSpannableStr, mCloseSpannableStr;

    private boolean hasAnimation = false;
    private Animation mOpenAnim, mCloseAnim;
    private int mOpenHeight, mCLoseHeight;
    private boolean mExpandable;
    private boolean mCloseInNewLine;
    @Nullable
    private SpannableString mOpenSuffixSpan;
    private String mOpenSuffixStr = DEFAULT_OPEN_SUFFIX;
    private String mCloseSuffixStr = DEFAULT_CLOSE_SUFFIX;
    private int mOpenSuffixColor, mCloseSuffixColor;

    private OnClickListener mOnClickListener;

    private CharSequenceToSpannableHandler mCharSequenceToSpannableHandler;

    public ExpandableTextView(Context context) {
        super(context);
        initialize();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /** ????????? */
    private void initialize() {
        mOpenSuffixColor = mCloseSuffixColor = Color.parseColor("#F23030");
        setMovementMethod(OverLinkMovementMethod.getInstance());
        setIncludeFontPadding(false);
        updateOpenSuffixSpan();
        updateCloseSuffixSpan();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setOriginalText(CharSequence originalText) {
        this.originalText = originalText;
        mExpandable = false;
        mCloseSpannableStr = new SpannableStringBuilder();
        final int maxLines = mMaxLines;
        SpannableStringBuilder tempText = charSequenceToSpannable(originalText);
        mOpenSpannableStr = charSequenceToSpannable(originalText);

        if (maxLines != -1) {
            Layout layout = createStaticLayout(tempText);
            mExpandable = layout.getLineCount() > maxLines;
            if (mExpandable) {
                //??????????????????
                if (mCloseInNewLine) {
                    mOpenSpannableStr.append("\n");
                }
//                if (mCloseSuffixSpan != null) {
//                    mOpenSpannableStr.append(mCloseSuffixSpan);
//                }
                //????????????????????????
                int endPos = layout.getLineEnd(maxLines - 1);
                if (originalText.length() <= endPos) {
                    mCloseSpannableStr = charSequenceToSpannable(originalText);
                } else {
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, endPos));
                }
                SpannableStringBuilder tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                if (mOpenSuffixSpan != null) {
                    tempText2.append(mOpenSuffixSpan);
                }
                //?????????????????????????????????????????????????????????
                Layout tempLayout = createStaticLayout(tempText2);
                while (tempLayout.getLineCount() > maxLines) {
                    int lastSpace = mCloseSpannableStr.length() - 1;
                    if (lastSpace == -1) {
                        break;
                    }
                    if (originalText.length() <= lastSpace) {
                        mCloseSpannableStr = charSequenceToSpannable(originalText);
                    } else {
                        mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace));
                    }
                    tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                    if (mOpenSuffixSpan != null) {
                        tempText2.append(mOpenSuffixSpan);
                    }
                    tempLayout = createStaticLayout(tempText2);

                }
                int lastSpace = mCloseSpannableStr.length() - mOpenSuffixSpan.length();
                if(lastSpace >= 0 && originalText.length() > lastSpace){
                    CharSequence redundantChar = originalText.subSequence(lastSpace, lastSpace + mOpenSuffixSpan.length());
                    int offset = hasEnCharCount(redundantChar) - hasEnCharCount(mOpenSuffixSpan) + 1;
                    lastSpace = offset <= 0 ? lastSpace : lastSpace - offset;
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace));
                }
                //???????????????????????????
                mCLoseHeight = tempLayout.getHeight() + getPaddingTop() + getPaddingBottom();

                mCloseSpannableStr.append(ELLIPSIS_STRING);
                if (mOpenSuffixSpan != null) {
                    mCloseSpannableStr.append(mOpenSuffixSpan);
                }
            }
        }
        isClosed = mExpandable;
        if (mExpandable) {
            setText(mCloseSpannableStr);
            //????????????
            super.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    switchOpenClose();
//                    if (mOnClickListener != null) {
//                        mOnClickListener.onClick(v);
//                    }
                }
            });
        } else {
            setText(mOpenSpannableStr);
        }
    }

    private int hasEnCharCount(CharSequence str){
        int count = 0;
        if(!TextUtils.isEmpty(str)){
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if(c >= ' ' && c <= '~'){
                    count++;
                }
            }
        }
        return count;
    }

    private void switchOpenClose() {
        if (mExpandable) {
            isClosed = !isClosed;
            if (isClosed) {
                close();
            } else {
                open();
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param hasAnimation
     */
    public void setHasAnimation(boolean hasAnimation) {
        this.hasAnimation = hasAnimation;
    }

    /** ?????? */
    private void open() {
        if (hasAnimation) {
            Layout layout = createStaticLayout(mOpenSpannableStr);
            mOpenHeight = layout.getHeight() + getPaddingTop() + getPaddingBottom();
            executeOpenAnim();
        } else {
            ExpandableTextView.super.setMaxLines(Integer.MAX_VALUE);
            setText(mOpenSpannableStr);
            if (mOpenCloseCallback != null){
                mOpenCloseCallback.onOpen();
            }
        }
    }

    /** ?????? */
    private void close() {
        if (hasAnimation) {
            executeCloseAnim();
        } else {
            ExpandableTextView.super.setMaxLines(mMaxLines);
            setText(mCloseSpannableStr);
            if (mOpenCloseCallback != null){
                mOpenCloseCallback.onClose();
            }
        }
    }

    /** ?????????????????? */
    private void executeOpenAnim() {
        //??????????????????
        if (mOpenAnim == null) {
            mOpenAnim = new ExpandCollapseAnimation(this, mCLoseHeight, mOpenHeight);
            mOpenAnim.setFillAfter(true);
            mOpenAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ExpandableTextView.super.setMaxLines(Integer.MAX_VALUE);
                    setText(mOpenSpannableStr);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //  ???????????????textview?????????????????????
                    getLayoutParams().height = mOpenHeight;
                    requestLayout();
                    animating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        clearAnimation();
        //  ????????????
        startAnimation(mOpenAnim);
    }

    /** ?????????????????? */
    private void executeCloseAnim() {
        //??????????????????
        if (mCloseAnim == null) {
            mCloseAnim = new ExpandCollapseAnimation(this, mOpenHeight, mCLoseHeight);
            mCloseAnim.setFillAfter(true);
            mCloseAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animating = false;
                    ExpandableTextView.super.setMaxLines(mMaxLines);
                    setText(mCloseSpannableStr);
                    getLayoutParams().height = mCLoseHeight;
                    requestLayout();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        clearAnimation();
        //  ????????????
        startAnimation(mCloseAnim);
    }

    /**
     * @param spannable
     *
     * @return
     */
private Layout createStaticLayout(SpannableStringBuilder spannable) {
    int contentWidth = initWidth - getPaddingLeft() - getPaddingRight();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        StaticLayout.Builder builder = StaticLayout.Builder.obtain(spannable, 0, spannable.length(), getPaint(), contentWidth);
        builder.setAlignment(Layout.Alignment.ALIGN_NORMAL);
        builder.setIncludePad(getIncludeFontPadding());
        builder.setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier());
        return builder.build();
    }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        return new StaticLayout(spannable, getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
    }else{
        return new StaticLayout(spannable, getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                getFloatField("mSpacingMult",1f), getFloatField("mSpacingAdd",0f), getIncludeFontPadding());
    }
}

    private float getFloatField(String fieldName, float defaultValue){
        float value = defaultValue;
        if(TextUtils.isEmpty(fieldName)){
            return value;
        }
        try {
            // ?????????????????????????????????
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field:fields) {
                if(TextUtils.equals(fieldName,field.getName())){
                    value = field.getFloat(this);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * @param charSequence
     *
     * @return
     */
    private SpannableStringBuilder charSequenceToSpannable(@NonNull CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = null;
        if (mCharSequenceToSpannableHandler != null) {
            spannableStringBuilder = mCharSequenceToSpannableHandler.charSequenceToSpannable(charSequence);
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = new SpannableStringBuilder(charSequence);
        }
        return spannableStringBuilder;
    }

    /**
     * ?????????TextView??????????????????
     *
     * @param width
     */
    public void initWidth(int width) {
        initWidth = width;
    }

    @Override
    public void setMaxLines(int maxLines) {
        this.mMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    /**
     * ??????????????????text
     *
     * @param openSuffix
     */
    public void setOpenSuffix(String openSuffix) {
        mOpenSuffixStr = openSuffix;
        updateOpenSuffixSpan();
    }

    /**
     * ??????????????????????????????
     *
     * @param openSuffixColor
     */
    public void setOpenSuffixColor(@ColorInt int openSuffixColor) {
        mOpenSuffixColor = openSuffixColor;
        updateOpenSuffixSpan();
    }

    /**
     * ??????????????????text
     *
     * @param closeSuffix
     */
    public void setCloseSuffix(String closeSuffix) {
        mCloseSuffixStr = closeSuffix;
        updateCloseSuffixSpan();
    }

    /**
     * ??????????????????????????????
     *
     * @param closeSuffixColor
     */
    public void setCloseSuffixColor(@ColorInt int closeSuffixColor) {
        mCloseSuffixColor = closeSuffixColor;
        updateCloseSuffixSpan();
    }

    /**
     * ??????????????????????????????
     *
     * @param closeInNewLine
     */
    public void setCloseInNewLine(boolean closeInNewLine) {
        mCloseInNewLine = closeInNewLine;
        updateCloseSuffixSpan();
    }

    /** ??????????????????Spannable */
    private void updateOpenSuffixSpan() {
        if (TextUtils.isEmpty(mOpenSuffixStr)) {
            mOpenSuffixSpan = null;
            return;
        }
        mOpenSuffixSpan = new SpannableString(mOpenSuffixStr);
        mOpenSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mOpenSuffixSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                switchOpenClose();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mOpenSuffixColor);
                ds.setUnderlineText(false);
            }
        },0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /** ??????????????????Spannable */
    private void updateCloseSuffixSpan() {
//        if (TextUtils.isEmpty(mCloseSuffixStr)) {
//            mCloseSuffixSpan = null;
//            return;
//        }
//        mCloseSuffixSpan = new SpannableString(mCloseSuffixStr);
//        mCloseSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        if (mCloseInNewLine) {
//            AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
//            mCloseSuffixSpan.setSpan(alignmentSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        mCloseSuffixSpan.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                switchOpenClose();
//            }
//
//            @Override
//            public void updateDrawState(@NonNull TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(mCloseSuffixColor);
//                ds.setUnderlineText(false);
//            }
//        },1, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public OpenAndCloseCallback mOpenCloseCallback;
    public void setOpenAndCloseCallback(OpenAndCloseCallback callback){
        this.mOpenCloseCallback = callback;
    }

    public interface OpenAndCloseCallback{
        void onOpen();
        void onClose();
    }
    /**
     * ????????????????????????
     *
     * @param handler
     */
    public void setCharSequenceToSpannableHandler(CharSequenceToSpannableHandler handler) {
        mCharSequenceToSpannableHandler = handler;
    }

    public interface CharSequenceToSpannableHandler {
        @NonNull
        SpannableStringBuilder charSequenceToSpannable(CharSequence charSequence);
    }

    class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;//????????????view
        private final int mStartHeight;//???????????????????????????
        private final int mEndHeight;//????????????????????????

        ExpandCollapseAnimation(View target, int startHeight, int endHeight) {
            mTargetView = target;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(400);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mTargetView.setScrollY(0);
            //????????????????????????????????????,????????????view????????????????????????
            mTargetView.getLayoutParams().height = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTargetView.requestLayout();
        }
    }
}
