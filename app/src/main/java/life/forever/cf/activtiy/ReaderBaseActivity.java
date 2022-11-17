package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import life.forever.cf.publics.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class ReaderBaseActivity extends BaseActivity {


    protected CompositeDisposable mDisposable;


    private Unbinder unbinder;

    @LayoutRes
    protected abstract int getContentId();

    /************************init area************************************/
    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    /**
     * 配置Toolbar
     *
     * @param toolbar
     */
    protected void setUpToolbar(Toolbar toolbar) {

    }

    protected void initData() {

    }



    /**
     * 初始化零件
     */
    protected void initWidget() {

    }

    /**
     * 初始化点击事件
     */
    protected void initClick() {
    }

    /**
     * 逻辑使用区
     */
    protected void processLogic() {
    }

    /*************************lifecycle area*****************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(getContentId());
        unbinder = ButterKnife.bind(this);
        initWidget();

        processLogic();

        initData();
        initClick();

//        View decorView = getWindow().getDecorView();
//        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(option);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        if (Build.VERSION.SDK_INT >= 28) {
//            WindowManager.LayoutParams params = getWindow().getAttributes();
//            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            getWindow().setAttributes(params);
//        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }


    @Override
    protected void initializeView() {
//        setContentView(getContentId());
//        unbinder = ButterKnife.bind(this);
//        initWidget();
//        initClick();
//        processLogic();
    }

    @Override
    protected void initializeData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**************************used method area*******************************************/

    protected void startActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected ActionBar supportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        return actionBar;
    }
}
