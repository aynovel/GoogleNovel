package life.forever.cf.activtiy;

import life.forever.cf.interfaces.BaseContract;

public abstract class ReaderBaseMvpActivity<T extends BaseContract.BasePresenter> extends ReaderBaseActivity {
    protected  T mPresenter;
    protected  abstract T bindPresenter();


    @Override
    protected void processLogic() {
        attachView(bindPresenter());
    }

    private void attachView(T presenter){
        if(presenter != null)
        {
            mPresenter = presenter;
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null)
        {
            mPresenter.detachView();
        }
    }

}
