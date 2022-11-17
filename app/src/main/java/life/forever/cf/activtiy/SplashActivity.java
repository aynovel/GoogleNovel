package life.forever.cf.activtiy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import life.forever.cf.R;

import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import com.kc.openset.OSETListener;
import com.kc.openset.OSETSplash;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.fl)
    FrameLayout fl;
    public SharedPreferences config;

    private boolean isOnPause = false;//判断是否跳转了广告落地页
    private boolean isClick = false;//是否进行了点击
    private String mRecId;

    @SuppressLint("MissingPermission")
    @Override
    protected void initializeView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRootLayout.setFitsSystemWindows(FALSE);
        }
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

    }


    @Override
    protected void initializeData() {

        PlotRead.getAppUser().fetchUserInfoNOHide(this);
        SharedPreferencesUtil.putInt(Constant.APP,"Breathing",1);
        OSETSplash.getInstance().show(this, fl, "1E4F9AF326F7A047277E16A268594B4A", new OSETListener() {
            @Override
            public void onShow() {
                Log.e("openseterror", "onShow");
            }

            @Override
            public void onError(String s, String s1) {
                Log.e("openseterror", "onError——————code:" + s + "----message:" + s1);
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onClick() {
                isClick = true;
                Log.e("openseterror", "onClick");
            }

            @Override
            public void onClose() {
                Log.e("aaaaaaa", "onclose");
                if (!isOnPause) {//如果已经调用了onPause说明已经跳转了广告落地页
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isOnPause) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isClick) {
            isOnPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        OSETSplash.getInstance().destroy();
        super.onDestroy();
    }





}
