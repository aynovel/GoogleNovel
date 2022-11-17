package life.forever.cf.publics.textviewfold.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.publics.textviewfold.listener.OnTextClickListener;

/**
 * Blog : https://blog.csdn.net/qq_37077360
 * <p>
 * Function：
 * 支持全文展开/收起（按钮紧跟着文本后面显示，非另起一行）
 * 可设置收起时最大显示行数；
 * “展开/收起”的具体文字显示、字体颜色；
 * 支持 “展开/收起”点击事件（即，后缀点击事件）、剩余区间点击事件（TextView点击事件）、父容器点击事件。
 * <p>
 * Note:
 * (1)  UTF8编码：中文占3个字节，英文占用1个字节。
 * 所以，“...”占用3个字节，也就是只占一个中文的宽度，因而计算字数时不能- 3（即tag.length()）,而是按一个中文算-1
 * <p>
 * (2)解决父容器点击事件冲突问题：
 * 点击时，判断当前点击的区域是否有ClickableSpan，如果有，则拦截TextView的onTouchEvent方法，交由viewgroup处理，否则不拦截
 * <p>
 * ----方法：
 * <p>
 * ① 写一个类，继承LinkMovementMethod，名字记作CustomLinkMovementMethod
 * ②将场面创建的CustomLinkMovementMethod对象,传给setMovementMethod()
 * ③重写onTouchEvent()
 * <p>
 * (3)解决clickspan点击事件与自身onclick事件冲突问题（点击clickspan，onclick也响应了）
 * <p>
 * ----方法：
 * <p>
 * 禁掉onclick事件（直接设置setClickable(false)无用，一旦外部设置onclicklistener，点击clickspan，还是会执行onclick）,
 * 注释掉setOnClickListener中代码，通过设置setOnTextClickListener()与clickspan实现。
 * <p>
 * reference：
 * 编码长度规则
 * https://blog.csdn.net/yaomingyang/article/details/79374209
 * 点击事件冲突问题（包含点击clickspan之外的文字，上层viewgroup点击事件不响应）
 * https://blog.csdn.net/qqwuy_muzi/article/details/79731163
 * http://blog.csdn.net/zhaizu/article/details/51038113
 * 国外论坛解决方案
 * https://stackoverflow.com/questions/16792963/android-clickablespan-intercepts-the-click-event
 */


public class CollapsibleTextView extends androidx.appcompat.widget.AppCompatTextView {

    Context mContext;
    /**
     * 最大显示行数，其余折叠
     */
    private int collapsedLines;
    /**
     * 折叠时后缀文字
     */
    private String collapsedText;
    /**
     * 展开时，后缀名字
     */
    private String expandedText;
    /**
     * 后缀字体颜色
     */
    private int suffixColor;

    final int defaultLines = 4;//默认显示5行
    final int defaultColor = getResources().getColor(R.color.theme_color);//默认后缀颜色
    final String defaultCollapsedText = "More";//默认收起时的后缀文字
    final String defaultExpandedText = "";//默认展开时的后缀文字

    final String tag = "...";
    /**
     * 文本内容
     */
    String mText;

    String suffix;
    /**
     * 是否展开。默认收起
     */
    boolean mIsneedExpanded;

