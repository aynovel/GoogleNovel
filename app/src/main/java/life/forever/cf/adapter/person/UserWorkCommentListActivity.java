package life.forever.cf.adapter.person;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.WorkCommentDetailActivity;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.LevelView;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.publics.weight.viewtext.FixedClickableSpan;
import life.forever.cf.publics.weight.viewtext.TextViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserWorkCommentListActivity extends BaseRecyclerViewActivity {

    private final List<PersonalComment> comments = new ArrayList<>();
    private PersonalCommentAdapter commentAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MINE_STRING_MY_COMMENT);
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        commentAdapter = new PersonalCommentAdapter();
        mRecyclerView.setAdapter(commentAdapter);
        fetch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_COMMENT_ADD_LIKE) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i).comment;
                if (comment.equals(temp)) {
                    comment.isLike = temp.isLike;
                    comment.likeCount = temp.likeCount;
                    commentAdapter.notifyDataSetChanged();
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_COMMENT_ADD_REPLY) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i).comment;
                if (comment.equals(temp)) {
                    comment.replyCount = temp.replyCount;
                    commentAdapter.notifyDataSetChanged();
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_COMMENT_DELETE) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i).comment;
                if (comment.equals(temp)) {
                    comments.remove(i);
                    commentAdapter.notifyDataSetChanged();
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_LOG_IN) {
            comments.clear();
            commentAdapter.notifyDataSetChanged();
            reload();
        }
    }

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetch();
        }
    };

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private class PersonalCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(getBaseContext(), parent);
            }
            return new PersonalCommentViewHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_personal_work_comment, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText(getString(R.string.looking_forward_comments));
                return;
            }
            PersonalCommentViewHolder viewHolder = (PersonalCommentViewHolder) holder;
            final PersonalComment personal = comments.get(position);
            GlideUtil.load(context, PlotRead.getAppUser().head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(PlotRead.getAppUser().nickName);
            viewHolder.level.setLevel(PlotRead.getAppUser().level);
            viewHolder.reply.setText(personal.comment.replyCount == ZERO ? getString(R.string.reply) : String.valueOf(personal.comment.replyCount));
            viewHolder.like.setText(personal.comment.likeCount == ZERO ? getString(R.string.like) : String.valueOf(personal.comment.likeCount));
            viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(personal.comment.isLike == ONE ? R.drawable.r_like_icon : R.drawable.r_like_icon, ZERO, ZERO, ZERO);
            viewHolder.score.setRating(personal.comment.score);
            viewHolder.type.setText(getString(R.string.book_review));
            if (personal.comment.contentType == TWO) {
                // viewHolder.type.setText("打赏");
                viewHolder.reward.setVisibility(View.VISIBLE);
                viewHolder.score.setVisibility(View.GONE);
            } else if (personal.comment.contentType == ONE) {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(personal.comment.score > ZERO ? View.VISIBLE : View.GONE);
            } else {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(View.GONE);
            }
            // 评论内容
            CharSequence result = TextViewUtil.replaceSpan(personal.comment.content);
            if (personal.comment.contentType == ONE) {
                if (personal.comment.cid == ZERO) {
                    // viewHolder.type.setText("书评");
                    viewHolder.content.setText(result);
                } else {
                    // viewHolder.type.setText("吐槽");
                    SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.poking_fun));
                    SpannableString titleSpan = new SpannableString("【" + personal.comment.title + "】");
                    titleSpan.setSpan(new TitleSpan(), ZERO, titleSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    ssb.append(titleSpan).append("”：").append(result);
                    viewHolder.content.setText(ssb);
                }
            }
            if (personal.comment.contentType == TWO) {
                viewHolder.reward.setText(personal.comment.title);
                viewHolder.content.setText(result);
            }

            GlideUtil.load(context, personal.work.cover, R.drawable.default_work_cover, viewHolder.cover);
            viewHolder.title.setText(personal.work.title);
            viewHolder.author.setText(personal.work.author);
            viewHolder.date.setText(ComYou.formatTime(personal.comment.addtime));

            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLike(personal.comment);
                }
            });
            viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), WorkCommentDetailActivity.class);
                    intent.putExtra("wid", personal.comment.wid);
                    intent.putExtra("id", personal.comment.id);
                    startActivity(intent);
                }
            });
            viewHolder.work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), WorkDetailActivity.class);
                    intent.putExtra("wid", personal.comment.wid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (comments.size() == ZERO) {
                return ONE;
            }
            return comments.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (comments.size() == ZERO) {
                return ZERO;
            }
            return ONE;
        }
    }

    class PersonalCommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment)
        View comment;
        @BindView(R.id.head)
        RadiusImageView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.level)
        LevelView level;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.like)
        TextView like;
        @BindView(R.id.score)
        NiceRatingBar score;
        @BindView(R.id.reward)
        TextView reward;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.work)
        View work;
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.type)
        TextView type;

        PersonalCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class PersonalComment {
        Comment comment;
        Work work;
    }

    private void fetch() {
        NetRequest.myCommentList(pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        int count = JSONUtil.getInt(result, "count");
                        if (pageIndex == ONE && totalPage == ZERO) {
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + ONE;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(lists, i);
                            PersonalComment personalComment = new PersonalComment();
                            personalComment.comment = BeanParser.getComment(child);
                            personalComment.work = BeanParser.getWork(child);
                            personalComment.work.title = JSONUtil.getString(child, "book_name");
                            comments.add(personalComment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(UserWorkCommentListActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                    PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void reload() {
        pageIndex = ONE;
        totalPage = ZERO;
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        fetch();
    }

    private void doLike(final Comment comment) {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(UserWorkCommentListActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        NetRequest.workCommentLike(comment.wid, comment.id, comment.isLike == ONE ? TWO : ONE, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (comment.isLike == ONE) {
                            comment.isLike = ZERO;
                            comment.likeCount--;
                            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.cancel_success));
                        } else {
                            comment.isLike = ONE;
                            comment.likeCount++;
                            PlotRead.toast(PlotRead.SUCCESS,  getString(R.string.give_like_success));
                        }
                        // 发通知
                        Message message = Message.obtain();
                        message.what = BUS_COMMENT_ADD_LIKE;
                        message.obj = comment;
                        EventBus.getDefault().post(message);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(UserWorkCommentListActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    private class TitleSpan extends FixedClickableSpan {

        TitleSpan() {
            super(THEME_COLOR, THEME_COLOR, Color.TRANSPARENT, Color.TRANSPARENT);
        }

        @Override
        public void onSpanClick(View widget) {

        }
    }
}
