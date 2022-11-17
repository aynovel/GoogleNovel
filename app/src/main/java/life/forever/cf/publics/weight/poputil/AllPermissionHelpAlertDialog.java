package life.forever.cf.publics.weight.poputil;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;

import java.util.Locale;

public class AllPermissionHelpAlertDialog implements Constant {

    public static void show(Context context, final View.OnClickListener onClickListener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_all_permission_help_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(FALSE);
        final AlertDialog dialog = adb.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = dialog.findViewById(R.id.title);
        title.setText(String.format(Locale.getDefault(), context.getString(R.string.permission_to_illustrate), context.getString(R.string.app_name)));

        dialog.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
    }
}
