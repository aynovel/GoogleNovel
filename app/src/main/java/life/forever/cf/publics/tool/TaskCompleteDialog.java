package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.TaskReword;
import life.forever.cf.interfaces.ReceviedRewardCallBack;


public class TaskCompleteDialog extends Dialog {

    private final Context mContext;
    private final TaskReword mTaskReword;

    public TaskCompleteDialog(final Context context, TaskReword taskReword, ReceviedRewardCallBack callBack) {
        super(context, R.style.Theme_Report_Dialog);
        mContext = context;
        mTaskReword = taskReword;
        this.setContentView(R.layout.task_complete_dialog);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        WindowManager.LayoutParams params;
        if (window != null) {
            params = window.getAttributes();
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (params != null) {
                params.width = (int) (dm.widthPixels * 0.98);
            }
            window.setAttributes(params);
        }

        //收入
        TextView mNumber =  findViewById(R.id.tvNumber);
        TextView mTaskName = findViewById(R.id.tvTaskName);

        if (mTaskReword != null){
            mTaskName.setText(mTaskReword.title);
            mNumber.setText("+"+mTaskReword.giving+" bonus");
        }

        mTaskName.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(callBack != null)
                {
                    callBack.getReceviedRewardResult(true);
                }
                dismiss();
            }
        },1000);

    }

}
