package life.forever.cf.activtiy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.CommentClassify;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.CommentGroupAdapter;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.OnItemClickListener;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.RefreshHeaderView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;
import life.forever.cf.publics.weight.poputil.SharePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WorkCommentListActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.refreshHeader)
    RefreshHeaderView mRefreshHeader;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;

    @BindView(R.id.ll_white_comment)
    LinearLayout ll_white_comment;
    @BindView(R.id.header_comment)
    RelativeLayout header_comment;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.tv_commit)
    TextView tv_commit;
    @BindView(R.id.opreview_ratingbar_comment)
    NiceRatingBar opreview_ratingbar_comment;
    @BindView(R.id.edit_comment)
    EditText edit_comment;


    @BindView(R.id.ll_white_comment_child)
    LinearLayout ll_white_comment_child;
    @BindView(R.id.header_comment_child)
    RelativeLayout header_comment_child;
    @BindView(R.id.iv_close_child)
    ImageView iv_close_child;

    @BindView(R.id.fragment_container)
    FrameLayout fragment_container;



    private int wid;
    private Work work;
    /*
     * 1-最热 2 最新
     */
    private int order = 1;
//    private CommentClassify goodClassify;
    private CommentClassify allClassify;
    private final List<CommentClassify> classifies = new ArrayList<>();
    private CommentGroupAdapter commentGroupAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    private SharePopup sharePopup;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_work_comment_list);
        ButterKnife.bind(this);
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(COMMENT_STRING_ALL_COMMENT);
//        mTitleBar.setRightImageResource(R.drawable.aiye_share_icon_gray);
//        mTitleBar.setRightImageViewOnClickListener(onShareClick);
//        mTitleBar.showRightImageView(FALSE);
        mRefreshHeader.setOnRefreshListener(onRefreshListener);
        mLoadFooter.setOnLoadListener(onLoadListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));


    }


    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };







    private final BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
            pageIndex = ONE;
            totalPage = ZERO;
            fetchComment(order);
//            fetchHotComment();
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetchComment(order);
        }
    };

    private final OnItemClickListener replyClick = new OnItemClickListener() {

        @Override
        public void onItemClick(RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
//            goodClassify.comments.size();
            allClassify.comments.size();

            if (position <= classifies.get(0).comments.size()){
                getSupportFragmentManager()    //
                        .beginTransaction()
                        .add(R.id.fragment_container,WorkCommentDetailFragment.get(wid, classifies.get(0).comments.get(position-1).id))   // 此处的R.id.fragment_container是要盛放fragment的父容器
                        .commit();
                onDirectoryUpClick(ll_white_comment_child);
            }

        }
    };

    /**
     * 最热评论
     */
    private final View.OnClickListener hottestClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            order = 1;
            pageIndex = ONE;
            totalPage = ZERO;
            allClassify.comments.clear();
            fetchComment(order);
//            fetchHotComment();
        }
    };
    /**
     * 最新评论
     */
    private final View.OnClickListener latestClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            order = 2;
            pageIndex = ONE;
            totalPage = ZERO;
            allClassify.comments.clear();
            fetchComment(order);
//            fetchHotComment();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        wid = getIntent().getIntExtra("wid", wid);

//        goodClassify = new CommentClassify();
//        goodClassify.classify = getString(R.string.comment_wonderful_comment);
        allClassify = new CommentClassify();
        allClassify.classify = getString(R.string.comment_total_comment);
//        classifies.add(goodClassify);
        classifies.add(allClassify);
        commentGroupAdapter = new CommentGroupAdapter(this, classifies,replyClick,hottestClick,latestClick);
        mRecyclerView.setAdapter(commentGroupAdapter);
        setTextViewWatcher();
        fetchComment(order);
