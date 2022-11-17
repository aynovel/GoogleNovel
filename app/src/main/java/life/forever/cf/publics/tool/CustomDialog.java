package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import life.forever.cf.R;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener{
    private final Context mContext;
    private Button mCancel, mDelete;
    private TextView mTvIntroduce;
    private String mInfo;

    private OnDialogClickListener onDialogClickListener;

    public interface OnDialogClickListener {
        void onClick(View view);
    }

    public void setOnClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    public CustomDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomDialog(Context context, String info, OnDialogClickListener listener) {
        super(context);
        mContext = context;
        mInfo = info;
        this.onDialogClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.custom_dialog);

        mTvIntroduce = findViewById(R.id.tv_introduce);
        mTvIntroduce.setText(mInfo);
        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mDelete = findViewById(R.id.delete);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onDialogClickListener.onClick(v);
    }

}