    /**
     * 局部点击事件: 展开 or 收起
     */
    ClickableSpan mClickSpanListener = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            mIsneedExpanded = !mIsneedExpanded;
            if(!TextUtils.isEmpty(suffix)){
                updateUI(mIsneedExpanded);
            }

        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
            ds.setUnderlineText(false);//点击区域下方是否有下划线。默认true，会像超链接一样显示下划线
            setHighlightColor(Color.TRANSPARENT);//取消选中文字背景色高亮显示
        }
    };
    /**
     * TextView自身点击事件(除后缀外的部分)
     */
    ClickableSpan selfClickSpan = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            if (onTextClickListener != null)
                onTextClickListener.onTextClick(null);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    };

    /**
     * TextView自身点击事件，代替onClickListener
     */
    OnTextClickListener onTextClickListener;

    private CustomLinkMovementMethod customLinkMovementMethod;
    private boolean isNeedEllipsis = true;//是否需要默认的省略点“...”


    public CollapsibleTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CollapsibleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CollapsibleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    /**
     * 初始化操作 ，比如获取某些属性值
     *
     * @param context
     * @param attrs
     */
    private void init(final Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs == null)
            return;

        /*
            获取属性值
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleTextView);
        collapsedLines = typedArray.getInt(R.styleable.CollapsibleTextView_collapsedLines, defaultLines);
        collapsedText = typedArray.getString(R.styleable.CollapsibleTextView_collapsedText);
        expandedText = typedArray.getString(R.styleable.CollapsibleTextView_expandedText);
        suffixColor = typedArray.getInt(R.styleable.CollapsibleTextView_suffixColor, defaultColor);

        typedArray.recycle();


        if (TextUtils.isEmpty(collapsedText))
            collapsedText = defaultCollapsedText;
        if (TextUtils.isEmpty(expandedText))
            expandedText = defaultExpandedText;


        //获取xml中设置的文字内容
        this.mText = getText() == null ? null : getText().toString();
        //使TextView支持局部点击--ClickSpan。【如果不需要对Textview本身以及上层bujurong布局容器进行点击事件，直接添加该句即可。】
//     setMovementMethod(LinkMovementMethod.getInstance());

        //☆☆☆若还需要对TextView的父容器进行点击事件设置，需要判断点击区域有没有clickspan，有自身消费，无则交由上层viewGroup处理。
        customLinkMovementMethod = CustomLinkMovementMethod.getInstance();
        setMovementMethod(customLinkMovementMethod);

    }

    /**
     * 更新文本显示方式：展开 or 收起
     *
     * @param mIsneedExpanded 是否需要展开
     */
    private void updateUI(boolean mIsneedExpanded) {
        try {
        if (TextUtils.isEmpty(mText)) return;

        String temp = mText;
        if (mIsneedExpanded) {//展开
            suffix = expandedText;
        } else { //收起
            suffix = collapsedText;

            if (collapsedLines < 1) {
                throw new RuntimeException("CollapsedLines must larger than 0");
            }

            int lineEnd = getLayout().getLineVisibleEnd(collapsedLines - 1);//第 mCollapsedLines 行打上省略点+后缀【padding不计算在内】
            if (isNeedEllipsis) {//使用默认省略点
                int newEnd = lineEnd - 1 - suffix.length();//不能- tag.length()，实际占空间并不是三个字的大小，而是一个中文大小
                int end = newEnd > 0 ? newEnd : lineEnd;

                temp = temp.substring(0, end) + tag;
            } else {
                int newEnd = lineEnd - suffix.length();//不需要-1
                int end = newEnd > 0 ? newEnd : lineEnd;

                temp = temp.substring(0, end);//不需要+tag
            }


        }

            final SpannableString str = new SpannableString(temp + suffix);

            //设置后缀点击事件
            str.setSpan(mClickSpanListener,
                    temp.length(),
                    temp.length() + suffix.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置后缀文字颜色
            str.setSpan(new ForegroundColorSpan(suffixColor),
                    temp.length(),
                    temp.length() + suffix.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            //设置自身点击事件【避免与自身点击事件冲突，采用剩余部位点击事件实现】
            if (onTextClickListener != null) {//一定要判断。【一般自身点击与父容器点击只存其一。如果不判断，相当于整个TextView均响应clickspan，不会再响应父容器点击事件】
                str.setSpan(selfClickSpan,
                        0,
                        temp.length(),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            post(new Runnable() {
                @Override
                public void run() {
                    setText(str);
                }
            });
        }catch (Exception e){

        }

    }

    /**
     * 使用setFullString代替setText
     *
     * @param str
     */
    public void setFullString(String str) {
        this.mText = str;
        setText(mText);
    }

    /**
     * 是否需要省略点“...”
     *
     * @param isNeedEllipsis
     */
    public void isNeedEllipsis(boolean isNeedEllipsis) {
        this.isNeedEllipsis = isNeedEllipsis;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //由于 getLineCount() 等函数只有在 layout 过程之后值才有意义,所以要合理的选择 updateUI 的时机

        if (/*mShouldInitLayout &&*/ getLineCount() > collapsedLines) {
//            mShouldInitLayout = false;
            updateUI(mIsneedExpanded);
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = super.onTouchEvent(event);
        return customLinkMovementMethod != null ? customLinkMovementMethod.isPressedSpan() : result;
    }


    public void setOnTextClickListener(OnTextClickListener onTextClickListener) {
        this.onTextClickListener = onTextClickListener;
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//         super.setOnClickListener(l);
        //禁止掉TextView本身的点击事件（不禁，则clickspan响应时，同时会执行自身onclick事件）。如果需要使用onclick事件,通过setOnClickListener()实现。
    }

    /**
     * 设置后缀文字颜色
     * @param color
     */
    public void setSuffixColor(@ColorInt int color) {
        suffixColor = color;
    }

    /**
     * 设置收起时的最大显示行数
     * @param collapsedLines
     */
    public void setCollapsedLines(int collapsedLines) {
        this.collapsedLines = collapsedLines;
    }

    /**
     * 设置收起时的后缀文字，eg.“展开”
     * @param collapsedText
     */
    public void setCollapsedText(String collapsedText) {
        this.collapsedText = collapsedText;
    }

    /**
     * 设置展开时的后缀文字，eg.“收起”
     * @param expandedText
     */
    public void setExpandedText(String expandedText) {
        this.expandedText = expandedText;
    }
}
