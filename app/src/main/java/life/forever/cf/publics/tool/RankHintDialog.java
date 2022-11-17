package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import life.forever.cf.R;


public class RankHintDialog extends Dialog implements View.OnClickListener {

    public RankHintDialog(final Context context, String hint) {
        super(context, R.style.Theme_Update_Dialog);
        this.setContentView(R.layout.rank_hint_dialog);
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
        TextView mHint = findViewById(R.id.tv_dialog_hint);
        findViewById(R.id.layout_rank_cancel).setOnClickListener(this);
        mHint.setText(hint);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_rank_cancel) {
            this.dismiss();
        }
    }
}
