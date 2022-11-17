package life.forever.cf.publics;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.publics.weight.TitleBar;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;
import life.forever.cf.publics.weight.poputil.SinglePermissionHelpAlertDialog;
import com.google.firebase.analytics.FirebaseAnalytics;


public abstract class BaseFragmentActivity extends AppCompatActivity implements Constant {

    protected Context context;
    protected LinearLayout mRootLayout;
    protected TitleBar mTitleBar;
    protected View mLoadingLayout;
    protected FrameLayout mContentLayout;
    protected LinearLayout mWrongLayout;
    public FirebaseAnalytics mFirebaseAnalytics;

    protected AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        context = this;
        mRootLayout = findViewById(R.id.rootLayout);
        mTitleBar = findViewById(R.id.titleBar);
        mLoadingLayout = findViewById(R.id.loadingLayout);

        mContentLayout = findViewById(R.id.contentLayout);
        mWrongLayout = findViewById(R.id.wrongLayout);
        mWrongLayout.setOnClickListener(onReloadClick);

//        GifImageView mLoading = findViewById(R.id.loading);
//        GlideUtil.load(context, R.drawable.loading, ZERO, mLoading);

        ImageView mLoading = findViewById(R.id.loading);
//        GlideUtil.load(context, R.drawable.loading, ZERO, mLoading);
        Glide.with(context).asGif().load(R.drawable.loading).into(mLoading);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ScreenUtil.transparentStatusBar(this);
//            ScreenUtil.setStatusBarDark(this,false);
//            mRootLayout.setFitsSystemWindows(TRUE);
//        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        initializeView();
        initializeData();

//        if (context instanceof SplashActivity
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            AllPermissionHelpAlertDialog.show(context, new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    requestPermissions();
//                }
//            });
//        } else {
//            requestPermission();
//        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                initializeData();
            } else {
                showStorageRational();
            }
        } else {
            showPhoneRational();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults[ONE] == PackageManager.PERMISSION_GRANTED) {
                    initializeData();
                } else {
                    showStorageRational();
                }
            } else {
                showPhoneRational();
            }
        } else if (requestCode == PERMISSION_PHONE) {
            if (grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    initializeData();
                } else {
                    showStorageRational();
                }
            } else {
                showPhoneRational();
            }
        } else if (requestCode == PERMISSION_STORAGE) {
            if (grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                initializeData();
            } else {
                showStorageRational();
            }
        }
    }

    private void toAppDetails(String permissionTitle, String permissionHelp) {
        SinglePermissionHelpAlertDialog.show(this, permissionTitle, permissionHelp, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_SETTINGS);
            }
        });
    }

    private void permissionRequestAgain(String permissionTitle, String permissionHelp, final String permission, final int requestCode) {
        SinglePermissionHelpAlertDialog.show(this, permissionTitle, permissionHelp, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(BaseFragmentActivity.this, new String[]{permission}, requestCode);
            }
        });
    }

    private void showPhoneRational() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BaseFragmentActivity.this, Manifest.permission.READ_PHONE_STATE)) {
            permissionRequestAgain(PERMISSION_PHONE_TITLE, PERMISSION_PHONE_HELP, Manifest.permission.READ_PHONE_STATE, PERMISSION_PHONE);
        } else {
            toAppDetails(PERMISSION_PHONE_TITLE, PERMISSION_PHONE_HELP);
        }
    }

    private void showStorageRational() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BaseFragmentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionRequestAgain(PERMISSION_STORAGE_TITLE, PERMISSION_STORAGE_HELP, Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_STORAGE);
        } else {
            toAppDetails(PERMISSION_STORAGE_TITLE, PERMISSION_STORAGE_HELP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_SETTINGS) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    initializeData();
                } else {
                    showStorageRational();
                }
            } else {
                showPhoneRational();
            }
        }
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(context).inflate(layoutResID, mContentLayout, true);
    }

    private final View.OnClickListener onReloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reload();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(context);
    }

    /**
     * 初始化界面
     */
    protected abstract void initializeView();

    /**
     * 请求数据
     */
    protected abstract void initializeData();

    /**
     * 重载数据
     */
    protected void reload() {

    }

    /**
     * 展示loading弹窗
     *
     * @param tip
     */
    public void showLoading(String tip) {
        dismissLoading();
        loadingDialog = LoadingAlertDialog.show(context, tip);
    }

    /**
     * 隐藏loading弹窗
     */
    public void dismissLoading() {
        LoadingAlertDialog.dismiss(loadingDialog);
    }
}
