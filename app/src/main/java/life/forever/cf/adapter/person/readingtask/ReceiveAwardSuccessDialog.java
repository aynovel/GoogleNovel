package life.forever.cf.adapter.person.readingtask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.Task;
import life.forever.cf.adapter.person.account.VoucherActivity;
import life.forever.cf.publics.Constant;

import java.util.Locale;


public class ReceiveAwardSuccessDialog implements Constant {

    public static void show(final Context context, String title, Task task, final DialogInterface.OnDismissListener onDismissListener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_receive_award_success_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        final AlertDialog dialog = adb.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tips = window.findViewById(R.id.tips);
        TextView reward = window.findViewById(R.id.reward);
        final TextView jump = window.findViewById(R.id.jump);
        window.findViewById(R.id.checkNow).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VoucherActivity.class);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 <= ZERO) {
                    dialog.dismiss();
                } else {
                    msg.arg1 -= ONE;
                    jump.setText(String.format(Locale.getDefault(), "%s %ds", aiye_STRING_JUMP, msg.arg1));

                    Message message = Message.obtain();
                    message.what = ZERO;
                    message.arg1 = msg.arg1;
                    sendMessageDelayed(message, ONE_THOUSAND);
                }
            }
        };

        tips.setText(title);
        reward.setText(String.format(Locale.getDefault(), TASK_STRING_AWARD_DESCRIPTION, task.giving, task.experience));
        if (task.id == 19) { // 绑定手机号任务
            jump.setVisibility(View.VISIBLE);
            jump.setText(String.format(Locale.getDefault(), "%s %ds", aiye_STRING_JUMP, FIVE));
            Message message = Message.obtain();
            message.what = ZERO;
            message.arg1 = FIVE;
            handler.sendMessageDelayed(message, ONE_THOUSAND);
        } else {
            jump.setVisibility(View.GONE);
        }

        jump.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacksAndMessages(null);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(dialog);
                }
            }
        });

    }
}
