package life.forever.cf.adapter;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.publics.weight.viewtext.FixedClickableSpan;
import life.forever.cf.publics.weight.viewtext.FixedTextView;
import life.forever.cf.publics.weight.viewtext.TextViewUtil;
import life.forever.cf.activtiy.ReadActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Locale;


public class CommentDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final int HEADER = ZERO;
    private final int CLASSIFY = ONE;
    private final int REPLY = TWO;
    private final int NONE = THREE;

    private final Activity context;
    private Comment comment;
    private Work work;

    public CommentDetailAdapter(Activity context, Comment comment, Work work) {
        this.context = context;
        this.comment = comment;
        this.work = work;
    }

    public void update(Comment comment, Work work) {
        this.comment = comment;
        this.work = work;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_comment_detail_header, parent, FALSE));
        } else if (viewType == CLASSIFY) {
            return new ClassifyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_comment_classify, parent, FALSE));
        } else if (viewType == REPLY) {
            return new ReplyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_comment_detail_reply, parent, FALSE));
        }
        return new NoneViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            GlideUtil.load(context, comment.head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(comment.nickname);

            if(comment.is_author_comment == 1){
                viewHolder.iv_start.setVisibility(View.VISIBLE);
            }else{
                viewHolder.iv_start.setVisibility(View.GONE);
            }
            viewHolder.date.setText(ComYou.formatTime(comment.addtime));
            viewHolder.score.setRating(comment.score);
            if (comment.contentType == TWO) {
                viewHolder.reward.setVisibility(View.VISIBLE);
                viewHolder.score.setVisibility(View.GONE);
            } else if (comment.contentType == ONE) {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(comment.score > ZERO ? View.VISIBLE : View.GONE);
            } else {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(View.GONE);
            }
            // 评论内容
            CharSequence result = TextViewUtil.replaceSpan(comment.content);
            if (comment.contentType == ONE) {
                if (comment.cid == ZERO) {
                    viewHolder.content.setText(result);
                } else {
                    SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(R.string.poking_fun));
                    SpannableString titleSpan = new SpannableString("【" + comment.title + "】");
                    titleSpan.setSpan(new TitleClickSpan(comment.wid, comment.cid), ZERO, titleSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    ssb.append(titleSpan).append("”：").append(result);
                    viewHolder.content.setText(ssb);
                }
            }
            if (comment.contentType == TWO) {
                viewHolder.reward.setText(comment.title);
                viewHolder.content.setText(result);
            }
            // 作品信息
            GlideUtil.load(context, work.cover, R.drawable.default_work_cover, viewHolder.cover);
            viewHolder.title.setText(work.title);
            viewHolder.author.setText(work.author);

            viewHolder.work.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkDetailActivity.class);
                    intent.putExtra("wid", work.wid);
                    context.startActivity(intent);
                }
            });
            viewHolder.likeCount.setTextColor(comment.isLike == ONE ? context.getResources().getColor(R.color.theme_color) : context.getResources().getColor(R.color.color_999999));
            viewHolder.likeCount.setText(comment.likeCount+"");
            viewHolder.like.setImageResource(comment.isLike == ONE ? R.drawable.praised : R.drawable.praise);
            viewHolder.ll_likeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PlotRead.getAppUser().login()) {
                        doLike();
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what = BUS_SEND_COMMENT_TO_REPLY;
                    message.obj = comment;
                    EventBus.getDefault().post(message);
                }
            });
        }
        if (holder instanceof ClassifyViewHolder) {
            ClassifyViewHolder viewHolder = (ClassifyViewHolder) holder;
            viewHolder.classify.setText(context.getString(R.string.reply));
            viewHolder.count.setText(String.format(Locale.getDefault(), "（%d）", comment.replyCount));
        }
        if (holder instanceof ReplyViewHolder) {
            ReplyViewHolder viewHolder = (ReplyViewHolder) holder;
            final Comment reply = comment.replays.get(position - TWO);
            if (comment.replays.size() == position - ONE){
                viewHolder.all_content_displaye.setVisibility(View.VISIBLE);
            }else{
                viewHolder.all_content_displaye.setVisibility(View.GONE);
            }
            GlideUtil.load(context, reply.head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(reply.nickname);
            viewHolder.date.setText(ComYou.formatTime(reply.addtime));

            if (reply.relateId == reply.pid) { // 一级回复
                viewHolder.content.setText(reply.content);
            } else {
                SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(R.string.reply));
                SpannableString nameSpan = new SpannableString(reply.toName);
                nameSpan.setSpan(new NameClickable(), ZERO, nameSpan.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ssb.append(nameSpan)
                        .append("：")
                        .append(reply.content);
                viewHolder.content.setText(ssb);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Message message = Message.obtain();
                    message.what = BUS_SEND_COMMENT_TO_REPLY;
                    message.obj = reply;
                    EventBus.getDefault().post(message);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (comment == null) {
            return ZERO;
        }
        if (comment.replays.size() == ZERO) {
            return THREE;
        }
        return comment.replays.size() + TWO;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == ZERO) {
            return HEADER;
        } else if (position == ONE) {
            return CLASSIFY;
        } else if (comment.replays.size() == ZERO) {
            return NONE;
        }
        return REPLY;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        public RadiusImageView head;
        public TextView name;
        public Button iv_start;
        public TextView date;
        public NiceRatingBar score;
        public TextView reward;
        public FixedTextView content;
        public ImageView like;
        public RelativeLayout ll_likeCount;
        public TextView likeCount;

        public View work;
        public ImageView cover;
        public TextView title;
        public TextView author;

        HeaderViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            name = itemView.findViewById(R.id.name);
            iv_start = itemView.findViewById(R.id.iv_start);
            date = itemView.findViewById(R.id.date);
            score = itemView.findViewById(R.id.score);
            reward = itemView.findViewById(R.id.reward);
            content = itemView.findViewById(R.id.content);
            like = itemView.findViewById(R.id.like);
            work = itemView.findViewById(R.id.work);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);


            ll_likeCount = itemView.findViewById(R.id.ll_likeCount);
            likeCount = itemView.findViewById(R.id.likeCount);
            content.setDefaultMovementMethod();
            content.setNeedForceEventToParent(TRUE);
        }
    }

    private class ClassifyViewHolder extends RecyclerView.ViewHolder {

        public TextView classify;
        public TextView count;

        ClassifyViewHolder(View itemView) {
            super(itemView);
            classify = itemView.findViewById(R.id.classify);
            count = itemView.findViewById(R.id.count);
        }
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder {

        public RadiusImageView head;
        public TextView name;

        public TextView date;
        public FixedTextView content;
        public LinearLayout all_content_displaye;
        ReplyViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            name = itemView.findViewById(R.id.name);
            all_content_displaye = itemView.findViewById(R.id.all_content_displaye);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
        }
    }

    private void doLike() {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
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
                            PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.cancel_success));
                            comment.isLike = ZERO;

                            comment.likeCount--;
                        } else {
                            PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.give_like_success));
                            comment.isLike = ONE;
                            comment.likeCount++;
                        }
                        notifyDataSetChanged();
                        // 发通知
                        Message message = Message.obtain();
                        message.what = BUS_COMMENT_ADD_LIKE;
                        message.obj = comment;
                        EventBus.getDefault().post(message);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, context.getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(context, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    private class NameClickable extends FixedClickableSpan {

        NameClickable() {
            super(0xFF4399FA, 0xFF4399FA, Color.TRANSPARENT, Color.TRANSPARENT);
        }

        @Override
        public void onSpanClick(View widget) {

        }
    }

    private class TitleClickSpan extends FixedClickableSpan {

        private final int wid;
        private final int cid;

        TitleClickSpan(int wid, int cid) {
            super(THEME_COLOR, THEME_COLOR, Color.TRANSPARENT, Color.TRANSPARENT);
            this.wid = wid;
            this.cid = cid;
        }

        @Override
        public void onSpanClick(View widget) {
            Intent intent = new Intent(context, ReadActivity.class);
            Work work = new Work();
            work.wid = wid;
            work.lastChapterId = cid;
            work.toReadType = 1;
            intent.putExtra("work", work);
            CollBookBean mCollBook  = new CollBookBean();
            mCollBook.setTitle(work.title);
            mCollBook.set_id(work.wid+"");
            intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
            context.startActivity(intent);
        }
    }
}
