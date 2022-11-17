package life.forever.cf.publics.weight.poputil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;


public class LoadingAlertDialog implements Constant {


    public static AlertDialog show(Context context, String tip) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(FALSE);
        final AlertDialog dialog = adb.create();
        dialog.show();

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return TRUE;
                }
                return FALSE;
            }
        });

        View root = LayoutInflater.from(context).inflate(R.layout.layout_loading_popup, null, FALSE);
        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = window.findViewById(R.id.tip);
        textView.setText(tip);
        textView.setVisibility(TextUtils.isEmpty(tip) ? View.GONE : View.VISIBLE);

        return dialog;
    }

    /**
     * 关闭对话框
     *
     * @param dialog
     */
    public static void dismiss(AlertDialog dialog) {
        try{
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }catch (Exception e){

        }

    }



}
