package life.forever.cf.publics.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KOL extends FrameLayout implements Constant {


    public final static int TYPE_TIP = ZERO;
    public final static int TYPE_BUTTON = ONE;

    public final static int TYPE_SWITCH = TWO;

    @BindView(R.id.root)
    View root;
    @BindView(R.id.itemIcon)
    ImageView mItemIcon;
    @BindView(R.id.itemTitle)
    TextView mItemTitle;
    @BindView(R.id.itemSubTitle)
    TextView mItemSubTitle;
    @BindView(R.id.button)
    TextView mButton;
    @BindView(R.id.tipGroup)
    LinearLayout mTipGroup;
    @BindView(R.id.tip)
    TextView mTip;
    @BindView(R.id.checkBox)
    CheckBox mCheckBox;

    public KOL(Context context) {
        this(context, null);
    }

    public KOL(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(context).inflate(R.layout.view_item, this, Constant.TRUE);
        ButterKnife.bind(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.aiyeItemView);
        Drawable itemIcon = array.getDrawable(R.styleable.aiyeItemView_itemIcon);
        if (itemIcon != null) {
            mItemIcon.setImageDrawable(itemIcon);
        }
        String title = array.getString(R.styleable.aiyeItemView_itemTitle);
        if (!TextUtils.isEmpty(title)) {
            mItemTitle.setText(title);
        }
        String subTitle = array.getString(R.styleable.aiyeItemView_subTitle);
        setSubTitle(subTitle);
        int type = array.getInt(R.styleable.aiyeItemView_itemType, TYPE_TIP);
        setType(type);
        String tip = array.getString(R.styleable.aiyeItemView_itemTip);
        setTip(tip);

        int paddingLeft = array.getDimensionPixelOffset(R.styleable.aiyeItemView_paddingLeft, ZERO);
        int paddingRight = array.getDimensionPixelOffset(R.styleable.aiyeItemView_paddingRight, ZERO);
        root.setPadding(paddingLeft, ZERO, paddingRight, ZERO);

        array.recycle();

    }

    /**
     * 设置副标题
     *
     * @param subTitle
     */
    public void setSubTitle(String subTitle) {
        mItemSubTitle.setText(subTitle);
        if (TextUtils.isEmpty(subTitle)) {
            mItemSubTitle.setVisibility(GONE);
        } else {
            mItemSubTitle.setVisibility(VISIBLE);
        }
    }

    /**
     * 获取tip控件，用于配置显示样式
     *
     * @return
     */
    public TextView getTip() {
        return mTip;
    }

    /**
     * 设置显示类型
     *
     * @param type {@link KOL#TYPE_BUTTON},{@link KOL#TYPE_TIP},{@link KOL#TYPE_SWITCH}
     */
    public void setType(int type) {
        if (type == TYPE_BUTTON) {
            mButton.setVisibility(VISIBLE);
            mTipGroup.setVisibility(GONE);
            mCheckBox.setVisibility(GONE);
        } else if (type == TYPE_SWITCH) {
            mButton.setVisibility(GONE);
            mTipGroup.setVisibility(GONE);
            mCheckBox.setVisibility(VISIBLE);
        } else {
            mButton.setVisibility(GONE);
            mTipGroup.setVisibility(VISIBLE);
            mCheckBox.setVisibility(GONE);
        }
    }

    /**
     * 设置tip内容
     *
     * @param tip
     */
    public void setTip(String tip) {
        mTip.setText(tip);
    }

    /**
     * 获取button控件，用于配置显示样式
     *
     * @return
     */
    public TextView getButton() {
        return mButton;
    }

    /**
     * 设置button内容
     *
     * @param text
     */
    public void setButton(String text) {
        mButton.setText(text);
    }

    /**
     * 设置CheckBox选中状态
     *
     * @param check
     */
    public void setCheck(boolean check) {
        mCheckBox.setChecked(check);
    }

    /**
     * 获取CheckBox选中状态
     *
     * @return
     */
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

}
