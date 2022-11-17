package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.CommentDetailAdapter;
import life.forever.cf.popup.CommentDetailMenuPopup;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.RefreshHeaderView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.TextCheckUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


public class WorkCommentDetailFragment extends BaseFragment {

    private int wid;
    private int commentId;
    private Comment comment;
    private Work work;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.refreshHeader)
    RefreshHeaderView mRefreshHeader;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.send)
    TextView mSend;

    private CommentDetailMenuPopup mMenuPopup;
    private CommentDetailAdapter detailAdapter;
    private Comment toReply;

    private static WorkCommentDetailFragment instance;
    /**
     * 获取fragment实例
     *
     * @param wid
     * @param commentId
     * @return
     */
    public static WorkCommentDetailFragment get(int wid, int commentId) {
        instance = new WorkCommentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("wid", wid);
        bundle.putInt("commentId", commentId);
        instance.setArguments(bundle);
        return instance;
    }
    @Override
    protected void bindView() {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_work_comment_detail, mContentLayout, TRUE);
        ButterKnife.bind(this, view);
        mTitleBar.setVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRefreshHeader.setOnRefreshListener(onRefreshListener);
        mLoadFooter.setOnLoadListener(onLoadListener);
        mRecyclerView.setOnTouchListener(onTouchListener);
    }

    @Override
    protected void fetchData() {
        Bundle bundle = getArguments();
        wid = bundle.getInt("wid", ZERO);
        commentId = bundle.getInt("commentId", ZERO);
        detailAdapter = new CommentDetailAdapter(getActivity(), comment, work);
        mRecyclerView.setAdapter(detailAdapter);
        NetRequest.workCommentDetail(wid, commentId, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                            comment = null;
                            work = null;
                        }
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                        if (comment == null) {
                            JSONObject json = JSONUtil.getJSONObject(result, "comment");
                            comment = BeanParser.getComment(json);
                            int count = JSONUtil.getInt(result, "count");
                            if (pageIndex == ONE && totalPage == ZERO) {
                                totalPage = count % TWENTY == ONE ? count / TWENTY : count / TWENTY + ONE;
                                mRefreshLayout.setHasFooter(totalPage > ONE);
                            }
                        }
                        if (work == null) {
                            JSONObject workinfo = JSONUtil.getJSONObject(result, "workinfo");
                            work = BeanParser.getWork(workinfo);
                        }
                        JSONArray reply = JSONUtil.getJSONArray(result, "reply");
                        for (int i = ZERO; reply != null && i < reply.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(reply, i);
                            comment.replays.add(BeanParser.getComment(child));
                        }
                        detailAdapter.update(comment, work);
                        mLoadingLayout.setVisibility(View.GONE);
                        mContentLayout.setVisibility(View.VISIBLE);
                        mTitleBar.getRightImageView().setVisibility(View.VISIBLE);
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                        } else if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.stopRefresh();
                } else if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }


    private final BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
            pageIndex = ONE;
            totalPage = ZERO;
            fetchData();
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetchData();
        }
    };

    @OnTextChanged(value = R.id.editText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterCommentEditChange(Editable s) {
        mSend.setEnabled(s.length() > ZERO);
    }

    @OnClick(R.id.send)
    void onSendClick() {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        String trim = mEditText.getText().toString().trim();
        if (TextCheckUtil.isEmpty(trim)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.comment_null));
            mEditText.setText(BLANK);
            return;
        }
        sendComment(trim);
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!ComYou.isDestroy(getActivity())){
                    ComYou.closeKeyboard(mEditText, getActivity());
                }

            }
            return false;
        }
    };




    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        pageIndex = ONE;
        totalPage = ZERO;
        comment = null;
        work = null;
        fetchData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SEND_COMMENT_TO_REPLY) {
            toReply = (Comment) message.obj;
            mEditText.setHint(getString(R.string.reply)+"：" + toReply.nickname);
            mEditText.setText(BLANK);
            mEditText.requestFocus();
            if (!ComYou.isDestroy(getActivity())){
                ComYou.openKeyboard(getActivity());
            }

        }
    }




    private final View.OnClickListener onMenuClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mMenuPopup == null) {
                mMenuPopup = new CommentDetailMenuPopup((BaseActivity) getActivity(), comment, work);
            }
            mMenuPopup.show(mTitleBar);
        }
    };

    private void sendComment(String content) {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        if (toReply == null) {
            toReply = comment;
        }
//        aiyeUtil.closeKeyboard(mEditText, context);
        if (!ComYou.isDestroy(getActivity())){
            ComYou.closeKeyboard(mEditText, getActivity());
        }
        showLoading(getString(R.string.loading));
        NetRequest.workAddComment(wid, ZERO, TWO,
                toReply.pid == ZERO ? toReply.id : toReply.pid,
                toReply.id,
                PlotRead.getAppUser().uid, BLANK, content, new OkHttpResult() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        dismissLoading();
                        String serverNo = JSONUtil.getString(data, "ServerNo");
                        if (SN000.equals(serverNo)) {
                            JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                            int status = JSONUtil.getInt(result, "status");
                            if (status == ONE) {
                                JSONObject object = JSONUtil.getJSONObject(result, "comment");
                                Comment newAdd = BeanParser.getComment(object);
                                comment.replays.add(ZERO, newAdd);
                                comment.replyCount++;
                                detailAdapter.update(comment, work);

                                toReply = null;
                                mEditText.setText(BLANK);
                                mEditText.setHint(getString(R.string.reply_poster));
                                mEditText.clearFocus();

                                // 发通知
                                Message message = Message.obtain();
                                message.what = BUS_COMMENT_ADD_REPLY;
                                message.obj = comment;
                                EventBus.getDefault().post(message);
                            } else {
                                String msg = JSONUtil.getString(result, "msg");
                                PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
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



}