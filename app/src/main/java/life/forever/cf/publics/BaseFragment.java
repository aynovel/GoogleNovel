package life.forever.cf.publics;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.publics.weight.TitleBar;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;
import com.google.firebase.analytics.FirebaseAnalytics;


public abstract class BaseFragment extends Fragment implements Constant {

    protected Context context;
    protected LinearLayout mRootLayout;
    protected TitleBar mTitleBar;
    protected View mLoadingLayout;
    protected FrameLayout mContentLayout;
    protected LinearLayout mWrongLayout;
    public FirebaseAnalytics mFirebaseAnalytics;
    protected AlertDialog loadingDialog;

    private View rootView;
    private boolean isFirstVisibleToUser;
    private boolean isFragmentVisible;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        isFirstVisibleToUser = true;
        isFragmentVisible = false;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_base, container, FALSE);
        mRootLayout = view.findViewById(R.id.rootLayout);
        mTitleBar = view.findViewById(R.id.titleBar);
        mLoadingLayout = view.findViewById(R.id.loadingLayout);



        mContentLayout = view.findViewById(R.id.contentLayout);
        mWrongLayout = view.findViewById(R.id.wrongLayout);
        mWrongLayout.setOnClickListener(onReloadClick);

//        GifImageView mLoading = view.findViewById(R.id.loading);
//        GlideUtil.load(context, R.drawable.loading, ZERO, mLoading);
        ImageView mLoading = view.findViewById(R.id.loading);
//        GlideUtil.load(context, R.drawable.loading, ZERO, mLoading);
        Glide.with(context).asGif().load(R.drawable.loading).into(mLoading);

        bindView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisibleToUser) {
                    isFirstVisibleToUser = false;
                    fetchData();
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(rootView, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView == null) {
            return;
        }
        if (isVisibleToUser && isFirstVisibleToUser) {
            isFirstVisibleToUser = false;
            fetchData();
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    protected void onFragmentVisibleChange(boolean isVisible) {
//        if (isVisible) {
//            MobclickAgent.onPageStart(getClass().getSimpleName());
//        } else {
//            MobclickAgent.onPageEnd(getClass().getSimpleName());
//        }
    }

    private final View.OnClickListener onReloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reload();
        }
    };

    /**
     * 设置fragment布局
     */
    protected abstract void bindView();

    /**
     * 初始化数据和监听
     */
    protected abstract void fetchData();

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
