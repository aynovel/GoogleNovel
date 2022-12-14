package life.forever.cf.publics.weight.poputil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.publics.CommonUtil;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ApkDownloadManager;

import java.io.File;
import java.util.Locale;

import okhttp3.Request;

public class ForceUpdateAlertDialog implements Constant {

    public static void show(final Activity context, String version, String intro, final String url) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_force_update_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(FALSE);
        final AlertDialog dialog = adb.create();
        if (CommonUtil.isDestroy(context)) {
            return;
        }
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView name = dialog.findViewById(R.id.versionName);
        TextView description = dialog.findViewById(R.id.versionDescription);
        final View update = dialog.findViewById(R.id.update);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final View install = dialog.findViewById(R.id.install);

        description.setMovementMethod(ScrollingMovementMethod.getInstance());
        name.setText(String.format(Locale.getDefault(), context.getString(R.string.find_version), version));
        description.setText(intro);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)) {
                    update.clearAnimation();
                    update.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    ApkDownloadManager.downloadFile(url, new ApkDownloadManager.DownloadCallBack() {

                        @Override
                        public void onError(Request request, Exception e) {
                            dialog.dismiss();
                            PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                        }

                        @Override
                        public void onResponse(final Object response) {
                            install(context);
                            install.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onProgress(int total, int current) {
                            progressBar.setMax(total);
                            progressBar.setProgress(current);
                        }
                    });
                } else {
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }
                }

            }
        });
        install.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                install(context);
            }
        });
    }

    /**
     * ??????7.0??????????????????
     */
    private static void install(Context context) {
        File apk = new File(context.getExternalCacheDir(), "ZdVersionUpdate.apk");
        if (!apk.exists()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder mBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(mBuilder.build());
        }

        Uri contentUri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apk);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            contentUri = Uri.fromFile(apk);
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
