package life.forever.cf.activtiy;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.BookEndRecommend;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.adapter.NewWorkInfoRecommendAdapter;
import life.forever.cf.popup.RewardPopup;
import life.forever.cf.adapter.DiscoverMoreActivity;
import life.forever.cf.sql.CacheSQLiteHelper;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 阅读尾页
 *
 * @author haojie
 * Created on 2018/3/10.
 */
public class NewReadEndActivity extends BaseActivity {

    @BindView(R.id.isFinish)
    TextView mIsFinish;


    @BindView(R.id.upload_hint)
    TextView upload_hint;

    @BindView(R.id.tv_commit)
    TextView tv_commit;

    @BindView(R.id.opreview_ratingbar)
    NiceRatingBar opreview_ratingbar;

    @BindView(R.id.edit_comment)
    EditText edit_comment;

    @BindView(R.id.iv_sang)
    ImageView iv_sang;

    @BindView(R.id.comment_name)
    TextView comment_name;
    @BindView(R.id.comment_content)
    TextView comment_content;
    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.go_comment)
    TextView go_comment;
    @BindView(R.id.ll_comment)
    LinearLayout ll_comment;


    @BindView(R.id.rl_upload)
    RelativeLayout rl_upload;
    @BindView(R.id.more)
    LinearLayout more;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.like_more_content)
    RecyclerView like_more_content;

    @BindView(R.id.rl_complete)
    RelativeLayout rl_complete;
    @BindView(R.id.recommend_book_cover)
    ImageView recommend_book_cover;

    @BindView(R.id.recommend_book_title)
    TextView recommend_book_title;
    @BindView(R.id.recommend_book_content)
    TextView recommend_book_content;

    @BindView(R.id.recommend_chapter_title)
    TextView recommend_chapter_title;
    @BindView(R.id.recommend_chapter_content)
    TextView recommend_chapter_content;
    @BindView(R.id.recommend_chapter_go_on)
    TextView recommend_chapter_go_on;

    @BindView(R.id.all_comment)
    LinearLayout all_comment;

    private Work work;
    private final List<BookEndRecommend> mBookEndRecommends = new ArrayList<>();
    private NewWorkInfoRecommendAdapter freeAdapter;

    private CacheSQLiteHelper cacheSQLiteHelper;


    int next_cid;
    int wid;
    int rec_id;
    public int ACTIONBAR = 100;
    private String content = "";
    private final Work mWork = new Work();
    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_new_read_end);
        ButterKnife.bind(this);
        setTextViewWatcher();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        like_more_content.setLayoutManager(linearLayoutManager);
        like_more_content.setHasFixedSize(true);
        like_more_content.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        like_more_content.setItemViewCacheSize(10);
        like_more_content.setDrawingCacheEnabled(true);
        like_more_content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        like_more_content.setItemAnimator(null);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        work = getIntent().getParcelableExtra("work");
        mTitleBar.setMiddleText(work.title);
        mIsFinish.setText(work.isfinish == ZERO ? context.getString(R.string.author_working_draft) : context.getString(R.string.author_greatly));
        if (work.isfinish == ZERO) {
            rl_upload.setVisibility(View.VISIBLE);
            rl_complete.setVisibility(View.GONE);
            freeAdapter = new NewWorkInfoRecommendAdapter(this, mBookEndRecommends);
            like_more_content.setAdapter(freeAdapter);
        } else {
            rl_upload.setVisibility(View.GONE);
            rl_complete.setVisibility(View.VISIBLE);
        }
        fetchData();
    }


    private void setTextViewWatcher(){
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>9){
                    //编辑框长度>0
                    tv_commit.setEnabled(true);
                    tv_commit.setTextColor(getResources().getColor(R.color.theme_color));
                }else{
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
        if (message.what == BUS_LOG_IN) {
            fetchData();
        }
    }

    private void fetchData() {
        getBookReadRecommend();
    }


    @OnClick(R.id.iv_sang)
    void OnRewardClick() {
        new RewardPopup(this, work).show(mTitleBar, ZERO);
    }


    @OnClick(R.id.look_all_comment)
    void OnCommentClick() {
        Intent intent = new Intent(this, WorkCommentListActivity.class);
        intent.putExtra("wid", work.wid);
        startActivity(intent);
    }




    @OnClick(R.id.more)
    void OnCommentMoreClick() {
        Intent intent = new Intent(this, DiscoverMoreActivity.class);
        intent.putExtra("rec_id", rec_id);
        startActivity(intent);
    }

    @OnClick(R.id.go_comment)
    void setGoCommentClick() {
        onDirectoryUpClick();
    }

    @OnClick(R.id.iv_close)
    void onHideClick() {
        onDirectoryDownClick();
    }

    @OnClick(R.id.ll_comment)
    void setHideClick() {
    }

    @OnClick(R.id.tv_commit)
    void setPublishClick() {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return;
        }
        int score = (int)(opreview_ratingbar.getRating()*2);
        String trim = edit_comment.getText().toString().trim();

        showLoading(getString(R.string.loading));
        if (score == ZERO) {
            addComment(trim);
        } else {
            addScoreComment(trim, score);
        }
    }

    @OnClick(R.id.rl)
    void OnRecommentClick() {
        Intent intent = new Intent(context, WorkDetailActivity.class);
        intent.putExtra("wid", mWork.wid);
        startActivity(intent);

    }

    @OnClick(R.id.recommend_chapter_go_on)
    void OnContinueClick() {
        mWork.lastChapterOrder = 1;
        mWork.toReadType = 1;
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("work", mWork);

        CollBookBean mCollBook  = new CollBookBean();
        mCollBook.setTitle(mWork.title);
        mCollBook.set_id(mWork.wid+"");
        intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
        startActivity(intent);
    }

    void onDirectoryUpClick() {


        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
        ll_comment.setVisibility(View.VISIBLE);

        ll_comment.startAnimation(ctrlAnimation);
    }

    void onDirectoryDownClick() {
        if (ll_comment.getVisibility() == View.VISIBLE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
            ll_comment.setVisibility(View.GONE);
            ll_comment.startAnimation(ctrlAnimation);
            ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ll_comment.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ll_comment.getWindowToken(), 0);
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
            dismissLoading();
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
                    onBackPressed();
                } else {
                    String msg = JSONUtil.getString(result, "msg");
                    PlotRead.toast(PlotRead.FAIL, msg);
                }
            } else {
                NetRequest.error(NewReadEndActivity.this, serverNo);
            }
        }

        @Override
        public void onFailure(String error) {
            dismissLoading();
            PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 阅读书籍尾部推荐接口
     */
    private void getBookReadRecommend() {
        NetRequest.getBookReadRecommend(work.wid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject ResultData = JSONUtil.getJSONObject(data, "ResultData");
                    JSONObject result = JSONUtil.getJSONObject(ResultData, "result");


//
                    JSONObject hot_comment = JSONUtil.getJSONObject(result, "hot_comment");
                    if (hot_comment == null) {
                        all_comment.setVisibility(View.GONE);
                    } else {
                        all_comment.setVisibility(View.VISIBLE);

                        String avatar_url = JSONUtil.getString(hot_comment, "avatar_url");
                        if (!TextUtils.isEmpty(avatar_url)) {
                            GlideUtil.load(context, avatar_url, R.drawable.default_user_logo, iv_head);
                        }
                        String nickname = JSONUtil.getString(hot_comment, "nickname");
                        String content = JSONUtil.getString(hot_comment, "content");
                        comment_name.setText(nickname);
                        comment_content.setText(content);
                    }
                    JSONObject rec = JSONUtil.getJSONObject(result, "rec");
                    JSONObject rec_info = JSONUtil.getJSONObject(rec, "rec_info");
                    if (rec_info == null) {
                        rl_upload.setVisibility(View.GONE);
                        rl_complete.setVisibility(View.VISIBLE);
                        wid = JSONUtil.getInt(rec, "wid");
                        String title = JSONUtil.getString(rec, "title");
                        String description = JSONUtil.getString(rec, "description");
                        String author = JSONUtil.getString(rec, "author");
                        String h_url = JSONUtil.getString(rec, "h_url");
                        next_cid = JSONUtil.getInt(rec, "next_cid");
                        String content = JSONUtil.getString(rec, "content");
                        recommend_book_title.setText(title);
//                        recommend_chapter_title.setText(title);
                        recommend_book_content.setText(description);
                        GlideUtil.load(context, h_url, R.drawable.default_work_cover, recommend_book_cover);
                        fetchReallyContent(content);

                        JSONObject chapter_info = JSONUtil.getJSONObject(rec, "chapter_info");
                        String chapterTitle = JSONUtil.getString(chapter_info, "title");
                        recommend_chapter_title.setText(chapterTitle);
                        mWork.wid = wid;
                        mWork.title = title;
                        mWork.author = author;
                        mWork.description = description;
                    } else {
                        rl_upload.setVisibility(View.VISIBLE);
                        rl_complete.setVisibility(View.GONE);
                        rec_id = JSONUtil.getInt(rec_info, "rec_id");
                        String title = JSONUtil.getString(rec_info, "title");
                        if (!TextUtils.isEmpty(title)) {
                            tv_name.setText(title);
                        }
                        JSONArray rec_list = JSONUtil.getJSONArray(rec, "rec_list");
                        String isimg = JSONUtil.getString(rec_info,"isimg");
                        String recimg = JSONUtil.getString(rec_info,"recimg");
                        for (int i = ZERO; i < rec_list.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(rec_list, i);
                            BookEndRecommend mReadRecommend = new BookEndRecommend();
                            mReadRecommend.rec_id = JSONUtil.getInt(child, "rec_id");
                            mReadRecommend.wid = JSONUtil.getInt(child, "wid");
                            mReadRecommend.author = JSONUtil.getString(child, "author");
                            mReadRecommend.description = JSONUtil.getString(child, "description");
                            mReadRecommend.title = JSONUtil.getString(child, "title");
                            mReadRecommend.h_url = JSONUtil.getString(child, "h_url");
                            mReadRecommend.sortname = JSONUtil.getString(child, "sortname");
                            mReadRecommend.isimg = isimg;
                            mReadRecommend.isimgUrl = recimg;

                            JSONArray tag = JSONUtil.getJSONArray(child, "tag");
                            List<BookEndRecommend.Tag> mTagBeans = new ArrayList<>();
                            for (int t = ZERO; tag != null && t < tag.length(); t++) {
                                BookEndRecommend.Tag mTagbean = new BookEndRecommend.Tag();
                                mTagbean.id = JSONUtil.getString(JSONUtil.getJSONObject(tag, t), "id");
                                mTagbean.tag = JSONUtil.getString(JSONUtil.getJSONObject(tag, t), "tag");
                                mTagBeans.add(mTagbean);
                            }
                            mReadRecommend.tag = mTagBeans;

                            mBookEndRecommends.add(mReadRecommend);
                        }
                        freeAdapter.notifyDataSetChanged();
                    }


                } else {
                    NetRequest.error(NewReadEndActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    /**
     * 获取oss内容
     * @param url
     */
    public void fetchReallyContent(String url) {

        if (TextUtils.isEmpty(url)){
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    content = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                content = DF.decrypt(content);
                Message msg=new Message();
                msg.what = ACTIONBAR;
                handler.sendMessage(msg);

            }
        });

    }
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 100:
                    if (!TextUtils.isEmpty(content)) {
                        recommend_chapter_content.setText(content);
                        if (cacheSQLiteHelper == null) {
                            cacheSQLiteHelper = CacheSQLiteHelper.get(NewReadEndActivity.this, mWork.wid);
                        }
                        content = DF.decrypt(content);
                        cacheSQLiteHelper.insert(next_cid, content);
                        // 更新阅读历史
                        mWork.lastChapterOrder = 1;
                        mWork.cover = "";
                        ShelfUtil.insertRecord(mWork);

                    }

                    break;
            }
        }
    };

}
