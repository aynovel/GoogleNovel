package life.forever.cf.publics.weight.poputil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;


public class Pop extends PopupWindow implements Constant {

    Activity activity;

    @BindView(R.id.unknown)
    RadioButton mUnknown;
    @BindView(R.id.boy)
    RadioButton mBoy;
    @BindView(R.id.girl)
    RadioButton mGirl;

    private int sexIndex = ZERO;

    public Pop(Activity activity, OnDismissListener onDismissListener) {
        this.activity = activity;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_oice_pop, null);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_slide_alpha_bottom_style);
        setOutsideTouchable(FALSE);
        setFocusable(TRUE);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(root);
        ButterKnife.bind(this, root);
        setOnDismissListener(onDismissListener);
    }

    public void show(View parent, int sexIndex) {
        switch (sexIndex) {
            case ONE:
                mBoy.setChecked(TRUE);
                break;
            case TWO:
                mGirl.setChecked(TRUE);
                break;
            default:
                mUnknown.setChecked(true);
                break;
        }
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
        ComYou.setWindowAlpha(activity, DOT_FIVE);
    }

    @Override
    public void dismiss() {
        ComYou.setWindowAlpha(activity, ONE);
        super.dismiss();
    }

    @OnCheckedChanged(R.id.unknown)
    void onUnknownChanged(boolean check) {
        if (check) {
            mUnknown.setChecked(TRUE);
            sexIndex = ZERO;
            dismiss();
        }
    }

    @OnCheckedChanged(R.id.boy)
    void onBoyChanged(boolean check) {
        if (check) {
            mBoy.setChecked(TRUE);
            sexIndex = ONE;
            dismiss();
        }
    }

    @OnCheckedChanged(R.id.girl)
    void onGirlChanged(boolean check) {
        if (check) {
            mGirl.setChecked(TRUE);
            sexIndex = TWO;
            dismiss();
        }
    }

    /**
     * 获取性别索引
     *
     * @return
     */
    public int getSexIndex() {
        return sexIndex;
    }

}
