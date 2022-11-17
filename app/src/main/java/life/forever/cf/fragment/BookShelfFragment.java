package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.BookShelfRec;
import life.forever.cf.entry.SignBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.BookRecordBean;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.HomeActivity;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.BookShelfAdapter;
import life.forever.cf.activtiy.ReadHistoryActivity;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.bookcase.bookweight.EditPopup;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.SearchActivity;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.adapter.person.personcenter.PushUpdateManagerActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.CustomDialog;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ObjectSaveUtils;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.tool.SignDialog;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.publics.weight.poputil.AnimotionPopupWindow;
import life.forever.cf.activtiy.TaskCenterActivity;
import life.forever.cf.activtiy.ReadActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import life.forever.cf.activtiy.Cods;

public class BookShelfFragment extends BaseFragment {

    private BookShelfRec.ResultData.shelfRecommend.Rec_list mRec_list;
    private List<BookShelfRec.ResultData.shelfRecommend.Rec_list> mBookShelfList = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;

    private static final List<Work> works = new ArrayList<>();
    private static final List<Work> mEditWorks = new ArrayList<>();
    private final List<Work> selects = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static BookShelfAdapter bookShelfAdapter;

    private CustomDialog mCustomDialog;

    //是否编辑状态标识
    boolean isEditStatus;
    //书架是否改变标识
    boolean isShelfChanged;

    static RecyclerView mRecyclerView;
    //任务中心有奖励待领取
    private boolean mHasGift = false;

    @BindView(R.id.noneView)
    View mNoneView;

    @BindView(R.id.rl_library_hint)
    RelativeLayout mRlLibraryHint;

    @BindView(R.id.img_sign)
    ImageView mImgSign;

    @BindView(R.id.img_search)
    ImageView mImgSearch;

    @BindView(R.id.img_manage)
    ImageView mImgManage;

    @BindView(R.id.complete)
    TextView mComplete;

    @BindView(R.id.tv_left)
    TextView mTvLeft;

    @BindView(R.id.head)
    RadiusImageView mRadiusImageView;

    private EditPopup editPopup;
    private SignDialog mSignDialog;
    private SignBean.ResultData mSignBean;

    @BindView(R.id.layout_bookshelf_rec)
    LinearLayout mLayoutBookShelfRec;
    @BindView(R.id.cover)
    ImageView mCover;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.info)
    TextView mInfo;
    @BindView(R.id.imgBr1)
    ImageView mImgBr1;
    @BindView(R.id.imgBr2)
    ImageView mImgBr2;
    @BindView(R.id.imgBr3)
    ImageView mImgBr3;
    @BindView(R.id.imgBr4)
    ImageView mImgBr4;

    private int mCount = 0;
    private float mDensity;
    private boolean mNeedShake = false;
    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 94;
    private static final float DEGREE_0 = 2.0f;
    private static final float DEGREE_1 = -2.0f;
    private static final int ANIMATION_DURATION = 500;
    //3天内是否存在过期书卷
    private boolean isOverTime = false;
    private Animation animation1;
    private Animation animation2;
    private Animation animation3;
    private Animation animation4;

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_book_shelf, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mRecyclerView = root.findViewById(R.id.recyclerView);

        DisplayMetrics dm = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mNeedShake = true;
        shakeAnimation(mImgSign);
        mRadiusImageView.setOnClickListener(openDrawerClick);
        mImgSign.setOnClickListener(onSignClick);
        mImgSearch.setOnClickListener(onSearchClick);
        mImgManage.setOnClickListener(onManageClick);
        mComplete.setOnClickListener(onCompleteClick);

        gridLayoutManager = new GridLayoutManager(context, THREE);

        boolean isLibrary = PlotRead.getConfig().getBoolean(FIRST_LIBRARY, TRUE);
        if (isLibrary) {
            SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_LIBRARY, FALSE);
            mRlLibraryHint.setVisibility(View.VISIBLE);
            mRlLibraryHint.bringToFront();
        } else {
            mRlLibraryHint.setVisibility(View.GONE);
        }
        ScreenUtil.setStatusBarDark(getActivity(), false);
        if (isOverTime){
//            doBreathinglamp();
        }
    }

    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        fetchWeekRecommend();
        if (PlotRead.getAppUser().login()) {
            fetchSignInfo();
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.logo_default_user, mRadiusImageView);
        } else {
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.default_user_logo, mRadiusImageView);
        }
        // 初始化显示方式
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        gridLayoutManager.setInitialPrefetchItemCount(3);
        mRecyclerView.setItemAnimator(null);

        // 初始化适配器
        bookShelfAdapter = new BookShelfAdapter(context, works);
        bookShelfAdapter.setOnItemClickListener(onItemClickListener);
        /*bookShelfAdapter.setOnItemLongClickListener(onItemLongClickListener);*/
        mRecyclerView.setAdapter(bookShelfAdapter);


        // 初始化数据
        if (works != null && works.size() > 0) {
            works.clear();
        }
        if (mEditWorks != null && mEditWorks.size() > 0) {
            mEditWorks.clear();
        }
