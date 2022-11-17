package life.forever.cf.interfaces;

public interface BaseContract {
    interface  BasePresenter<T>{
        void attachView(T view);
        void detachView();
    }

    interface  BaseView{
        void showLoading();
        void showError();
        void complete();
    }

}
