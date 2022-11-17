package life.forever.cf.activtiy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;


public class TipAddShelfAlertDialog implements Constant {

    public static void show(Context context, final View.OnClickListener onNoClick, final View.OnClickListener onYesClick) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_add_shelf_tip_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(FALSE);
        final AlertDialog dialog = adb.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



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

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onNoClick != null) {
                    onNoClick.onClick(v);
                }
            }
        });

        dialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onYesClick != null) {
                    onYesClick.onClick(v);
                }
            }
        });
    }

}
