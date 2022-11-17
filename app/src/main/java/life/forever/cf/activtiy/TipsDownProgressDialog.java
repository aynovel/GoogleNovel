package life.forever.cf.activtiy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.forever.cf.R;

public class TipsDownProgressDialog extends Dialog {
    public TipsDownProgressDialog(@NonNull Context context) {
        super(context, R.style.ReadSettingDialog);
    }

    public TipsDownProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TipsDownProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_download_tips);

        setUpWindow();

        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    @Override
    public void show() {
        super.show();
        //这样后边的view怎么获取焦点呢？ 谁调用 谁就可以获取焦点 如下
        // mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //mWm.updateViewLayout(view, mParams);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager
                .LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    public void freashProgressText(String text)
    {
        TextView progressTextView = findViewById(R.id.tips_progress);
        if(progressTextView != null && text != null)
        {
            progressTextView.setText(text);
        }
    }
}