//        if (NA_PlotRead.getAppUser().login()) {
//        works.addAll(ShelfUtil.firstqueryShelfWorks());
//        }
        firstRecRandom();
        works.addAll(ShelfUtil.queryShelfWorks());
        mEditWorks.addAll(ShelfUtil.queryShelfWorks());
        bookShelfAdapter.notifyDataSetChanged();
        switchPageBySize();
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    public static void Refresh() {
        mRecyclerView.smoothScrollToPosition(0);
        if (works != null && works.size() > 0) {
            works.clear();
        }
        if (mEditWorks != null && mEditWorks.size() > 0) {
            mEditWorks.clear();
        }
        firstRecRandom();
        works.addAll(ShelfUtil.queryShelfWorks());
        mEditWorks.addAll(ShelfUtil.queryShelfWorks());
        bookShelfAdapter.notifyDataSetChanged();
    }

    private static void firstRecRandom() {
        Random r = new Random();
        //取3个随机数
        int mRandomNum;
        if (ShelfUtil.firstqueryShelfWorks().size() >= 3) {
            mRandomNum = 3;
        } else if (ShelfUtil.firstqueryShelfWorks().size() == 2) {
            mRandomNum = 2;
        } else if (ShelfUtil.firstqueryShelfWorks().size() == 1) {
            mRandomNum = 1;
        } else {
            mRandomNum = 0;
        }
        if (mRandomNum != 0) {
            while (works.size() < mRandomNum) {
                int num = r.nextInt(ShelfUtil.firstqueryShelfWorks().size());
                //判断是否重复
                if (!works.contains(ShelfUtil.firstqueryShelfWorks().get(num))) {
                    works.add(ShelfUtil.firstqueryShelfWorks().get(num));
                }
            }
        }
    }

    private void shakeAnimation(final View v) {
        float rotate;
        int c = mCount++ % 5;
        switch (c) {
            case 1:
            case 3:
                rotate = DEGREE_1;
                break;
            default:
                rotate = DEGREE_0;
                break;
        }
        final RotateAnimation mra = new RotateAnimation(rotate, -rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);
        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate, ICON_WIDTH * mDensity / 2, ICON_HEIGHT * mDensity / 2);

        mra.setDuration(ANIMATION_DURATION);
        mrb.setDuration(ANIMATION_DURATION);

        mra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake | mHasGift) {
                    mra.reset();
                    v.startAnimation(mrb);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        mrb.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (mNeedShake | mHasGift) {
                    mrb.reset();
                    v.startAnimation(mra);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        v.startAnimation(mra);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SHELF_CHANGE) {
            if (isEditStatus) {
                selects.clear();
                editPopup.update(mEditWorks.size(), selects.size());
                if (mEditWorks.size() == ZERO) {
                    isShelfChanged = TRUE;
                    endEdit();
                }
            }
            ShelfUtil.firstRecommend(getActivity());
            new Handler().postDelayed(() -> {
                /*
                 *要执行的操作
                 */
                works.clear();
                mEditWorks.clear();
                firstRecRandom();
                works.addAll(ShelfUtil.queryShelfWorks());
                mEditWorks.addAll(ShelfUtil.queryShelfWorks());
                bookShelfAdapter.notifyDataSetChanged();
                switchPageBySize();
            }, 300);

            return;
        }
        if (message.what == BUS_USER_INFO_SUCCESS) {
            fetchSignInfo();
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.logo_default_user, mRadiusImageView);
            return;
        }

        if (message.what == BUS_USER_SIGN_STATE_CHANGE) {
            PlotRead.getAppUser().fetchUserInfo(getActivity());
            return;
        }

        if (message.what == BUS_MODIFY_INFO_SUCCESS) {
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.logo_default_user, mRadiusImageView);
            return;
        }
        if (message.what == BUS_LOG_OUT) {
            AppUser user = PlotRead.getAppUser();
            GlideUtil.load(context, user.head, R.drawable.default_user_logo, mRadiusImageView);
        }
        if (message.what == BOOKSHELFTAB) {
            Refresh();
            if (mBookShelfList != null && mBookShelfList.size() > 0) {
                mBookShelfList.clear();
            }
            mBookShelfList = (List<BookShelfRec.ResultData.shelfRecommend.Rec_list>) ObjectSaveUtils.getObject(context, "ns_bookshelfrec_list");
            if (mBookShelfList == null || mBookShelfList.size() == ZERO) {
                mLayoutBookShelfRec.setVisibility(View.GONE);
            } else {
                mLayoutBookShelfRec.setVisibility(View.VISIBLE);
                Random r = new Random();
                int i = r.nextInt(mBookShelfList.size());
                mRec_list = mBookShelfList.get(i);
                GlideUtil.picCache(context,  mRec_list.recimg,mRec_list.id + "bookshelfrec",R.drawable.default_work_cover, mCover);

//                String covers = PlotRead.getConfig().getString(mRec_list.id + "bookshelfrec", "");
//                if (TextUtils.isEmpty(covers)) {
//                    GlideUtil.recommentLoad(context, mRec_list.id + "bookshelfrec", mRec_list.recimg, mRec_list.recimg, R.drawable.default_work_cover, mCover);
//                } else {
//                    GlideUtil.recommentLoad(context, "", covers, mRec_list.recimg, R.drawable.default_work_cover, mCover);
//                }
                mTitle.setText(mRec_list.title);
                mInfo.setText(mRec_list.description);
            }
        }

        if (message.what == BONUS_OVER_LIMIT){
            //过期书卷提醒，开启动画
            Log.e("TAG", "onEventBus: 收到");
           if ( ((HomeActivity) requireActivity()).isOverTime){
               isOverTime = true;
               doBreathinglamp();
           }

        }
    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (works.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    private void showTitleBarRightText(boolean showText) {
        mRadiusImageView.setVisibility(showText ? View.GONE : View.VISIBLE);
        mImgSearch.setVisibility(showText ? View.GONE : View.VISIBLE);
        mImgSign.setVisibility(showText ? View.GONE : View.VISIBLE);
        mImgManage.setVisibility(showText ? View.GONE : View.VISIBLE);
        mTvLeft.setVisibility(showText ? View.VISIBLE : View.GONE);
        mComplete.setVisibility(showText ? View.VISIBLE : View.GONE);
    }

    private final View.OnClickListener openDrawerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mRlLibraryHint.setVisibility(View.GONE);
            ((HomeActivity) requireActivity()).openDrawer();
        }
    };
    private final BookShelfAdapter.OnItemClickListener onItemClickListener = new BookShelfAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            mRlLibraryHint.setVisibility(View.GONE);
            Work work = works.get(position);
            if (mEditWorks != null && mEditWorks.size() > 0) {
                if (isEditStatus) {
                    if (selects.contains(work)) {
                        selects.remove(work);
                    } else {
                        selects.add(work);
                    }
                    bookShelfAdapter.notifyItemChanged(position);
                    editPopup.update(mEditWorks.size(), selects.size());
                } else {
                    startRead(work);
                }
            } else {
                startRead(work);
            }
        }
    };

    /*private BookShelfAdapter.OnItemLongClickListener onItemLongClickListener = new BookShelfAdapter.OnItemLongClickListener() {

        @Override
        public void onItemLongClick() {
            mRlLibraryHint.setVisibility(View.GONE);
            if (!isEditStatus) {
                isEditStatus = true;
                showTitleBarRightText(TRUE);
                bookShelfAdapter.update(isEditStatus, selects);
                startEdit();
            }
        }
    };*/

    private final View.OnClickListener onSignClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
