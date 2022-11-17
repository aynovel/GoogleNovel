package life.forever.cf.activtiy;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract public class ReaderFullScreenBaseActivity extends AppCompatActivity {


    protected CompositeDisposable mDisposable;

    private Unbinder unbinder;

    /****************************abstract area*************************************/

    @LayoutRes
    protected abstract int getContentId();

    /************************init area************************************/



    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        unbinder = ButterKnife.bind(this);


        initWidget();
        initData();
        initClick();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

}
