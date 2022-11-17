package life.forever.cf.publics.weight.poputil;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;

import java.util.Locale;


public class SinglePermissionHelpAlertDialog implements Constant {

    public static void show(final Activity activity, String permission, String description, final View.OnClickListener openClick) {
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_single_permission_help_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setCancelable(FALSE);
        final AlertDialog dialog = adb.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = dialog.findViewById(R.id.title);
        title.setText(String.format(Locale.getDefault(), activity.getString(R.string.permission_to_illustrate), activity.getString(R.string.app_name)));
        TextView why = dialog.findViewById(R.id.why);
        why.setText(String.format(Locale.getDefault(), activity.getString(R.string.why_need), permission));
        TextView message = dialog.findViewById(R.id.reason);
        message.setText(description);

        dialog.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (openClick != null) {
                    openClick.onClick(v);
                }
                dialog.dismiss();
            }
        });
    }
}
