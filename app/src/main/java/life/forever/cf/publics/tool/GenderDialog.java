package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import life.forever.cf.R;

public class GenderDialog extends Dialog implements View.OnClickListener{
    private final Context mContext;
    private TextView mTvMale, mTvFemale, mTvCancel;
    private String mType;

    private OnDialogClickListener onDialogClickListener;

    public interface OnDialogClickListener {
        void onClick(View view);
    }

    public void setOnClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    public GenderDialog(Context context) {
        super(context);
        mContext = context;
    }

    public GenderDialog(Context context, String type, int theme, OnDialogClickListener listener) {
        super(context);
        mContext = context;
        mType = type;
        this.onDialogClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gender_dialog);
        mTvMale = findViewById(R.id.tv_male);
        mTvFemale = findViewById(R.id.tv_female);
        if("gender".equals(mType)){
            mTvMale.setText("Male");
            mTvFemale.setText("Female");
        }else {
            mTvMale.setText("Photo");
            mTvFemale.setText("Select from album");
        }
        mTvMale.setOnClickListener(this);

        mTvFemale.setOnClickListener(this);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onDialogClickListener.onClick(v);
    }

}