//        fetchHotComment();

    }

    private void setTextViewWatcher() {
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 9) {
                    //编辑框长度>0
                    tv_commit.setEnabled(true);
                    tv_commit.setTextColor(getResources().getColor(R.color.theme_color));
                } else {
                    //编辑框长度为0
                    tv_commit.setTextColor(getResources().getColor(R.color.color_999999));
                    tv_commit.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_WORK_COMMENT_ADD_SUCCESS) {
            Comment comment = (Comment) message.obj;
            allClassify.comments.add(ZERO, comment);
            allClassify.count++;
            commentGroupAdapter.update();
            return;
        }
        if (message.what == BUS_COMMENT_ADD_LIKE) {
            Comment temp = (Comment) message.obj;
//            for (int i = ZERO; i < goodClassify.comments.size(); i++) {
//                Comment comment = goodClassify.comments.get(i);
//                if (comment.equals(temp)) {
//                    comment.isLike = temp.isLike;
//                    comment.likeCount = temp.likeCount;
//                    break;
//                }
//            }
            for (int i = ZERO; i < allClassify.comments.size(); i++) {
                Comment comment = allClassify.comments.get(i);
                if (comment.equals(temp)) {
                    comment.isLike = temp.isLike;
                    comment.likeCount = temp.likeCount;
                    break;
                }
            }
            commentGroupAdapter.update();
            return;
        }
        if (message.what == BUS_COMMENT_ADD_REPLY) {
            Comment temp = (Comment) message.obj;
//            for (int i = ZERO; i < goodClassify.comments.size(); i++) {
//                Comment comment = goodClassify.comments.get(i);
//                if (comment.equals(temp)) {
//                    comment.replyCount = temp.replyCount;
//                    comment.replays.clear();
//                    comment.replays.addAll(temp.replays);
//                    break;
//                }
//            }
            for (int i = ZERO; i < allClassify.comments.size(); i++) {
                Comment comment = allClassify.comments.get(i);
                if (comment.equals(temp)) {
                    comment.replyCount = temp.replyCount;
                    comment.replays.clear();
                    comment.replays.addAll(temp.replays);
                    break;
                }
            }
            commentGroupAdapter.update();
            return;
        }
        if (message.what == BUS_COMMENT_DELETE) {
            Comment temp = (Comment) message.obj;
//            for (int i = ZERO; i < goodClassify.comments.size(); i++) {
//                Comment comment = goodClassify.comments.get(i);
//                if (comment.equals(temp)) {
//                    goodClassify.comments.remove(i);
//                    goodClassify.count--;
//                    break;
//                }
//            }
            for (int i = ZERO; i < allClassify.comments.size(); i++) {
                Comment comment = allClassify.comments.get(i);
                if (comment.equals(temp)) {
                    allClassify.comments.remove(i);
                    allClassify.count--;
                    break;
                }
            }
            commentGroupAdapter.update();
            return;
        }
        if (message.what == BUS_LOG_IN) {
//            goodClassify.count = ZERO;
//            goodClassify.comments.clear();
            allClassify.count = ZERO;
            allClassify.comments.clear();
            commentGroupAdapter.update();
            reload();
        }
    }


    @OnClick({R.id.comment,  R.id.iv_close, R.id.ll_white_comment, R.id.iv_close_child,R.id.ll_white_comment_child, R.id.tv_commit})
    public void setOnClick(View id) {
        Intent intent = new Intent();
        switch (id.getId()) {
            case R.id.comment:
                onDirectoryUpClick(ll_white_comment);
                break;
            case R.id.iv_close:
                onDirectoryDownClick(ll_white_comment);
                break;
            case R.id.ll_white_comment:

                break;
            case R.id.ll_white_comment_child:

                break;
            case R.id.iv_close_child:
                onDirectoryDownClick(ll_white_comment_child);
                break;


            case R.id.tv_commit:
                if (!PlotRead.getAppUser().login()) {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                int score = (int) (opreview_ratingbar_comment.getRating() * 2);
                String trim = edit_comment.getText().toString().trim();
                loadingDialog = LoadingAlertDialog.show(context, getString(R.string.loading));

                if (score == ZERO) {
                    addComment(trim);
                } else {
                    addScoreComment(trim, score);
                }
                break;


            default:
                break;
        }
    }



    @Override
    protected void reload() {
        pageIndex = ONE;
        totalPage = ZERO;
        work = null;
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        fetchComment(order);
//        fetchHotComment();
    }

    private void fetchComment(int order) {
        if (wid == 0){
            return;
        }
        NetRequest.workCommentList(wid, pageIndex,order, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                            allClassify.comments.clear();
                        } else if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        } else {
                            mLoadingLayout.setVisibility(View.GONE);
                            mContentLayout.setVisibility(View.VISIBLE);
                        }
                        if (work == null) {
                            JSONObject workinfo = JSONUtil.getJSONObject(result, "workinfo");
                            work = BeanParser.getWork(workinfo);
                            mTitleBar.getRightImageView().setVisibility(View.VISIBLE);
                        }
                        allClassify.count = JSONUtil.getInt(result, "count");
                        if (pageIndex == ONE && totalPage == ZERO) {
                            totalPage = allClassify.count % TWENTY == ZERO ? allClassify.count / TWENTY : allClassify.count / TWENTY + ONE;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(lists, i);
                            Comment comment = BeanParser.getComment(child);
                            allClassify.comments.add(comment);
                        }
                        if (allClassify.comments.size() > ZERO) {
                            commentGroupAdapter.update();
                        }

                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                        }
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
//                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));

                    }
                } else {
                    NetRequest.error(WorkCommentListActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.stopRefresh();
                } else if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

//    private void fetchHotComment() {
//        NetRequest.workHotCommentList(wid, new OkHttpResult() {
//
//            @Override
//            public void onSuccess(JSONObject data) {
//                String serverNo = JSONUtil.getString(data, "ServerNo");
//                if (SN000.equals(serverNo)) {
//                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
//                    int status = JSONUtil.getInt(result, "status");
//                    if (status == ONE) {
//                        goodClassify.comments.clear();
//                        goodClassify.count = JSONUtil.getInt(result, "count");
//                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
//                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
//                            JSONObject child = JSONUtil.getJSONObject(lists, i);
//                            Comment comment = BeanParser.getComment(child);
//                            goodClassify.comments.add(comment);
//                        }
//                        if (goodClassify.comments.size() > ZERO) {
//                            commentGroupAdapter.update();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//
//            }
//        });
//    }


    void onDirectoryUpClick(LinearLayout linearLayout) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
        linearLayout.setVisibility(View.VISIBLE);

        linearLayout.startAnimation(ctrlAnimation);



    }

    void onDirectoryDownClick(LinearLayout linearLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        }
        if (linearLayout.getVisibility() == View.VISIBLE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
            linearLayout.setVisibility(View.GONE);
            linearLayout.startAnimation(ctrlAnimation);
            ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
        }

    }

    private void addScoreComment(String content, int score) {
        NetRequest.workAddScoreComment(wid, score, content, okHttpResult);
    }

    private void addComment(String content) {
        NetRequest.workAddComment(wid, ZERO, ONE, ZERO, ZERO,
                PlotRead.getAppUser().uid, BLANK, content, okHttpResult);
    }
    private final OkHttpResult okHttpResult = new OkHttpResult() {

        @Override
        public void onSuccess(JSONObject data) {
            LoadingAlertDialog.dismiss(loadingDialog);
            String serverNo = JSONUtil.getString(data, "ServerNo");
            if (SN000.equals(serverNo)) {
                JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                int status = JSONUtil.getInt(result, "status");
                if (status == ONE) {
                    JSONObject child = JSONUtil.getJSONObject(result, "comment");
                    Comment comment = BeanParser.getComment(child);
                    // 发送通知
                    Message message = Message.obtain();
                    message.what = BUS_WORK_COMMENT_ADD_SUCCESS;
                    message.obj = comment;
                    EventBus.getDefault().post(message);
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.published_success));
                    onDirectoryDownClick(ll_white_comment);
                } else {
//                    String msg = JSONUtil.getString(result, "msg");
                    PlotRead.toast(PlotRead.FAIL, getString(R.string.no_internet));
                }
            } else {
                NetRequest.error(WorkCommentListActivity.this, serverNo);
            }
        }

        @Override
        public void onFailure(String error) {
            LoadingAlertDialog.dismiss(loadingDialog);
            PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
