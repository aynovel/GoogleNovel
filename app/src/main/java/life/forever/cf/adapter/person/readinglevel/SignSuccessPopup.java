package life.forever.cf.adapter.person.readinglevel;

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
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignSuccessPopup extends PopupWindow implements Constant {

    Activity activity;

    @BindView(R.id.reward)
    TextView mReward;
    @BindView(R.id.signDays)
    TextView mSignDays;
    @BindView(R.id.nextReward)
    TextView mNextReward;

    public SignSuccessPopup(Activity activity, int voucher, int experience, int signDays, int nextDays) {
        this.activity = activity;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_sign_success_popup, null, FALSE);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_scale_alpha_style);
        setOutsideTouchable(FALSE);
        setFocusable(TRUE);
        setBackgroundDrawable(new ColorDrawable());
        mReward.setText(String.format(Locale.getDefault(), activity.getString(R.string.task_award_description), voucher, experience));
        mSignDays.setText(String.format(Locale.getDefault(), activity.getString(R.string.mine_sign_continuous), signDays));
        mNextReward.setText(String.format(Locale.getDefault(), activity.getString(R.string.mine_future_sign_days), nextDays));
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

    @OnClick(R.id.doTask)
    void onDoTaskClick() {
        dismiss();
        if (activity instanceof SignAndWelfareActivity) {
            return;
        }
        Intent intent = new Intent(activity, SignAndWelfareActivity.class);
        activity.startActivity(intent);
    }
}
