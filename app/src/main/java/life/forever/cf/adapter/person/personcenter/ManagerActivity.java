package life.forever.cf.adapter.person.personcenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.tool.ManagerDialog;
import life.forever.cf.publics.tool.NotificationsUtils;
import life.forever.cf.publics.tool.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ManagerActivity extends BaseActivity {

    @BindView(R.id.book_update_cb)
    CheckBox mBookUpdateCB;

    @BindView(R.id.auto_lock_cb)
    CheckBox mAutoLockCB;

    private ManagerDialog mManagerDialog;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(BOOK_NOTIFICATIONS);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_manage);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        mBookUpdateCB.setChecked(NotificationsUtils.isNotifyEnabled(this));
        boolean isstate = PlotRead.getConfig().getBoolean(IS_STATE, TRUE);
        mAutoLockCB.setChecked(isstate);
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SHELF_CHANGE) {
            mBookUpdateCB.setChecked(true);
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    @OnClick(R.id.ll_book_update_cb)
    void onBookUpdateClick() {
        mManagerDialog = new ManagerDialog(this);
        mManagerDialog.findViewById(R.id.settings).setOnClickListener(v -> {
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", getApplicationContext().getPackageName());
                localIntent.putExtra("app_uid", getApplicationInfo().uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                } else {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", getApplicationContext().getPackageName());
                }
            }
            startActivity(localIntent);
        });
        mManagerDialog.show();
    }

    @OnClick(R.id.ll_auto_lock_cb)
    void onAutoLockClick() {
        boolean isstate = PlotRead.getConfig().getBoolean(IS_STATE, TRUE);
        if (isstate) {
//            AutoBuyUtil.update("false");
            SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_STATE, FALSE);
            mAutoLockCB.setChecked(false);
        } else {
//            AutoBuyUtil.update("true");
            SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), IS_STATE,TRUE);
            mAutoLockCB.setChecked(true);
        }
//        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.auto_lock));
//        mAutoLockCB.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBookUpdateCB.setChecked(NotificationsUtils.isNotifyEnabled(this));
        if (mManagerDialog != null) {
            mManagerDialog.dismiss();
        }
        boolean isstate = PlotRead.getConfig().getBoolean(IS_STATE, TRUE);
        mAutoLockCB.setChecked(isstate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
