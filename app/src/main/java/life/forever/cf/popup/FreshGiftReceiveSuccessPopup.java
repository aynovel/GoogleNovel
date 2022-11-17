package life.forever.cf.popup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.adapter.person.account.VoucherActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FreshGiftReceiveSuccessPopup extends PopupWindow implements Constant {

    Activity activity;

    @BindView(R.id.msg)
    TextView mMsg;

    FreshGiftReceiveSuccessPopup(Activity activity, String msg) {
        this.activity = activity;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_fresh_gift_receive_success_popup, null, FALSE);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_scale_alpha_style);
        setOutsideTouchable(FALSE);
        setFocusable(TRUE);
        setBackgroundDrawable(new ColorDrawable());
        mMsg.setText(msg);
    }

    public void show(View parent) {
        ComYou.setWindowAlpha(activity, DOT_FIVE);
        showAtLocation(parent, Gravity.CENTER, ZERO, ZERO);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ComYou.setWindowAlpha(activity, ONE);
    }

    @OnClick(R.id.checkNow)
    void onCheckNowClick() {
        dismiss();
        Intent intent = new Intent(activity, VoucherActivity.class);
        activity.startActivity(intent);
    }
}