//                boolean isSign = PlotRead.getConfig().getBoolean(FIRST_SIGN, TRUE);
//                if (isSign) {
//                    if (mSignBean != null && mSignBean.info != null) {
//                        mSignDialog = new SignDialog(getActivity(), mSignBean);
//                        mSignDialog.show();
//                    } else {
//                        fetchSignInfo();
//                    }
//                } else {
//                    sign(DataString.StringData());
//                }
                // 跳转任务中心
                Intent intent = new Intent();
                intent.setClass(context, TaskCenterActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    private final View.OnClickListener onSearchClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mRlLibraryHint.setVisibility(View.GONE);
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener onCompleteClick = v -> endEdit();
    /**
     * 右上角三个点事件
     */
    private final View.OnClickListener onManageClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mRlLibraryHint.setVisibility(View.GONE);
            AnimotionPopupWindow mPopupWindow = new AnimotionPopupWindow(getActivity());
            mPopupWindow.show();
            //弹出窗口关闭事件
            mPopupWindow.setOnDismissListener(() -> AnimotionPopupWindow.BackgroudAlpha((float) 1));
            mPopupWindow.setAnimotionPopupWindowOnClickListener(name -> {
                if ("Update reminder".equals(name)) {
                    //更新提醒
                    Intent intent = new Intent();
                    if (PlotRead.getAppUser().login()) {
                        intent.setClass(context, PushUpdateManagerActivity.class);
                    } else {
                        intent.setClass(context, LoginActivity.class);
                    }
                    startActivity(intent);
                } else if ("Edit Library".equals(name)) {
                    if (mEditWorks == null || mEditWorks.size() == 0) {
                        return;
                    }
                    //切换编辑书架
                    if (!isEditStatus) {
                        isEditStatus = true;
                        showTitleBarRightText(TRUE);
                        bookShelfAdapter.update(isEditStatus, selects);
                        startEdit();
                    }
                } else if ("Read History".equals(name)) {
                    DeepLinkUtil.addPermanent(context, "event_bookself_viewed", "书架", "点击阅读历史", "", "", "", "", "", "");
                    //阅读历史
                    Intent intent = new Intent();
                    if (PlotRead.getAppUser().login()) {
                        intent.setClass(context, ReadHistoryActivity.class);
                    } else {
                        intent.setClass(context, LoginActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }
    };

    private final EditPopup.OnItemClickListener onEditItemClick = new EditPopup.OnItemClickListener() {

        @Override
        public void onItemClick(TextView textView, int position) {
            if (position == ZERO) {
                if (selects.size() == mEditWorks.size()) {
                    selects.clear();
                } else {
                    selects.clear();
                    selects.addAll(mEditWorks);
                }
                bookShelfAdapter.notifyDataSetChanged();
                editPopup.update(mEditWorks.size(), selects.size());
                return;
            }
            if (position == ONE) {
                String info;
                if (selects.size() == mEditWorks.size()) {
                    info = application.getString(R.string.delete_shelf_checkall);
                } else {
                    info = application.getString(R.string.delete_shelf_radio);
                }
                mCustomDialog = new CustomDialog(getActivity(), info, view -> {
                    switch (view.getId()) {
                        case R.id.cancel:
                            mCustomDialog.dismiss();
                            break;
                        case R.id.delete:
                            for (Work work : mEditWorks) {
                                work.deleteflag = ONE;
                            }
                            for (Work work : works) {
                                work.deleteflag = ONE;
                            }

                            ShelfUtil.insert(getActivity(), selects,false);
                            isShelfChanged = TRUE;
                            mCustomDialog.dismiss();
                            break;
                    }
                });
                mCustomDialog.show();
                Window dialogWindow = mCustomDialog.getWindow();
                if (dialogWindow != null) {
                    dialogWindow.setGravity(Gravity.CENTER);
                }
                WindowManager.LayoutParams params = Objects.requireNonNull(mCustomDialog.getWindow()).getAttributes();
                //设置dialog的背景颜色为透明色,就可以显示圆角了!!
                mCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mCustomDialog.getWindow().setAttributes(params);
            }
        }
    };

    @OnClick(R.id.visitStore)
    void onVisitStoreClick() {
        mRlLibraryHint.setVisibility(View.GONE);
        HomeActivity parent = (HomeActivity) getActivity();
        if (parent != null) {
            parent.setCurrentItem(HomeActivity.INDEX_BOOK_DISCOVER);
        } else {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 启动编辑模式
     */
    private void startEdit() {
        isEditStatus = true;
        showTitleBarRightText(TRUE);
        mLayoutBookShelfRec.setVisibility(View.GONE);
        bookShelfAdapter.update(isEditStatus, selects);
        if (editPopup == null) {
            editPopup = new EditPopup(context, onEditItemClick);
        }
        editPopup.show(mTitleBar, works.size());
    }

    /**
     * 退出编辑模式
     */
    public void endEdit() {
        isEditStatus = false;
        selects.clear();
        showTitleBarRightText(FALSE);
        if (mBookShelfList == null || mBookShelfList.size() == ZERO) {
            mLayoutBookShelfRec.setVisibility(View.GONE);
        } else {
            mLayoutBookShelfRec.setVisibility(View.VISIBLE);
        }
        bookShelfAdapter.update(isEditStatus, selects);
        if (editPopup != null && editPopup.isShowing()) {
            editPopup.dismiss();
        }
        if (isShelfChanged) {
            isShelfChanged = FALSE;
            ShelfUtil.shelfUpload(getActivity());
        }
    }

    /**
     * 开始阅读
     */
    private void startRead(Work work) {

        if(work != null)
        {
            BookRecordBean recordBean = DBUtils.getInstance().getBookRecord(""+work.wid);

            if(recordBean == null)
            {
                recordBean = new BookRecordBean();
                recordBean.wid = ""+work.wid;
                recordBean.chapterIndex = work.lastChapterOrder;
                recordBean.chapterCharIndex = work.lastChapterOrder;
                DBUtils.getInstance().saveBookRecordWithAsync(recordBean);
            }
        }





        work.updateflag = ZERO;
        work.lasttime = ComYou.currentTimeSeconds();
//        new Handler().postDelayed(() ->
//                NA_ShelfUtil.insert(getActivity(), work), 2000);
        DeepLinkUtil.addPermanent(context, "event_bookself_click", "书架", "在书架内点击作品", "", work.wid + "", "", "", "", "");
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("work", work);
        CollBookBean mCollBook  = new CollBookBean();
        mCollBook.setTitle(work.title);
        mCollBook.set_id(work.wid+"");
        intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
        startActivity(intent);
    }

    /**
     * 签到
     */
    private void sign(int week) {
        showLoading(context.getString(R.string.content_loading));
        NetRequest.sign(week, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (ComYou.isDestroy(getActivity())) {
                    return;
                }
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mNeedShake) {
                            mNeedShake = false;
                            mCount = 0;
                        }
                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, TRUE);
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(data));
                            String resultString = jsonObject.getString("ResultData");

                            JSONObject jsonOders = new JSONObject(resultString);
                            String strOrders = jsonOders.getString("info");

                            JSONObject json = new JSONObject(strOrders);
                            String strResult = json.getString("sign");

                            Type listType = new TypeToken<List<SignBean.ResultData.Info.Sign>>() {
                            }.getType();
                            Gson gson = new Gson();
                            if (mSignBean != null && mSignBean.info != null) {
                                mSignBean.info.sign.clear();
                                mSignBean.info.sign.addAll(gson.fromJson(strResult, listType));

                                mSignDialog = new SignDialog(getActivity(), mSignBean);
                                mSignDialog.show();
                                // 发送通知
                                Message message = Message.obtain();
                                message.what = BUS_USER_SIGN_STATE_CHANGE;
                                EventBus.getDefault().post(message);
                                PlotRead.toast(PlotRead.SUCCESS, "check-in success");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mSignBean != null && mSignBean.info != null) {
                            mSignDialog = new SignDialog(getActivity(), mSignBean);
                            mSignDialog.show();
                        }

                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    /**
     * 请求签到信息
     */
    private void fetchSignInfo() {
        NetRequest.signInfo(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        Gson gson = new Gson();
                        mSignBean = gson.fromJson(resultString, SignBean.ResultData.class);
                        if (mSignBean != null && mSignBean.info != null) {
                            if (1 == mSignBean.info.today_is_sign) {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, TRUE);
                                if (mNeedShake) {
                                    mNeedShake = false;
                                    mCount = 0;
                                }
                            } else {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, FALSE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    /**
     * 请求书架推荐位
     */
    private void fetchWeekRecommend() {
        NetRequest.shelfWeekRecommend(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        if (mBookShelfList != null && mBookShelfList.size() > 0) {
                            mBookShelfList.clear();
                        }
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String strOrders = jsonOders.getString("shelfRecommend");
                        JSONObject json = new JSONObject(strOrders);
                        String strResult = json.getString("rec_list");
                        Type listType = new TypeToken<List<BookShelfRec.ResultData.shelfRecommend.Rec_list>>() {
                        }.getType();
                        Gson gson = new Gson();
                        mBookShelfList = gson.fromJson(strResult, listType);
                        ObjectSaveUtils.saveObject(context, "ns_bookshelfrec_list", mBookShelfList);
                        if (mBookShelfList.size() == ZERO) {
                            mLayoutBookShelfRec.setVisibility(View.GONE);
                        } else {
                            mLayoutBookShelfRec.setVisibility(View.VISIBLE);
                            Random r = new Random();
                            int i = r.nextInt(mBookShelfList.size());
                            mRec_list = mBookShelfList.get(i);
                            GlideUtil.picCache(context,   mRec_list.recimg,mRec_list.id + "bookshelfrec",R.drawable.default_work_cover, mCover);

//                            String covers = PlotRead.getConfig().getString(mRec_list.id + "bookshelfrec", "");
//                            if (TextUtils.isEmpty(covers)) {
//                                GlideUtil.recommentLoad(context, mRec_list.id + "bookshelfrec", mRec_list.recimg, mRec_list.recimg, R.drawable.default_work_cover, mCover);
//                            } else {
//                                GlideUtil.recommentLoad(context, "", covers, mRec_list.recimg, R.drawable.default_work_cover, mCover);
//                            }
                            mTitle.setText(mRec_list.title);
                            mInfo.setText(mRec_list.description);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mLayoutBookShelfRec.setVisibility(View.GONE);
                    }
                } else {
                    mLayoutBookShelfRec.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                mLayoutBookShelfRec.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.layout_bookshelf_rec)
    public void BookShelfRecOnClick() {
        if (mBookShelfList != null && mBookShelfList.size() > 0) {
            Intent intent = new Intent();
            /*
             * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
             * readflag: 0：作品信息 1：阅读
             */
            String advertise_type = mRec_list.advertise_type;
            if ("1".equals(advertise_type)) {
                String readflag = mRec_list.advertise_data.readflag;
                int wids = Integer.parseInt(mRec_list.advertise_data.wid);
                DeepLinkUtil.addPermanent(context, "event_bookself_recommend", "书架", "点击推荐位作品", "", wids + "", "", "", "", "");
                if ("1".equals(readflag)) {
                    Work work = new Work();
                    work.wid = wids;
                    intent.setClass(context, ReadActivity.class);
                    intent.putExtra("work", work);
                    CollBookBean mCollBook  = new CollBookBean();
                    mCollBook.setTitle(work.title);
                    mCollBook.set_id(work.wid+"");
                    intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
                } else {
                    intent.setClass(context, WorkDetailActivity.class);
                    intent.putExtra("wid", wids);
                    intent.putExtra("recid", 0);
                }
                context.startActivity(intent);
            } else if ("2".equals(advertise_type)) {
                String ht = mRec_list.advertise_data.ht;
                String path = mRec_list.advertise_data.path;
                String ps = mRec_list.advertise_data.ps;
                String is = mRec_list.advertise_data.is;
                String su = mRec_list.advertise_data.su;
                String st = mRec_list.advertise_data.st;
                String ifreash = mRec_list.advertise_data.ifreash;
                intent.setClass(context, WerActivity.class);

                intent.putExtra("index", ht);
                intent.putExtra("path", path);
                intent.putExtra("pagefresh", ps);
                intent.putExtra("share", is);
                intent.putExtra("shareUrl", su);
                intent.putExtra("shareType", st);
                intent.putExtra("sharefresh", ifreash);
                context.startActivity(intent);
            } else if ("3".equals(advertise_type)) {
                String url = mRec_list.advertise_data.url;
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null != getActivity() && isVisibleToUser) {
            ScreenUtil.setStatusBarDark(getActivity(), false);
        }
        if (isVisibleToUser){
            getExistReward();
        }
    }

    public void doBreathinglamp(){
        animation1 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation2 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation3 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation4 = AnimationUtils.loadAnimation(getActivity(),R.anim.breathing_lamp);
        animation2.setStartOffset(600);
        animation3.setStartOffset(1200);
        animation4.setStartOffset(1800);
        mImgBr1.startAnimation(animation1);
        mImgBr2.startAnimation(animation2);
        mImgBr3.startAnimation(animation3);
        mImgBr4.startAnimation(animation4);

    }

    public void clearBreathingAnimation(){
        if (animation1 != null){
            mImgBr1.clearAnimation();
            animation1.cancel();
            mImgBr1.setVisibility(View.GONE);
        }
        if (animation2 != null){
            mImgBr2.clearAnimation();
            animation2.cancel();
            mImgBr2.setVisibility(View.GONE);
        }
        if (animation3 != null){
            mImgBr3.clearAnimation();
            animation3.cancel();
            mImgBr3.setVisibility(View.GONE);
        }
        if (animation4 != null){
            mImgBr4.clearAnimation();
            animation4.cancel();
            mImgBr4.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getExistReward();
        Log.e("bookshelf", "onResume: " );
    }

    /**
     * 是否存在奖励需要领取
     */
    private void getExistReward(){
        NetRequest.getExistRewardStatus(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result,"status");
                    //0 没有待领取 1有待领取
                    int claimedStatus = JSONUtil.getInt(result,"unclaimed");
                    mHasGift = claimedStatus == 1;
                    if (mHasGift){
                        if (mImgSign.getAnimation() != null  ){
                            mImgSign.getAnimation().cancel();
                            shakeAnimation(mImgSign);
                        }else {
                            shakeAnimation(mImgSign);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }



}
