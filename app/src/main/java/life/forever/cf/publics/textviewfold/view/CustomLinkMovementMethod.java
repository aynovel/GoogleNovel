package life.forever.cf.publics.textviewfold.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Blog : https://blog.csdn.net/qq_37077360
 * 功能：
 * clickSpan和外部viewGroup点击事件冲突
 * ——点击时，判断当前点击的区域是否有ClickableSpan，如果有，则拦截TextView的onTouchEvent方法，否则不拦截
 */

public class CustomLinkMovementMethod extends LinkMovementMethod {
    private ClickableSpan mPressedSpan;
    static CustomLinkMovementMethod instance;

    public static CustomLinkMovementMethod getInstance() {
        if (instance == null)
            instance = new CustomLinkMovementMethod();
        return instance;
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        Log.d("文本执行", "CustomLinkMovementMethod  onTouchEvent()  " + event.getAction());
        int action = event.getAction();
        //以下 基本copy 的LinkMovementMethod
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event);
            if (mPressedSpan != null) {
                if (action == MotionEvent.ACTION_UP) {
                    mPressedSpan.onClick(textView);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(spannable,
                            spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }

            } else {
                Selection.removeSelection(spannable);
            }

        } else {
            mPressedSpan = null;
            Selection.removeSelection(spannable);
        }

        return mPressedSpan != null;


        /*switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));//选中从索引start 到索引stop 的区域
                }
                break;
            case MotionEvent.ACTION_MOVE:
                ClickableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
                break;
            default:
                if (mPressedSpan != null) {
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);

                break;
        }*/

        // 如果点击区域有ClickableSpan，则拦截TextView的onTouchEvent方法
        /*return mPressedSpan != null;*/

        /*return super.onTouchEvent(widget, buffer, event);*/
        //始终会返回true。
        //改为false发现 TextView 还是会消费事件。究其原因，是因为我们在调用 TextView.setMovementMethod() 的时候源码调用了 fixFocusableAndClickableSettigns()，
        //需要设置setFocusable(false); setClickable(false);setLongClickable(false);
    }

    /**
     * 获取点击区域的Clickspan对象
     * —— 基本copy LinkMovementMethod
     * @param textView
     * @param spannable
     * @param event
     * @return
     */
    private ClickableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();//触摸点相对于其所在组件原点的x坐标
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();//获取触摸点距离可绘制区域(即减去padding后的区域)边界的距离
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();//获取触摸点相对于屏幕原点的偏移量，一般为0，除非可以滑动。其中view.getScrollX()获取的不是当前View相对于屏幕原点的偏移量，而是当前View可绘制区域（显示区域，-padding）相对于屏幕原点在Y轴上的偏移量
        y += textView.getScrollY();
        Log.d("文本", "getScrollX() = " + textView.getScrollX());
        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);//该点击位置的行号----触摸点在textView中垂直方向上的行数值。参数是触摸点在Y轴上的偏移量
        int off = layout.getOffsetForHorizontal(line, x);//该点击位置的字符索引-----触摸点在某一行水平方向上的偏移量。参数分别是：该行行数值 和 触摸点在该行X轴上的偏移量。此方法得到的该值会根据该行上的文字的多少而变化，并不是横向上的像素大小。

        Log.d("文本", "line = " + line + "  off = " + off);//打印结果：line = 1  off = 41 和 line = 8  off = 170

        ClickableSpan[] link = spannable.getSpans(off, off, ClickableSpan.class);//获取点击位置存在的ClickableSpan对象
        ClickableSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }

    public boolean isPressedSpan() {
        return mPressedSpan != null;
    }

}
