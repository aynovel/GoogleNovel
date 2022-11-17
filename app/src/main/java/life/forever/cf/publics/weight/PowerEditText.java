package life.forever.cf.publics.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import life.forever.cf.R;
import life.forever.cf.publics.OnTextChangeListener;

public class PowerEditText extends AppCompatEditText {


    private static final int TYPE_NORMAL = -1;

    private static final int TYPE_CAN_CLEAR = 0;

    private static final int TYPE_CAN_WATCH_PWD = 1;


    private Drawable mRightDrawable;

    private Drawable mEyeOpenDrawable;


    private final int funcType;


    private boolean eyeOpen = false;

    private final int eyeCloseResourseId;

    private final int eyeOpenResourseId;

    private final TypedArray ta;

    private OnRightClickListener onRightClickListener;
    private OnTextChangeListener onTextChangeListener;

    public PowerEditText(Context context) {
        this(context, null);
    }

    public PowerEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public PowerEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ta = context.obtainStyledAttributes(attrs, R.styleable.PowerEditText);
        funcType = ta.getInt(R.styleable.PowerEditText_funcType, TYPE_NORMAL);
        eyeCloseResourseId = ta.getResourceId(R.styleable.PowerEditText_eyeClose, R.drawable.yyd);
        eyeOpenResourseId = ta.getResourceId(R.styleable.PowerEditText_eyeOpen, R.drawable.tty);
        init();
    }


    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,左上右下
        Drawable leftDrawable = getCompoundDrawables()[0];
        mRightDrawable = getCompoundDrawables()[2];

        if (mRightDrawable == null) {
            //如果右侧没有图标
            if (funcType == TYPE_CAN_CLEAR) {
                //有清除功能，设置默认叉号选择器
                mRightDrawable = getResources().getDrawable(R.drawable.ear_icon);
            } else if (funcType == TYPE_CAN_WATCH_PWD) {
                //有查看密码功能，设置默认查看密码功能
                mRightDrawable = getResources().getDrawable(eyeCloseResourseId);
                mEyeOpenDrawable = getResources().getDrawable(eyeOpenResourseId);
            }
        }

        if (leftDrawable != null) {
            int leftWidth = ta.getDimensionPixelOffset(R.styleable.PowerEditText_leftDrawableWidth, leftDrawable.getIntrinsicWidth());
            int leftHeight = ta.getDimensionPixelOffset(R.styleable.PowerEditText_leftDrawableHeight, leftDrawable.getIntrinsicHeight());
            leftDrawable.setBounds(0, 0, leftWidth, leftHeight);
        }

        if (mRightDrawable != null) {
            int rightWidth = ta.getDimensionPixelOffset(R.styleable.PowerEditText_rightDrawableWidth, mRightDrawable.getIntrinsicWidth());
            int rightHeight = ta.getDimensionPixelOffset(R.styleable.PowerEditText_rightDrawableWidth, mRightDrawable.getIntrinsicHeight());
            mRightDrawable.setBounds(0, 0, rightWidth, rightHeight);
            if (mEyeOpenDrawable != null) {
                mEyeOpenDrawable.setBounds(0, 0, rightWidth, rightHeight);
            }
            if (funcType == TYPE_CAN_CLEAR) {
                //如果是清除功能
                String content = getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    //初始化内容不为空，则不隐藏右侧图标
                    setRightIconVisible(true);
                    setSelection(content.length());
                } else {
                    setRightIconVisible(false);//隐藏右侧图标
                }
            } else {
                //如果不是清除功能,不隐藏右侧默认图标
                setRightIconVisible(true);
            }
            //设置输入框里面内容发生改变的监听
            addTextChangedListener(new TextWatcher() {
                /**
                 * 当输入框里面内容发生变化的时候回调的方法
                 */
                @Override
                public void onTextChanged(CharSequence s, int start, int count, int after) {
                    //如果是带有清除功能的类型，当文本内容发生变化的时候，根据内容的长度是否为0进行隐藏或显示
                    if (funcType == TYPE_CAN_CLEAR) {
                        setRightIconVisible(s.length() > 0);
                    }
                    if (onTextChangeListener != null) {
                        onTextChangeListener.onTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (onTextChangeListener != null) {
                        onTextChangeListener.beforeTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (onTextChangeListener != null) {
                        onTextChangeListener.afterTextChanged(s);
                    }
                }

            });
        }

        ta.recycle();
    }


    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean isTouched = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (isTouched) {
                    if (onRightClickListener == null) {
                        if (funcType == TYPE_CAN_CLEAR) {
                            //如果没有设置右边图标的点击事件，并且带有清除功能，默认清除文本
                            this.setText("");
                        } else if (funcType == TYPE_CAN_WATCH_PWD) {
                            //如果没有设置右边图标的点击事件，并且带有查看密码功能，点击切换密码查看方式
                            if (eyeOpen) {
                                //变为密文 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
                                this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                eyeOpen = false;
                            } else {
                                //变为明文
                                this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                eyeOpen = true;
                            }
                            switchWatchPwdIcon();//切换图标
                        }
                    } else {
                        //如果有则回调
                        onRightClickListener.onClick(this);
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置右侧图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setRightIconVisible(boolean visible) {
        Drawable right = visible ? mRightDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 切换查看密码的图标
     */
    private void switchWatchPwdIcon() {
        if (eyeOpen) {
            //开启查看
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], mEyeOpenDrawable, getCompoundDrawables()[3]);
        } else {
            //关闭查看
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], mRightDrawable, getCompoundDrawables()[3]);
        }
    }

    public interface OnRightClickListener {
        void onClick(EditText editText);
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

}
